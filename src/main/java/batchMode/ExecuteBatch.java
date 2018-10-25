package batchMode;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import curvatureUtils.Node;
import ellipsoidDetector.Intersectionobject;
import ellipsoidDetector.KymoSaveobject;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Line;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.plugin.PlugIn;
import ij.plugin.frame.RoiManager;
import ij.process.ColorProcessor;
import kalmanForSegments.Segmentobject;
import kalmanTracker.TrackModel;
import mpicbg.imglib.util.Util;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;
import pluginTools.ComputeAngles;
import pluginTools.ComputeCurvature;
import pluginTools.InteractiveSimpleEllipseFit;
import pluginTools.InteractiveSimpleEllipseFit.ValueChange;
import utility.Curvatureobject;
import utility.DisplayAuto;
import utility.Roiobject;
import utility.ShowResultView;
import utility.Slicer;

public class ExecuteBatch implements PlugIn, Runnable {

	public String batchfolder;
	public String userfile;
	public JProgressBar jpb;
	public final File[] C1_AllImages;
	public final File[] C2_AllImages;
	public final File[] SegmentationImages;

	public JLabel label = new JLabel("Progress..");
	public JFrame frame = new JFrame();
	public JPanel panel = new JPanel();
	public int Progressmin = 0;
	public int Progressmax = 100;
	public int max = Progressmax;
	public Color confirmedRois = Color.BLUE;
	public Color defaultRois = Color.YELLOW;
	public Color colorChange = Color.GRAY;
	public Color colorInChange = Color.RED;
	public int maxlabel;
	public Color colorOval = Color.CYAN;
	public Color colorDet = Color.GREEN;
	public Color colorLineA = Color.YELLOW;
	public Color colorLineB = Color.YELLOW;
	public Color colorPoints = Color.RED;
	public Color colorresult = Color.magenta;
	public ArrayList<Pair<String, Intersectionobject>> Tracklist;
	public ArrayList<Pair<String, Intersectionobject>> denseTracklist;

	public HashMap<String, ArrayList<Intersectionobject>> HashTrackList;
	public HashMap<String, ArrayList<Intersectionobject>> HashdenseTrackList;
	public SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge> parentgraph;
	public HashMap<String, SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>> parentgraphZ;
	public HashMap<String, SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>> parentdensegraphZ;
	public HashMap<String, SimpleWeightedGraph<Segmentobject, DefaultWeightedEdge>> parentgraphSegZ;
	public HashMap<String, ArrayList<Intersectionobject>> ALLIntersections;
	public HashMap<String, ArrayList<Intersectionobject>> ALLdenseIntersections;
	public HashMap<String, ArrayList<Segmentobject>> ALLSegments;
	public HashMap<Integer, ArrayList<double[]>> HashresultCurvature;
	public HashMap<Integer, List<RealLocalizable>> SubHashresultCurvature;

	public HashMap<Integer, Double> HashresultSegCurvature;
	public HashMap<Integer, Double> HashresultSegIntensityA;
	public HashMap<Integer, Double> HashresultSegIntensityB;
	public HashMap<Integer, Double> HashresultSegPerimeter;
	public HashMap<String, KymoSaveobject> KymoFileobject;
	public Set<Integer> pixellist;
	ColorProcessor cp = null;

	public HashMap<String, Intersectionobject> Finalresult;
	public HashMap<String, Segmentobject> SegmentFinalresult;
	public HashMap<Integer, Curvatureobject> Finalcurvatureresult;
	public ArrayList<Node<RealLocalizable>> Allnodes = new ArrayList<Node<RealLocalizable>>();
	public HashMap<String, Node<RealLocalizable>> Nodemap = new HashMap<String, Node<RealLocalizable>>();
	public HashMap<Integer, List<RealLocalizable>> Listmap = new HashMap<Integer, List<RealLocalizable>>();
	public HashMap<Integer, Integer> CellLabelsizemap = new HashMap<Integer, Integer>();
	public Overlay overlay;
	public HashMap<String, ArrayList<Pair<Integer, Double>>> StripList = new HashMap<String, ArrayList<Pair<Integer, Double>>>();
	
	
	public int minNumInliers = LocalPrefs.getInt(".NumberofSegments", 10);
	public int background = LocalPrefs.getInt(".BackgroundLabel.int", 0);

	public int resolution = LocalPrefs.getInt(".Resolution.int", 1);
	public double timecal = LocalPrefs.getDouble(".TimeCalibration", 1);
	public double calibration = LocalPrefs.getDouble(".SpaceCalibration", 1);

	public boolean pixelcelltrackcirclefits = LocalPrefs.getBoolean(".CurvatureViaCircle.boolean", true);
	public boolean distancemethod = LocalPrefs.getBoolean(".CurvatureViaDistance.boolean", false);
	public RandomAccessibleInterval<FloatType> originalimgsmooth;
	public RandomAccessibleInterval<FloatType> originalimg;
	public RandomAccessibleInterval<FloatType> originalSecimg;
	public RandomAccessibleInterval<IntType> originalimgsuper;
	public RandomAccessibleInterval<FloatType> originalimgbefore;
	
	public boolean automode;
	public boolean supermode;
	public boolean curveautomode;
	public boolean curvesupermode;
	public final InteractiveSimpleEllipseFit parent;
	public final String channelA;
	public final String channelB;
	public final String channelSeg;
	public final boolean twochannel;
	
	public RandomAccessibleInterval<FloatType> CurrentView;
	public RandomAccessibleInterval<FloatType> CurrentViewSmooth;
	public RandomAccessibleInterval<FloatType> CurrentViewOrig;
	public RandomAccessibleInterval<FloatType> CurrentViewSecOrig;
	public RandomAccessibleInterval<FloatType> CurrentResultView;
	public ArrayList<Curvatureobject> localCurvature, interpolatedlocalCurvature;
	public ArrayList<ArrayList<Curvatureobject>> AlllocalCurvature;
	public ArrayList<Pair<String, double[]>> resultAngle;
	public ArrayList<Pair<String, Pair<Integer, ArrayList<double[]>>>> resultCurvature;
	public String uniqueID, tmpID, ZID, TID;
	
	public int thirdDimensionslider = 1;
	public int thirdDimensionsliderInit = 1;
	public int fourthDimensionslider = 1;
	public int fourthDimensionsliderInit = 1;
	public int fourthDimension;
	public int thirdDimension;
	public int thirdDimensionSize;
	public int fourthDimensionSize;
	public ImagePlus imp;
	public ImagePlus localimp;
	public float lowprob = 0f;
	public float highprob = 1f;
	public ImagePlus RMStrackImages;
	
	
	public HashMap<String, ArrayList<Intersectionobject>> sortedMappair = new HashMap<String, ArrayList<Intersectionobject>>();
	
	public ExecuteBatch() {

		this.parent = null;
		this.C1_AllImages = null;
		this.C2_AllImages = null;
		this.channelA = null;
		this.channelB = null;
		twochannel = false;
		this.SegmentationImages = null;
		this.channelSeg = null;
	};

	public ExecuteBatch(final File[] C1_AllImages, final File[] SegmentationImages, final String channelA,
			final String channelSeg, final InteractiveSimpleEllipseFit parent, final File batchdirectory,
			final boolean twochannel) {

		this.C1_AllImages = C1_AllImages;
		this.C2_AllImages = null;
		this.channelA = channelA;
		this.channelB = null;
		this.channelSeg = channelSeg;
		this.parent = parent;
		this.batchfolder = batchdirectory.getParent();
		this.twochannel = twochannel;
		this.SegmentationImages = SegmentationImages;

	}

	public ExecuteBatch(final File[] C1_AllImages, final File[] C2_AllImages, final File[] SegmentationImages,
			final String channelA, final String channelB, final String channelSeg,
			final InteractiveSimpleEllipseFit parent, final File batchdirectory, final boolean twochannel) {

		this.C1_AllImages = C1_AllImages;
		this.C2_AllImages = C2_AllImages;
		this.channelA = channelA;
		this.channelB = channelB;
		this.channelSeg = channelSeg;
		this.parent = parent;
		this.batchfolder = batchdirectory.getParent();
		this.twochannel = twochannel;
		this.SegmentationImages = SegmentationImages;

	}

	public void goTrack() {

		LocalPrefs.setHomeDir(C1_AllImages[0].getParent());
		parent.usefolder = batchfolder;

		LocalPrefs.load(C1_AllImages[0].getParent(), IJ.getApplet());
		ProgressBatch startbatch = new ProgressBatch(this);
		startbatch.execute();

	}

	@Override
	public void run(String arg) {
		run();

	}

	@Override
	public void run() {
		jpb = new JProgressBar();
		goTrack();
	}
	public void updatePreview(final ValueChange change) {
	

		uniqueID = Integer.toString(thirdDimension) + Integer.toString(fourthDimension);
		ZID = Integer.toString(thirdDimension);
		TID = Integer.toString(fourthDimension);
		tmpID = Float.toString(thirdDimension) + Float.toString(fourthDimension);
		overlay = imp.getOverlay();

		if (overlay == null) {

			overlay = new Overlay();
			imp.setOverlay(overlay);
		}

	

		if (change == ValueChange.SEG) {

			if (!supermode && !curvesupermode) {
				RandomAccessibleInterval<FloatType> tempview = null;

				if (automode || curveautomode)
					tempview = utility.Binarization.CreateBinary(CurrentViewSmooth, lowprob, highprob);

				if (localimp == null || !localimp.isVisible() && automode) {
					localimp = ImageJFunctions.show(tempview);

				}

				else {

					final float[] pixels = (float[]) localimp.getProcessor().getPixels();
					final Cursor<FloatType> c = Views.iterable(tempview).cursor();

					for (int i = 0; i < pixels.length; ++i)
						pixels[i] = c.next().get();

					localimp.updateAndDraw();

				}

				if (automode || curveautomode)
					localimp.setTitle(
							"Seg Image" + " " + "time point : " + fourthDimension + " " + " Z: " + thirdDimension);

			}
		}

		
		if (change == ValueChange.THIRDDIMmouse || change == ValueChange.FOURTHDIMmouse) {
			if (Tracklist.size() > 0 && (automode || supermode)) {

				ComputeAngles current = new ComputeAngles(parent, null);

				current.Lineage();

			}
			if (Tracklist.size() > 0 && (curveautomode || curvesupermode)) {

				ComputeCurvature current = new ComputeCurvature(parent, null);
				current.CurvedLineage();

			}
		
			if (StripList.size() > 0) {

				for (Map.Entry<String, SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>> entryZ : parentdensegraphZ
						.entrySet()) {
					TrackModel model = new TrackModel(entryZ.getValue());
					RandomAccessibleInterval<FloatType> StripImage = new ArrayImgFactory<FloatType>()
							.create(CurrentViewOrig, new FloatType());
					RandomAccess<FloatType> ranacStrip = StripImage.randomAccess();
					for (final Integer id : model.trackIDs(true)) {
						String targetid = id + entryZ.getKey();
						

						
						for (Map.Entry<String, ArrayList<Pair<Integer, Double>>> item : StripList.entrySet()) {
							String TrackID = item.getKey();
							if (TrackID.equals(targetid)) {
								for (Pair<Integer, Double> singleitem : item.getValue()) {

									String TimeId = Integer.toString(thirdDimension);

									ArrayList<Intersectionobject> currentlist = sortedMappair.get(TrackID + TimeId);

									for (Intersectionobject currentobject : currentlist) {

										ArrayList<double[]> sortedlinelist = currentobject.linelist;

										int i = singleitem.getA();

										if(sortedlinelist.size() > i) {
										ranacStrip.setPosition(new long[] { (long) sortedlinelist.get(i)[0],
												(long) sortedlinelist.get(i)[1] });
										ranacStrip.get().setReal(singleitem.getB());
										}
									}

								}
								
								
								
								

							}
						}
						
						
					  if(RMStrackImages == null || !RMStrackImages.isVisible()) {
						  
						  RMStrackImages = ImageJFunctions.show(StripImage);
						  
					  }
					  else {
						  
						  final float[] pixels = (float[]) RMStrackImages.getProcessor().getPixels();
							final Cursor<FloatType> c = Views.iterable(StripImage).cursor();

							for (int i = 0; i < pixels.length; ++i)
								pixels[i] = c.next().get();

							RMStrackImages.updateAndDraw();
						  
						  
					  }
						
						

					  RMStrackImages.setTitle("Root Mean square of Curvature" + targetid);
						
					}
				}

			}
			

		}

	}
}
