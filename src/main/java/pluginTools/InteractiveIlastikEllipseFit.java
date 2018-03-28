package pluginTools;

import javax.swing.JFrame;

import ij.ImageJ;
import ij.ImagePlus;
import ij.io.Opener;

public class InteractiveIlastikEllipseFit {

	

	public static void main(String[] args) {

		new ImageJ();
		JFrame frame = new JFrame("");

		ImagePlus impB = new Opener().openImage("/Users/aimachine/Documents/IlastikJLM/datasets_for_ilastic_training/Stage3Training/TestEtrack/TestSingleTime/RawData.tif");
		impB.show();
		
		ImagePlus impC = new Opener().openImage("/Users/aimachine/Documents/IlastikJLM/datasets_for_ilastic_training/Stage3Training/TestEtrack/TestSingleTime/SmoothMulticut.tif");
		impC.show();
		
		ImagePlus impA = new Opener().openImage("/Users/aimachine/Documents/IlastikJLM/datasets_for_ilastic_training/Stage3Training/TestEtrack/TestSingleTime/ProbabilityMap.tif");
		impA.show();
		IlastikEllipseFileChooser panel = new IlastikEllipseFileChooser();

		frame.getContentPane().add(panel, "Center");
		frame.setSize(panel.getPreferredSize());
		
	}
	
}
