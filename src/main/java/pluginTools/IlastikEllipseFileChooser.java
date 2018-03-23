package pluginTools;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

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
import listeners.ChooseOrigMap;
import listeners.ChooseProbMap;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;

public class IlastikEllipseFileChooser extends JPanel {

	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public JFrame Cardframe = new JFrame("Ilastik based Ellipsoid detector");
	  public JPanel panelCont = new JPanel();
	  public ImagePlus impA, impOrig;
	  public JPanel panelFirst = new JPanel();
	  public JPanel Panelfile = new JPanel();
	  public JPanel Panelfileoriginal = new JPanel();
	  public static final Insets insets = new Insets(10, 0, 0, 0);
	  public final GridBagLayout layout = new GridBagLayout();
	  public final GridBagConstraints c = new GridBagConstraints();
	  public final String[] imageNames;
	  public JComboBox<String> ChooseImage;
	  public JComboBox<String> ChooseoriginalImage;
	  
	  
	  public IlastikEllipseFileChooser() {
		
		  Border choosefile = new CompoundBorder(new TitledBorder("Probability Map chooser"),
					new EmptyBorder(c.insets));
		  
		  Border chooseoriginalfile = new CompoundBorder(new TitledBorder("Choose original Image(Optional, to display results)"),
					new EmptyBorder(c.insets));
		  
		   panelFirst.setLayout(layout);
		   Panelfile.setLayout(layout);
		   Panelfileoriginal.setLayout(layout);
	       CardLayout cl = new CardLayout();
			
			panelCont.setLayout(cl);
			panelCont.add(panelFirst, "1");
			imageNames = WindowManager.getImageTitles();
			
			
			ChooseImage = new JComboBox<String>(imageNames);
			
			ChooseoriginalImage = new JComboBox<String>(imageNames);
			
			Panelfile.add(ChooseImage, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			Panelfile.setBorder(choosefile);
			panelFirst.add(Panelfile, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));
			
			Panelfileoriginal.add(ChooseoriginalImage, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			Panelfileoriginal.setBorder(chooseoriginalfile);
			panelFirst.add(Panelfileoriginal, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));
			
			ChooseImage.addActionListener(new ChooseProbMap(this, ChooseImage));
			ChooseoriginalImage.addActionListener(new ChooseOrigMap(this, ChooseoriginalImage));
			
			panelFirst.setVisible(true);
			cl.show(panelCont, "1");
			Cardframe.add(panelCont, "Center");

			Cardframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			Cardframe.pack();
			Cardframe.setVisible(true);
		}
		
	
	  public void DoneCurr(Frame parent){
			
			// Tracking and Measurement is done with imageA 
	        
		    
			RandomAccessibleInterval<FloatType> image = ImageJFunctions.convertFloat(impA);
			RandomAccessibleInterval<FloatType> imagebefore = ImageJFunctions.convertFloat(impOrig);
			WindowManager.closeAllWindows();
			new InteractiveSimpleEllipseFit(image, imagebefore, true).run(null);
			close(parent);
			
			
		}
	  protected final void close(final Frame parent) {
			if (parent != null)
				parent.dispose();

			
		}

	
	
	
}
