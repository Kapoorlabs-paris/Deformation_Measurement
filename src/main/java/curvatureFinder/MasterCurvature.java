package curvatureFinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import curvatureUtils.PointExtractor;
import ellipsoidDetector.Intersectionobject;
import kalmanForSegments.Segmentobject;
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
import pluginTools.RegressionCurveSegment;
import ransacPoly.RegressionFunction;
import utility.Curvatureobject;
import utility.Listordereing;

public abstract class MasterCurvature<T extends RealType<T> & NativeType<T>>  implements CurvatureFinders<T> {
	
	/**
	 * 
	 * Function to fit on a list of points which are not tree based
	 * 
	 * @param parent
	 * @param centerpoint
	 * @param sublist
	 * @param functions
	 * @param interpolatedCurvature
	 * @param smoothing
	 * @param maxError
	 * @param minNumInliers
	 * @param degree
	 * @param secdegree
	 * @return
	 */
	public Pair<RegressionFunction, ArrayList<double[]>> FitonList(InteractiveSimpleEllipseFit parent,
			RealLocalizable centerpoint, List<RealLocalizable> sublist) {

		ArrayList<double[]> Cordlist = new ArrayList<double[]>();

		for (int i = 0; i < sublist.size(); ++i) {

			Cordlist.add(new double[] { sublist.get(i).getDoublePosition(0), sublist.get(i).getDoublePosition(1) });
		}

		Pair<RegressionFunction, ArrayList<double[]>> resultcurvature = getLocalcurvature(Cordlist, centerpoint);

		// Draw the function

		return resultcurvature;

	}
	
	public RegressionCurveSegment CommonLoop(InteractiveSimpleEllipseFit parent,	 List<RealLocalizable> Ordered, 
			RealLocalizable centerpoint, int ndims, int celllabel, int t, int z) {

		// Get the sparse list of points
		HashMap<Integer, RegressionCurveSegment> Bestdelta = new HashMap<Integer, RegressionCurveSegment>();

		int count = 0;
	

		int i = parent.increment;

		// Get the sparse list of points

		List<RealLocalizable> allorderedtruths = Listordereing.getList(Ordered, i);

		if (parent.fourthDimensionSize > 1)
			parent.timeslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.fourthDimension,
					parent.fourthDimensionsliderInit, parent.fourthDimensionSize, parent.scrollbarSize));
		parent.zslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.thirdDimension,
				parent.thirdDimensionsliderInit, parent.thirdDimensionSize, parent.scrollbarSize));
		// Make a tree of a certain depth

		int treedepth = parent.depth - 1;

		if (treedepth <= 0)
			treedepth = 0;


		RegressionCurveSegment resultpair = getCurvature(parent, allorderedtruths, centerpoint, ndims, celllabel, z, t);

		// Here counter the segments where the number of inliers was too low

		Bestdelta.put(count, resultpair);
		count++;

		parent.localCurvature = resultpair.Curvelist;

		parent.functions = resultpair.functionlist;
		parent.localSegment = resultpair.Seglist;

		return resultpair;

	}
	
	/**
	 * 
	 * Take in a list of ordered co-ordinates and compute a curvature object
	 * containing the curvature information at each co-ordinate Makes a tree
	 * structure of the list
	 * 
	 * @param orderedtruths
	 * @param ndims
	 * @param Label
	 * @param t
	 * @param z
	 * @return
	 */
	public RegressionCurveSegment getCurvature(InteractiveSimpleEllipseFit parent,	List<RealLocalizable> truths, RealLocalizable centerpoint,
			 int ndims, int Label,  int z, int t) {

		ArrayList<Curvatureobject> curveobject = new ArrayList<Curvatureobject>();

		ArrayList<double[]> totalinterpolatedCurvature = new ArrayList<double[]>();

		ArrayList<RegressionFunction> totalfunctions = new ArrayList<RegressionFunction>();

		double perimeter = 0;


		MakeSegments(parent, truths, parent.minNumInliers, Label);
		// Now do the fitting
		ArrayList<Segmentobject> Allcellsegment = new ArrayList<Segmentobject>();
		for (Map.Entry<Integer, List<RealLocalizable>> entry : parent.Listmap.entrySet()) {
			
			
			List<RealLocalizable> sublist = entry.getValue();
			/***
			 * 
			 * Main method that fitst on segments a function to get the curvature
			 * 
			 */
			Pair<RegressionFunction, ArrayList<double[]>> localfunction = FitonList(parent, centerpoint, sublist);

			perimeter += localfunction.getA().Curvaturepoints.get(0)[3];
			totalfunctions.add(localfunction.getA());
			totalinterpolatedCurvature.addAll(localfunction.getB());

			RealLocalizable Cord = Listordereing.getMeanCord(sublist);
			double Curvature = localfunction.getB().get(0)[2];
			double IntensityA = localfunction.getB().get(0)[4];
			double IntensityB = localfunction.getB().get(0)[5];
			double SegPeri = localfunction.getB().get(0)[3];

			Iterator<RealLocalizable> iter = sublist.iterator();
			ArrayList<double[]> curvelist = new ArrayList<double[]>();
			while (iter.hasNext()) {

				RealLocalizable current = iter.next();

				curvelist.add(new double[] { current.getDoublePosition(0), current.getDoublePosition(1), Curvature,
						IntensityA, IntensityB });
			}

			Segmentobject cellsegment = new Segmentobject(curvelist, centerpoint, Cord, Curvature, IntensityA,
					IntensityB, SegPeri, entry.getKey(), Label, z, t);

			Allcellsegment.add(cellsegment);

		}

		for (int indexx = 0; indexx < totalinterpolatedCurvature.size(); ++indexx) {

			Curvatureobject currentobject = new Curvatureobject(
					totalinterpolatedCurvature.get(indexx)[2], perimeter, totalinterpolatedCurvature.get(indexx)[4],
					totalinterpolatedCurvature.get(indexx)[5], Label, new double[] {
							totalinterpolatedCurvature.get(indexx)[0], totalinterpolatedCurvature.get(indexx)[1] },
					z, t);

			curveobject.add(currentobject);

		}

		// All nodes are returned

	
		
		
		RegressionCurveSegment returnSeg = new RegressionCurveSegment(totalfunctions, curveobject, Allcellsegment);
		return returnSeg;

	}
	
	public Pair<Intersectionobject, Intersectionobject> GetAverage(InteractiveSimpleEllipseFit parent, RealLocalizable centerpoint, HashMap<Integer, RegressionCurveSegment> Bestdelta, int count){
		
		RegressionCurveSegment resultpair = Bestdelta.get(0);
		ArrayList<Curvatureobject> RefinedCurvature = new ArrayList<Curvatureobject>();
		ArrayList<Curvatureobject> localCurvature = resultpair.Curvelist;

		double[] X = new double[localCurvature.size()];
		double[] Y = new double[localCurvature.size()];
		double[] Z = new double[localCurvature.size()];
		double[] I = new double[localCurvature.size()];
		double[] ISec = new double[localCurvature.size()];

		ArrayList<Double> CurvePeri = new ArrayList<Double>();
		CurvePeri.add(localCurvature.get(0).perimeter);

		for (int index = 0; index < localCurvature.size(); ++index) {

			ArrayList<Double> CurveXY = new ArrayList<Double>();
			ArrayList<Double> CurveI = new ArrayList<Double>();
			ArrayList<Double> CurveISec = new ArrayList<Double>();

			X[index] = localCurvature.get(index).cord[0];
			Y[index] = localCurvature.get(index).cord[1];
			Z[index] = localCurvature.get(index).radiusCurvature;
			I[index] = localCurvature.get(index).Intensity;
			ISec[index] = localCurvature.get(index).SecIntensity;

			CurveXY.add(Z[index]);
			CurveI.add(I[index]);
			CurveISec.add(ISec[index]);
			for (int secindex = 1; secindex < count; ++secindex) {

				RegressionCurveSegment testpair = Bestdelta.get(secindex);

				ArrayList<Curvatureobject> testlocalCurvature = testpair.Curvelist;

				double[] Xtest = new double[testlocalCurvature.size()];
				double[] Ytest = new double[testlocalCurvature.size()];
				double[] Ztest = new double[testlocalCurvature.size()];
				double[] Itest = new double[testlocalCurvature.size()];
				double[] ISectest = new double[testlocalCurvature.size()];

				CurvePeri.add(testlocalCurvature.get(0).perimeter);
				for (int testindex = 0; testindex < testlocalCurvature.size(); ++testindex) {

					Xtest[testindex] = testlocalCurvature.get(testindex).cord[0];
					Ytest[testindex] = testlocalCurvature.get(testindex).cord[1];
					Ztest[testindex] = testlocalCurvature.get(testindex).radiusCurvature;
					Itest[index] = testlocalCurvature.get(testindex).Intensity;
					ISectest[index] = testlocalCurvature.get(testindex).SecIntensity;
					if (X[index] == Xtest[testindex] && Y[index] == Ytest[testindex]) {

						CurveXY.add(Ztest[testindex]);
						CurveI.add(Itest[index]);
						CurveISec.add(ISectest[index]);

					}

				}

			}
			double frequdeltaperi = localCurvature.get(0).perimeter;
			double frequdelta = Z[index];
			double intensitydelta = I[index];
			double intensitySecdelta = ISec[index];

			Iterator<Double> setiter = CurveXY.iterator();
			while (setiter.hasNext()) {

				Double s = setiter.next();

				frequdelta += s;

			}

			frequdelta /= CurveXY.size();
			Iterator<Double> perisetiter = CurvePeri.iterator();
			while (perisetiter.hasNext()) {

				Double s = perisetiter.next();

				frequdeltaperi += s;

			}

			frequdeltaperi /= CurvePeri.size();

			Iterator<Double> Iiter = CurveI.iterator();
			while (Iiter.hasNext()) {

				Double s = Iiter.next();

				intensitydelta += s;

			}

			intensitydelta /= CurveI.size();

			Iterator<Double> ISeciter = CurveISec.iterator();
			while (ISeciter.hasNext()) {

				Double s = ISeciter.next();

				intensitySecdelta += s;

			}

			intensitySecdelta /= CurveISec.size();

			Curvatureobject newobject = new Curvatureobject((float) frequdelta, frequdeltaperi, intensitydelta,
					intensitySecdelta, localCurvature.get(index).Label, localCurvature.get(index).cord,
					localCurvature.get(index).t, localCurvature.get(index).z);

			RefinedCurvature.add(newobject);
		}

		Pair<ArrayList<RegressionFunction>, ArrayList<Curvatureobject>> Refinedresultpair = new ValuePair<ArrayList<RegressionFunction>, ArrayList<Curvatureobject>>(
				resultpair.functionlist, RefinedCurvature);
		parent.localCurvature = Refinedresultpair.getB();
		parent.functions.addAll(Refinedresultpair.getA());
		// Make intersection object here

		Pair<Intersectionobject, Intersectionobject> currentobjectpair = PointExtractor.CurvaturetoIntersection(parent,
				parent.localCurvature, parent.functions, centerpoint, parent.smoothing);
		Intersectionobject densecurrentobject = currentobjectpair.getA();
		Intersectionobject sparsecurrentobject = currentobjectpair.getB();
		
		return new ValuePair<Intersectionobject, Intersectionobject> (sparsecurrentobject, densecurrentobject);
	}
	
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
      public double getDistance(RealLocalizable point, RealLocalizable centerpoint) {
  		
  		double distance = 0;
  		
  		int ndims = point.numDimensions();
  		
  		
  		for (int i = 0; i < ndims; ++i) {
  			
  			distance+= (point.getDoublePosition(i) - centerpoint.getDoublePosition(i)) * (point.getDoublePosition(i) - centerpoint.getDoublePosition(i)) ;
  			
  		}
  		
  		return Math.sqrt(distance);
  		
  	}
  	

}
