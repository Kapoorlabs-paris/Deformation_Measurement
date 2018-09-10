package curvatureFinder;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RealLocalizable;
import net.imglib2.algorithm.region.hypersphere.HyperSphere;
import net.imglib2.algorithm.region.hypersphere.HyperSphereCursor;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.InteractiveSimpleEllipseFit;

public abstract class SegmentCreator<T extends RealType<T> & NativeType<T>>  implements CurvatureFinders<T> {
	
	
	public void MakeSegments(InteractiveSimpleEllipseFit parent, final List<RealLocalizable> truths, int numSeg,
			int celllabel) {

		if(truths.size() < 3)
			return;
		else {
		int size = truths.size();

		
		int maxpoints = size / numSeg;
		if (maxpoints <= 2)
			maxpoints = 3;
       int biggestsize = maxpoints;		
		int segmentLabel = 1;

		List<RealLocalizable> sublist = new ArrayList<RealLocalizable>();

		for (int i = 0; i <= size - maxpoints; i += maxpoints) {

			int endindex = i + maxpoints;

			
			if(endindex  >= size)
				endindex = size -1;
			
			
			sublist = truths.subList(i, endindex);
			parent.Listmap.put(segmentLabel, sublist);
			
			if(biggestsize >= endindex - i)
				biggestsize = endindex - i;
			
			
			parent.CellLabelsizemap.put(celllabel, biggestsize);
			segmentLabel++;
			
		}
			
		}

	}
	
	/**
	 * Obtain intensity in the user defined
	 * 
	 * @param point
	 * @return
	 */

	public Pair<Double, Double> getIntensity(InteractiveSimpleEllipseFit parent, Localizable point, Localizable centerpoint) {

		RandomAccess<FloatType> ranac = parent.CurrentViewOrig.randomAccess();

		double Intensity = 0;
		double IntensitySec = 0;
		RandomAccess<FloatType> ranacsec;
		if (parent.CurrentViewSecOrig != null)
			ranacsec = parent.CurrentViewSecOrig.randomAccess();
		else
			ranacsec = ranac;

		ranac.setPosition(point);
		ranacsec.setPosition(ranac);
		double mindistance = getDistance(point, centerpoint);
		double[] currentPosition = new double[point.numDimensions()];
		
		 HyperSphere< FloatType > hyperSphere = new HyperSphere<FloatType>( parent.CurrentViewOrig, ranac, (int)parent.insidedistance );
		HyperSphereCursor<FloatType> localcursor = hyperSphere.localizingCursor();
		int Area = 1;
		while(localcursor.hasNext()) {
			
			localcursor.fwd();
			
			ranacsec.setPosition(localcursor);
			
			ranacsec.localize(currentPosition);
			
			
			double currentdistance = getDistance(localcursor, centerpoint);
			if ((currentdistance - mindistance) <= parent.insidedistance) {
			Intensity += localcursor.get().getRealDouble();
			IntensitySec += ranacsec.get().getRealDouble();
			Area++;
			}
		}
	
		
			return new ValuePair<Double, Double>(Intensity/ Area, IntensitySec/Area);
		}
	
	
      public double getDistance(Localizable point, Localizable centerpoint) {
		
		double distance = 0;
		
		int ndims = point.numDimensions();
		
		
		for (int i = 0; i < ndims; ++i) {
			
			distance+= (point.getDoublePosition(i) - centerpoint.getDoublePosition(i)) * (point.getDoublePosition(i) - centerpoint.getDoublePosition(i)) ;
			
		}
		
		return Math.sqrt(distance);
		
	}
	

}
