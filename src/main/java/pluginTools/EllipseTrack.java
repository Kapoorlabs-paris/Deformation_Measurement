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
	Pair<Boolean, String> isVisited;

	public EllipseTrack(final InteractiveEllipseFit parent, final JProgressBar jpb) {

		this.parent = parent;

		this.jpb = jpb;
	}

	public void IntersectandTrack() {

		// Main method for computing intersections and tangents and angles between
		// tangents
		double percent = 0;

		for (Map.Entry<String, Integer> entry : parent.Accountedframes.entrySet()) {

			int t = entry.getValue();

			for (Map.Entry<String, Integer> entryZ : parent.AccountedZ.entrySet()) {

				int z = entryZ.getValue();
				percent++;
				if (parent.fourthDimensionSize != 0)
					utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.Accountedframes.entrySet().size()),
							"Fitting ellipses and computing angles T = " + t + "/" + parent.fourthDimensionSize
									+ " Z = " + z + "/" + parent.thirdDimensionSize);
				else
					utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.AccountedZ.entrySet().size()),
							"Fitting ellipses and computing angles T/Z = " + z + "/" + parent.thirdDimensionSize);

				RandomAccessibleInterval<BitType> CurrentView = utility.Slicer.getCurrentViewBit(parent.empty, z,
						parent.thirdDimensionSize, t, parent.fourthDimensionSize);

				RandomAccessibleInterval<IntType> CurrentViewInt = utility.Slicer.getCurrentViewInt(parent.emptyWater,
						z, parent.thirdDimensionSize, t, parent.fourthDimensionSize);

				Computeinwater compute = new Computeinwater(parent, CurrentView, CurrentViewInt, t, z);
				compute.ParallelRansac();

			}

		}

		parent.updatePreview(ValueChange.FOURTHDIMmouse);
		parent.updatePreview(ValueChange.THIRDDIMmouse);

	}

	public void IntersectandTrackCurrent() {

		// Main method for computing intersections and tangents and angles between
		// tangents
		double percent = 0;

		int z = parent.thirdDimension;
		int t = parent.fourthDimension;
		if (parent.fourthDimensionSize != 0)
			utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.Accountedframes.entrySet().size()),
					"Fitting ellipses and computing angles T = " + t + "/" + parent.fourthDimensionSize + " Z = " + z
							+ "/" + parent.thirdDimensionSize);
		else
			utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.AccountedZ.entrySet().size()),
					"Fitting ellipses and computing angles T/Z = " + z + "/" + parent.thirdDimensionSize);

		if (parent.rect != null) {
			RandomAccessibleInterval<BitType> CurrentView = utility.Slicer.getCurrentViewBit(parent.empty, z,
					parent.thirdDimensionSize, t, parent.fourthDimensionSize);

			RandomAccessibleInterval<IntType> CurrentViewInt = utility.Slicer.getCurrentViewInt(parent.emptyWater, z,
					parent.thirdDimensionSize, t, parent.fourthDimensionSize);
			ComputeinwaterMistake compute = new ComputeinwaterMistake(parent, CurrentView, CurrentViewInt, t, z);
			compute.ParallelRansac();
		} else {

			RandomAccessibleInterval<BitType> CurrentView = utility.Slicer.getCurrentViewBit(parent.empty, z,
					parent.thirdDimensionSize, t, parent.fourthDimensionSize);

			RandomAccessibleInterval<IntType> CurrentViewInt = utility.Slicer.getCurrentViewInt(parent.emptyWater, z,
					parent.thirdDimensionSize, t, parent.fourthDimensionSize);
			ComputeinwaterMistake compute = new ComputeinwaterMistake(parent, CurrentView, CurrentViewInt, t, z);
			compute.ParallelRansac();
		}

		parent.updatePreview(ValueChange.FOURTHDIMmouse);
		parent.updatePreview(ValueChange.THIRDDIMmouse);

	}

}
