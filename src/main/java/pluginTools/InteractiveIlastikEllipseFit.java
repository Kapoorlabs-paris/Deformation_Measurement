package pluginTools;

import javax.swing.JFrame;

import ij.ImageJ;
import ij.ImagePlus;
import ij.io.Opener;

public class InteractiveIlastikEllipseFit {

	public static void main(String[] args) {

		new ImageJ();
		JFrame frame = new JFrame("");

		ImagePlus impB = new Opener()
				.openImage("/Users/aimachine/Documents/Ozga_curvature/Test/Binary/Binary_20180905_4x16_1_44um.tif");
		impB.show();

		ImagePlus impA = new Opener()
				.openImage("/Users/aimachine/Documents/Ozga_curvature/Test/c1/C1-20180905_4x16_1_44um.tif");
		impA.show();

		IlastikEllipseFileChooser panel = new IlastikEllipseFileChooser();

		frame.getContentPane().add(panel, "Center");
		frame.setSize(panel.getPreferredSize());

	}

}
