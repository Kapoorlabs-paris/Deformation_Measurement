package pluginTools;

import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import listeners.ChooseOrigMap;
import listeners.ChooseProbMap;
import listeners.ChoosesuperProbMap;
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
	  public ImagePlus impA, impOrig, impsuper;
	  public File impAfile, impOrigfile, impsuperfile;
	  public JPanel panelFirst = new JPanel();
	  public JPanel Panelfile = new JPanel();
	  public JPanel Panelsuperfile = new JPanel();
	  public JPanel Panelfileoriginal = new JPanel();
	  public JPanel Paneldone = new JPanel();
	  public JPanel Panelrun = new JPanel();
	  public final Insets insets = new Insets(10, 0, 0, 0);
	  public final GridBagLayout layout = new GridBagLayout();
	  public final GridBagConstraints c = new GridBagConstraints();
	  public final String[] imageNames;
	  public JComboBox<String> ChooseImage;
	  public JComboBox<String> ChoosesuperImage;
	  public JComboBox<String> ChooseoriginalImage;
	  public JButton Done =  new JButton("Finished choosing files, start ETrack");
	  public boolean superpixel = false;
	  public boolean simple = true;
	  public CheckboxGroup runmode = new CheckboxGroup();
	  public Checkbox Gosuper = new Checkbox("Input Multicut Trained segmentation", superpixel, runmode);
	  public Checkbox Gosimple = new Checkbox("Input Pixel Classification output only", simple, runmode);
	  Border choosefile = new CompoundBorder(new TitledBorder("Probability Map chooser"),
				new EmptyBorder(c.insets));
	  public Border choosesuperfile = new CompoundBorder(new TitledBorder("Superpixel SegMap chooser"),
				new EmptyBorder(c.insets));
	  
	  public Border chooseoriginalfile = new CompoundBorder(new TitledBorder("Choose original Image"),
				new EmptyBorder(c.insets));
	  
	  public Border LoadEtrack = new CompoundBorder(new TitledBorder("Done Selection"),
				new EmptyBorder(c.insets));
	  public Border runmodetrack = new CompoundBorder(new TitledBorder("Runmode"),
				new EmptyBorder(c.insets));
	  public IlastikEllipseFileChooser() {
		
		
		  
		   panelFirst.setLayout(layout);
		   Panelfile.setLayout(layout);
		   
		   Panelsuperfile.setLayout(layout);
		   Panelfileoriginal.setLayout(layout);
		   Paneldone.setLayout(layout);
		   Panelrun.setLayout(layout);
	       CardLayout cl = new CardLayout();
			
			panelCont.setLayout(cl);
			panelCont.add(panelFirst, "1");
			imageNames = WindowManager.getImageTitles();
			
			
			ChooseImage = new JComboBox<String>(imageNames);
			
			ChooseoriginalImage = new JComboBox<String>(imageNames);
			
			ChoosesuperImage = new JComboBox<String>(imageNames);
			
			Panelrun.add(Gosuper, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			Panelrun.add(Gosimple, new GridBagConstraints(3, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			Panelrun.setBorder(runmodetrack);
			panelFirst.add(Panelrun, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));
			
			Panelfileoriginal.add(ChooseoriginalImage, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
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
			
			
			
			
			Paneldone.add(Done, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			Paneldone.setBorder(LoadEtrack);
			panelFirst.add(Paneldone, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));
			
			
			
			ChooseImage.addActionListener(new ChooseProbMap(this, ChooseImage));
			ChooseoriginalImage.addActionListener(new ChooseOrigMap(this, ChooseoriginalImage));

			Done.addActionListener(new DoneListener());
			ChoosesuperImage.addActionListener(new ChoosesuperProbMap(this, ChoosesuperImage));
			
			panelFirst.setVisible(true);
			cl.show(panelCont, "1");
			Cardframe.add(panelCont, "Center");
			Panelsuperfile.setEnabled(false);
			ChoosesuperImage.setEnabled(false);
			Gosuper.addItemListener(new SupermodeListener(this));
			Gosimple.addItemListener(new SimplemodeListener(this) );
			Cardframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			Cardframe.pack();
			Cardframe.setVisible(true);
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
			
			// Tracking and Measurement is done with imageA 
	        
		  org.apache.log4j.BasicConfigurator.configure();
			RandomAccessibleInterval<FloatType> image = new ImgOpener().openImgs(impA.getOriginalFileInfo().directory + impA.getOriginalFileInfo().fileName , new FloatType()).iterator().next();
			RandomAccessibleInterval<FloatType> imagebefore = new ImgOpener().openImgs(impOrig.getOriginalFileInfo().directory + impOrig.getOriginalFileInfo().fileName, new FloatType()).iterator().next();
			
			WindowManager.closeAllWindows();
			if(superpixel) {
			
				RandomAccessibleInterval<IntType> imagesuper = new ImgOpener().openImgs(impsuper.getOriginalFileInfo().directory + impsuper.getOriginalFileInfo().fileName, new IntType()).iterator().next();
				new InteractiveSimpleEllipseFit(image, imagebefore, imagesuper, simple, superpixel).run(null);
			
			}
			else
			new InteractiveSimpleEllipseFit(image, imagebefore, simple).run(null);
			close(parent);
			
			
		}
	  protected final void close(final Frame parent) {
			if (parent != null)
				parent.dispose();

			
		}

	
	
	
}
