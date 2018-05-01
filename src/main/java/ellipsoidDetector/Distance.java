package ellipsoidDetector;

import net.imglib2.RealLocalizable;

public class Distance {


	public static double DistanceSq(final double[] pointA, final double[] pointB) {

		double distance = 0;
		int numDim = pointA.length;

		for (int d = 0; d < numDim; ++d) {

			distance += (pointA[d] - pointB[d])
					* (pointA[d] - pointB[d]);

		}
		return distance;
	}
	
	
	public static double DistanceSqrt(final RealLocalizable pointA, final RealLocalizable pointB) {

		double distance = 0;
		
		double[] pointAA = new double[pointA.numDimensions()];
		double[] pointBB = new double[pointB.numDimensions()];
		
		pointA.localize(pointAA);
		pointB.localize(pointBB);
		
		int numDim = pointAA.length;

		for (int d = 0; d < numDim; ++d) {

			distance += (pointAA[d] - pointBB[d])
					* (pointAA[d] - pointBB[d]);

		}
		return Math.sqrt(distance);
	}
}
