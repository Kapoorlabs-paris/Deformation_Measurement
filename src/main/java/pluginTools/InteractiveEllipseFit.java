package pluginTools;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.TextField;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeriesCollection;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import ellipsoidDetector.Distance;
import ellipsoidDetector.Intersectionobject;
import ellipsoidDetector.Tangentobject;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.EllipseRoi;
import ij.gui.ImageCanvas;
import ij.gui.Line;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.io.Opener;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.plugin.frame.RoiManager;
import ij.process.ColorProcessor;
import listeners.AngleListener;
import listeners.DisplayRoiListener;
import listeners.DrawListener;
import listeners.EllipseNonStandardMouseListener;
import listeners.FilenameListener;
import listeners.InsideCutoffListener;
import listeners.MaxTryListener;
import listeners.MinpercentListener;
import listeners.OutsideCutoffListener;
import listeners.RListener;
import listeners.RoiListener;
import listeners.SaveListener;
import listeners.SaverDirectory;
import listeners.TimeListener;
import listeners.TlocListener;
import listeners.ZListener;
import listeners.ZlocListener;
import mpicbg.imglib.util.Util;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.algorithm.ransac.RansacModels.DisplayasROI;
import net.imglib2.algorithm.ransac.RansacModels.Ellipsoid;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;
import pluginTools.InteractiveEllipseFit.ValueChange;

import utility.NearestRoi;
import utility.Roiobject;
import utility.ShowResultView;
import utility.ShowView;
import utility.Slicer;
import utility.TrackModel;

public class InteractiveEllipseFit extends JPanel implements PlugIn {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String usefolder = IJ.getDirectory("imagej");
	public String addToName = "EllipseFits";
	public final int scrollbarSize = 1000;
	public int tablesize;
	public Overlay overlay;
	public Overlay emptyoverlay;
	public int thirdDimensionslider = 1;
	public int thirdDimensionsliderInit = 1;
	public int fourthDimensionslider = 1;
	public int fourthDimensionsliderInit = 1;
	public int rowchoice;
	public int radiusdetection = 5;
	public int maxtry = 30;
	public float minpercent = 0.65f;
	public float minpercentINI = 0.65f;
	public final double minSeperation = 5;

	public float insideCutoff = 5;
	public float outsideCutoff = 5;
	
	public float insideCutoffmin = 5;
	public float outsideCutoffmin = 5;
	
	
	public float insideCutoffmax = 50;
	public float outsideCutoffmax = 50;
	public int roiindex;
	public int fourthDimension;
	public int thirdDimension;
	public int thirdDimensionSize;
	public int fourthDimensionSize;
	public ImagePlus impA;
	public boolean isDone;
	public  int MIN_SLIDER = 0;
	public int MAX_SLIDER = 500;
	public int row;
	public HashMap<String, Integer> Accountedframes;
	public HashMap<String, Integer> AccountedZ;
	public JProgressBar jpb;
	public JLabel label = new JLabel("Fitting..");
	public int Progressmin = 0;
	public int Progressmax = 100;
	public int max = Progressmax;
	public File userfile;
	public Frame jFreeChartFrame;
	public NumberFormat nf;
	public XYSeriesCollection dataset;
	public JFreeChart chart;
	public RandomAccessibleInterval<FloatType> originalimg;
	ResultsTable rtAll;
	public File inputfile;
	public String inputdirectory;
	public int radiusInt = 2;
	public float radius = 50f;
	public int strokewidth = 1;
	public float radiusMin = radiusInt;
	public float radiusMax = 300f;
	public MouseMotionListener ml;
	public MouseListener mvl;
	public Roi nearestRoiCurr;
	public OvalRoi nearestIntersectionRoiCurr;
	public Roi selectedRoi;
	public TextField inputFieldIter;
	public JTable table;
	public ArrayList<Roi> Allrois;
	public HashMap<String, Roiobject> ZTRois;
	public HashMap<String, Roiobject> DefaultZTRois;
	public HashMap<String, Roiobject> IntersectionZTRois;
	public ImagePlus imp;
	public ImagePlus resultimp;
	public ImagePlus emptyimp;
	public int ndims;
	public RandomAccessibleInterval<FloatType> CurrentView;
	public RandomAccessibleInterval<FloatType> CurrentResultView;
	public Color confirmedRois = Color.BLUE;
	public Color defaultRois = Color.YELLOW;
	public Color colorChange = Color.GRAY;
	public Color colorInChange = Color.RED;
	public int maxlabel;
	public Color colorOval = Color.CYAN;
	public Color colorDet = Color.GREEN;
	public Color colorLineA = Color.YELLOW;
	public Color colorLineB = Color.YELLOW;
	public Color colorresult = Color.magenta;
	public double maxdistance = 10;
	ImageStack prestack;
	public MouseAdapter mouseadapter;

	public int[] Clickedpoints;
	public int starttime;
	public int endtime;
	public ArrayList<Pair<Integer, Intersectionobject>> Tracklist;
	public ArrayList<Pair<String, double[]>> resultAngle;
	public HashMap<String, ArrayList<double[]>> resultDraw;
	public KeyListener kl;
	public SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge> parentgraph;
	public HashMap<String, SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>> parentgraphZ;
	public HashMap<String, ArrayList<Intersectionobject>> ALLIntersections;
	ColorProcessor cp = null;
	public HashMap<String, Intersectionobject> Finalresult;
	public boolean isCreated = false;
	public RoiManager roimanager;
	public String uniqueID, tmpID, ZID, TID;
	public RandomAccessibleInterval<BitType> empty;
	public RandomAccessibleInterval<IntType> emptyWater;

	public static enum ValueChange {
		ROI, ALL, THIRDDIMmouse, FOURTHDIMmouse, DISPLAYROI, RADIUS, INSIDE, OUTSIDE, RESULT
	}

	public void setTime(final int value) {
		thirdDimensionslider = value;
		thirdDimensionsliderInit = 1;
		thirdDimension = 1;
	}

	public int getTimeMax() {

		return thirdDimensionSize;
	}

	public void setZ(final int value) {
		fourthDimensionslider = value;
		fourthDimensionsliderInit = 1;
		fourthDimension = 1;
	}

	public void setInsidecut(final int value) {

		insideCutoff = value;
	}

	public void setOutsidecut(final int value) {

		outsideCutoff = value;
	}

	public int getZMax() {

		return fourthDimensionSize;
	}

	public InteractiveEllipseFit() {
		nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setMaximumFractionDigits(3);
		this.dataset = new XYSeriesCollection();
		this.chart = utility.ChartMaker.makeChart(dataset, "Angle evolution", "Timepoint", "Angle");
		this.jFreeChartFrame = utility.ChartMaker.display(chart, new Dimension(500, 500));
		this.jFreeChartFrame.setVisible(false);
	}

	public InteractiveEllipseFit(RandomAccessibleInterval<FloatType> originalimg, File file) {
		this.inputfile = file;
		this.inputdirectory = file.getParent();
		this.originalimg = originalimg;
		this.ndims = originalimg.numDimensions();
		this.dataset = new XYSeriesCollection();
		this.chart = utility.ChartMaker.makeChart(dataset, "Angle evolution", "Timepoint", "Angle");
		this.jFreeChartFrame = utility.ChartMaker.display(chart, new Dimension(500, 500));
		this.jFreeChartFrame.setVisible(false);
		nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setMaximumFractionDigits(3);
	}

	public InteractiveEllipseFit(RandomAccessibleInterval<FloatType> originalimg) {
		this.inputfile = null;
		this.inputdirectory = null;
		this.originalimg = originalimg;
		this.ndims = originalimg.numDimensions();
		this.dataset = new XYSeriesCollection();
		this.chart = utility.ChartMaker.makeChart(dataset, "Angle evolution", "Timepoint", "Angle");
		this.jFreeChartFrame = utility.ChartMaker.display(chart, new Dimension(500, 500));
		this.jFreeChartFrame.setVisible(false);
		nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setMaximumFractionDigits(3);
	}

	public void run(String arg0) {
		rtAll = new ResultsTable();
		jpb = new JProgressBar();
		Allrois = new ArrayList<Roi>();
		ZTRois = new HashMap<String, Roiobject>();
		DefaultZTRois = new HashMap<String, Roiobject>();
		IntersectionZTRois = new HashMap<String, Roiobject>();
		Clickedpoints = new int[ndims];
		ALLIntersections = new HashMap<String, ArrayList<Intersectionobject>>();
		Finalresult = new HashMap<String, Intersectionobject>();
		Tracklist = new ArrayList<Pair<Integer, Intersectionobject>>();
		resultDraw = new HashMap<String, ArrayList<double[]>>();
		Accountedframes = new HashMap<String, Integer>();
		AccountedZ= new HashMap<String, Integer>();
		if (ndims < 3) {

			thirdDimensionSize = 0;
			fourthDimensionSize = 0;
		}

		if (ndims == 3) {

			fourthDimension = 1;
			thirdDimension = 1;
			fourthDimensionSize = 0;

			thirdDimensionSize = (int) originalimg.dimension(2);

		}

		if (ndims == 4) {

			fourthDimension = 1;
			thirdDimension = 1;

			thirdDimensionSize = (int) originalimg.dimension(2);
			fourthDimensionSize = (int) originalimg.dimension(3);

			prestack = new ImageStack((int) originalimg.dimension(0), (int) originalimg.dimension(1),
					java.awt.image.ColorModel.getRGBdefault());
		}

		if (ndims > 4) {

			System.out.println("Image has wrong dimensionality, upload an XYZT/XYT/XYZ/XY image");
			return;
		}

		setTime(fourthDimension);
		setZ(thirdDimension);
		CurrentView = utility.Slicer.getCurrentView(originalimg, fourthDimension, thirdDimensionSize, thirdDimension,
				fourthDimensionSize);

		imp = ImageJFunctions.show(CurrentView);
		imp.setTitle("Active image" + " " + "time point : " + fourthDimension + " " + " Z: " + thirdDimension);

		// Create empty Hyperstack

		empty = new ArrayImgFactory<BitType>().create(originalimg, new BitType());
		emptyWater = new ArrayImgFactory<IntType>().create(originalimg, new IntType());

		roimanager = RoiManager.getInstance();

		if (roimanager == null) {
			roimanager = new RoiManager();
		}

		updatePreview(ValueChange.ALL);

		Cardframe.repaint();
		Cardframe.validate();
		panelFirst.repaint();
		panelFirst.validate();

		Card();
	}

	public void updatePreview(final ValueChange change) {

		roimanager = RoiManager.getInstance();

		if (roimanager == null) {
			roimanager = new RoiManager();
		}

		//
		uniqueID = Integer.toString(thirdDimension) + Integer.toString(fourthDimension);
		ZID = Integer.toString(thirdDimension);
		TID = Integer.toString(fourthDimension);
		tmpID = Float.toString(thirdDimension) + Float.toString(fourthDimension);
		overlay = imp.getOverlay();

		if (overlay == null) {

			overlay = new Overlay();
			imp.setOverlay(overlay);
		}

		if (change == ValueChange.INSIDE || change == ValueChange.OUTSIDE) {

			StartComputing();

		}

		if (change == ValueChange.RESULT) {
			prestack = new ImageStack((int) originalimg.dimension(0), (int) originalimg.dimension(1),
					java.awt.image.ColorModel.getRGBdefault());

			String ID = (String) table.getValueAt(rowchoice, 0);
			ArrayList<double[]> resultlist = new ArrayList<double[]>();
			for (Pair<Integer, Intersectionobject> currentangle : Tracklist) {

				if (Integer.parseInt(ID) == currentangle.getA()) {

					resultlist.add(new double[] { currentangle.getB().t, currentangle.getB().z,
							currentangle.getB().Intersectionpoint[0], currentangle.getB().Intersectionpoint[1] });

				}

			}
			resultDraw.put(ID, resultlist);

			resultimp = ImageJFunctions.show(Slicer.getCurrentViewLarge(originalimg, thirdDimension));
			for (int time = 1; time <= fourthDimensionSize; ++time)
				prestack.addSlice(resultimp.getImageStack().getProcessor(time).convertToRGB());

			for (double[] current : resultDraw.get(ID)) {
				Overlay resultoverlay = new Overlay();
				int time = (int) current[0];
				int Z = (int) current[1];
				double IntersectionX = current[2];
				double IntersectionY = current[3];
				int radius = 3;
				ShowResultView showcurrent = new ShowResultView(this, time, Z);
				showcurrent.shownew();

				cp = (ColorProcessor) (prestack.getProcessor(time).duplicate());
				cp.reset();

				resultimp.setOverlay(resultoverlay);

				OvalRoi selectedRoi = new OvalRoi(Util.round(IntersectionX - radius),
						Util.round(IntersectionY - radius), Util.round(2 * radius), Util.round(2 * radius));
				resultoverlay.add(selectedRoi);

				cp.setColor(colorresult);
				cp.setLineWidth(4);
				cp.draw(selectedRoi);

				if (prestack != null)
					prestack.setPixels(cp.getPixels(), time);

			}

			new ImagePlus("Overlays", prestack).show();
			resultimp.close();

		}

		

		if (change == ValueChange.ROI) {

			
			IJ.run("Select None");
			DefaultZTRois.clear();
			// roimanager.runCommand("show all");
			Roi[] Rois = roimanager.getRoisAsArray();
			Roiobject CurrentRoi = new Roiobject(Rois, thirdDimension, fourthDimension, true);

			
			DefaultZTRois.put(uniqueID, CurrentRoi);
			
			
		

				Accountedframes.put(TID, fourthDimension);
				
				AccountedZ.put(ZID, thirdDimension);

				System.out.println(AccountedZ.size());
				ZTRois.put(uniqueID, CurrentRoi);

			

				Display();

		}

		if (change == ValueChange.THIRDDIMmouse) {

			if (imp == null) {
				imp = ImageJFunctions.show(CurrentView);

			}

			else {

				final float[] pixels = (float[]) imp.getProcessor().getPixels();
				final Cursor<FloatType> c = Views.iterable(CurrentView).cursor();

				for (int i = 0; i < pixels.length; ++i)
					pixels[i] = c.next().get();

				imp.updateAndDraw();

			}

			if (ZTRois.get(uniqueID) == null)
				DisplayDefault();
			else
				Display();
			imp.setTitle("Active image" + " " + "time point : " + fourthDimension + " " + " Z: " + thirdDimension);

		}

		if (change == ValueChange.FOURTHDIMmouse) {

			if (imp == null) {
				imp = ImageJFunctions.show(CurrentView);

			}

			else {

				final float[] pixels = (float[]) imp.getProcessor().getPixels();
				final Cursor<FloatType> c = Views.iterable(CurrentView).cursor();

				for (int i = 0; i < pixels.length; ++i)
					pixels[i] = c.next().get();

				imp.updateAndDraw();

			}

			if (ZTRois.get(uniqueID) == null) {
				DisplayDefault();

			} else
				Display();
			imp.setTitle("Active image" + " " + "time point : " + fourthDimension + " " + " Z: " + thirdDimension);

		}

	}

	public void StartComputing() {

		ComputeAngles compute = new ComputeAngles(this, jpb);

		compute.execute();

	}

	public void Display() {

		overlay.clear();

		if (ZTRois.size() > 0) {

			for (Map.Entry<String, Roiobject> entry : ZTRois.entrySet()) {

				Roiobject currentobject = entry.getValue();

				if (currentobject.fourthDimension == fourthDimension
						&& currentobject.thirdDimension == thirdDimension) {

					for (int indexx = 0; indexx < currentobject.roilist.length; ++indexx) {

						Roi or = currentobject.roilist[indexx];
						or.setStrokeColor(confirmedRois);
						overlay.add(or);
					}

					if (currentobject.resultroi != null) {

						for (int i = 0; i < currentobject.resultroi.size(); ++i) {

							EllipseRoi ellipse = currentobject.resultroi.get(i);
							ellipse.setStrokeColor(colorInChange);
							overlay.add(ellipse);

						}

					}

					if (currentobject.resultovalroi != null) {

						for (int i = 0; i < currentobject.resultovalroi.size(); ++i) {

							OvalRoi ellipse = currentobject.resultovalroi.get(i);
							ellipse.setStrokeColor(colorDet);
							overlay.add(ellipse);

						}

					}

					if (currentobject.resultlineroi != null) {

						for (int i = 0; i < currentobject.resultlineroi.size(); ++i) {

							Line ellipse = currentobject.resultlineroi.get(i);
							ellipse.setStrokeColor(colorLineA);

							overlay.add(ellipse);

						}

					}

					break;
				}

			}
			imp.updateAndDraw();

			mark();
			select();

		}
	}

	public void DisplayOnly() {

		overlay.clear();

		if (ZTRois.size() > 0) {

			for (Map.Entry<String, Roiobject> entry : ZTRois.entrySet()) {

				Roiobject currentobject = entry.getValue();

				if (currentobject.fourthDimension == fourthDimension
						&& currentobject.thirdDimension == thirdDimension) {

					for (int indexx = 0; indexx < currentobject.roilist.length; ++indexx) {

						Roi or = currentobject.roilist[indexx];

						if (or == nearestRoiCurr) {

							or.setStrokeColor(colorInChange);

						}

						overlay.add(or);
					}

					if (currentobject.resultroi != null) {

						for (int i = 0; i < currentobject.resultroi.size(); ++i) {

							EllipseRoi ellipse = currentobject.resultroi.get(i);
							ellipse.setStrokeColor(colorInChange);
							overlay.add(ellipse);

						}

					}
					if (currentobject.resultovalroi != null) {

						for (int i = 0; i < currentobject.resultovalroi.size(); ++i) {

							OvalRoi ellipse = currentobject.resultovalroi.get(i);
							ellipse.setStrokeColor(colorDet);
							overlay.add(ellipse);

						}

					}

					if (currentobject.resultlineroi != null) {

						for (int i = 0; i < currentobject.resultlineroi.size(); ++i) {

							Line ellipse = currentobject.resultlineroi.get(i);
							ellipse.setStrokeColor(colorLineA);
							overlay.add(ellipse);

						}

					}
					break;
				}

			}
			imp.updateAndDraw();

		}
	}

	public void DisplayDefault() {

		overlay.clear();
		if (DefaultZTRois.size() > 0) {

			for (Map.Entry<String, Roiobject> entry : DefaultZTRois.entrySet()) {

				Roiobject currentobject = entry.getValue();

				for (int indexx = 0; indexx < currentobject.roilist.length; ++indexx) {

					Roi or = currentobject.roilist[indexx];
					or.setStrokeColor(defaultRois);
					overlay.add(or);
				}

				break;

			}
			imp.updateAndDraw();
			mark();
			select();

		}
	}

	public void select() {

		if (mvl != null)
			imp.getCanvas().removeMouseListener(mvl);
		imp.getCanvas().addMouseListener(mvl = new MouseListener() {

			final ImageCanvas canvas = imp.getWindow().getCanvas();

			@Override
			public void mouseClicked(MouseEvent e) {

				int x = canvas.offScreenX(e.getX());
				int y = canvas.offScreenY(e.getY());

				Clickedpoints[0] = x;
				Clickedpoints[1] = y;
				if (SwingUtilities.isLeftMouseButton(e) && !e.isShiftDown() && e.isAltDown()) {

					roiindex = roimanager.getRoiIndex(nearestRoiCurr);
					roimanager.select(roiindex);
					nearestRoiCurr.setStrokeColor(colorChange);

					imp.updateAndDraw();


				}

				if (SwingUtilities.isLeftMouseButton(e) && e.isShiftDown()) {
					if (!jFreeChartFrame.isVisible())
						jFreeChartFrame = utility.ChartMaker.display(chart, new Dimension(500, 500));

					displayclicked(rowchoice);
				}

			}

			@Override
			public void mousePressed(MouseEvent e) {

			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}
		});

	}

	public void mark() {

		if (ml != null)
			imp.getCanvas().removeMouseMotionListener(ml);
		imp.getCanvas().addMouseMotionListener(ml = new MouseMotionListener() {

			final ImageCanvas canvas = imp.getWindow().getCanvas();
			Roi lastnearest = null;

			@Override
			public void mouseMoved(MouseEvent e) {

				int x = canvas.offScreenX(e.getX());
				int y = canvas.offScreenY(e.getY());

				final HashMap<Integer, double[]> loc = new HashMap<Integer, double[]>();

				loc.put(0, new double[] { x, y });

				Color roicolor;
				Roiobject currentobject;
				if (ZTRois.get(uniqueID) == null) {
					roicolor = defaultRois;

					currentobject = DefaultZTRois.entrySet().iterator().next().getValue();

				} else {
					roicolor = confirmedRois;

					currentobject = ZTRois.get(uniqueID);

				}
				nearestRoiCurr = NearestRoi.getNearestRois(currentobject, loc.get(0), InteractiveEllipseFit.this);

				if (nearestRoiCurr != null) {
					nearestRoiCurr.setStrokeColor(colorChange);

					if (lastnearest != nearestRoiCurr && lastnearest != null)
						lastnearest.setStrokeColor(roicolor);

					lastnearest = nearestRoiCurr;

					imp.updateAndDraw();
				}

				double distmin = Double.MAX_VALUE;
				if (tablesize > 0) {

					for (int row = 0; row < tablesize; ++row) {
						String CordX = (String) table.getValueAt(row, 1);
						String CordY = (String) table.getValueAt(row, 2);
						
						String CordZ = (String) table.getValueAt(row, 5);

						double dCordX = Double.parseDouble(CordX);
						double dCordY = Double.parseDouble(CordY);
						double dCordZ = Double.parseDouble(CordZ);
						
						double dist = Distance.DistanceSq(new double[] { dCordX, dCordY }, new double[] { x, y });
						if (Distance.DistanceSq(new double[] { dCordX, dCordY }, new double[] { x, y }) < distmin && thirdDimension == (int)dCordZ) {

							rowchoice = row;
							distmin = dist;

						}

					}

					table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
						@Override
						public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
								boolean hasFocus, int row, int col) {

							super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
							if (row == rowchoice) {
								setBackground(Color.green);

							} else {
								setBackground(Color.white);
							}
							return this;
						}
					});

					table.validate();
					scrollPane.validate();
					panelFirst.repaint();
					panelFirst.validate();

				}

			}

			@Override
			public void mouseDragged(MouseEvent e) {

			}

		});

	}

	public JFrame Cardframe = new JFrame("Ellipsoid detector");
	public JPanel panelCont = new JPanel();
	public JPanel panelFirst = new JPanel();
	public JPanel Timeselect = new JPanel();
	public JPanel Zselect = new JPanel();
	public JPanel Roiselect = new JPanel();
	public JPanel Angleselect = new JPanel();

	public TextField inputFieldT;
	public TextField inputFieldZ;
	public TextField inputFieldmaxtry;
	public TextField inputFieldminpercent;
	public TextField inputFieldmaxellipse;

	public Label inputLabelmaxellipse;
	public Label inputLabelminpercent;
	public Label inputLabelIter;
	public JPanel Original = new JPanel();
	public int SizeX = 400;
	public int SizeY = 200;

	public JButton Roibutton = new JButton("Confirm current roi selection");
	public JButton DisplayRoibutton = new JButton("Display roi selection");
	public JButton Anglebutton = new JButton("Intersection points and angles");
	public JButton Savebutton = new JButton("Save Track");

	public Label timeText = new Label("Current T = " + 1, Label.CENTER);
	public Label zText = new Label("Current Z = " + 1, Label.CENTER);
	final Label rText = new Label("Left Click selects a Roi");
	final Label contText = new Label("After making all roi selections");
	final Label insideText = new Label("Cutoff distance for points belonging to ellipse = " + insideCutoff,
			Label.CENTER);
	final Label outsideText = new Label("Cutoff distance for points outside ellipse = " + outsideCutoff, Label.CENTER);

	final String timestring = "Current T";
	final String zstring = "Current Z";
	final String rstring = "Radius";
	final String insidestring = "Cutoff distance for points inside ellipse";
	final String outsidestring = "Cutoff distance for points outside ellipse";

	public final Insets insets = new Insets(10, 0, 0, 0);
	public final GridBagLayout layout = new GridBagLayout();
	public final GridBagConstraints c = new GridBagConstraints();
	public JScrollPane scrollPane;
	public JFileChooser chooserA = new JFileChooser();
	public String choosertitleA;
	public JScrollBar timeslider = new JScrollBar(Scrollbar.HORIZONTAL, fourthDimensionsliderInit, 10, 0,
			scrollbarSize + 10);
	public JScrollBar zslider = new JScrollBar(Scrollbar.HORIZONTAL, thirdDimensionsliderInit, 10, 0,
			10 + scrollbarSize);
	public JScrollBar rslider = new JScrollBar(Scrollbar.HORIZONTAL, radiusInt, 10, 0, 10 + scrollbarSize);
	public JScrollBar insideslider = new JScrollBar(Scrollbar.HORIZONTAL, 0, 10, 0, 10 + scrollbarSize);
	public JScrollBar outsideslider = new JScrollBar(Scrollbar.HORIZONTAL, 0, 10, 0, 10 + scrollbarSize);
	JPanel PanelSelectFile = new JPanel();
	Border selectfile = new CompoundBorder(new TitledBorder("Select Track"), new EmptyBorder(c.insets));
	public JLabel inputLabel = new JLabel("Filename:");
	public TextField inputField = new TextField();
	final JButton ChooseDirectory = new JButton("Choose Directory to save results in");
	public JComboBox<String> ChooseMethod;
	Border origborder = new CompoundBorder(new TitledBorder("Enter filename for results files"),
			new EmptyBorder(c.insets));

	public void Card() {
		CardLayout cl = new CardLayout();

		c.insets = new Insets(5, 5, 5, 5);
		panelCont.setLayout(cl);

		panelCont.add(panelFirst, "1");

		panelFirst.setName("Angle Tool for ellipsoids");

		panelFirst.setLayout(layout);

		Timeselect.setLayout(layout);

		Zselect.setLayout(layout);
		Original.setLayout(layout);
		Roiselect.setLayout(layout);

		Angleselect.setLayout(layout);

		inputFieldZ = new TextField();
		inputFieldZ = new TextField(5);
		inputFieldZ.setText(Integer.toString(thirdDimension));

		inputField.setColumns(10);

		inputFieldT = new TextField();
		inputFieldT = new TextField(5);
		inputFieldT.setText(Integer.toString(fourthDimension));

		inputFieldIter = new TextField();
		inputFieldIter = new TextField(5);
		inputFieldIter.setText(Integer.toString(maxtry));

		inputLabelIter = new Label("Max. attempts to find ellipses");

		String[] DrawType = { "Closed Loops", "Arcs" };
		ChooseMethod = new JComboBox<String>(DrawType);

		inputLabelmaxellipse = new Label("Max. number of ellipses");

		inputFieldminpercent = new TextField();
		inputFieldminpercent = new TextField(5);
		inputFieldminpercent.setText(Float.toString(minpercent));

		inputLabelminpercent = new Label("Min. percent points to lie on ellipse");

		Object[] colnames = new Object[] { "Track Id", "Location X", "Location Y", "Starting Angle", "Start time",
				"Start Z" };

		Object[][] rowvalues = new Object[0][colnames.length];
		if (Finalresult != null && Finalresult.size() > 0) {

			rowvalues = new Object[Finalresult.size()][colnames.length];

		}

		table = new JTable(rowvalues, colnames);
		Border timeborder = new CompoundBorder(new TitledBorder("Select time"), new EmptyBorder(c.insets));
		Border zborder = new CompoundBorder(new TitledBorder("Select Z"), new EmptyBorder(c.insets));
		Border roitools = new CompoundBorder(new TitledBorder("Roi and ellipse finder tools"),
				new EmptyBorder(c.insets));
		Border origborder = new CompoundBorder(new TitledBorder("Enter filename for results files"),
				new EmptyBorder(c.insets));
		Border ellipsetools = new CompoundBorder(new TitledBorder("Ransac and Angle computer"),
				new EmptyBorder(c.insets));
		c.anchor = GridBagConstraints.BOTH;
		c.ipadx = 35;

		c.gridwidth = 10;
		c.gridheight = 10;
		c.gridy = 1;
		c.gridx = 0;

		if (ndims >= 3) {

			// Put time slider

			Timeselect.add(timeText, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));

			Timeselect.add(timeslider, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));

			Timeselect.add(inputFieldT, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));

			Timeselect.setBorder(timeborder);
			Timeselect.setMinimumSize(new Dimension(SizeX, SizeY));
			Timeselect.setPreferredSize(new Dimension(SizeX, SizeY));
			panelFirst.add(Timeselect, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));

		}

		if (ndims > 3) {

			// Put z slider

			Zselect.add(zText, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));

			Zselect.add(zslider, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));

			Zselect.add(inputFieldZ, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));

			Zselect.setBorder(zborder);
			Zselect.setMinimumSize(new Dimension(SizeX, SizeY));
			Zselect.setPreferredSize(new Dimension(SizeX, SizeY));
			panelFirst.add(Zselect, new GridBagConstraints(3, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));

		}

		Roiselect.add(rText, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		Roiselect.add(ChooseMethod, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		Roiselect.add(Roibutton, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Roiselect.setBorder(roitools);

		panelFirst.add(Roiselect, new GridBagConstraints(0, 1, 5, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Angleselect.add(inputLabelIter, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Angleselect.add(inputFieldIter, new GridBagConstraints(4, 2, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.RELATIVE, insets, 0, 0));

		Angleselect.add(inputLabelminpercent, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Angleselect.add(inputFieldminpercent, new GridBagConstraints(4, 3, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.RELATIVE, insets, 0, 0));
		Angleselect.add(insideText, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Angleselect.add(insideslider, new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Angleselect.add(Anglebutton, new GridBagConstraints(0, 8, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		Angleselect.setBorder(ellipsetools);
		Angleselect.setMinimumSize(new Dimension(SizeX, SizeY));
		panelFirst.add(Angleselect, new GridBagConstraints(5, 1, 5, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		table = new JTable(rowvalues, colnames);

		table.setFillsViewportHeight(true);

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		table.setMinimumSize(new Dimension(500, 300));
		table.setPreferredSize(new Dimension(500, 200));

		scrollPane = new JScrollPane(table);
		scrollPane.setMinimumSize(new Dimension(300, 200));
		scrollPane.setPreferredSize(new Dimension(300, 200));

		scrollPane.getViewport().add(table);
		scrollPane.setAutoscrolls(true);

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		table.setMinimumSize(new Dimension(500, 300));
		table.setPreferredSize(new Dimension(500, 200));

		scrollPane = new JScrollPane(table);
		scrollPane.setMinimumSize(new Dimension(300, 200));
		scrollPane.setPreferredSize(new Dimension(300, 200));

		scrollPane.getViewport().add(table);
		scrollPane.setAutoscrolls(true);
		PanelSelectFile.add(scrollPane, BorderLayout.CENTER);

		PanelSelectFile.setBorder(selectfile);

		panelFirst.add(PanelSelectFile, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.RELATIVE, new Insets(10, 10, 0, 10), 0, 0));

		Original.add(inputLabel, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		Original.add(inputField, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		Original.add(ChooseDirectory, new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		Original.add(Savebutton, new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		Original.setBorder(origborder);

		Original.setMinimumSize(new Dimension(SizeX, SizeY));
		Original.setPreferredSize(new Dimension(SizeX, SizeY));
		panelFirst.add(Original, new GridBagConstraints(3, 3, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		timeslider.addAdjustmentListener(new TimeListener(this, timeText, timestring, fourthDimensionsliderInit,
				fourthDimensionSize , scrollbarSize, timeslider));
		zslider.addAdjustmentListener(new ZListener(this, zText, zstring, thirdDimensionsliderInit, thirdDimensionSize ,
				scrollbarSize, zslider));
		rslider.addAdjustmentListener(
				new RListener(this, rText, rstring, radiusMin, radiusMax, scrollbarSize, rslider));

		insideslider.addAdjustmentListener(
				new InsideCutoffListener(this, insideText, insidestring, insideCutoffmin, insideCutoffmax, scrollbarSize, insideslider));
		outsideslider.addAdjustmentListener(
				new OutsideCutoffListener(this, outsideText, outsidestring, outsideCutoffmin, outsideCutoffmax, scrollbarSize, outsideslider));

		Anglebutton.addActionListener(new AngleListener(this));
		Roibutton.addActionListener(new RoiListener(this));
		inputFieldZ.addTextListener(new ZlocListener(this, false));
		inputFieldT.addTextListener(new TlocListener(this, false));
		inputFieldminpercent.addTextListener(new MinpercentListener(this));
		inputFieldIter.addTextListener(new MaxTryListener(this));
		ChooseDirectory.addActionListener(new SaverDirectory(this));
		inputField.addTextListener(new FilenameListener(this));
		Savebutton.addActionListener(new SaveListener(this));
		ChooseMethod.addActionListener(new DrawListener(this, ChooseMethod));
		panelFirst.setMinimumSize(new Dimension(SizeX, SizeY));

		panelFirst.setVisible(true);
		cl.show(panelCont, "1");
		Cardframe.add(panelCont, "Center");
		Cardframe.add(jpb, "Last");

		Cardframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Cardframe.pack();
		Cardframe.setVisible(true);
	}

	public void displayclicked(int trackindex) {

		// Make something happen
		row = trackindex;

		String ID = (String) table.getValueAt(trackindex, 0);

		resultAngle = new ArrayList<Pair<String, double[]>>();

		for (Pair<Integer, Intersectionobject> currentangle : Tracklist) {

			if (Integer.parseInt(ID) == currentangle.getA())
				resultAngle.add(new ValuePair<String, double[]>(ID,
						new double[] { currentangle.getB().t, currentangle.getB().angle }));

		}

		this.dataset.removeAllSeries();

		this.dataset.addSeries(utility.ChartMaker.drawPoints(resultAngle));
		utility.ChartMaker.setColor(chart, 0, new Color(255, 64, 64));
		utility.ChartMaker.setStroke(chart, 0, 2f);
		if (fourthDimensionSize > 0)
			updatePreview(ValueChange.RESULT);

	}

	public static int round(double value) {
		return (int) (value + 0.5D * Math.signum(value));
	}

	public static void main(String[] args) {
		new ImageJ();
		ImagePlus impA = new Opener().openImage("/Users/varunkapoor/Documents/JLMData/Hyperstack_ML7.tif");
		impA.show();
		JFrame frame = new JFrame("");
		EllipseFileChooser panel = new EllipseFileChooser();

		frame.getContentPane().add(panel, "Center");
		frame.setSize(panel.getPreferredSize());
	}

}
