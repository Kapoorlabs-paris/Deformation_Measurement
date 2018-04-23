package pluginTools;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import distanceTransform.CreateDistanceTransform;
import distanceTransform.DistWatershed;
import distanceTransform.DistWatershedBinary;
import distanceTransform.WatershedBinary;
import javax.swing.JProgressBar;

import ellipsoidDetector.Distance;
import ellipsoidDetector.Intersectionobject;
import ellipsoidDetector.Tangentobject;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.EllipseRoi;
import ij.gui.Line;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.gui.Roi;
import net.imglib2.Cursor;
import net.imglib2.KDTree;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.RealPointSampleList;
import net.imglib2.algorithm.morphology.table2d.Thin;
import net.imglib2.algorithm.ransac.RansacModels.BisectorEllipsoid;
import net.imglib2.algorithm.ransac.RansacModels.ConnectedComponentCoordinates;
import net.imglib2.algorithm.ransac.RansacModels.DisplayasROI;
import net.imglib2.algorithm.ransac.RansacModels.Ellipsoid;
import net.imglib2.algorithm.ransac.RansacModels.Intersections;
import net.imglib2.algorithm.ransac.RansacModels.NumericalSolvers;
import net.imglib2.algorithm.ransac.RansacModels.SortSegments;
import net.imglib2.algorithm.ransac.RansacModels.Tangent2D;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.neighborsearch.NearestNeighborSearchOnKDTree;
import net.imglib2.type.Type;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.Util;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;
import pluginTools.InteractiveSimpleEllipseFit.ValueChange;
import preProcessing.Kernels;
import regionRemoval.RemoveTiny;
import strategies.ThinningStrategy;
import strategies.ThinningStrategyFactory;
import strategies.ThinningStrategyFactory.Strategy;
import thinning.ThinningOp;
import utility.Roiobject;

public class EllipseTrack {

	final InteractiveSimpleEllipseFit parent;
	final JProgressBar jpb;
	Pair<Boolean, String> isVisited;

	public EllipseTrack(final InteractiveSimpleEllipseFit parent, final JProgressBar jpb) {

		this.parent = parent;

		this.jpb = jpb;
	}

	public void BlockRepeatRect(double percent, int z, int t) {

		parent.updatePreview(ValueChange.THIRDDIMmouse);

		percent++;

		RandomAccessibleInterval<BitType> CurrentView = utility.Slicer.getCurrentViewBit(parent.empty, z,
				parent.thirdDimensionSize, t, parent.fourthDimensionSize);
		RandomAccessibleInterval<IntType> CurrentViewInt = utility.Slicer.getCurrentViewInt(parent.originalimgsuper, z,
				parent.thirdDimensionSize, t, parent.fourthDimensionSize);

		RandomAccessibleInterval<BitType> CurrentViewthin = getThin(CurrentView);

		GetPixelList(CurrentViewInt);

		Computeinwater compute = new Computeinwater(parent, CurrentViewthin, CurrentViewInt, t, z, (int) percent);
		compute.ParallelRansac();

	}

	public void BlockRepeatAutoRect(double percent, int z, int t) {

		parent.updatePreview(ValueChange.THIRDDIMmouse);

		percent++;

		RandomAccessibleInterval<BitType> CurrentView = utility.Slicer.getCurrentViewBit(parent.empty, z,
				parent.thirdDimensionSize, t, parent.fourthDimensionSize);
		RandomAccessibleInterval<BitType> CurrentViewthin = getThin(CurrentView);
		Pair<RandomAccessibleInterval<IntType>, RandomAccessibleInterval<BitType>> Current = getAutoint(CurrentView);

		parent.maxlabel = GetMaxlabelsseeded(Current.getA());
		Computeinwater compute = new Computeinwater(parent, CurrentViewthin, Current.getA(), t, z, (int) percent,
				parent.maxlabel);
		compute.ParallelRansac();

	}

	public void BlockRepeatManualRect(double percent, int z, int t) {

		parent.updatePreview(ValueChange.THIRDDIMmouse);
		percent++;

		RandomAccessibleInterval<BitType> CurrentView = utility.Slicer.getCurrentViewBit(parent.empty, z,
				parent.thirdDimensionSize, t, parent.fourthDimensionSize);

		RandomAccessibleInterval<IntType> CurrentViewInt = utility.Slicer.getCurrentViewInt(parent.emptyWater, z,
				parent.thirdDimensionSize, t, parent.fourthDimensionSize);

		parent.maxlabel = GetMaxlabelsseeded(CurrentViewInt);

		Computeinwater compute = new Computeinwater(parent, CurrentView, CurrentViewInt, t, z, (int) percent,
				parent.maxlabel);
		compute.ParallelRansac();

	}

	public void BlockRepeat(double percent, int z, int t) {

		parent.updatePreview(ValueChange.THIRDDIMmouse);

		percent++;
		if(jpb!=null)
		utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.fourthDimensionSize),
				"Fitting ellipses and computing angles T = " + t + "/" + parent.fourthDimensionSize + " Z = " + z + "/"
						+ parent.thirdDimensionSize);

		RandomAccessibleInterval<BitType> CurrentView = utility.Slicer.getCurrentViewBit(parent.empty, z,
				parent.thirdDimensionSize, t, parent.fourthDimensionSize);
		RandomAccessibleInterval<IntType> CurrentViewInt = utility.Slicer.getCurrentViewInt(parent.originalimgsuper, z,
				parent.thirdDimensionSize, t, parent.fourthDimensionSize);

		RandomAccessibleInterval<BitType> CurrentViewthin = getThin(CurrentView);
		GetPixelList(CurrentViewInt);
		Computeinwater compute = new Computeinwater(parent, CurrentViewthin, CurrentViewInt, t, z, (int) percent);
		compute.ParallelRansac();
	}
	public void TestAuto(int z, int t) {
		
		parent.updatePreview(ValueChange.THIRDDIMmouse);


		RandomAccessibleInterval<BitType> CurrentViewSmooth = utility.Slicer.getCurrentViewBit(parent.emptysmooth, z,
				parent.thirdDimensionSize, t, parent.fourthDimensionSize);

		// Use smoothed image for segmentation and non smooth image for getting the
		// candidate points for fitting ellipses
		RandomAccessibleInterval<IntType> CurrentInt = getSeg(CurrentViewSmooth);
		if (parent.showWater) {

			Watershow(CurrentInt);

		}
		
	}
	public void BlockRepeatAuto(double percent, int z, int t) {

		parent.updatePreview(ValueChange.THIRDDIMmouse);

		percent++;
		if(jpb!=null)
		utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.fourthDimensionSize),
				"Fitting ellipses and computing angles T = " + t + "/" + parent.fourthDimensionSize + " Z = " + z + "/"
						+ parent.thirdDimensionSize);

		RandomAccessibleInterval<BitType> CurrentView = utility.Slicer.getCurrentViewBit(parent.empty, z,
				parent.thirdDimensionSize, t, parent.fourthDimensionSize);
		RandomAccessibleInterval<BitType> CurrentViewSmooth = utility.Slicer.getCurrentViewBit(parent.emptysmooth, z,
				parent.thirdDimensionSize, t, parent.fourthDimensionSize);

		// Use smoothed image for segmentation and non smooth image for getting the
		// candidate points for fitting ellipses
		RandomAccessibleInterval<IntType> CurrentInt = getSeg(CurrentViewSmooth);
		RandomAccessibleInterval<BitType> CurrentViewthin = getThin(CurrentView);
		if (parent.showWater) {

			Watershow(CurrentInt);

		}

		GetPixelList(CurrentInt);
		Computeinwater compute = new Computeinwater(parent, CurrentViewthin, CurrentInt, t, z, (int) percent);
		compute.ParallelRansac();

	}

	public void Watershow(RandomAccessibleInterval<IntType> CurrentInt) {

		if (parent.localwaterimp == null || !parent.localwaterimp.isVisible()) {
			parent.localwaterimp = ImageJFunctions.show(CurrentInt);

		}

		else {

			final short[] pixels = (short[]) parent.localwaterimp.getProcessor().getPixels();
			final Cursor<IntType> c = Views.iterable(CurrentInt).cursor();

			for (int i = 0; i < pixels.length; ++i)
				pixels[i] = (short) c.next().get();

			parent.localwaterimp.updateAndDraw();
			parent.localwaterimp.setTitle("Watershed Image" + " " + "time point : " + parent.fourthDimension + " "
					+ " Z: " + parent.thirdDimension);

		}

	}

	public void BlockRepeatManual(double percent, int z, int t) {

		parent.updatePreview(ValueChange.THIRDDIMmouse);
		percent++;
		if (parent.fourthDimensionSize != 0)
			utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.Accountedframes.entrySet().size()),
					"Fitting ellipses and computing angles T = " + t + "/" + parent.fourthDimensionSize + " Z = " + z
							+ "/" + parent.thirdDimensionSize);
		else
			utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.AccountedZ.entrySet().size()),
					"Fitting ellipses and computing angles T/Z = " + z + "/" + parent.thirdDimensionSize);

		RandomAccessibleInterval<BitType> CurrentView = utility.Slicer.getCurrentViewBit(parent.empty, z,
				parent.thirdDimensionSize, t, parent.fourthDimensionSize);

		RandomAccessibleInterval<IntType> CurrentViewInt = utility.Slicer.getCurrentViewInt(parent.emptyWater, z,
				parent.thirdDimensionSize, t, parent.fourthDimensionSize);

		parent.maxlabel = GetMaxlabelsseeded(CurrentViewInt);

		Computeinwater compute = new Computeinwater(parent, CurrentView, CurrentViewInt, t, z, (int) percent,
				parent.maxlabel);
		compute.ParallelRansac();

	}

	public void IntersectandTrack() {

		// Main method for computing intersections and tangents and angles between
		// tangents
		double percent = 0;

		if (parent.supermode) {

			if (parent.originalimg.numDimensions() > 3) {

				for (int t = parent.AutostartTime; t <= parent.AutoendTime; ++t) {
					parent.TID = Integer.toString(t);
					parent.Accountedframes.put(parent.TID, t);
					for (int z = 1; z <= parent.thirdDimensionSize; ++z) {

						parent.thirdDimension = z;
						parent.fourthDimension = t;

						BlockRepeat(percent, z, t);

					}
				}

			}

			else if (parent.originalimg.numDimensions() > 2) {

				for (int z = parent.AutostartTime; z <= parent.AutoendTime; ++z) {

					parent.thirdDimension = z;
					parent.ZID = Integer.toString(z);
					parent.AccountedZ.put(parent.ZID, z);
					BlockRepeat(percent, z, 1);

				}

			} else {
				int z = parent.thirdDimension;
				int t = parent.fourthDimension;

				BlockRepeat(percent, z, t);

			}

		}

		if (parent.automode) {

			if (parent.originalimg.numDimensions() > 3) {
				for (int t = parent.AutostartTime; t <= parent.AutoendTime; ++t) {
					parent.TID = Integer.toString(t);
					parent.Accountedframes.put(parent.TID, t);
					for (int z = 1; z <= parent.thirdDimensionSize; ++z) {

						parent.thirdDimension = z;
						parent.fourthDimension = t;

						BlockRepeatAuto(percent, z, t);

					}
				}

			}

			else if (parent.originalimg.numDimensions() > 2) {

				for (int z = parent.AutostartTime; z <= parent.AutoendTime; ++z) {

					parent.thirdDimension = z;
					parent.ZID = Integer.toString(z);
					parent.AccountedZ.put(parent.ZID, z);
					BlockRepeatAuto(percent, z, 1);

				}

			} else {
				int z = parent.thirdDimension;
				int t = parent.fourthDimension;
				BlockRepeatAuto(percent, z, t);

			}
		}

		else if (!parent.automode && !parent.supermode) {

			if (parent.originalimg.numDimensions() > 3) {

				for (Map.Entry<String, Integer> entry : parent.Accountedframes.entrySet()) {

					int t = entry.getValue();

					for (Map.Entry<String, Integer> entryZ : parent.AccountedZ.entrySet()) {

						int z = entryZ.getValue();

						BlockRepeatManual(percent, z, t);
					}
				}

			} else if (parent.originalimg.numDimensions() > 2 && parent.originalimg.numDimensions() <= 3) {

				int t = parent.fourthDimension;

				for (Map.Entry<String, Integer> entryZ : parent.AccountedZ.entrySet()) {

					int z = entryZ.getValue();
					BlockRepeatManual(percent, z, t);

				}

			}

			else {

				int z = parent.thirdDimension;
				int t = parent.fourthDimension;
				percent++;
				parent.updatePreview(ValueChange.THIRDDIMmouse);

				parent.maxlabel = GetMaxlabelsseeded(parent.emptyWater);
				Computeinwater compute = new Computeinwater(parent, parent.empty, parent.emptyWater, t, z,
						(int) percent, parent.maxlabel);
				compute.ParallelRansac();

			}

		}

		parent.updatePreview(ValueChange.THIRDDIMmouse);

	}

	public void DistanceTransformImage(RandomAccessibleInterval<BitType> inputimg,
			RandomAccessibleInterval<BitType> bitimg, RandomAccessibleInterval<BitType> outimg) {
		int n = inputimg.numDimensions();

		// make an empty list
		final RealPointSampleList<BitType> list = new RealPointSampleList<BitType>(n);

		// cursor on the binary image
		final Cursor<BitType> cursor = Views.iterable(bitimg).localizingCursor();

		// for every pixel that is 1, make a new RealPoint at that location
		while (cursor.hasNext())
			if (cursor.next().getInteger() == 1)
				list.add(new RealPoint(cursor), cursor.get());

		// build the KD-Tree from the list of points that == 1
		final KDTree<BitType> tree = new KDTree<BitType>(list);

		// Instantiate a nearest neighbor search on the tree (does not modifiy
		// the tree, just uses it)
		final NearestNeighborSearchOnKDTree<BitType> search = new NearestNeighborSearchOnKDTree<BitType>(tree);

		// randomaccess on the output
		final RandomAccess<BitType> ranac = outimg.randomAccess();

		// reset cursor for the input (or make a new one)
		cursor.reset();

		// for every pixel of the binary image
		while (cursor.hasNext()) {
			cursor.fwd();

			// set the randomaccess to the same location
			ranac.setPosition(cursor);

			// if value == 0, look for the nearest 1-valued pixel
			if (cursor.get().getInteger() == 0) {
				// search the nearest 1 to the location of the cursor (the
				// current 0)
				search.search(cursor);

				// get the distance (the previous call could return that, this
				// for generality that it is two calls)

				ranac.get().setReal(search.getDistance());

			} else {
				// if value == 1, no need to search
				ranac.get().setZero();
			}
		}

	}

	public RandomAccessibleInterval<BitType> getDist(RandomAccessibleInterval<BitType> CurrentView) {

		RandomAccessibleInterval<BitType> copyoriginal = new ArrayImgFactory<BitType>().create(CurrentView,
				new BitType());
		final ImgFactory<BitType> factory = Util.getArrayOrCellImgFactory(CurrentView, new BitType());
		RandomAccessibleInterval<BitType> distimg = factory.create(CurrentView, new BitType());
		DistanceTransformImage(CurrentView, CurrentView, distimg);
		final Cursor<BitType> distcursor = Views.iterable(distimg).localizingCursor();
		final RandomAccess<BitType> distranac = copyoriginal.randomAccess();
		while (distcursor.hasNext()) {

			distcursor.fwd();

			distranac.setPosition(distcursor);
			if (distcursor.get().getRealDouble() > parent.lowprob
					&& distcursor.get().getRealDouble() < parent.highprob) {

				distranac.get().setOne();
			} else {
				distranac.get().setZero();
			}

		}

		return distimg;

	}

	public RandomAccessibleInterval<BitType> getMedian(RandomAccessibleInterval<BitType> CurrentView, double sigma) {

		RandomAccessibleInterval<BitType> newthinCurrentView = new ArrayImgFactory<BitType>().create(CurrentView,
				new BitType());
		newthinCurrentView = Kernels.MedianFilter(CurrentView, sigma);
		return newthinCurrentView;

	}

	public RandomAccessibleInterval<BitType> getCannyEdge(RandomAccessibleInterval<BitType> CurrentView) {

		RandomAccessibleInterval<BitType> newthinCurrentView = new ArrayImgFactory<BitType>().create(CurrentView,
				new BitType());
		newthinCurrentView = Kernels.CannyEdgeandMeanBit(CurrentView, 0);

		return newthinCurrentView;
	}

	public RandomAccessibleInterval<BitType> getThin(RandomAccessibleInterval<BitType> CurrentView) {

		ThinningStrategyFactory fact = new ThinningStrategyFactory(true);
		ThinningStrategy strat = fact.getStrategy(Strategy.ZHANGSUEN);

		ThinningOp thinit = new ThinningOp(strat, true, new ArrayImgFactory<BitType>());

		RandomAccessibleInterval<BitType> newthinCurrentView = new ArrayImgFactory<BitType>().create(CurrentView,
				new BitType());

		thinit.compute(CurrentView, newthinCurrentView);

		return newthinCurrentView;
	}

	public RandomAccessibleInterval<IntType> getSeg(RandomAccessibleInterval<BitType> CurrentView) {
		ThinningStrategyFactory fact = new ThinningStrategyFactory(true);
		ThinningStrategy strat = fact.getStrategy(Strategy.HILDITCH);
		ThinningOp thinit = new ThinningOp(strat, true, new ArrayImgFactory<BitType>());

		RandomAccessibleInterval<BitType> newCurrentView = new ArrayImgFactory<BitType>().create(CurrentView,
				new BitType());
		RandomAccessibleInterval<BitType> newthinCurrentView = new ArrayImgFactory<BitType>().create(CurrentView,
				new BitType());

		newCurrentView = Kernels.CannyEdgeandMeanBit(CurrentView, 1);
		thinit.compute(newCurrentView, newthinCurrentView);
		DistWatershedBinary segmentimage = new DistWatershedBinary(newthinCurrentView);

		segmentimage.process();

		RandomAccessibleInterval<IntType> CurrentViewInt = segmentimage.getResult();

		// ImageJFunctions.show(CurrentViewInt).setTitle("Segmented image");

		return CurrentViewInt;

	}

	public RandomAccessibleInterval<BitType> getCand(RandomAccessibleInterval<BitType> CurrentView) {

		ThinningStrategyFactory fact = new ThinningStrategyFactory(true);
		ThinningStrategy strat = fact.getStrategy(Strategy.HILDITCH);
		ThinningOp thinit = new ThinningOp(strat, true, new ArrayImgFactory<BitType>());

		RandomAccessibleInterval<BitType> newCurrentView = new ArrayImgFactory<BitType>().create(CurrentView,
				new BitType());
		RandomAccessibleInterval<BitType> newthinCurrentView = new ArrayImgFactory<BitType>().create(CurrentView,
				new BitType());

		newCurrentView = Kernels.CannyEdgeandMeanBit(CurrentView, 1);
		thinit.compute(newCurrentView, newthinCurrentView);

		return newthinCurrentView;

	}

	public Pair<RandomAccessibleInterval<IntType>, RandomAccessibleInterval<BitType>> getAutoint(
			RandomAccessibleInterval<BitType> CurrentView) {

		ThinningStrategyFactory fact = new ThinningStrategyFactory(true);
		ThinningStrategy strat = fact.getStrategy(Strategy.HILDITCH);
		ThinningOp thinit = new ThinningOp(strat, true, new ArrayImgFactory<BitType>());

		RandomAccessibleInterval<BitType> newCurrentView = new ArrayImgFactory<BitType>().create(CurrentView,
				new BitType());
		RandomAccessibleInterval<BitType> newthinCurrentView = new ArrayImgFactory<BitType>().create(CurrentView,
				new BitType());

		newCurrentView = Kernels.CannyEdgeandMeanBit(CurrentView, 1);
		thinit.compute(newCurrentView, newthinCurrentView);

		// ImageJFunctions.show(newthinCurrentView).setTitle("Thinned image");

		DistWatershedBinary segmentimage = new DistWatershedBinary(newthinCurrentView);

		segmentimage.process();

		RandomAccessibleInterval<IntType> CurrentViewInt = segmentimage.getResult();

		// ImageJFunctions.show(CurrentViewInt).setTitle("Segmented image");

		return new ValuePair<RandomAccessibleInterval<IntType>, RandomAccessibleInterval<BitType>>(CurrentViewInt,
				newthinCurrentView);

	}

	public RandomAccessibleInterval<BitType> CreateBinary(RandomAccessibleInterval<FloatType> source, double lowprob,
			double highprob) {

		RandomAccessibleInterval<BitType> copyoriginal = new ArrayImgFactory<BitType>().create(source, new BitType());

		final RandomAccess<BitType> ranac = copyoriginal.randomAccess();
		final Cursor<FloatType> cursor = Views.iterable(source).localizingCursor();

		while (cursor.hasNext()) {

			cursor.fwd();

			ranac.setPosition(cursor);
			if (cursor.get().getRealDouble() > lowprob && cursor.get().getRealDouble() < highprob) {

				ranac.get().setOne();
			} else {
				ranac.get().setZero();
			}

		}

		return copyoriginal;

	}

	public void GetPixelList(RandomAccessibleInterval<IntType> intimg) {

		IntType min = new IntType();
		IntType max = new IntType();
		computeMinMax(Views.iterable(intimg), min, max);
		Cursor<IntType> intCursor = Views.iterable(intimg).cursor();
		int currentLabel = min.get();
		parent.pixellist.clear();
		parent.pixellist.add(currentLabel);
		while (intCursor.hasNext()) {
			intCursor.fwd();
			int i = intCursor.get().get();
			if (i != currentLabel) {

				parent.pixellist.add(i);

				currentLabel = i;

			}

		}

	}

	public int GetMaxlabelsseeded(RandomAccessibleInterval<IntType> intimg) {

		IntType min = new IntType();
		IntType max = new IntType();
		computeMinMax(Views.iterable(intimg), min, max);

		return max.get();

	}

	public <T extends Comparable<T> & Type<T>> void computeMinMax(final Iterable<T> input, final T min, final T max) {
		// create a cursor for the image (the order does not matter)
		final Iterator<T> iterator = input.iterator();

		// initialize min and max with the first image value
		T type = iterator.next();

		min.set(type);
		max.set(type);

		// loop over the rest of the data and determine min and max value
		while (iterator.hasNext()) {
			// we need this type more than once
			type = iterator.next();

			if (type.compareTo(min) < 0)
				min.set(type);

			if (type.compareTo(max) > 0)
				max.set(type);
		}
	}

	public void IntersectandTrackCurrent() {

		double percent = 0;

		int z = parent.thirdDimension;
		int t = parent.fourthDimension;
		if (parent.supermode && parent.automode) {

		

			BlockRepeat(percent, z, t);

		}

		if (parent.automode && !parent.supermode) {

			

			BlockRepeatAuto(percent, z, t);

		}

		else if (!parent.automode && !parent.supermode) {

			BlockRepeatManual(percent, z, t);

		}

		parent.updatePreview(ValueChange.THIRDDIMmouse);

	}

	public RandomAccessibleInterval<IntType> getIntimg(RandomAccessibleInterval<BitType> CurrentView) {

		DistWatershedBinary segmentimage = new DistWatershedBinary(CurrentView);
		segmentimage.process();

		RandomAccessibleInterval<IntType> CurrentViewInt = segmentimage.getResult();

		return CurrentViewInt;
	}

}
