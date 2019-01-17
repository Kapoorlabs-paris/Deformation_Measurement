package pluginTools;

import java.awt.Rectangle;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import com.esotericsoftware.kryo.util.IdentityMap.Entry;

import batchMode.LocalPrefs;
import curvatureUtils.PointExtractor;
import ellipsoidDetector.Intersectionobject;
import ellipsoidDetector.Tangentobject;
import ij.gui.EllipseRoi;
import ij.gui.Line;
import ij.gui.OvalRoi;
import ij.gui.Roi;
import kalmanForSegments.Segmentobject;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.algorithm.ransac.RansacModels.Ellipsoid;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.Type;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.RealSum;
import net.imglib2.util.Util;
import net.imglib2.view.Views;
import pluginTools.InteractiveSimpleEllipseFit.ValueChange;
import utility.DisplayAuto;
import utility.LabelCurvature;
import utility.LabelRansac;
import utility.NormalIntersection;
import utility.Roiobject;
import utility.SuperIntersection;
import utility.Watershedobject;

public class Computeinwater {

	final InteractiveSimpleEllipseFit parent;
	final RandomAccessibleInterval<BitType> CurrentView;
	final RandomAccessibleInterval<IntType> CurrentViewInt;
	final int t;
	final int z;
	final int maxlabel;
	int percent;

	public Computeinwater(final InteractiveSimpleEllipseFit parent, final RandomAccessibleInterval<BitType> CurrentView,
			final RandomAccessibleInterval<IntType> CurrentViewInt, final int t, final int z, int percent,
			final int maxlabel) {

		this.parent = parent;
		this.CurrentView = CurrentView;
		this.CurrentViewInt = CurrentViewInt;
		this.t = t;
		this.z = z;
		this.maxlabel = maxlabel;
		this.percent = percent;
	}

	public Computeinwater(final InteractiveSimpleEllipseFit parent, final RandomAccessibleInterval<BitType> CurrentView,
			final RandomAccessibleInterval<IntType> CurrentViewInt, final int t, final int z, int percent) {

		this.parent = parent;
		this.CurrentView = CurrentView;
		this.CurrentViewInt = CurrentViewInt;
		this.t = t;
		this.z = z;
		this.maxlabel = 0;
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

		Iterator<Integer> setiter = parent.pixellist.iterator();

		parent.superReducedSamples = new ArrayList<Pair<Ellipsoid, List<Pair<RealLocalizable, FloatType>>>>();
		while (setiter.hasNext()) {
			percent++;

			int label = setiter.next();

			Watershedobject current;

			if (parent.supermode || parent.automode) {
				current = utility.Watershedobject.CurrentLabelBinaryImage(CurrentViewInt, label);
				
			System.out.println("Should open in only super or auto mode");	
			}
			else {
				current = utility.Watershedobject.CurrentLabelImage(CurrentViewInt, label);
		
			System.out.println("Should open in manual mode");
			}
		
			// Neglect the small watershed regions by choosing only those regions which have
			// more than 9 candidate points for ellipse fitting
			List<Pair<RealLocalizable, FloatType>> truths = new ArrayList<Pair<RealLocalizable, FloatType>>();
			
		      
				
			if (current.Size > parent.minperimeter / 3 * parent.minperimeter / 3
					&& current.Size < parent.maxperimeter / 3 * parent.maxperimeter / 3
					&& current.meanIntensity > parent.minellipsepoints) {
				tasks.add(Executors.callable(new LabelRansac(parent, current.source, truths, t, z, resultroi,
						resultovalroi, resultlineroi, AllPointsofIntersect, Allintersection, fitmapspecial, parent.jpb,
						percent, parent.supermode)));
			

			}
		}

		try {
			
			System.out.println("Executing tasks");
			taskExecutor.invokeAll(tasks);

			
			// Get superintersection

			
			SuperIntersection newintersect = new SuperIntersection(parent);
			AllPointsofIntersect = new ArrayList<Tangentobject>();
			Allintersection = new ArrayList<Intersectionobject>();
			newintersect.Getsuperintersection(resultroi, resultovalroi, resultlineroi, AllPointsofIntersect,
					Allintersection, t, z);

		} catch (InterruptedException e1) {

			System.out.println(e1 + " Task not executed");
			
		}

	}

	public void ParallelRansacCurve() {

		int nThreads = Runtime.getRuntime().availableProcessors();
		// set up executor service
		final ExecutorService taskExecutor = Executors.newFixedThreadPool(nThreads);
		List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
		List<Future<HashMap<Integer, Intersectionobject>>> list = new ArrayList<Future<HashMap<Integer, Intersectionobject>>>();
		
		ArrayList<Line> resultlineroi = new ArrayList<Line>();
		ArrayList<OvalRoi> resultcurvelineroi = new ArrayList<OvalRoi>();
		ArrayList<OvalRoi> resultallcurvelineroi = new ArrayList<OvalRoi>();
		ArrayList<EllipseRoi> ellipselineroi = new ArrayList<EllipseRoi>();
		ArrayList<Roi>Segmentrect = new ArrayList<Roi>();
		// Obtain the points of intersections

		Iterator<Integer> setiter = parent.pixellist.iterator();
		parent.superReducedSamples = new ArrayList<Pair<Ellipsoid, List<Pair<RealLocalizable, FloatType>>>>();
		ArrayList<Intersectionobject> AllCurveintersection = new ArrayList<Intersectionobject>();
		
		 
		while (setiter.hasNext()) {

			percent++;

			int label = setiter.next();
			if(label!=parent.background) {
			// Creating a binary image in the integer image region from the boundary
			// probability map
			Watershedobject current =

					utility.Watershedobject.CurrentLabelBinaryImage(CurrentViewInt, label);


			    List<RealLocalizable> truths = new ArrayList<RealLocalizable>();
		
			    LabelCurvature getLabel = new LabelCurvature(parent, current.source, truths, resultlineroi, resultcurvelineroi,resultallcurvelineroi,ellipselineroi, Segmentrect,  AllCurveintersection, 
						 t, z,
							parent.jpb, percent, label);
			  
			    
			    Future<HashMap<Integer, Intersectionobject>> future = taskExecutor.submit(getLabel);
			    list.add(future);
			    
			
				
				
				
			}
		}
		
		ArrayList<Intersectionobject> AlldenseCurveintersection = new ArrayList<Intersectionobject>();
		
		for(Future<HashMap<Integer, Intersectionobject>> fut : list){
            try {
               
            	
            	HashMap<Integer, Intersectionobject> LabelMap = 	fut.get();
            	for(Map.Entry<Integer, Intersectionobject> entry: LabelMap.entrySet())
            		AlldenseCurveintersection.add(entry.getValue());
            	
            	
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        //shut down the executor service now
		taskExecutor.shutdown();
		
		
		
				try {
					
					
					taskExecutor.invokeAll(tasks);

					String uniqueID = Integer.toString(z) + Integer.toString(t);
					parent.ALLIntersections.put(uniqueID, AllCurveintersection);
					parent.ALLdenseIntersections.put(uniqueID, AlldenseCurveintersection);
					

				} catch (InterruptedException e)  {

					System.out.println(e + " Task not executed");
					
				}
			
		

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
		

}