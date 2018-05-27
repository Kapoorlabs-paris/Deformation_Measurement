package utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import curvatureUtils.Node;
import drawUtils.DrawFunction;
import ellipsoidDetector.Distance;
import ij.ImagePlus;
import mpicbg.models.Point;
import net.imglib2.RealLocalizable;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.InteractiveSimpleEllipseFit;
import ransac.PointFunctionMatch.PointFunctionMatch;
import ransac.loadFiles.Tracking;
import ransacPoly.AbstractFunction2D;
import ransacPoly.QuadraticFunction;
import ransacPoly.Sort;
import regression.RegressionFunction;
import regression.Threepointfit;

public class CurvatureFunction {

	
	
	

	
	
	
	/**
	 * 
	 * Take in a list of ordered co-ordinates and compute a curvature object
	 * containing the curvature information at each co-ordinate
	 * Makes a tree structure of the list
	 * 
	 * @param orderedtruths
	 * @param ndims
	 * @param Label
	 * @param t
	 * @param z
	 * @return
	 */
	public static ValuePair<ArrayList<RegressionFunction> , ArrayList<Curvatureobject>> getCurvature(InteractiveSimpleEllipseFit parent, List<RealLocalizable> truths, 
			double maxError, int minNumInliers, double maxDist,
			int ndims, int Label, int t, int z) {

		ArrayList<Curvatureobject> curveobject = new ArrayList<Curvatureobject>();
		ArrayList<double[]> interpolatedCurvature = new ArrayList<double[]>();
		ArrayList<RegressionFunction> functions = new ArrayList<RegressionFunction>();
		
		int depth = 0;
		// Extract the depth of the tree from the user input
		
		int maxdepth = (int) (Math.log(parent.numseg)/Math.log(2));
		
		if(parent.numseg == 1)
			maxdepth = 1;
		
		// Make a tree of a certain depth
		MakeTree(truths,parent.Allnodes, depth, maxdepth);

		
		for (int i = 1; i <= maxdepth; ++i) {
			ArrayList<Node<RealLocalizable>> Inode = new ArrayList<Node<RealLocalizable>>();
		
			
			
			for(Node<RealLocalizable> node: parent.Allnodes) {
				
				if (node.depth == i) {
					
					Inode.add(node);
					
				}
				
				
			}
			
			parent.Nodemap.put(i, Inode);
		}
		
		
		
		ArrayList<Node<RealLocalizable>> leafnode = parent.Nodemap.get(maxdepth);
	
		double perimeter = 0;
		for (Node<RealLocalizable> leaf: leafnode) {
			
			List<RealLocalizable> Leftsubtruths = leaf.leftTree;
			List<RealLocalizable> Rightsubtruths = leaf.rightTree;
			
			
			
		
			// Fit function on left tree
			
			ArrayList<double[]> LeftCordlist = new ArrayList<double[]>();
			for (int i = 0; i < Leftsubtruths.size(); ++i) {

				LeftCordlist.add(new double[] { Leftsubtruths.get(i).getDoublePosition(0),
						Leftsubtruths.get(i).getDoublePosition(1) });

	
				
			}
			RegressionFunction Leftresultcurvature = getLocalcurvature(LeftCordlist, maxError, minNumInliers, maxDist);
			
			// Draw the function
			
			functions.add(Leftresultcurvature);
			
			interpolatedCurvature.addAll(Leftresultcurvature.Curvaturepoints);

			perimeter+=Leftresultcurvature.perimeter;
			
			// Fit function on right tree
			ArrayList<double[]> RightCordlist = new ArrayList<double[]>();
			for (int i = 0; i < Rightsubtruths.size(); ++i) {

				RightCordlist.add(new double[] { Rightsubtruths.get(i).getDoublePosition(0),
						Rightsubtruths.get(i).getDoublePosition(1) });
			

			}
			RegressionFunction Rightresultcurvature = getLocalcurvature(RightCordlist, maxError, minNumInliers, maxDist);
			
			
			
			
			
			// Draw the function
			functions.add(Rightresultcurvature);
			
			
			interpolatedCurvature.addAll(Rightresultcurvature.Curvaturepoints);

			perimeter+=Rightresultcurvature.perimeter;
		
		}
		
	


		for (int indexx = 0; indexx < interpolatedCurvature.size(); ++indexx) {
			Curvatureobject currentobject = new Curvatureobject(interpolatedCurvature.get(indexx)[2], perimeter, Label,
					new double[] { interpolatedCurvature.get(indexx)[0], interpolatedCurvature.get(indexx)[1] }, t, z);

			curveobject.add(currentobject);
		}

		return new ValuePair<ArrayList<RegressionFunction>, ArrayList<Curvatureobject>> (functions, curveobject);

	}

	
	
	

	
	public static void  MakeTree(final List<RealLocalizable> truths,ArrayList<Node<RealLocalizable>> Allnodes, int depth, int maxdepth) {
		
		
		if(depth > maxdepth)
			return;
		
		else {
		int size = truths.size();
		
		
		
		int splitindex;
		if (size % 2 == 0)
		 splitindex = size / 2;
		else
		splitindex = (size - 1) / 2;
		
		
		final ArrayList<RealLocalizable> childA = new ArrayList<RealLocalizable>((int) size/ 2);

		final ArrayList<RealLocalizable> childB = new ArrayList<RealLocalizable>((int) (size / 2 + size % 2));
		
		
		Iterator<RealLocalizable> iterator = truths.iterator();
		int index = 0;
		while(iterator.hasNext()) {
			
			iterator.next();
			
			
			if (index < splitindex)
				
				childA.add(truths.get(index));

			else

				childB.add(truths.get(index));

			index++;
			
		}
		
		Node<RealLocalizable> currentnode = new Node<RealLocalizable>(truths.get(splitindex), childA, childB, depth++);
		
		
		
		MakeTree(childA, Allnodes, depth, maxdepth);
		MakeTree(childB, Allnodes, depth, maxdepth);
		Allnodes.add(currentnode);
		}
		
	}
	
	
	
	
	
	/**
	 * 
	 * Implementation of the curvature function to compute curvature at a point
	 * 
	 * @param previousCord
	 * @param currentCord
	 * @param nextCord
	 * @return
	 */

	
	public static RegressionFunction getLocalcurvature(ArrayList<double[]> Cordlist, double maxError, int minNumInliers, double maxDist) {

		double[] x = new double[Cordlist.size()];
		double[] y = new double[Cordlist.size()];

		ArrayList<double[]> points = new ArrayList<double[]>();
		ArrayList<Point> pointlist = new ArrayList<Point>();
		ArrayList<double[]> Curvaturepoints = new ArrayList<double[]>();
		AbstractFunction2D function = new QuadraticFunction();
		
		
		for (int index = 0; index < Cordlist.size() - 1; ++index) {
			x[index] = Cordlist.get(index)[0];
			y[index] = Cordlist.get(index)[1];
			points.add(new double[] { x[index], y[index] });
			
			pointlist.add(new Point( new double[]{ x[index],y[index]} ) );
		}

	
		// Regression or Ransac
		//Regression block
		Threepointfit regression = new Threepointfit(x, y, 2);

		double highestCoeff = regression.GetCoefficients(2);
		double sechighestCoeff = regression.GetCoefficients(1);
		
		// Ransac block
		
		final Pair<QuadraticFunction, ArrayList<PointFunctionMatch>> segment = Tracking
				.findFunction(pointlist, function, maxError, minNumInliers, maxDist);
		
		highestCoeff  = segment.getA().getCoefficient(2);
		sechighestCoeff = segment.getA().getCoefficient(1);

		for (int index = 0; index < points.size() - 1; ++index) {


			double secderiv = 2 * highestCoeff;
			double firstderiv = 2 * highestCoeff * points.get(index)[0] + sechighestCoeff;
			double Kappa = secderiv / Math.pow((1 + firstderiv * firstderiv), 3.0 / 2.0);
            
			Curvaturepoints.add(new double[] { points.get(index)[0], points.get(index)[1], Math.abs(Kappa) });
			System.out.println("Kappa" + Math.abs(Kappa));
		}
	
		double perimeter = 0;
		for (int index = 0; index < points.size() - 1; ++index) {
			
			double firstderiv =  2 * highestCoeff *  points.get(index)[0]  + sechighestCoeff;
			perimeter+= Math.sqrt(1 + firstderiv * firstderiv);
			
		}
            System.out.println(perimeter + " " + "Peri");
            
            RegressionFunction finalfunction = new RegressionFunction(segment.getA(), Curvaturepoints, perimeter);
            
		return finalfunction;
	}

	/**
	 * 
	 * Interpolate from (x, y) to (x, y) + 1 by filling up the values in between
	 * 
	 */

	public static Pair<double[], double[]> InterpolateValues(final double[] Xcurr, final double[] Xnext,
			Threepointfit regression) {

		double minX = Xcurr[0] < Xnext[0] ? Xcurr[0] : Xnext[0];
		double maxX = Xcurr[0] > Xnext[0] ? Xcurr[0] : Xnext[0];

		double interpolant = 0.1;
		double X = minX;
		double Y = regression.predict(X);

		int steps = (int) ((maxX - minX) / interpolant);
		if (steps > 0) {
			double[] returnValX = new double[steps];
			double[] returnValY = new double[steps];

			returnValX[0] = X;
			returnValY[0] = Y;

			for (int i = 1; i < steps; ++i) {

				returnValX[i] = X + i * interpolant;
				returnValY[i] = regression.predict(returnValX[i]);

			}

			Pair<double[], double[]> interpolXY = new ValuePair<double[], double[]>(returnValX, returnValY);

			return interpolXY;
		}

		else {
			Pair<double[], double[]> interpolXY = new ValuePair<double[], double[]>(new double[] { X, Y },
					new double[] { X, Y });

			return interpolXY;

		}
	}

	/**
	 * 
	 * Evenly or unevenly spaced data derivative is computed via Lagrangian
	 * interpolation
	 * 
	 * @param previousCord
	 * @param currentCord
	 * @param nextCord
	 */
	public static double[] InterpolatedFirstderiv(double[] previousCord, double[] currentCord, double[] nextCord) {

		double y0 = previousCord[1];
		double y1 = currentCord[1];
		double y2 = nextCord[1];

		double x0 = previousCord[0];
		double x1 = currentCord[0];
		double x2 = nextCord[0];

		double x01 = x0 - x1;
		double x02 = x0 - x2;
		double x12 = x1 - x2;
		if (x01 != 0 && x02 != 0 && x12 != 0) {
			double diffatx0 = y0 * (x01 + x02) / (x01 * x02) - y1 * x02 / (x01 * x12) + y2 * x01 / (x02 * x12);
			double diffatx2 = -y0 * x12 / (x01 * x02) + y1 * x02 / (x01 * x12) - y2 * (x02 + x12) / (x02 * x12);
			double diffatx1 = y0 * (x12) / (x01 * x02) + y1 * (1.0 / x12 - 1.0 / x01) - y2 * x01 / (x02 * x12);

			double[] threepointdiff = { diffatx0, diffatx1, diffatx2 };

			return threepointdiff;
		} else

			return new double[] { 0, 0, 0 };

	}

	/**
	 * 
	 * Compute perimeter of a curve by adding up the distance between ordered set of
	 * points
	 * 
	 * @param orderedtruths
	 * @param ndims
	 * @return
	 */
	public static double getPerimeter(List<RealLocalizable> orderedtruths, int ndims) {

		double perimeter = 0;
		for (int index = 1; index < orderedtruths.size(); ++index) {

			double[] lastpoint = new double[ndims];
			double[] currentpoint = new double[ndims];

			orderedtruths.get(index - 1).localize(lastpoint);
			orderedtruths.get(index).localize(currentpoint);
			perimeter += Distance.DistanceSq(lastpoint, currentpoint);

		}

		return perimeter;
	}

}
