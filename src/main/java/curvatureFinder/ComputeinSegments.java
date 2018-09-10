package curvatureFinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import curvatureUtils.PointExtractor;
import ellipsoidDetector.Intersectionobject;
import net.imglib2.RealLocalizable;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.InteractiveSimpleEllipseFit;
import pluginTools.RegressionCurveSegment;
import ransacPoly.RegressionFunction;
import utility.CurvatureFunction;
import utility.Curvatureobject;
import utility.Listordereing;

public class ComputeinSegments {

	
	
	public static void OverSliderLoop(InteractiveSimpleEllipseFit parent, List<RealLocalizable> Ordered, RealLocalizable centerpoint, List<RealLocalizable> truths,ArrayList<Intersectionobject> AllCurveintersection,
	ArrayList<Intersectionobject> AlldenseCurveintersection, int ndims, int celllabel, int t, int z) {

		// Get the sparse list of points
		HashMap<Integer, RegressionCurveSegment> Bestdelta = new HashMap<Integer, RegressionCurveSegment>();

		int count = 0;
		if (parent.minNumInliers > truths.size())
			parent.minNumInliers = truths.size();
		
		int i = parent.increment;
		RegressionCurveSegment resultpair = CommonLoop(parent, Ordered, centerpoint, ndims, celllabel, t , z);
		int maxstride = parent.CellLabelsizemap.get(celllabel);

		// Get the sparse list of points, skips parent.resolution pixel points


		for (int index = 0; index < maxstride; ++index) {
			List<RealLocalizable> allorderedtruths = Listordereing.getList(Ordered, i + index);

			if (parent.fourthDimensionSize > 1)
				parent.timeslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.fourthDimension,
						parent.fourthDimensionsliderInit, parent.fourthDimensionSize, parent.scrollbarSize));
			parent.zslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.thirdDimension,
					parent.thirdDimensionsliderInit, parent.thirdDimensionSize, parent.scrollbarSize));

			// Make a tree of a certain depth

			int treedepth = parent.depth - 1;

			if (treedepth <= 0)
				treedepth = 0;

			CurvatureFunction computecurve = new CurvatureFunction(parent);

			resultpair = computecurve.getCurvature(allorderedtruths, centerpoint, parent.insideCutoff,
					parent.minNumInliers, ndims, celllabel, Math.abs(Math.max(parent.degree, parent.secdegree)),
					Math.abs(Math.min(parent.degree, parent.secdegree)), z, t);

			// Here counter the segments where the number of inliers was too low

			Bestdelta.put(count, resultpair);
			count++;

			parent.localCurvature = resultpair.Curvelist;

			parent.functions = resultpair.functionlist;
			parent.localSegment = resultpair.Seglist;
			// Make intersection object here

		}

		resultpair = Bestdelta.get(0);
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

		AlldenseCurveintersection.add(densecurrentobject);
		AllCurveintersection.add(sparsecurrentobject);

		parent.AlllocalCurvature.add(parent.localCurvature);

	}
	
	private static RegressionCurveSegment CommonLoop(InteractiveSimpleEllipseFit parent,	 List<RealLocalizable> Ordered, 
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

		CurvatureFunction computecurve = new CurvatureFunction(parent);

		RegressionCurveSegment resultpair = computecurve.getCurvature(allorderedtruths, centerpoint,
				parent.insideCutoff, parent.minNumInliers, ndims, celllabel,
				Math.abs(Math.max(parent.degree, parent.secdegree)),
				Math.abs(Math.min(parent.degree, parent.secdegree)), z, t);

		// Here counter the segments where the number of inliers was too low

		Bestdelta.put(count, resultpair);
		count++;

		parent.localCurvature = resultpair.Curvelist;

		parent.functions = resultpair.functionlist;
		parent.localSegment = resultpair.Seglist;

		return resultpair;

	}
	
}
