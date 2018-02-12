package utility;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ellipsoidDetector.Distance;
import ellipsoidDetector.Intersectionobject;
import ellipsoidDetector.Tangentobject;
import ij.gui.EllipseRoi;
import ij.gui.Line;
import ij.gui.OvalRoi;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.algorithm.ransac.RansacModels.*;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.InteractiveSimpleEllipseFit;

public class LabelRansacMistake implements Runnable {

	final InteractiveSimpleEllipseFit parent;
	final RandomAccessibleInterval<BitType> ActualRoiimg;
	final Rectangle rect;
	final int t;
	final int z;


	public LabelRansacMistake(final InteractiveSimpleEllipseFit parent, final RandomAccessibleInterval<BitType> ActualRoiimg,
			final int t, final int z, Rectangle rect) {

		this.parent = parent;
		this.rect = rect;
		this.ActualRoiimg = ActualRoiimg;
		this.t = t;
		this.z = z;

	}

	@Override
	public void run() {

		String uniqueID = Integer.toString(z) + Integer.toString(t);
		ArrayList<Intersectionobject> Intersectionlist = parent.ALLIntersections.get(uniqueID);
		
		ArrayList<Intersectionobject> IntersectionlistChange = new ArrayList<Intersectionobject>();
		IntersectionlistChange.addAll(Intersectionlist);
		
		for (Intersectionobject currentintersection: Intersectionlist) {
			
			Pair<Ellipsoid, Ellipsoid> ellipsepair = currentintersection.ellipsepair;
			
			double[] centerA = ellipsepair.getA().getCenter();
			double[] centerB = ellipsepair.getB().getCenter();
			 Roiobject currentroi = parent.ZTRois.get(uniqueID);
				
              ArrayList<Line> currentlines = currentroi.resultlineroi;
              
              ArrayList<OvalRoi> currentovals = currentroi.resultovalroi;
              
             
              
              
              for (int i = 0; i< currentlines.size(); ++i) {
            	  
            	  int[] point = new int[] {currentlines.get(i).x1 , currentlines.get(i).y1};
            	  
            	  if (rect.contains(point[0], point[1]))
            		  currentlines.remove(currentlines.get(i));
            	  
              }
              
              
              for (int i = 0; i< currentovals.size(); ++i) {
            	  
            	  int[] point = new int[] {currentovals.get(i).getBounds().x , currentovals.get(i).getBounds().y};
            	  
            	  if (rect.contains(point[0], point[1]))
            		  currentovals.remove(currentovals.get(i));
            	  
              }
              
         
              
              
			// Check for the intersection points inside the rectangle
			if(rect.contains(centerA[0], centerA[1]) || rect.contains(centerB[0], centerB[1])) {
				
				
				// Compute the intersection point again
				
				
				IntersectionlistChange.remove(currentintersection);
				 parent.ALLIntersections.remove(uniqueID);
				
				
				 
				ArrayList<double[]> pos = Intersections.PointsofIntersection(ellipsepair);


				for (int j = 0; j < pos.size(); ++j) {

					OvalRoi intersectionsRoi = new OvalRoi(pos.get(j)[0] - parent.radiusdetection,
							pos.get(j)[1] - parent.radiusdetection, 2 * parent.radiusdetection,
							2 * parent.radiusdetection);
					intersectionsRoi.setStrokeColor(parent.colorDet);
					currentovals.add(intersectionsRoi);

					double[] lineparamA = Tangent2D.GetTangent(ellipsepair.getA(), pos.get(j));
					Line newlineA = DisplayasROI.create2DLine(lineparamA, pos.get(j));
					newlineA.setStrokeColor(parent.colorLineA);
					newlineA.setStrokeWidth(2);
					currentlines.add(newlineA);

					double[] lineparamB = Tangent2D.GetTangent(ellipsepair.getB(), pos.get(j));
					Line newlineB = DisplayasROI.create2DLine(lineparamB, pos.get(j));
					newlineB.setStrokeColor(parent.colorLineB);
					newlineB.setStrokeWidth(2);
					currentlines.add(newlineB);

					double angle = Tangent2D.GetAngle(lineparamA, lineparamB);

					Intersectionobject currentintersectionobject = new Intersectionobject(pos.get(j), angle, ellipsepair, t,
							z);

					IntersectionlistChange.add(currentintersectionobject);

					System.out.println(angle + " " + pos.get(j)[0]);

				}
				
				parent.ALLIntersections.put(uniqueID, IntersectionlistChange);
				// Add new result rois to ZTRois
				for (Map.Entry<String, Roiobject> entry : parent.ZTRois.entrySet()) {

					Roiobject currentobject = entry.getValue();

					if (currentobject.fourthDimension == t && currentobject.thirdDimension == z) {

						currentobject.resultovalroi = currentovals;
						currentobject.resultlineroi = currentlines;

					}

				}
			}
			
			
		}
		

	}
	}
