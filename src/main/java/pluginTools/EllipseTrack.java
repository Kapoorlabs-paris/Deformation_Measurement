package pluginTools;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import distanceTransform.DistWatershed;
import distanceTransform.DistWatershedBinary;
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
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.algorithm.morphology.table2d.Thin;
import net.imglib2.algorithm.ransac.RansacModels.BisectorEllipsoid;
import net.imglib2.algorithm.ransac.RansacModels.ConnectedComponentCoordinates;
import net.imglib2.algorithm.ransac.RansacModels.DisplayasROI;
import net.imglib2.algorithm.ransac.RansacModels.Ellipsoid;
import net.imglib2.algorithm.ransac.RansacModels.Intersections;
import net.imglib2.algorithm.ransac.RansacModels.NumericalSolvers;
import net.imglib2.algorithm.ransac.RansacModels.SortSegments;
import net.imglib2.algorithm.ransac.RansacModels.Tangent2D;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.Type;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
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

	public void IntersectandTrack() {

		// Main method for computing intersections and tangents and angles between
		// tangents
		double percent = 0;
		
		
		if(parent.supermode && parent.automode) {
			
			if (parent.originalimg.numDimensions() > 3) {

				for (int t = 1; t <= parent.fourthDimensionSize; ++t) {

					for (int z = 1; z <= parent.thirdDimensionSize; ++z) {

						parent.thirdDimension = z;
						parent.fourthDimension = t;

						parent.updatePreview(ValueChange.THIRDDIMmouse);

						percent++;
						utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.fourthDimensionSize),
								"Fitting ellipses and computing angles T = " + t + "/" + parent.fourthDimensionSize
										+ " Z = " + z + "/" + parent.thirdDimensionSize);

						RandomAccessibleInterval<BitType> CurrentView = utility.Slicer.getCurrentViewBit(parent.empty,
								z, parent.thirdDimensionSize, t, parent.fourthDimensionSize);
						RandomAccessibleInterval<IntType> CurrentViewInt = utility.Slicer.getCurrentViewInt(
								parent.originalimgsuper, z, parent.thirdDimensionSize, t, parent.fourthDimensionSize);

						RandomAccessibleInterval<BitType> CurrentViewthin = getThin(CurrentView);
						
						GetPixelList(CurrentViewInt);
						Computeinwater compute = new Computeinwater(parent, CurrentViewthin, CurrentViewInt, t, z,
								(int) percent);
						compute.ParallelRansac();

					}
				}

			}

			else if (parent.originalimg.numDimensions() > 2) {

				for (int z = 1; z <= parent.thirdDimensionSize; ++z) {

				
					parent.thirdDimension = z;
					parent.updatePreview(ValueChange.THIRDDIMmouse);

					percent++;
					utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.thirdDimensionSize),
							"Fitting ellipses and computing angles T/Z = " + z + "/" + parent.thirdDimensionSize);
					RandomAccessibleInterval<BitType> CurrentView = utility.Slicer.getCurrentViewBit(parent.empty, z,
							parent.thirdDimensionSize, 1, parent.fourthDimensionSize);
					RandomAccessibleInterval<IntType> CurrentViewInt = utility.Slicer.getCurrentViewInt(
							parent.originalimgsuper, z, parent.thirdDimensionSize, 1, parent.fourthDimensionSize);
					
					

					RandomAccessibleInterval<BitType> CurrentViewthin = getThin(CurrentView);
					GetPixelList(CurrentViewInt);

					Computeinwater compute = new Computeinwater(parent, CurrentViewthin, CurrentViewInt, 1, z,
							(int) percent);

					compute.ParallelRansac();

				}

			} else {
				int z = parent.thirdDimension;
				int t = parent.fourthDimension;
				parent.updatePreview(ValueChange.THIRDDIMmouse);
				percent++;

				RandomAccessibleInterval<BitType> CurrentView = utility.Slicer.getCurrentViewBit(parent.empty, z,
						parent.thirdDimensionSize, t, parent.fourthDimensionSize);

				RandomAccessibleInterval<IntType> CurrentViewInt = utility.Slicer.getCurrentViewInt(
						parent.originalimgsuper, z, parent.thirdDimensionSize, 1, parent.fourthDimensionSize);
				RandomAccessibleInterval<BitType> CurrentViewthin = getThin(CurrentView);
				ImageJFunctions.show(CurrentViewthin);
				GetPixelList(CurrentViewInt);

				Computeinwater compute = new Computeinwater(parent, CurrentView, CurrentViewInt, t, z,
						(int) percent);

				compute.ParallelRansac();

			}
			
			
			
		
		
		
		
		
		

		}
		
		
		if (parent.automode && !parent.supermode) {


			int span = parent.span;
			if (parent.originalimg.numDimensions() > 3) {
			for (int t = 1; t <= parent.fourthDimensionSize; ++t) {

				for (int z = 1; z <= parent.thirdDimensionSize; ++z) {
					
					parent.thirdDimension = z;
					parent.fourthDimension = t;
					
					
					parent.updatePreview(ValueChange.THIRDDIMmouse);
					
					percent++;
					utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.fourthDimensionSize),
							"Fitting ellipses and computing angles T = " + t + "/" + parent.fourthDimensionSize
									+ " Z = " + z + "/" + parent.thirdDimensionSize);

					RandomAccessibleInterval<BitType> CurrentView = utility.Slicer.getCurrentViewBit(parent.empty,
							z, parent.thirdDimensionSize, t, parent.fourthDimensionSize);


					Pair<RandomAccessibleInterval<IntType>, RandomAccessibleInterval<BitType>> Current = getAutoint(CurrentView, span);

					parent.maxlabel = GetMaxlabelsseeded(Current.getA());
					Computeinwater compute = new Computeinwater(parent, Current.getB(), Current.getA(), t, z,
							(int) percent, parent.maxlabel);	
					compute.ParallelRansac();

				}
			}

			}

		else if (parent.originalimg.numDimensions() > 2) {

			for (int z = 1; z <= parent.thirdDimensionSize; ++z) {
				
				parent.thirdDimension = z;
				parent.updatePreview(ValueChange.THIRDDIMmouse);
				
				percent++;
				utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.thirdDimensionSize),
						"Fitting ellipses and computing angles T/Z = " + z + "/" + parent.thirdDimensionSize);
				RandomAccessibleInterval<BitType> CurrentView = utility.Slicer.getCurrentViewBit(parent.empty, z,
						parent.thirdDimensionSize, 1, parent.fourthDimensionSize);


				Pair<RandomAccessibleInterval<IntType>, RandomAccessibleInterval<BitType>> Current = getAutoint(CurrentView, parent.span);

				parent.maxlabel = GetMaxlabelsseeded(Current.getA());
				Computeinwater compute = new Computeinwater(parent, Current.getB(), Current.getA(), 1, z,
						(int) percent, parent.maxlabel);
				compute.ParallelRansac();

			}
		
		} else  {
			int z = parent.thirdDimension;
			int t = parent.fourthDimension;
			parent.updatePreview(ValueChange.THIRDDIMmouse);
			percent++;

			RandomAccessibleInterval<BitType> CurrentView = utility.Slicer.getCurrentViewBit(parent.empty, z,
					parent.thirdDimensionSize, t, parent.fourthDimensionSize);


			Pair<RandomAccessibleInterval<IntType>, RandomAccessibleInterval<BitType>> Current = getAutoint(CurrentView, parent.span);

			parent.maxlabel = GetMaxlabelsseeded(Current.getA());
			Computeinwater compute = new Computeinwater(parent, Current.getB(), Current.getA(), t, z, (int)percent, parent.maxlabel);
			compute.ParallelRansac();

		}
		}
		
		
		else if (!parent.automode && !parent.supermode) {

			if (parent.originalimg.numDimensions() > 3) {

				for (Map.Entry<String, Integer> entry : parent.Accountedframes.entrySet()) {

					int t = entry.getValue();

					for (Map.Entry<String, Integer> entryZ : parent.AccountedZ.entrySet()) {

						int z = entryZ.getValue();
						parent.updatePreview(ValueChange.THIRDDIMmouse);
						percent++;
						if (parent.fourthDimensionSize != 0)
							utility.ProgressBar.SetProgressBar(jpb,
									100 * percent / (parent.Accountedframes.entrySet().size()),
									"Fitting ellipses and computing angles T = " + t + "/" + parent.fourthDimensionSize
											+ " Z = " + z + "/" + parent.thirdDimensionSize);
						else
							utility.ProgressBar.SetProgressBar(jpb,
									100 * percent / (parent.AccountedZ.entrySet().size()),
									"Fitting ellipses and computing angles T/Z = " + z + "/"
											+ parent.thirdDimensionSize);

						RandomAccessibleInterval<BitType> CurrentView = utility.Slicer.getCurrentViewBit(parent.empty,
								z, parent.thirdDimensionSize, t, parent.fourthDimensionSize);

						RandomAccessibleInterval<IntType> CurrentViewInt = utility.Slicer.getCurrentViewInt(
								parent.emptyWater, z, parent.thirdDimensionSize, t, parent.fourthDimensionSize);

						parent.maxlabel = GetMaxlabelsseeded(CurrentViewInt);

						Computeinwater compute = new Computeinwater(parent, CurrentView, CurrentViewInt, t, z,
								(int) percent, parent.maxlabel);
						compute.ParallelRansac();

					}
				}

			} else if (parent.originalimg.numDimensions() > 2 && parent.originalimg.numDimensions() <= 3) {

				int t = parent.fourthDimension;

				for (Map.Entry<String, Integer> entryZ : parent.AccountedZ.entrySet()) {

					int z = entryZ.getValue();
					System.out.println(z + " " + t + "Z and T" + parent.AccountedZ.size());
					parent.updatePreview(ValueChange.THIRDDIMmouse);
					percent++;

					utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.AccountedZ.entrySet().size()),
							"Fitting ellipses and computing angles T/Z = " + z + "/" + parent.thirdDimensionSize);

					RandomAccessibleInterval<BitType> CurrentView = utility.Slicer.getCurrentViewBit(parent.empty, z,
							parent.thirdDimensionSize, t, parent.fourthDimensionSize);

					RandomAccessibleInterval<IntType> CurrentViewInt = utility.Slicer.getCurrentViewInt(
							parent.emptyWater, z, parent.thirdDimensionSize, t, parent.fourthDimensionSize);
					parent.maxlabel = GetMaxlabelsseeded(CurrentViewInt);

					Computeinwater compute = new Computeinwater(parent, CurrentView, CurrentViewInt, t, z,
							(int) percent, parent.maxlabel);
					compute.ParallelRansac();

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

	
	public RandomAccessibleInterval<BitType> getThin(RandomAccessibleInterval<BitType> CurrentView){
		
		ThinningStrategyFactory fact = new ThinningStrategyFactory(true);
		ThinningStrategy strat = fact.getStrategy(Strategy.HILDITCH);
		
		ThinningOp thinit = new ThinningOp(strat, true, new ArrayImgFactory<BitType>());
		RandomAccessibleInterval<BitType> newCurrentView = new ArrayImgFactory<BitType>().create(CurrentView,
				new BitType());
		RandomAccessibleInterval<BitType> newthinCurrentView = new ArrayImgFactory<BitType>().create(CurrentView,
				new BitType());
		newthinCurrentView = thinit.compute(CurrentView, newthinCurrentView);
		
		return newthinCurrentView;
	}
	
	public Pair<RandomAccessibleInterval<IntType>, RandomAccessibleInterval<BitType>> getAutoint(
			RandomAccessibleInterval<BitType> CurrentView, int span) {

		ThinningStrategyFactory fact = new ThinningStrategyFactory(true);
		ThinningStrategy strat = fact.getStrategy(Strategy.GUOHALL);
		ThinningOp thinit = new ThinningOp(strat, true, new ArrayImgFactory<BitType>());
		RandomAccessibleInterval<BitType> newCurrentView = new ArrayImgFactory<BitType>().create(CurrentView,
				new BitType());
		RandomAccessibleInterval<BitType> newthinCurrentView = new ArrayImgFactory<BitType>().create(CurrentView,
				new BitType());

		newCurrentView = Kernels.CannyEdgeandMeanBit(CurrentView, parent.span);
		newthinCurrentView = thinit.compute(newCurrentView, newthinCurrentView);

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
        computeMinMax( Views.iterable(intimg), min, max );
        Cursor<IntType> intCursor = Views.iterable(intimg).cursor();
		int currentLabel = min.get();
		  parent.pixellist.add(currentLabel);
		while (intCursor.hasNext()) {
			intCursor.fwd();
			int i = intCursor.get().get();
		   if (i!=currentLabel) {
			   
			   
			   parent.pixellist.add(i);
			   
			   currentLabel = i;
			   
		   }
			
			
		}
		
	}

	public int GetMaxlabelsseeded(RandomAccessibleInterval<IntType> intimg) {

		IntType min = new IntType();
        IntType max = new IntType();
        computeMinMax( Views.iterable(intimg), min, max );
		
		
		return max.get();

	}
	
    public < T extends Comparable< T > & Type< T > > void computeMinMax(
        final Iterable< T > input, final T min, final T max )
    {
        // create a cursor for the image (the order does not matter)
        final Iterator< T > iterator = input.iterator();
 
        // initialize min and max with the first image value
        T type = iterator.next();
 
        min.set( type );
        max.set( type );
 
        // loop over the rest of the data and determine min and max value
        while ( iterator.hasNext() )
        {
            // we need this type more than once
            type = iterator.next();
 
            if ( type.compareTo( min ) < 0 )
                min.set( type );
 
            if ( type.compareTo( max ) > 0 )
                max.set( type );
        }
    }
	public void IntersectandTrackCurrent() {

		// Main method for computing intersections and tangents and angles between
		// tangents
		double percent = 0;
		int span = parent.span;
		if (parent.automode) {

			if (parent.originalimg.numDimensions() > 3) {

				int z = parent.thirdDimension;
				int t = parent.fourthDimension;

				parent.updatePreview(ValueChange.THIRDDIMmouse);

				percent++;
				utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.fourthDimensionSize),
						"Fitting ellipses and computing angles T = " + t + "/" + parent.fourthDimensionSize + " Z = "
								+ z + "/" + parent.thirdDimensionSize);

				RandomAccessibleInterval<BitType> CurrentView = utility.Slicer.getCurrentViewBit(parent.empty, z,
						parent.thirdDimensionSize, t, parent.fourthDimensionSize);

				Pair<RandomAccessibleInterval<IntType>, RandomAccessibleInterval<BitType>> Current = getAutoint(
						CurrentView, span);

				parent.maxlabel = GetMaxlabelsseeded(Current.getA());
				Computeinwater compute = new Computeinwater(parent, Current.getB(), Current.getA(), t, z, (int) percent,
						parent.maxlabel);
				compute.ParallelRansac();

			}

			else if (parent.originalimg.numDimensions() > 2) {

				int z = parent.thirdDimension;
				parent.updatePreview(ValueChange.THIRDDIMmouse);

				System.out.println(z + " " + parent.thirdDimension);
				percent++;
				utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.thirdDimensionSize),
						"Fitting ellipses and computing angles T/Z = " + z + "/" + parent.thirdDimensionSize);
				RandomAccessibleInterval<BitType> CurrentView = utility.Slicer.getCurrentViewBit(parent.empty, z,
						parent.thirdDimensionSize, 1, parent.fourthDimensionSize);

				Pair<RandomAccessibleInterval<IntType>, RandomAccessibleInterval<BitType>> Current = getAutoint(
						CurrentView, span);

				parent.maxlabel = GetMaxlabelsseeded(Current.getA());
				Computeinwater compute = new Computeinwater(parent, Current.getB(), Current.getA(), 1, z, (int) percent,
						parent.maxlabel);
				compute.ParallelRansac();

			} else {
				int z = parent.thirdDimension;
				int t = parent.fourthDimension;
				parent.updatePreview(ValueChange.THIRDDIMmouse);
				percent++;

				RandomAccessibleInterval<BitType> CurrentView = utility.Slicer.getCurrentViewBit(parent.empty, z,
						parent.thirdDimensionSize, t, parent.fourthDimensionSize);

				Pair<RandomAccessibleInterval<IntType>, RandomAccessibleInterval<BitType>> Current = getAutoint(
						CurrentView, span);

				parent.maxlabel = GetMaxlabelsseeded(Current.getA());
				Computeinwater compute = new Computeinwater(parent, Current.getB(), Current.getA(), t, z, (int) percent,
						parent.maxlabel);
				compute.ParallelRansac();

			}

		} else {

			if (parent.originalimg.numDimensions() > 3) {

				int z = parent.thirdDimension;
				int t = parent.fourthDimension;

				parent.updatePreview(ValueChange.THIRDDIMmouse);
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
				parent.maxlabel = GetMaxlabelsseeded(CurrentViewInt);
				Computeinwater compute = new Computeinwater(parent, CurrentView, CurrentViewInt, t, z, (int) percent,
						parent.maxlabel);
				compute.ParallelRansac();

			} else if (parent.originalimg.numDimensions() > 2 && parent.originalimg.numDimensions() <= 3) {

				int z = parent.thirdDimension;
				int t = parent.fourthDimension;

				System.out.println(z + " " + t + "Z and T" + parent.AccountedZ.size());
				parent.updatePreview(ValueChange.THIRDDIMmouse);
				percent++;

				utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.AccountedZ.entrySet().size()),
						"Fitting ellipses and computing angles T/Z = " + z + "/" + parent.thirdDimensionSize);

				RandomAccessibleInterval<BitType> CurrentView = utility.Slicer.getCurrentViewBit(parent.empty, z,
						parent.thirdDimensionSize, t, parent.fourthDimensionSize);

				RandomAccessibleInterval<IntType> CurrentViewInt = utility.Slicer.getCurrentViewInt(parent.emptyWater,
						z, parent.thirdDimensionSize, t, parent.fourthDimensionSize);
				parent.maxlabel = GetMaxlabelsseeded(CurrentViewInt);

				System.out.println(parent.maxlabel);
				Computeinwater compute = new Computeinwater(parent, CurrentView, CurrentViewInt, t, z, (int) percent,
						parent.maxlabel);
				compute.ParallelRansac();

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

				System.out.println(z + " " + t + "Z and T");

			}

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
