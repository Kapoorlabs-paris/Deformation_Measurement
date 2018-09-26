package pluginTools;

import javax.swing.JFrame;

import ij.ImageJ;
import ij.ImagePlus;
import ij.io.Opener;

public class InteractiveIlastikEllipseFit {

	

	public static void main(String[] args) {

		//new ImageJ();
		JFrame frame = new JFrame("");
		// /Users/aimachine/Documents/IlastikJLM/datasets_for_ilastic_training/Stage3Training/TestEtrack/TestTimeLapse/Test3_1/RawData.tif
		// /Users/aimachine/Documents/IlastikJLM/datasets_for_ilastic_training/Stage3Training/TestEtrack/TestTimeLapse/Test3_1/MulticutSegmentation.tif
		// /Users/aimachine/Documents/IlastikJLM/datasets_for_ilastic_training/Stage3Training/TestEtrack/TestTimeLapse/Test3_1/BoundaryProbability.tif
		///Users/aimachine/Documents/TalkCurie
		///Users/aimachine/Documents/CurvatureTest/MultiCircles.tif
		///Users/aimachine/Documents/CurvatureTest/Circles.tif	
		//"/Users/aimachine/Documents/CurvatureTest/GroundTruthEllipseTest/RawImage.tif"
				//"/Users/aimachine/Documents/CurvatureTest/GroundTruthEllipseTest/FakeMultiActual.tif"
		///Users/aimachine/Documents/CurvatureTest/images_and_paper_for_curvature_measurements/RealCurveRaw.tif
		///Users/aimachine/Documents/CurvatureTest/images_and_paper_for_curvature_measurements/BoundaryProbability.tif
		///Users/aimachine/Documents/CurvatureTest/images_and_paper_for_curvature_measurements/MultiCut.tif
		ImagePlus impB = new Opener().openImage("/Users/aimachine/Documents/JLMCurvature/RegisteredImages/RawTest.tif");
		impB.show();
		
	
		
	
		
		ImagePlus impA = new Opener().openImage("/Users/aimachine/Documents/JLMCurvature/RegisteredImages/MulticutTest.tif");
		impA.show();
		
		
		IlastikEllipseFileChooser panel = new IlastikEllipseFileChooser();

		frame.getContentPane().add(panel, "Center");
		frame.setSize(panel.getPreferredSize());
		
	}
	
}
