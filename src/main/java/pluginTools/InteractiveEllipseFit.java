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
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
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
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.io.Opener;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.plugin.frame.RoiManager;
import listeners.DisplayRoiListener;
import listeners.EllipseNonStandardMouseListener;
import listeners.RoiListener;
import listeners.TimeListener;
import listeners.TlocListener;
import listeners.ZListener;
import listeners.ZlocListener;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.view.Views;
import pluginTools.InteractiveEllipseFit.ValueChange;
import utility.Roiobject;

public class InteractiveEllipseFit implements PlugIn {

	public String usefolder = IJ.getDirectory("imagej");
	public String addToName = "EllipseFits";
	public final int scrollbarSize = 10000;


	public Overlay overlay;
	public int thirdDimensionslider = 1;
	public int thirdDimensionsliderInit = 1;
	public int fourthDimensionslider = 1;
	public int fourthDimensionsliderInit = 1;

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
	public JLabel inputLabelwidth;
	public TextField inputFieldwidth;
	public JLabel inputLabelBins;
	public TextField inputFieldBins;
	public JLabel inputLabelIter;
	public TextField inputFieldIter;
	public JTable table;
	public HashMap<Integer, Roiobject> ZTRois;
	public ImagePlus imp;
	public int ndims;
	public RandomAccessibleInterval<FloatType> CurrentView;
	public Color confirmedRois = Color.BLUE;
	public Color defaultRois = Color.YELLOW;
	private Label inputLabelZ,  inputLabelT;
	public TextField inputFieldZ;
	public KeyListener kl;
	public TextField inputFieldT;
	public RoiManager roimanager;
	public boolean isCreated = false;
	public static enum ValueChange {
		ROI, ALL, THIRDDIMmouse, FOURTHDIMmouse, DISPLAYROI
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
        ZTRois = new HashMap<Integer, Roiobject>();
	
        
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
		CurrentView = utility.Slicer.getCurrentView(originalimg, fourthDimension,
				thirdDimensionSize, thirdDimension, fourthDimensionSize);
		imp = ImageJFunctions.show(CurrentView);
		imp.setTitle("Active image" + " " +  "time point : " + fourthDimension + " " + " Z: " + thirdDimension );
		
		Card();
		updatePreview(ValueChange.ALL);
	}

	
	public void updatePreview(final ValueChange change) {
		
		roimanager = RoiManager.getInstance();

		if (roimanager == null) {
			roimanager = new RoiManager();
		}

		overlay = imp.getOverlay();

		if (overlay == null) {

			overlay = new Overlay();
			imp.setOverlay(overlay);
		}
		
		
		
		if (change == ValueChange.ROI) {
		
			
			Roi[] Rois = roimanager.getRoisAsArray();
			
			Roiobject CurrentRoi = new Roiobject(Rois, thirdDimension, fourthDimension, true);
			
			int key = (int) (thirdDimension * Math.log(2) + fourthDimension * Math.log(3));
			
			System.out.println(key + " " + fourthDimension + " " + thirdDimension + "" + ZTRois.size() );
			
			if (ZTRois.get(key)==null)
			
				ZTRois.put(key, CurrentRoi);
			
			
			else {
				
				ZTRois.remove(key);
				ZTRois.put(key, CurrentRoi);
				
			}
			
		}

		
		
		if (change == ValueChange.THIRDDIMmouse) {
		
			if (imp == null){
				imp = ImageJFunctions.show(CurrentView);
			
			}
			
			else {
			
				final float[] pixels = (float[]) imp.getProcessor().getPixels();
				final Cursor<FloatType> c = Views.iterable(CurrentView).cursor();

				for (int i = 0; i < pixels.length; ++i)
					pixels[i] = c.next().get();

				imp.updateAndDraw();

			}
			 Display();
			imp.setTitle("Active image" + " " +  "time point : " + fourthDimension + " " + " Z: " + thirdDimension );

	    
			
			}
		
		
		
		if (change == ValueChange.FOURTHDIMmouse) {
			
			
			if (imp == null){
				imp = ImageJFunctions.show(CurrentView);
			
			}
			
			else {
			
				final float[] pixels = (float[]) imp.getProcessor().getPixels();
				final Cursor<FloatType> c = Views.iterable(CurrentView).cursor();

				for (int i = 0; i < pixels.length; ++i)
					pixels[i] = c.next().get();

				imp.updateAndDraw();

			}
			 Display();
			imp.setTitle("Active image" + " " +  "time point : " + fourthDimension + " " + " Z: " + thirdDimension );
			
			}
		
		
	}
	
	public  void Display(){
		
		overlay.setStrokeColor(defaultRois);
		
		for (int index = 0; index < overlay.size(); ++index){
				overlay.remove(index);
			   --index;
			}
		
		if(ZTRois.size() > 0) {
		
		for (Map.Entry<Integer, Roiobject> entry: ZTRois.entrySet()) {
			
			Roiobject currentobject = entry.getValue();
			
		
			System.out.println(currentobject.fourthDimension + "" + currentobject.thirdDimension + "" + currentobject.isCreated);
			if(currentobject.fourthDimension == fourthDimension && currentobject.thirdDimension == thirdDimension && currentobject.isCreated) {
				
				
				
				for (int index = 0; index < currentobject.roilist.length; ++index) {
					
					
					Roi or = currentobject.roilist[index];
					or.setStrokeColor(confirmedRois);
					overlay.add(or);
				}
				
				break;
			}
			
			
			
			}
		

		imp.updateAndDraw();
	}
	}
	
	public JFrame Cardframe = new JFrame("Ellipsoid detector");
	public JPanel panelCont = new JPanel();
	public JPanel panelFirst = new JPanel();
	private JPanel Timeselect = new JPanel();
	private JPanel Zselect = new JPanel();
	private JPanel Roiselect = new JPanel();
	private JPanel Angleselect = new JPanel();
    
	
	
	private int SizeX = 400;
    private int SizeY = 200;
	
    
    public JButton Roibutton = new JButton("Confirm current roi selection");
    public JButton DisplayRoibutton = new JButton("Display roi selection");
    public JButton Anglebutton = new JButton("Intersection points and angles");
   
	public Label timeText = new Label("Current time point = " + 1, Label.CENTER);
	public Label zText = new Label("Current Z location = " + 1, Label.CENTER);

	final String timestring = "Current Time point";
	final String zstring = "Current Z location";
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
	public void Card() {
		CardLayout cl = new CardLayout();
		
		panelCont.setLayout(cl);

		panelCont.add(panelFirst, "1");

		panelFirst.setName("Angle Tool for ellipsoids");

		panelFirst.setLayout(layout);
		
		Timeselect.setLayout(layout);

		Zselect.setLayout(layout);

		Roiselect.setLayout(layout);
		
		Angleselect.setLayout(layout);
	
		
		inputFieldZ = new TextField();

		inputFieldZ.setText(Integer.toString(thirdDimension));
		
		inputFieldT = new TextField();

		inputFieldT.setText(Integer.toString(fourthDimension));
	
		
		
		Border timeborder = new CompoundBorder(new TitledBorder("Select time"), new EmptyBorder(c.insets));
		Border zborder = new CompoundBorder(new TitledBorder("Select Z"), new EmptyBorder(c.insets));
		Border roitools = new CompoundBorder(new TitledBorder("Roi tools"), new EmptyBorder(c.insets));
		
		Border ellipsetools = new CompoundBorder(new TitledBorder("Angle computer"), new EmptyBorder(c.insets));
		
		if (ndims >= 3) {

			// Put time slider

			Timeselect.add(timeText, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));

			
			Timeselect.add(timeslider, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));
			
			Timeselect.add(inputFieldT, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));
			
			Timeselect.setBorder(timeborder);
			Timeselect.setMinimumSize(new Dimension(SizeX,SizeY));
			Timeselect.setPreferredSize(new Dimension(SizeX,SizeY));
			panelFirst.add(Timeselect, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL,insets, 0, 0));
			
		}

		if (ndims > 3) {

			// Put z slider
			

			Zselect.add(zText, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));

			Zselect.add(zslider, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL,insets, 0, 0));

			Zselect.add(inputFieldZ, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));
			
			Zselect.setBorder(zborder);
			Zselect.setMinimumSize(new Dimension(SizeX,SizeY));
			Zselect.setPreferredSize(new Dimension(SizeX,SizeY));
			panelFirst.add(Zselect, new GridBagConstraints(5, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL,insets, 0, 0));
			
			

		}
		
		Roiselect.add(Roibutton, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));
		
		Roiselect.setBorder(roitools);
		panelFirst.add(Roiselect, new GridBagConstraints(0, 1, 5, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));
		
		Angleselect.add(Anglebutton,  new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		Angleselect.setBorder(ellipsetools);
		panelFirst.add(Angleselect, new GridBagConstraints(5, 1, 5, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL,insets, 0, 0));
		

		timeslider.addAdjustmentListener(new TimeListener(this, timeText, timestring, fourthDimensionsliderInit,
				fourthDimensionSize, scrollbarSize, timeslider));
		zslider.addAdjustmentListener(new ZListener(this, zText, zstring, thirdDimensionsliderInit,
				thirdDimensionSize, scrollbarSize, zslider));
		Roibutton.addActionListener(new RoiListener(this));
		inputFieldZ.addTextListener(new ZlocListener(this, false));
		inputFieldT.addTextListener(new TlocListener(this, false));
		
		
		
		panelFirst.setMinimumSize(new Dimension(SizeX,SizeY));
		
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
		ImagePlus impA = new Opener().openImage("/Users/varunkapoor/Documents/JLMData/Hyperstack_ML7.tif");
		impA.show();
		JFrame frame = new JFrame("");
		EllipseFileChooser panel = new EllipseFileChooser();

		frame.getContentPane().add(panel, "Center");
		frame.setSize(panel.getPreferredSize());
	}

}
