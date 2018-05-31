package drawUtils;

import java.awt.Color;
import java.util.ArrayList;

import ij.ImagePlus;
import ij.gui.Line;
import ij.gui.Overlay;
import ransacPoly.RegressionFunction;


public class DrawFunction {

	
	public static void DrawPolynomial(final ImagePlus imp, final RegressionFunction regression ) {
		
		
		Overlay overlay = new Overlay();
		
		imp.setOverlay(overlay);
		
		for (int index = 0; index < regression.Curvaturepoints.size() - 1; ++index) {
			int xs = (int) regression.Curvaturepoints.get(index)[0];
			int xe = (int) regression.Curvaturepoints.get(index + 1)[0];
			
			int ys = (int)regression.regression.predict(xs);
			int ye = (int)regression.regression.predict(xe);
			Line line = new Line(xs, ys, xe, ye);
			overlay.add(line);
			line.setStrokeColor(Color.BLUE);
		
		}
		
		imp.updateAndDraw();
		
		
		
		
	}
	
}
