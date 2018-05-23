package utility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import ellipsoidDetector.Intersectionobject;
import ellipsoidDetector.Tangentobject;
import ij.gui.EllipseRoi;
import ij.gui.Line;
import ij.gui.OvalRoi;
import net.imglib2.RealLocalizable;
import net.imglib2.algorithm.ransac.RansacModels.Angleobject;
import net.imglib2.algorithm.ransac.RansacModels.Ellipsoid;
import net.imglib2.algorithm.ransac.RansacModels.Intersections;
import net.imglib2.algorithm.ransac.RansacModels.Tangent2D;
import net.imglib2.type.logic.BitType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.InteractiveSimpleEllipseFit;

public class NormalIntersection {

	

	final InteractiveSimpleEllipseFit parent;

	
	public NormalIntersection(final InteractiveSimpleEllipseFit parent) {

		this.parent = parent;

	}

	// Normal
	public void Getsuperintersection(ArrayList<EllipseRoi> resultroi, ArrayList<OvalRoi> resultovalroi,
			ArrayList<Line> resultlineroi, final ArrayList<Tangentobject> AllPointsofIntersect,
			final ArrayList<Intersectionobject> Allintersection, int t, int z) {

		String uniqueID = Integer.toString(z) + Integer.toString(t);

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
		// Remove duplicate pairs
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

			// Reject wrong points that are not candidate points

		

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


			}

			AllPointsofIntersect.add(PointsIntersect);

		}

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

	}

	

	public double Sqdistance(double[] sourceLocation, double[] targetLocation) {

		double distance = 0;

		for (int d = 0; d < sourceLocation.length; ++d) {

			distance += (sourceLocation[d] - targetLocation[d]) * (sourceLocation[d] - targetLocation[d]);
		}

		return distance;

	}

	
}
