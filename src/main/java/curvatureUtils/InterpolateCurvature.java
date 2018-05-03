package curvatureUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ellipsoidDetector.Distance;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import utility.Curvatureobject;
import utility.Listordereing;

public class InterpolateCurvature {

	static double currentperimeter;
	static int currentlabel;
	static int currentz;
	static int currentt;

	public static ArrayList<Curvatureobject> InterpolateValue(List<RealLocalizable> densetruths, final ArrayList<Curvatureobject> localCurvature) {

		ArrayList<Curvatureobject> interpolatedCurvature = new ArrayList<Curvatureobject>();
		interpolatedCurvature.addAll(localCurvature);

		List<Pair<RealLocalizable, Double>> Treelist = MakeList(localCurvature);
		currentperimeter = localCurvature.get(0).perimeter;
		currentlabel = localCurvature.get(0).Label;
		currentz = localCurvature.get(0).z;
		currentt = localCurvature.get(0).t;

		Iterator<RealLocalizable> iter = densetruths.iterator();

		while (iter.hasNext()) {

			RealLocalizable current = iter.next();


				Pair<RealLocalizable, Double> nearestpoint = Listordereing.getNextNearestPoint(current,
						Treelist);
				
				Treelist.remove(nearestpoint);
				
				Pair<RealLocalizable, Double> nextnearestpoint = Listordereing.getNextNearestPoint(current,
						Treelist);
				Treelist.add(nearestpoint);
				
				Pair<RealLocalizable, Double> illegalpoint = Listordereing.getNextNearestPoint(nearestpoint.getA(),
						Treelist);
				
				
                if(nearestpoint!=null && nextnearestpoint!=null && illegalpoint!=nextnearestpoint)
				Interpolate(nearestpoint, nextnearestpoint, interpolatedCurvature);


		}

		return interpolatedCurvature;
	}

	public static List<Pair<RealLocalizable, Double>> MakeList(final ArrayList<Curvatureobject> localCurvature) {

		List<Pair<RealLocalizable, Double>> Treelist = new ArrayList<Pair<RealLocalizable, Double>>();

		for (Curvatureobject currentCurvature : localCurvature) {

			RealPoint currentpoint = new RealPoint(currentCurvature.cord.length);

			for (int d = 0; d < currentCurvature.cord.length; ++d) {

				currentpoint.setPosition(currentCurvature.cord[d], d);

			}

			Treelist.add(new ValuePair<RealLocalizable, Double>(currentpoint, currentCurvature.radiusCurvature));

		}

		return Treelist;
	}

	public static void Interpolate(Pair<RealLocalizable, Double> nearestpoint,
			Pair<RealLocalizable, Double> nextnearestpoint, ArrayList<Curvatureobject> interpolatedCurvature) {

		double startX = (nearestpoint.getA().getDoublePosition(0) - nextnearestpoint.getA().getDoublePosition(0)) > 0
				? nextnearestpoint.getA().getDoublePosition(0)
				: nearestpoint.getA().getDoublePosition(0);

		double startY = (startX - nearestpoint.getA().getDoublePosition(0)) == 0
				? nearestpoint.getA().getDoublePosition(1)
				: nextnearestpoint.getA().getDoublePosition(1);

		double CurveA = nearestpoint.getB();
		double CurveB = nextnearestpoint.getB();

		double maxcurve = (CurveB - CurveA) > 0 ? CurveB : CurveA;
		double mincurve = (CurveB - CurveA) > 0 ? CurveA : CurveB;

		double endX = (startX - nearestpoint.getA().getDoublePosition(0)) == 0
				? nextnearestpoint.getA().getDoublePosition(0)
				: nearestpoint.getA().getDoublePosition(0);

		double endY = (endX - nearestpoint.getA().getDoublePosition(0)) == 0 ? nearestpoint.getA().getDoublePosition(1)
				: nextnearestpoint.getA().getDoublePosition(1);

		double slope = (endY - startY) / (endX - startX);
		double intercept = endY - slope * endX;

		double distance = Distance.DistanceSqrt(nearestpoint.getA(), nextnearestpoint.getA());
		double rate = 0.1;
		if (slope != Double.NaN) {
			int i = 1;
			do {

				
				double X = startX + rate;
				double Y = slope * X + intercept;
				double currentcurve =  (maxcurve + mincurve) / 2;
				Curvatureobject newobject = new Curvatureobject(currentcurve, currentperimeter, currentlabel,
						new double[] { X, Y }, currentt, currentz);
				interpolatedCurvature.add(newobject);
				i++;
				startX = X;
				
				if(startX >= endX || Y >= endY && slope > 0)
					break;
				if(startX >= endX || Y <= endY && slope < 0)
					break;
				
			}while(true);

		}

	}

}
