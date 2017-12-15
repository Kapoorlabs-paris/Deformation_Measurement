package pluginTools;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import distanceTransform.WatershedBinary;
import javax.swing.JProgressBar;

import ellipsoidDetector.Distance;
import ellipsoidDetector.Intersectionobject;
import ellipsoidDetector.Tangentobject;
import ij.ImagePlus;
import ij.gui.EllipseRoi;
import ij.gui.Line;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.gui.Roi;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.algorithm.ransac.RansacModels.BisectorEllipsoid;
import net.imglib2.algorithm.ransac.RansacModels.ConnectedComponentCoordinates;
import net.imglib2.algorithm.ransac.RansacModels.DisplayasROI;
import net.imglib2.algorithm.ransac.RansacModels.Ellipsoid;
import net.imglib2.algorithm.ransac.RansacModels.Intersections;
import net.imglib2.algorithm.ransac.RansacModels.NumericalSolvers;
import net.imglib2.algorithm.ransac.RansacModels.SortSegments;
import net.imglib2.algorithm.ransac.RansacModels.Tangent2D;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.InteractiveEllipseFit.ValueChange;
import utility.Roiobject;

public class EllipseTrack {

	final InteractiveEllipseFit parent;
	final JProgressBar jpb;
	
	public EllipseTrack(final InteractiveEllipseFit parent, final JProgressBar jpb) {
		
		
		this.parent = parent;
		
		this.jpb = jpb;
	}
	
	
	public void IntersectandTrack() {
		
		// Main method for computing intersections and tangents and angles between tangents
		double percent = 0;
		for (int t = 1; t <= parent.fourthDimensionSize; ++t) {

			percent++;

			for (int z = 1; z <= parent.thirdDimensionSize; ++z) {

				percent++;

			
		        
				RandomAccessibleInterval<BitType> CurrentView = utility.Slicer.getCurrentViewBit(parent.empty, z , parent.thirdDimensionSize, t, parent.fourthDimensionSize);
				
				RandomAccessibleInterval<IntType> CurrentViewInt = utility.Slicer.getCurrentViewInt(parent.emptyWater, z , parent.thirdDimensionSize, t, parent.fourthDimensionSize);
				
        		   Computeinwater compute = new Computeinwater(parent, CurrentView, CurrentViewInt, t, z);
        		   compute.ParallelRansac();
        			utility.ProgressBar.SetProgressBar(jpb, 100 * percent/ (parent.thirdDimensionSize + parent.fourthDimensionSize), "Fitting ellipses and computing angles T = " + t + "/"
    						+ parent.fourthDimensionSize + " Z = " + z + "/" + parent.fourthDimensionSize);
    		        

					
			}
			
		}
		
		parent.updatePreview(ValueChange.FOURTHDIMmouse);
		parent.updatePreview(ValueChange.THIRDDIMmouse);
		
		
		
	}
	
	
}
