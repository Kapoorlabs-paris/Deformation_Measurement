package pluginTools;

import javax.swing.JFrame;

import ij.ImageJ;

public class InteractiveIlastikEllipseFit {

	

	public static void main(String[] args) {

		new ImageJ();
		JFrame frame = new JFrame("");
		IlastikEllipseFileChooser panel = new IlastikEllipseFileChooser();

		frame.getContentPane().add(panel, "Center");
		frame.setSize(panel.getPreferredSize());
	}
	
}
