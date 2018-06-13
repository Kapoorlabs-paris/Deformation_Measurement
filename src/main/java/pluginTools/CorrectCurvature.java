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
		if(currentleftnode.leftTree!=null && currentleftnode.leftTree.size() > degree + 1) {	
		List<RealLocalizable> Leftsubtruths = currentleftnode.leftTree;
		
		CurvatureFunction.getCurvature(parent,Leftsubtruths, maxError, minNumInliers,
				ndims, Label, degree, secdegree, t, z);
		}
		
		
		if(currentleftnode.rightTree!=null  && currentleftnode.rightTree.size() > degree + 1) {	
		List<RealLocalizable> Rightsubtruths = currentleftnode.rightTree;
		
		
		CurvatureFunction.getCurvature(parent,Rightsubtruths, maxError, minNumInliers,
				ndims, Label, degree, secdegree, t, z);
		}
		
		}
		
	}
	
	
	public static void CorrectCurvaturebyDegree(ArrayList<Node<RealLocalizable>> WrongNodes,InteractiveSimpleEllipseFit parent, double maxError, int minNumInliers,
			int ndims, int Label, int degree, int secdegree, int t, int z) {
		
		degree++;
		secdegree++;
	//	System.out.println("Correcting for mistake with higher degree fit" + degree + " " + secdegree);
		for(Node<RealLocalizable> currentleftnode : WrongNodes) {
			
		if(currentleftnode.parent!=null && currentleftnode.parent.size() > degree + 2) {	
		List<RealLocalizable> Leftsubtruths = currentleftnode.parent;
		
		CurvatureFunction.getCurvature(parent,Leftsubtruths, maxError, minNumInliers,
				ndims, Label, degree, secdegree, t, z);
		}
		
	
		
		}
		
	}
	
	
	
}
