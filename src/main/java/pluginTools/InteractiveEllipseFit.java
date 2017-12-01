package pluginTools;

import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.TextField;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
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
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import net.imglib2.util.Pair;

public class InteractiveEllipseFit implements PlugIn {

	
	 public String usefolder = IJ.getDirectory("imagej");
	  public String addToName = "EllipseFits";
	  public final int scrollbarSize = 1000;
	  public final int scrollbarSizebig = 1000;
	  
	  public static int standardSensitivity = 4;
	  public int sensitivity = standardSensitivity;
	  public ArrayList<Pair<Double, Double>> timeseries;
	  public ArrayList<Pair<Double, Double>> frequchirphist;
	  int FrequInt;
	  int ChirpInt;
	  int PhaseInt;
	  int BackInt; public boolean polymode = true;
	  public boolean randommode = false;
	  public boolean isDone;
	  public static int MIN_SLIDER = 0;
	  public static int MAX_SLIDER = 500;
	  public int row;
	  public static double MIN_FREQU = 0.0D;
	  public static double MAX_FREQU = 30.0D;
	  
	  public static double MIN_CHIRP = 0.0D;
	  public static double MAX_CHIRP = 40.0D;
	  public boolean enableHigh = false;
	  public double Lowfrequ = 2.6166666666666667D;
	  public double Highfrequ = Lowfrequ / 2.0D;
	  public double phase = 0.0D;
	  public double back = 0.0D;
	  
	  public int numBins = 10;

	  public int maxiter = 5000;
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
	  ResultsTable rtAll;
	  public File inputfile;
	  public File[] inputfiles;
	  public String inputdirectory;
	  public JLabel inputLabelwidth;
	  public TextField inputFieldwidth;
	  public JLabel inputLabelBins;
	  public TextField inputFieldBins;
	  public JLabel inputLabelIter;
	  public TextField inputFieldIter;
	  public JTable table;
	  
	  public InteractiveEllipseFit() {
	    nf = NumberFormat.getInstance(Locale.ENGLISH);
	    nf.setMaximumFractionDigits(3);
	  }
	  
	  public InteractiveEllipseFit(File[] file)
	  {
	    inputfiles = file;
	    inputdirectory = file[0].getParent();
	    

	    nf = NumberFormat.getInstance(Locale.ENGLISH);
	    nf.setMaximumFractionDigits(3);
	  }
	  
	  public void run(String arg0) {
	    frequchirphist = new ArrayList();
	    rtAll = new ResultsTable();
	    jpb = new JProgressBar();
	    Card();
	  }
	  

	  public JFrame Cardframe = new JFrame("Welcome to Chirp Fits ");
	  public JPanel panelCont = new JPanel();
	  public JPanel panelFirst = new JPanel();
	  public JComboBox<String> ChooseModel;
	  public JPanel Panelmodel = new JPanel();
	  private JPanel Panelparam = new JPanel();
	  public JPanel Panelfile = new JPanel();
	  public static final Insets insets = new Insets(10, 0, 0, 0);
	  JPanel PanelDirectory = new JPanel();
	  public final GridBagLayout layout = new GridBagLayout();
	  public final GridBagConstraints c = new GridBagConstraints();
	  public Border selectfile = new CompoundBorder(new TitledBorder("Select file"), new EmptyBorder(c.insets));
	  Border selectparam = new CompoundBorder(new TitledBorder("Select Chirp Fit parameters"), new EmptyBorder(c.insets));
	  Border Model = new CompoundBorder(new TitledBorder("Select model"), new EmptyBorder(c.insets));
	  Border selectdirectory = new CompoundBorder(new TitledBorder("Load directory of TxT files"), new EmptyBorder(c.insets));
	  public JScrollPane scrollPane;
	  public JFileChooser chooserA;
	  public String choosertitleA;
	  public final JButton AutoFit = new JButton("Auto-Fit all files");
	  public final Button Frequhist = new Button("Frequency Histogram");
	  

	  public void Card()
	  {
	    CardLayout cl = new CardLayout();
	    
	    DefaultTableModel userTableModel = new DefaultTableModel(new Object[0], 0)
	    {
	      public boolean isCellEditable(int row, int column) {
	        return false;
	      }
	    };
	    
	    if (inputfiles != null) {
	      for (int i = 0; i < inputfiles.length; i++)
	      {
	        String[] currentfile = { inputfiles[i].getName() };
	        userTableModel.addRow(currentfile);
	      }
	    }
	    
	    table = new JTable(userTableModel);
	    
	    table.setFillsViewportHeight(true);
	    
	    table.setAutoResizeMode(0);
	    
	    scrollPane = new JScrollPane(table);
	    scrollPane.setMinimumSize(new Dimension(200, 200));
	    scrollPane.setPreferredSize(new Dimension(200, 200));
	    

	    scrollPane.getViewport().add(table);
	    scrollPane.setAutoscrolls(true);
	    
	    panelCont.setLayout(cl);
	    
	    panelCont.add(panelFirst, "1");
	    
	    panelFirst.setName("Chirp Fits");
	    Panelmodel.setLayout(this.layout);
	    Panelparam.setLayout(this.layout);
	    Panelfile.setLayout(this.layout);
	    PanelDirectory.setLayout(this.layout);
	    


	    Cardframe.add(panelCont, "Center");
	    Cardframe.add(jpb, "Last");
	    
	    Cardframe.setDefaultCloseOperation(2);
	    Cardframe.pack();
	    Cardframe.setVisible(true);
	    Cardframe.pack();
	  }
	  
	  public void displayclicked(int trackindex)
	  {
	  
	  }
	  






	



	  public static double computeValueFromScrollbarPosition(int scrollbarPosition, int scrollbarMax, double minValue, double maxValue)
	  {
	    return minValue + scrollbarPosition / scrollbarMax * (maxValue - minValue);
	  }
	  
	  public static int computeScrollbarPositionFromValue(int scrollbarMax, double value, double minValue, double maxValue)
	  {
	    return (int)Math.round((value - minValue) / (maxValue - minValue) * scrollbarMax);
	  }
	  
	  public int computeScrollbarPositionFromValue(double sigma, float min, float max, int scrollbarSize)
	  {
	    return round((sigma - min) / (max - min) * scrollbarSize);
	  }
	  
	  public static int round(double value) {
	    return (int)(value + 0.5D * Math.signum(value));
	  }
	  
	  public static void main(String[] args)
	  {
	    new ImageJ();
	    
	    JFrame frame = new JFrame("");
	    EllipseFileChooser panel = new EllipseFileChooser();
	    
	    frame.getContentPane().add(panel, "Center");
	    frame.setSize(panel.getPreferredSize());
	  }
	
	
}
