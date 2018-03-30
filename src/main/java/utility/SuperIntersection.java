package utility;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ellipsoidDetector.Intersectionobject;
import ellipsoidDetector.Tangentobject;
import ij.gui.EllipseRoi;
import ij.gui.Line;
import ij.gui.OvalRoi;
import net.imglib2.Point;
import net.imglib2.RealLocalizable;
import net.imglib2.algorithm.ransac.RansacModels.Angleobject;
import net.imglib2.algorithm.ransac.RansacModels.Ellipsoid;
import net.imglib2.algorithm.ransac.RansacModels.Intersections;
import net.imglib2.algorithm.ransac.RansacModels.Tangent2D;
import net.imglib2.type.logic.BitType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.InteractiveSimpleEllipseFit;

public class SuperIntersection {

	final InteractiveSimpleEllipseFit parent;

	public SuperIntersection(final InteractiveSimpleEllipseFit parent) {

		this.parent = parent;

	}

	public void Getsuperintersection(ArrayList<EllipseRoi> resultroi, ArrayList<OvalRoi> resultovalroi,
			ArrayList<Line> resultlineroi, final ArrayList<Tangentobject> AllPointsofIntersect,
			final ArrayList<Intersectionobject> Allintersection, int t, int z) {

		System.out.println("Super fitting post loop");
		String uniqueID = Integer.toString(z) + Integer.toString(t);
		System.out.println("ID "+ " " +Integer.toString(z) + " " + Integer.toString(t) + " " +  uniqueID );
		ArrayList<EllipseRoi> preresultroi = new ArrayList<EllipseRoi>();
		ArrayList<OvalRoi> preresultovalroi = new ArrayList<OvalRoi>();
		ArrayList<Line> preresultlineroi = new ArrayList<Line>();
		if(parent.rect==null) {
			
			parent.rect = new Rectangle((int)parent.originalimg.min(0),(int) parent.originalimg.min(1), (int)parent.originalimg.max(0), (int)parent.originalimg.max(1));
			
		}
		
		if(parent.redoing) {
		// Change the Rois
		
		
					for (Map.Entry<String, Roiobject> entry : parent.ZTRois.entrySet()) {

						Roiobject currentobject = entry.getValue();

						preresultovalroi.addAll(currentobject.resultovalroi);
						preresultroi.addAll(currentobject.resultroi);
						preresultlineroi.addAll(currentobject.resultlineroi);
						
						if (currentobject.fourthDimension == t && currentobject.thirdDimension == z) {

							for( OvalRoi currentoval: currentobject.resultovalroi) {
								
								double[] center = currentoval.getContourCentroid();
								
								
							if (parent.rect.contains((int) center[0], (int) center[1])){
								
								preresultovalroi.remove(currentoval);
							}
							
						}
							
							
							for (EllipseRoi currentellipse: currentobject.resultroi) {
								
								double[] center = currentellipse.getContourCentroid();
								
								if (parent.rect.contains((int) center[0], (int) center[1])){
									
									preresultroi.remove(currentellipse);
								}
								
							}
							
							for (Line currentline: currentobject.resultlineroi) {
                                 
								double[] center = currentline.getContourCentroid();
								
								if (parent.rect.contains((int) center[0], (int) center[1])){
									
									preresultlineroi.remove(currentline);
								}
								
							}
							
							
			
					}
					}
					
					Roiobject precurrentobject = new Roiobject(preresultroi, preresultovalroi, preresultlineroi, z, t, true);
					parent.ZTRois.put(uniqueID, precurrentobject);
					DisplayAuto.Display(parent);
	}
					
		final ArrayList<Pair<Ellipsoid, Ellipsoid>> fitmapspecial = new ArrayList<Pair<Ellipsoid, Ellipsoid>>();

		for (int index = 0; index < parent.superReducedSamples.size(); ++index) {

			for (int indexx = 0; indexx < parent.superReducedSamples.size(); ++indexx) {

				if (index != indexx) {

					fitmapspecial.add(new ValuePair<Ellipsoid, Ellipsoid>(parent.superReducedSamples.get(index).getA(),
							parent.superReducedSamples.get(indexx).getA()));

				}

			}

		}

		final ArrayList<Pair<Ellipsoid, Ellipsoid>> fitmapspecialred = new ArrayList<Pair<Ellipsoid, Ellipsoid>>();
		fitmapspecialred.addAll(fitmapspecial);
		for (int i = 0; i < fitmapspecialred.size(); ++i) {

			Pair<Ellipsoid, Ellipsoid> ellipsepairA = fitmapspecialred.get(i);

			for (int j = 0; j < fitmapspecialred.size(); ++j) {

				if (i != j) {
					Pair<Ellipsoid, Ellipsoid> ellipsepairB = fitmapspecialred.get(j);

					if (ellipsepairA.getA().hashCode() == (ellipsepairB.getB().hashCode())
							&& ellipsepairA.getB().hashCode() == (ellipsepairB.getA().hashCode())) {
						fitmapspecialred.remove(ellipsepairB);
						break;
					}

				}

			}

		}

		for (int i = 0; i < fitmapspecialred.size(); ++i) {

			Pair<Ellipsoid, Ellipsoid> ellipsepair = fitmapspecialred.get(i);

			ArrayList<double[]> pos = Intersections.PointsofIntersection(ellipsepair);

			Tangentobject PointsIntersect = new Tangentobject(pos, ellipsepair, t, z);

			for (int j = 0; j < pos.size(); ++j) {

				OvalRoi intersectionsRoi = new OvalRoi(pos.get(j)[0] - parent.radiusdetection,
						pos.get(j)[1] - parent.radiusdetection, 2 * parent.radiusdetection, 2 * parent.radiusdetection);
				intersectionsRoi.setStrokeColor(parent.colorDet);
				resultovalroi.add(intersectionsRoi);

				double[] lineparamA = Tangent2D.GetTangent(ellipsepair.getA(), pos.get(j));

				double[] lineparamB = Tangent2D.GetTangent(ellipsepair.getB(), pos.get(j));

				Angleobject angleobject = Tangent2D.GetTriAngle(lineparamA, lineparamB, pos.get(j), ellipsepair);
				resultlineroi.add(angleobject.lineA);
				resultlineroi.add(angleobject.lineB);

				Intersectionobject currentintersection = new Intersectionobject(pos.get(j), angleobject.angle,
						ellipsepair, resultlineroi, t, z);

				Allintersection.add(currentintersection);

				System.out.println("Angle: " + angleobject.angle + " " + pos.get(j)[0]);

			}

			AllPointsofIntersect.add(PointsIntersect);

		}
	
		
		if (!parent.redoing) {
			

			parent.ALLIntersections.put(uniqueID, Allintersection);

			// Add new result rois to ZTRois
			for (Map.Entry<String, Roiobject> entry : parent.ZTRois.entrySet()) {

				Roiobject currentobject = entry.getValue();

				if (currentobject.fourthDimension == t && currentobject.thirdDimension == z) {

					currentobject.resultroi = resultroi;
					currentobject.resultovalroi = resultovalroi;
					currentobject.resultlineroi = resultlineroi;

				}

			}

			Roiobject currentobject = new Roiobject(resultroi, resultovalroi, resultlineroi, z, t, true);
			parent.ZTRois.put(uniqueID, currentobject);

			DisplayAuto.Display(parent);
		}

		else {
			ArrayList<Intersectionobject> target = parent.ALLIntersections.get(uniqueID);
			
			// Change the entry for a give ZT
			for(Intersectionobject current: Allintersection) {
				
				
				
				double[] changepoint = current.Intersectionpoint;
				
				
				Intersectionobject changeobject = utility.NearestRoi.getNearestIntersection(target, changepoint, parent);
				target.remove(changeobject);
				target.add(current);
				
			}
			
			
			
			System.out.println("I did it");
			preresultroi.addAll(resultroi);
			preresultlineroi.addAll(resultlineroi);
			preresultovalroi.addAll(resultovalroi);
			Roiobject currentobject = new Roiobject(preresultroi, preresultovalroi, preresultlineroi, z, t, true);
			parent.ZTRois.put(uniqueID, currentobject);
			parent.ALLIntersections.put(uniqueID, target);
			DisplayAuto.Display(parent);
		}

	}

}