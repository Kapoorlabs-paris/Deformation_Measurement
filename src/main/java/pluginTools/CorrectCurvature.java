package pluginTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import curvatureUtils.Node;
import net.imglib2.RealLocalizable;
import utility.CurvatureFunction;

public class CorrectCurvature {

	
	
	public static void CorrectCurvaturebySize(ArrayList<Node<RealLocalizable>> WrongNodes,InteractiveSimpleEllipseFit parent, double maxError, int minNumInliers,
			int ndims, int Label, int degree, int secdegree, int t, int z) {
		
		
		for(Node<RealLocalizable> currentleftnode : WrongNodes) {
		//	System.out.println("Correcting for mistake by reducing window size" + currentleftnode.leftTree.size() + " " + currentleftnode.rightTree.size());
		if(currentleftnode.leftTree!=null && currentleftnode.leftTree.size() > 10) {	
		List<RealLocalizable> Leftsubtruths = currentleftnode.leftTree;
		CurvatureFunction computecurvature = new CurvatureFunction(parent);
		computecurvature.getCurvature(Leftsubtruths, maxError, minNumInliers,
				ndims, Label, degree, secdegree, t, z);
		}
		else continue;
		
		
		if(currentleftnode.rightTree!=null  && currentleftnode.rightTree.size() > 10) {	
		List<RealLocalizable> Rightsubtruths = currentleftnode.rightTree;
		
		CurvatureFunction computecurvature = new CurvatureFunction(parent);
		computecurvature.getCurvature(Rightsubtruths, maxError, minNumInliers,
				ndims, Label, degree, secdegree, t, z);
		}
		else continue;
		
		}
		
	}
	
	
	public static void CorrectCurvaturebyDegree(ArrayList<Node<RealLocalizable>> WrongNodes,InteractiveSimpleEllipseFit parent, double maxError, int minNumInliers,
			int ndims, int Label, int degree, int secdegree, int t, int z) {
		
		degree--;
		secdegree--;
		for(Node<RealLocalizable> currentleftnode : WrongNodes) {
			
		if(currentleftnode.parent!=null && currentleftnode.parent.size() > degree + 2) {	
		List<RealLocalizable> Leftsubtruths = currentleftnode.parent;
		CurvatureFunction computecurvature = new CurvatureFunction(parent);
		computecurvature.getCurvature(Leftsubtruths, maxError, minNumInliers,
				ndims, Label, degree, secdegree, t, z);
		}
		
	
		
		}
		
	}
	
	
	
}
