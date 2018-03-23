package pluginTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import ellipsoidDetector.Intersectionobject;
import ellipsoidDetector.Tangentobject;
import ij.gui.EllipseRoi;
import ij.gui.Line;
import ij.gui.OvalRoi;
import ij.gui.Roi;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.algorithm.ransac.RansacModels.Ellipsoid;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.RealSum;
import net.imglib2.util.Util;
import net.imglib2.view.Views;
import pluginTools.InteractiveSimpleEllipseFit.ValueChange;
import utility.LabelRansac;
import utility.Roiobject;
import utility.Watershedobject;

public class Computeinwater   {
	
	
	final InteractiveSimpleEllipseFit parent;
	final RandomAccessibleInterval<BitType> CurrentView;
	final RandomAccessibleInterval<IntType> CurrentViewInt;
	final int t;
	final int z;
	final int maxlabel;
	int percent;

public Computeinwater (final InteractiveSimpleEllipseFit parent, final RandomAccessibleInterval<BitType> CurrentView, final RandomAccessibleInterval<IntType> CurrentViewInt, final int t,
		final int z, int percent, final int maxlabel ) {
		
		this.parent = parent;
		this.CurrentView = CurrentView;
		this.CurrentViewInt = CurrentViewInt;
		this.t = t;
		this.z = z;
		this.maxlabel = maxlabel;
		this.percent = percent;
	}


	public void ParallelRansac() {
		
		
		int nThreads = Runtime.getRuntime().availableProcessors();
		// set up executor service
		final ExecutorService taskExecutor = Executors.newFixedThreadPool(nThreads);
		 List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
		 
		 
		    ArrayList<EllipseRoi> resultroi = new ArrayList<EllipseRoi>();
			ArrayList<OvalRoi> resultovalroi = new ArrayList<OvalRoi>();
			ArrayList<Line> resultlineroi = new ArrayList<Line>();
			// Obtain the points of intersections

			ArrayList<Tangentobject> AllPointsofIntersect = new ArrayList<Tangentobject>();
			ArrayList<Intersectionobject> Allintersection = new ArrayList<Intersectionobject>();

			ArrayList<Pair<Ellipsoid, Ellipsoid>> fitmapspecial = new ArrayList<Pair<Ellipsoid, Ellipsoid>>();
			parent.maxlabel = maxlabel;
			
			
		     for (int label = 1; label<= maxlabel; ++label) {
		    	 
		    	 percent++;
		    	 
		    	 long size = CurrentViewInt.dimension(0) * CurrentViewInt.dimension(1);
		    	
		    		 Watershedobject current = CurrentLabelImage(CurrentViewInt, CurrentView, label);

		    		 if (size > current.Size && current.meanIntensity > parent.perimeter) {
			 
			 
			 List<Pair<RealLocalizable, BitType>> truths =  new ArrayList<Pair<RealLocalizable, BitType>>();
			 tasks.add(Executors.callable(new LabelRansac(parent, current.source, truths, t, z, resultroi, resultovalroi, resultlineroi,AllPointsofIntersect,Allintersection,fitmapspecial )));
			 }
			
		}
		try {
			taskExecutor.invokeAll(tasks);
			
			if (parent.automode) {
			
				String uniqueID = Integer.toString(z) + Integer.toString(t);
				Roiobject currentobject = new Roiobject(resultroi,resultovalroi,resultlineroi, z, t, true);
				parent.ZTRois.put(uniqueID, currentobject);

				Display();
				System.out.println("Here");
			}
			
		} catch (InterruptedException e1) {

		
		}
		
		
		
		
	}
	public void Display() {

		parent.overlay.clear();

		if (parent.ZTRois.size() > 0) {

			for (Map.Entry<String, Roiobject> entry : parent.ZTRois.entrySet()) {

				Roiobject currentobject = entry.getValue();
				if (currentobject.fourthDimension == parent.fourthDimension
						&& currentobject.thirdDimension == parent.thirdDimension) {

					if (currentobject.resultroi != null) {
						for (int i = 0; i < currentobject.resultroi.size(); ++i) {

							EllipseRoi ellipse = currentobject.resultroi.get(i);
							ellipse.setStrokeColor(parent.colorInChange);
							parent.overlay.add(ellipse);

						}

					}

					if (currentobject.resultovalroi != null) {
						for (int i = 0; i < currentobject.resultovalroi.size(); ++i) {

							OvalRoi ellipse = currentobject.resultovalroi.get(i);
							ellipse.setStrokeColor(parent.colorDet);
							parent.overlay.add(ellipse);

						}

					}

					if (currentobject.resultlineroi != null) {
						for (int i = 0; i < currentobject.resultlineroi.size(); ++i) {

							Line ellipse = currentobject.resultlineroi.get(i);
							ellipse.setStrokeColor(parent.colorLineA);

							parent.overlay.add(ellipse);

						}

					}

					break;
				}

			}
			parent.impOrig.setOverlay(parent.overlay);
			parent.impOrig.updateAndDraw();


		}
	}
	
	public static Watershedobject CurrentLabelImage(RandomAccessibleInterval<IntType> Intimg,
			RandomAccessibleInterval<BitType> currentimg, int currentLabel) {
		int n = currentimg.numDimensions();
		RandomAccess<BitType> inputRA = currentimg.randomAccess();
		long[] position = new long[n];
		
		
		
		Cursor<IntType> intCursor = Views.iterable(Intimg).cursor();
		final BitType type = currentimg.randomAccess().get().createVariable();
		final ImgFactory<BitType> factory = Util.getArrayOrCellImgFactory(currentimg, type);
		RandomAccessibleInterval<BitType> outimg = factory.create(currentimg, type);
		RandomAccess<BitType> imageRA = outimg.randomAccess();

		// Go through the whole image and add every pixel, that belongs to
		// the currently processed label
		long[] minVal = { currentimg.max(0), currentimg.max(1) };
		long[] maxVal = { currentimg.min(0), currentimg.min(1) };
		
		while (intCursor.hasNext()) {
			intCursor.fwd();
			inputRA.setPosition(intCursor);
			imageRA.setPosition(inputRA);
			int i = intCursor.get().get();
			if (i == currentLabel) {
				intCursor.localize(position);
				for (int d = 0; d < n; ++d) {
					if (position[d] < minVal[d]) {
						minVal[d] = position[d];
					}
					if (position[d] > maxVal[d]) {
						maxVal[d] = position[d];
					}

				}
				
			
			
				imageRA.get().set(inputRA.get());
			}
			else
				imageRA.get().setZero();
			

		}
		FinalInterval intervalsmall = new FinalInterval(minVal, maxVal) ;
		
		
		RandomAccessibleInterval<BitType> outimgsmall = extractImage(outimg, intervalsmall);
		double meanIntensity = computeAverage(Views.iterable(outimgsmall));
		double size = (intervalsmall.max(0) - intervalsmall.min(0)) * (intervalsmall.max(1) - intervalsmall.min(1));
		
		Watershedobject currentobject = new Watershedobject(outimgsmall, meanIntensity, size);
		
		
		return currentobject;

	}
	
	/**
     * Compute the average intensity for an {@link Iterable}.
     *
     * @param input - the input data
     * @return - the average as double
     */
    public static < T extends RealType< T > > double computeAverage( final Iterable< T > input )
    {
        // Count all values using the RealSum class.
        // It prevents numerical instabilities when adding up millions of pixels
        final RealSum realSum = new RealSum();
        long count = 0;
 
        for ( final T type : input )
        {
            realSum.add( type.getRealDouble() );
            ++count;
        }
 
        return realSum.getSum() ;
    }
	
	public static RandomAccessibleInterval<BitType> extractImage(final RandomAccessibleInterval<BitType> intervalView, final FinalInterval interval) {

		return intervalView;
	}
	

}
