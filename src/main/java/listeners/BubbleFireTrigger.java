package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;

import ij.IJ;
import ij.io.Opener;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import pluginTools.InteractiveEllipseFit;


public class BubbleFireTrigger implements ActionListener {

	
	final InteractiveEllipseFit parent;
	final JComboBox<String> choice;
	
	public BubbleFireTrigger(InteractiveEllipseFit parent, JComboBox<String> choice ){
		
		this.parent = parent;
		this.choice = choice;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		int selectedindex = choice.getSelectedIndex();
		
		
		if (selectedindex == 0) {
			
			
			parent.chooserA = new JFileChooser();
			parent.chooserA.setCurrentDirectory(new java.io.File("."));
			parent.chooserA.setDialogTitle(parent.choosertitleA);
			parent.chooserA.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			//
			
			//
			if (parent.chooserA.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
				System.out.println("getCurrentDirectory(): " + parent.chooserA.getCurrentDirectory());
				System.out.println("getSelectedFile() : " + parent.chooserA.getSelectedFile());
			} else {
				System.out.println("No Selection ");
			}
			
			if(parent.chooserA!=null) {
			parent.impA = new Opener().openImage(parent.chooserA.getSelectedFile().getPath());
			  
			RandomAccessibleInterval<FloatType> image = ImageJFunctions.convertFloat(parent.impA);
			
			new InteractiveEllipseFit(image, parent.chooserA.getSelectedFile()).run(null);
			}
		}
		
		
		if (selectedindex == 1) {

			parent.impA = IJ.getImage();
			
			RandomAccessibleInterval<FloatType> image = ImageJFunctions.convertFloat(parent.impA);
			
			new InteractiveEllipseFit(image).run(null);
			if(parent.impA!=null)
				parent.impA.close();
			
			
		}
		
		
		
	}
	
	
	
	
	
}
