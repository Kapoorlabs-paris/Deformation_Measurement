package pluginTools;

import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
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
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeriesCollection;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.EllipseRoi;
import ij.gui.Line;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.io.Opener;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.plugin.frame.RoiManager;
import listeners.AngleListener;
import listeners.DisplayRoiListener;
import listeners.EllipseNonStandardMouseListener;
import listeners.InsideCutoffListener;
import listeners.OutsideCutoffListener;
import listeners.RListener;
import listeners.RoiListener;
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
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.view.Views;
import pluginTools.InteractiveEllipseFit.ValueChange;
import utility.MarkNew;
import utility.Roiobject;
import utility.SelectNew;
import utility.ShowView;

public class InteractiveEllipseFit implements PlugIn {

	public String usefolder = IJ.getDirectory("imagej");
	public String addToName = "EllipseFits";
	public final int scrollbarSize = 10000;

	public Overlay overlay;
	public Overlay emptyoverlay;
	public int thirdDimensionslider = 1;
	public int thirdDimensionsliderInit = 1;
	public int fourthDimensionslider = 1;
	public int fourthDimensionsliderInit = 1;

	public int radiusdetection = 5;
	public int maxtry = 30;
	public float minpercent = 0.65f;
	public final double minSeperation = 5;
	public int maxEllipses = 15;

	public float insideCutoff = 5;
	public float outsideCutoff = 5;

	public int fourthDimension;
	public int thirdDimension;
	public int thirdDimensionSize;
	public int fourthDimensionSize;

	public boolean isDone;
	public static int MIN_SLIDER = 0;
	public static int MAX_SLIDER = 500;
	public int row;

	public JProgressBar jpb;
	public JLabel label = new JLabel("Fitting..");
	public int Progressmin = 0;
	public int Progressmax = 100;
	public int max = Progressmax;
	public File userfile;
	Frame jFreeChartFrame;
	public NumberFormat nf;
	public XYSeriesCollection dataset;
	JFreeChart chart;
	public RandomAccessibleInterval<FloatType> originalimg;
	ResultsTable rtAll;
	public File inputfile;
	public String inputdirectory;

	public int radiusInt = 2;
	public float radius = 50f;
	public float radiusMin = radiusInt;
	public float radiusMax = 300f;
	public MouseMotionListener ml;
	public MouseListener mvl;
	public Roi nearestRoiCurr;
	public Roi selectedRoi;
	public TextField inputFieldIter;
	public JTable table;
	public ArrayList<Roi> Allrois;
	public HashMap<String, Roiobject> ZTRois;
	public HashMap<String, Roiobject> DefaultZTRois;
	public ImagePlus imp;
	public ImagePlus emptyimp;
	public int ndims;
	public RandomAccessibleInterval<FloatType> CurrentView;
	public Color confirmedRois = Color.BLUE;
	public Color defaultRois = Color.YELLOW;
	public Color colorChange = Color.PINK;
	public Color colorInChange = Color.RED;

	public Color colorDet = Color.GREEN;
	public Color colorLineA = Color.YELLOW;
	public Color colorLineB = Color.YELLOW;
	public int[] Clickedpoints;

	public KeyListener kl;

	public boolean isCreated = false;
	public RoiManager roimanager;
	public String uniqueID, tmpID;
	public RandomAccessibleInterval<BitType> empty;

	public static enum ValueChange {
		ROI, ALL, THIRDDIMmouse, FOURTHDIMmouse, DISPLAYROI, RADIUS, INSIDE, OUTSIDE
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
	}

	public InteractiveEllipseFit(RandomAccessibleInterval<FloatType> originalimg, File file) {
		this.inputfile = file;
		this.inputdirectory = file.getParent();
		this.originalimg = originalimg;
		this.ndims = originalimg.numDimensions();

		nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setMaximumFractionDigits(3);
	}

	public InteractiveEllipseFit(RandomAccessibleInterval<FloatType> originalimg) {
		this.inputfile = null;
		this.inputdirectory = null;
		this.originalimg = originalimg;
		this.ndims = originalimg.numDimensions();

		nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setMaximumFractionDigits(3);
	}

	public void run(String arg0) {
		rtAll = new ResultsTable();
		jpb = new JProgressBar();
		Allrois = new ArrayList<Roi>();
		ZTRois = new HashMap<String, Roiobject>();
		DefaultZTRois = new HashMap<String, Roiobject>();
		Clickedpoints = new int[ndims];

		if (ndims < 3) {

			thirdDimensionSize = 0;
			fourthDimensionSize = 0;
		}

		if (ndims == 3) {

			fourthDimension = 1;
			fourthDimensionSize = 0;

			thirdDimensionSize = (int) originalimg.dimension(2);

		}

		if (ndims == 4) {

			fourthDimension = 1;
			thirdDimension = 1;

			thirdDimensionSize = (int) originalimg.dimension(2);
			fourthDimensionSize = (int) originalimg.dimension(3);
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

		Card();

		// Create empty Hyperstack

		empty = new ArrayImgFactory<BitType>().create(originalimg, new BitType());
		roimanager = RoiManager.getInstance();

		if (roimanager == null) {
			roimanager = new RoiManager();
		}

		updatePreview(ValueChange.ALL);
	}

	public void updatePreview(final ValueChange change) {

		roimanager = RoiManager.getInstance();

		if (roimanager == null) {
			roimanager = new RoiManager();
		}

		//
		uniqueID = Integer.toString(thirdDimension) + Integer.toString(fourthDimension);
		tmpID = Float.toString(thirdDimension) + Float.toString(fourthDimension);
		overlay = imp.getOverlay();

		if (overlay == null) {

			overlay = new Overlay();
			imp.setOverlay(overlay);
		}

		if (change == ValueChange.INSIDE || change == ValueChange.OUTSIDE) {

			StartComputing();

		}

		if (change == ValueChange.DISPLAYROI) {

			Roi[] Rois = roimanager.getRoisAsArray();
			Roiobject CurrentRoi = new Roiobject(Rois, thirdDimension, fourthDimension, true);

			if (ZTRois.get(tmpID) == null) {

				ZTRois.put(tmpID, CurrentRoi);

			} else {

				ZTRois.remove(tmpID);

				ZTRois.put(tmpID, CurrentRoi);

			}

			DisplayOnly();

		}

		if (change == ValueChange.ROI) {

			DefaultZTRois.clear();
			// roimanager.runCommand("show all");
			Roi[] Rois = roimanager.getRoisAsArray();
			Roiobject CurrentRoi = new Roiobject(Rois, thirdDimension, fourthDimension, true);

			DefaultZTRois.put(uniqueID, CurrentRoi);

			if (ZTRois.get(uniqueID) == null) {

				ZTRois.put(uniqueID, CurrentRoi);

			} else {

				ZTRois.remove(uniqueID);

				ZTRois.put(uniqueID, CurrentRoi);

			}

			if (ZTRois.get(uniqueID) == null)
				DisplayDefault();
			else
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

			if (ZTRois.get(uniqueID) == null)
				DisplayDefault();
			else
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
							ellipse.setStrokeColor(Color.RED);
							ellipse.setStrokeWidth(2);
							overlay.add(ellipse);

						}

					}

					if (currentobject.resultovalroi != null) {

						for (int i = 0; i < currentobject.resultovalroi.size(); ++i) {

							OvalRoi ellipse = currentobject.resultovalroi.get(i);
							ellipse.setStrokeColor(Color.GREEN);
							ellipse.setStrokeWidth(2);
							overlay.add(ellipse);

						}

					}

					if (currentobject.resultlineroi != null) {

						for (int i = 0; i < currentobject.resultlineroi.size(); ++i) {

							Line ellipse = currentobject.resultlineroi.get(i);
							ellipse.setStrokeColor(Color.YELLOW);
							ellipse.setStrokeWidth(2);
							overlay.add(ellipse);

						}

					}

					break;
				}

			}
			imp.updateAndDraw();

			MarkNew mark = new MarkNew(this);
			mark.mark();
			SelectNew show = new SelectNew(this);
			show.select();

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
							ellipse.setStrokeColor(Color.RED);
							ellipse.setStrokeWidth(2);
							overlay.add(ellipse);

						}

					}
					if (currentobject.resultovalroi != null) {

						for (int i = 0; i < currentobject.resultovalroi.size(); ++i) {

							OvalRoi ellipse = currentobject.resultovalroi.get(i);
							ellipse.setStrokeColor(Color.GREEN);
							ellipse.setStrokeWidth(2);
							overlay.add(ellipse);

						}

					}

					if (currentobject.resultlineroi != null) {

						for (int i = 0; i < currentobject.resultlineroi.size(); ++i) {

							Line ellipse = currentobject.resultlineroi.get(i);
							ellipse.setStrokeColor(Color.YELLOW);
							ellipse.setStrokeWidth(2);
							overlay.add(ellipse);

						}

					}
					break;
				}

			}
			imp.updateAndDraw();
			MarkNew mark = new MarkNew(this);
			mark.mark();
			SelectNew show = new SelectNew(this);
			show.select();

		}
	}

	public void DisplayDefault() {

		overlay.clear();

		if (DefaultZTRois.size() > 0) {

			for (Map.Entry<String, Roiobject> entry : DefaultZTRois.entrySet()) {

				Roiobject currentobject = entry.getValue();

				System.out.println(currentobject.fourthDimension + "" + currentobject.thirdDimension + ""
						+ currentobject.isCreated);

				for (int indexx = 0; indexx < currentobject.roilist.length; ++indexx) {

					Roi or = currentobject.roilist[indexx];
					or.setStrokeColor(defaultRois);
					overlay.add(or);
				}

				break;

			}
			imp.updateAndDraw();
			MarkNew mark = new MarkNew(this);
			mark.mark();

			SelectNew show = new SelectNew(this);
			show.select();

		}
	}

	public JFrame Cardframe = new JFrame("Ellipsoid detector");
	public JPanel panelCont = new JPanel();
	public JPanel panelFirst = new JPanel();
	private JPanel Timeselect = new JPanel();
	private JPanel Zselect = new JPanel();
	private JPanel Roiselect = new JPanel();
	private JPanel Angleselect = new JPanel();

	public TextField inputFieldT;
	public TextField inputFieldZ;
	public TextField inputFieldmaxtry;
	public TextField inputFieldminpercent;
	public TextField inputFieldmaxellipse;

	public Label inputLabelmaxellipse;
	public Label inputLabelminpercent;
	public Label inputLabelIter;

	private int SizeX = 400;
	private int SizeY = 200;

	public JButton Roibutton = new JButton("Confirm current roi selection");
	public JButton DisplayRoibutton = new JButton("Display roi selection");
	public JButton Anglebutton = new JButton("Intersection points and angles");

	public Label timeText = new Label("Current time point = " + 1, Label.CENTER);
	public Label zText = new Label("Current Z location = " + 1, Label.CENTER);
	final Label rText = new Label("Left Click selects a Roi");
	final Label contText = new Label("After making all roi selections");
	final Label insideText = new Label("Cutoff distance for points inside ellipse = " + insideCutoff, Label.CENTER);
	final Label outsideText = new Label("Cutoff distance for points outside ellipse = " + outsideCutoff, Label.CENTER);

	final String timestring = "Current Time point";
	final String zstring = "Current Z location";
	final String rstring = "Radius";
	final String insidestring = "Cutoff distance for points inside ellipse";
	final String outsidestring = "Cutoff distance for points outside ellipse";

	public static final Insets insets = new Insets(10, 0, 0, 0);
	public final GridBagLayout layout = new GridBagLayout();
	public final GridBagConstraints c = new GridBagConstraints();
	public JScrollPane scrollPane;
	public JFileChooser chooserA;
	public String choosertitleA;
	public JScrollBar timeslider = new JScrollBar(Scrollbar.HORIZONTAL, thirdDimensionsliderInit, 10, 0,
			10 + scrollbarSize);
	public JScrollBar zslider = new JScrollBar(Scrollbar.HORIZONTAL, fourthDimensionsliderInit, 10, 0,
			10 + scrollbarSize);
	public JScrollBar rslider = new JScrollBar(Scrollbar.HORIZONTAL, radiusInt, 10, 0, 10 + scrollbarSize);
	public JScrollBar insideslider = new JScrollBar(Scrollbar.HORIZONTAL, 0, 10, 0, 10 + scrollbarSize);
	public JScrollBar outsideslider = new JScrollBar(Scrollbar.HORIZONTAL, 0, 10, 0, 10 + scrollbarSize);

	public void Card() {
		CardLayout cl = new CardLayout();
		c.insets = new Insets(5, 5, 5, 5);
		panelCont.setLayout(cl);

		panelCont.add(panelFirst, "1");

		panelFirst.setName("Angle Tool for ellipsoids");

		panelFirst.setLayout(layout);

		Timeselect.setLayout(layout);

		Zselect.setLayout(layout);

		Roiselect.setLayout(layout);

		Angleselect.setLayout(layout);

		inputFieldZ = new TextField();
		inputFieldZ = new TextField(5);
		inputFieldZ.setText(Integer.toString(thirdDimension));

		inputFieldT = new TextField();
		inputFieldT = new TextField(5);
		inputFieldT.setText(Integer.toString(fourthDimension));

		inputFieldIter = new TextField();
		inputFieldIter = new TextField(5);
		inputFieldIter.setText(Integer.toString(maxtry));

		inputLabelIter = new Label("Max. attempts to find ellipses");

		inputFieldmaxellipse = new TextField();
		inputFieldmaxellipse = new TextField(5);
		inputFieldmaxellipse.setText(Integer.toString(maxEllipses));

		inputLabelmaxellipse = new Label("Max. number of ellipses");

		inputFieldminpercent = new TextField();
		inputFieldminpercent = new TextField(5);
		inputFieldminpercent.setText(Float.toString(minpercent));

		inputLabelminpercent = new Label("Min. percent points to lie on ellipse");

		Border timeborder = new CompoundBorder(new TitledBorder("Select time"), new EmptyBorder(c.insets));
		Border zborder = new CompoundBorder(new TitledBorder("Select Z"), new EmptyBorder(c.insets));
		Border roitools = new CompoundBorder(new TitledBorder("Roi and ellipse finder tools"),
				new EmptyBorder(c.insets));

		Border ellipsetools = new CompoundBorder(new TitledBorder("Ransac and Angle computer"),
				new EmptyBorder(c.insets));

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
			panelFirst.add(Zselect, new GridBagConstraints(5, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));

		}

		Roiselect.add(rText, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Roiselect.add(Roibutton, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Roiselect.add(inputLabelIter, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Roiselect.add(inputFieldIter, new GridBagConstraints(4, 2, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.RELATIVE, insets, 0, 0));

		Roiselect.add(inputLabelminpercent, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Roiselect.add(inputFieldminpercent, new GridBagConstraints(4, 3, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.RELATIVE, insets, 0, 0));
		Roiselect.setBorder(roitools);
		panelFirst.add(Roiselect, new GridBagConstraints(0, 1, 5, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Angleselect.add(insideText, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Angleselect.add(insideslider, new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Angleselect.add(outsideText, new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Angleselect.add(outsideslider, new GridBagConstraints(0, 7, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Angleselect.add(Anglebutton, new GridBagConstraints(0, 8, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		Angleselect.setBorder(ellipsetools);
		Angleselect.setMinimumSize(new Dimension(SizeX, SizeY));
		panelFirst.add(Angleselect, new GridBagConstraints(5, 1, 5, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		timeslider.addAdjustmentListener(new TimeListener(this, timeText, timestring, fourthDimensionsliderInit,
				fourthDimensionSize, scrollbarSize, timeslider));
		zslider.addAdjustmentListener(new ZListener(this, zText, zstring, thirdDimensionsliderInit, thirdDimensionSize,
				scrollbarSize, zslider));
		rslider.addAdjustmentListener(
				new RListener(this, rText, rstring, radiusMin, radiusMax, scrollbarSize, rslider));

		insideslider.addAdjustmentListener(
				new InsideCutoffListener(this, insideText, insidestring, 0, 100, scrollbarSize, insideslider));
		outsideslider.addAdjustmentListener(
				new OutsideCutoffListener(this, outsideText, outsidestring, 0, 100, scrollbarSize, outsideslider));

		Anglebutton.addActionListener(new AngleListener(this));
		Roibutton.addActionListener(new RoiListener(this));
		inputFieldZ.addTextListener(new ZlocListener(this, false));
		inputFieldT.addTextListener(new TlocListener(this, false));

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

	}

	public static int round(double value) {
		return (int) (value + 0.5D * Math.signum(value));
	}

	public static void main(String[] args) {
		new ImageJ();
		ImagePlus impA = new Opener().openImage("/Users/varunkapoor/Documents/JLMData/Hyperstack_ML7small.tif");
		impA.show();
		JFrame frame = new JFrame("");
		EllipseFileChooser panel = new EllipseFileChooser();

		frame.getContentPane().add(panel, "Center");
		frame.setSize(panel.getPreferredSize());
	}

}
