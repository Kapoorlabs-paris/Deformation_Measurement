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
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.Util;
import net.imglib2.view.Views;
import utility.LabelRansac;
import utility.Roiobject;

public class Computeinwater   {
	
	
	final InteractiveSimpleEllipseFit parent;
	final RandomAccessibleInterval<BitType> CurrentView;
	final RandomAccessibleInterval<IntType> CurrentViewInt;
	final int t;
	final int z;
	int percent;
	final JProgressBar jpb;	
	public Computeinwater (final InteractiveSimpleEllipseFit parent, final RandomAccessibleInterval<BitType> CurrentView, final RandomAccessibleInterval<IntType> CurrentViewInt, final int t, final int z ) {
		
		this.parent = parent;
		this.CurrentView = CurrentView;
		this.CurrentViewInt = CurrentViewInt;
		this.t = t;
		this.z = z;
		this.percent = 0;
		this.jpb = null;
	}
public Computeinwater (final InteractiveSimpleEllipseFit parent, final RandomAccessibleInterval<BitType> CurrentView, final RandomAccessibleInterval<IntType> CurrentViewInt, final int t, final int z, JProgressBar jpb, int percent ) {
		
		this.parent = parent;
		this.CurrentView = CurrentView;
		this.CurrentViewInt = CurrentViewInt;
		this.t = t;
		this.z = z;
		this.jpb = jpb;
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
		     for (int label = 1; label< parent.maxlabel; ++label) {
		    	 percent++;
		    	 utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.maxlabel),
							"Fitting ellipses and computing angles " );
			 RandomAccessibleInterval<BitType> ActualRoiimg = CurrentLabelImage(CurrentViewInt, CurrentView, label);
			 
			 long size = ActualRoiimg.dimension(0) * ActualRoiimg.dimension(1);
			 
			 if (size > parent.maxsize * parent.maxsize) {
			 
			 List<Pair<RealLocalizable, BitType>> truths =  new ArrayList<Pair<RealLocalizable, BitType>>();
			 tasks.add(Executors.callable(new LabelRansac(parent, ActualRoiimg, truths, t, z, resultroi, resultovalroi, resultlineroi,AllPointsofIntersect,Allintersection,fitmapspecial )));
			 }
			
		}
		try {
			taskExecutor.invokeAll(tasks);
			
			if (parent.automode) {
			
				String uniqueID = Integer.toString(z) + Integer.toString(t);
				Roiobject currentobject = new Roiobject(resultroi,resultovalroi,resultlineroi, z, t, true);
				parent.ZTRois.put(uniqueID, currentobject);

				Display();
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
	
	public static RandomAccessibleInterval<BitType> CurrentLabelImage(RandomAccessibleInterval<IntType> Intimg,
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
			
			

		}
		FinalInterval intervalsmall = new FinalInterval(minVal, maxVal) ;
		RandomAccessibleInterval<BitType> outimgsmall = extractImage(outimg, intervalsmall);
		return outimgsmall;

	}
	
	public static RandomAccessibleInterval<BitType> extractImage(final RandomAccessibleInterval<BitType> intervalView, final FinalInterval interval) {

		return intervalView;
	}
	

}
