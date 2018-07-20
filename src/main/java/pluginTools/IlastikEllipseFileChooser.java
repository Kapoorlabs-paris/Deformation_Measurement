package pluginTools;

import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.TextComponent;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.apache.log4j.BasicConfigurator;

import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.text.TextWindow;
import io.scif.config.SCIFIOConfig;
import io.scif.config.SCIFIOConfig.ImgMode;
import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import listeners.ChooseOrigMap;
import listeners.ChooseProbMap;
import listeners.ChoosesecOrigMap;
import listeners.ChoosesuperProbMap;
import listeners.CurveSimplemodeListener;
import listeners.CurveSupermodeListener;
import listeners.DoubleChannelListener;
import listeners.SimplemodeListener;
import listeners.SupermodeListener;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;

public class IlastikEllipseFileChooser extends JPanel {

	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	  public JFrame Cardframe = new JFrame("Ilastik based Ellipsoid detector");
	  public JPanel panelCont = new JPanel();
	  public ImagePlus impA, impOrig, impsuper, impSec;
	  public File impAfile, impOrigfile, impsuperfile, impsecfile;
	  public JPanel panelFirst = new JPanel();
	  public JPanel Panelfile = new JPanel();
	  public JPanel Panelsuperfile = new JPanel();
	  public JPanel Panelfileoriginal = new JPanel();
	  public JPanel Paneldone = new JPanel();
	  public JPanel Panelrun = new JPanel();
	  public JPanel Microscope = new JPanel();
	  public Label inputLabelcalX, wavesize;
	  public double calibration, Wavesize;
	  public TextField inputFieldcalX, Fieldwavesize;
	  public final Insets insets = new Insets(10, 0, 0, 0);
	  public final GridBagLayout layout = new GridBagLayout();
	  public final GridBagConstraints c = new GridBagConstraints();
	  public final String[] imageNames, blankimageNames;
	  public JComboBox<String> ChooseImage;
	  public JComboBox<String> ChoosesuperImage;
	  public JComboBox<String> ChooseoriginalImage;
	  public JComboBox<String> ChoosesecImage;
	  public JButton Done =  new JButton("Finished choosing files, start ETrack");
	  public boolean superpixel = false;
	  public boolean simple = false;
	  public boolean curvesuper = true;
	  public boolean curvesimple = false;
	  public boolean twochannel = true;
	  
	  public CheckboxGroup runmode = new CheckboxGroup();
	  public Checkbox Gosuper = new Checkbox("Angle tracking with Multicut Trained segmentation", superpixel, runmode);
	  public Checkbox Gosimple = new Checkbox("Angle tracking with Pixel Classification output only", simple, runmode);
	  
	  public Checkbox Godouble = new Checkbox("Load a second channel", twochannel);
	  
	  
	  public Checkbox Gocurvesuper = new Checkbox("Curvature Measurment with Multicut Trained segmentation", curvesuper, runmode);
	  public Checkbox Gocurvesimple = new Checkbox("Curvature Measurment with Pixel only", curvesimple, runmode);
	  
	  public Border choosefile = new CompoundBorder(new TitledBorder("Probability Map chooser"),
				new EmptyBorder(c.insets));
	  public Border choosesuperfile = new CompoundBorder(new TitledBorder("Superpixel SegMap chooser"),
				new EmptyBorder(c.insets));
	  public Border chooseoriginalfile = new CompoundBorder(new TitledBorder("Choose original Image"),
				new EmptyBorder(c.insets));
	  public Border LoadEtrack = new CompoundBorder(new TitledBorder("Done Selection"),
				new EmptyBorder(c.insets));
	  public Border runmodetrack = new CompoundBorder(new TitledBorder("Runmode"),
				new EmptyBorder(c.insets));
	  public 	Border microborder = new CompoundBorder(new TitledBorder("Microscope parameters"), new EmptyBorder(c.insets));
	  public IlastikEllipseFileChooser() {
		
		
		  
		   panelFirst.setLayout(layout);
		   Panelfile.setLayout(layout);
		   Microscope.setLayout(layout);
		   Panelsuperfile.setLayout(layout);
		   Panelfileoriginal.setLayout(layout);
		   Paneldone.setLayout(layout);
		   Panelrun.setLayout(layout);
	       CardLayout cl = new CardLayout();
	       inputLabelcalX = new Label("Pixel calibration in X,Y (um)");
	       inputFieldcalX = new TextField(5);
		   inputFieldcalX.setText("1");
			
		   wavesize = new Label("Size of Wavefront (um)");
		   Fieldwavesize = new TextField(5);
		   Fieldwavesize.setText("1");
		   
		   
		    panelCont.setLayout(cl);
			panelCont.add(panelFirst, "1");
			imageNames = WindowManager.getImageTitles();
			blankimageNames = new String[imageNames.length + 1];
			blankimageNames[0] = " " ;
			
			for(int i = 0; i < imageNames.length; ++i)
				blankimageNames[i + 1] = imageNames[i];
			
			ChooseImage = new JComboBox<String>(blankimageNames);
			ChooseoriginalImage = new JComboBox<String>(blankimageNames);
			ChoosesecImage = new JComboBox<String>(blankimageNames);
			ChoosesuperImage = new JComboBox<String>(blankimageNames);
			calibration = Float.parseFloat(inputFieldcalX.getText());
			Wavesize = Float.parseFloat(Fieldwavesize.getText());
			Panelrun.add(Gosuper, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		//	Panelrun.add(Gosimple, new GridBagConstraints(3, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
		//			GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			Panelrun.add(Gocurvesuper, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		//	Panelrun.add(Gocurvesimple, new GridBagConstraints(3, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
		//			GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			Panelrun.setBorder(runmodetrack);
			panelFirst.add(Panelrun, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));
			
			
			Panelfileoriginal.add(Godouble,  new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			Panelfileoriginal.add(ChooseoriginalImage, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			if(twochannel)
			Panelfileoriginal.add(ChoosesecImage,  new GridBagConstraints(3, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			Panelfileoriginal.setBorder(chooseoriginalfile);
			panelFirst.add(Panelfileoriginal, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));
			
			
			Panelfile.add(ChooseImage, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			Panelfile.setBorder(choosefile);
			panelFirst.add(Panelfile, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));
			
			Panelsuperfile.add(ChoosesuperImage, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			Panelsuperfile.setBorder(choosesuperfile);
			panelFirst.add(Panelsuperfile, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));
			
			Microscope.add(inputLabelcalX, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));
			
			Microscope.add(inputFieldcalX, new GridBagConstraints(0, 1, 3, 1, 0.1, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.RELATIVE, insets, 0, 0));
			
			Microscope.add(wavesize, new GridBagConstraints(3, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));
			
			Microscope.add(Fieldwavesize, new GridBagConstraints(3, 1, 3, 1, 0.1, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.RELATIVE, insets, 0, 0));
			
			Microscope.setBorder(microborder);
			panelFirst.add(Microscope, new GridBagConstraints(0, 4, 5, 1, 0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			
			
			Paneldone.add(Done, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			Paneldone.setBorder(LoadEtrack);
			panelFirst.add(Paneldone, new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));
			
			
			
			ChooseImage.addActionListener(new ChooseProbMap(this, ChooseImage));
			ChooseoriginalImage.addActionListener(new ChooseOrigMap(this, ChooseoriginalImage));
			ChoosesecImage.addActionListener(new ChoosesecOrigMap(this, ChoosesecImage));
			inputFieldcalX.addTextListener(new CalXListener());
			Fieldwavesize.addTextListener(new WaveListener());
			Done.addActionListener(new DoneListener());
			ChoosesuperImage.addActionListener(new ChoosesuperProbMap(this, ChoosesuperImage));
			
			panelFirst.setVisible(true);
			cl.show(panelCont, "1");
			Cardframe.add(panelCont, "Center");
			Panelsuperfile.setEnabled(true);
			ChoosesuperImage.setEnabled(true);
		    Godouble.setEnabled(true);
			Gosuper.addItemListener(new SupermodeListener(this));
			Gosimple.addItemListener(new SimplemodeListener(this) );
			Godouble.addItemListener(new DoubleChannelListener(this));
			Gocurvesimple.addItemListener(new CurveSimplemodeListener(this));
			Gocurvesuper.addItemListener(new CurveSupermodeListener(this));
			Cardframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			Cardframe.pack();
			Cardframe.setVisible(true);
		}
	  public class CalXListener implements TextListener {

			
		
			
			@Override
			public void textValueChanged(TextEvent e) {
				final TextComponent tc = (TextComponent)e.getSource();
			    String s = tc.getText();
			   
			    if (s.length() > 0)
				calibration = Float.parseFloat(s);
				
			}
			
	  }
	  
	  public class WaveListener implements TextListener {

			
			
			
			@Override
			public void textValueChanged(TextEvent e) {
				final TextComponent tc = (TextComponent)e.getSource();
			    String s = tc.getText();
			   
			    if (s.length() > 0)
				Wavesize = Float.parseFloat(s);
				
			}
			
	  }
	  
	  public class DoneListener implements ActionListener{
		  
		  
		  @Override
			public void actionPerformed(ActionEvent e) {
			  
			  
			  try {
				DoneCurr(Cardframe);
			} catch (ImgIOException e1) {

				// TODO Auto-generated catch block

			
			}
		  }
		  
		  
		  
	  }
	  
	
	  public void DoneCurr(Frame parent) throws ImgIOException{
			
		
			RandomAccessibleInterval<FloatType> image = new ImgOpener().openImgs(impA.getOriginalFileInfo().directory + impA.getOriginalFileInfo().fileName , new FloatType()).iterator().next();
			RandomAccessibleInterval<FloatType> imagebefore = new ImgOpener().openImgs(impOrig.getOriginalFileInfo().directory + impOrig.getOriginalFileInfo().fileName, new FloatType()).iterator().next();
			BasicConfigurator.configure();
			System.setProperty("scijava.log.level", "none");
			WindowManager.closeAllWindows();
			
			if(superpixel && !twochannel) {
			
				RandomAccessibleInterval<IntType> imagesuper = new ImgOpener().openImgs(impsuper.getOriginalFileInfo().directory + impsuper.getOriginalFileInfo().fileName, new IntType()).iterator().next();
				new InteractiveSimpleEllipseFit(image, imagebefore, imagesuper, calibration, Wavesize, simple, superpixel, impsuper.getOriginalFileInfo().directory).run(null);
			
			}
			if (simple && !twochannel)
			new InteractiveSimpleEllipseFit(image, imagebefore, calibration, Wavesize, simple,  impA.getOriginalFileInfo().directory).run(null);
			
			if (curvesimple && !twochannel) {
				// Activate curvature measurment simple
				new InteractiveSimpleEllipseFit(image, imagebefore, calibration, Wavesize,simple, superpixel, curvesimple, curvesuper, impA.getOriginalFileInfo().directory).run(null);
				
			}
			if(curvesuper && !twochannel) {
				// Activate curvature measurment super
			
				RandomAccessibleInterval<IntType> imagesuper = new ImgOpener().openImgs(impsuper.getOriginalFileInfo().directory + impsuper.getOriginalFileInfo().fileName, new IntType()).iterator().next();
				new InteractiveSimpleEllipseFit(image, imagebefore, imagesuper,calibration, Wavesize, simple, superpixel, curvesimple, curvesuper, impA.getOriginalFileInfo().directory).run(null);
			}
			
			
			if(curvesuper && twochannel) {
				// Activate curvature measurment super
			
				RandomAccessibleInterval<IntType> imagesuper = new ImgOpener().openImgs(impsuper.getOriginalFileInfo().directory + impsuper.getOriginalFileInfo().fileName, new IntType()).iterator().next();
				RandomAccessibleInterval<FloatType> secimage = new ImgOpener().openImgs(impSec.getOriginalFileInfo().directory + impSec.getOriginalFileInfo().fileName, new FloatType()).iterator().next();
				new InteractiveSimpleEllipseFit(image, secimage, imagebefore, imagesuper,calibration, Wavesize, simple, superpixel, curvesimple, curvesuper, impA.getOriginalFileInfo().directory).run(null);
			}
			
			close(parent);
			
			
		}
	  protected final void close(final Frame parent) {
			if (parent != null)
				parent.dispose();

			
		}

	
	
	
}
