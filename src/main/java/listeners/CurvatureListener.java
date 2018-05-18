package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.imglib2.img.display.imagej.ImageJFunctions;
import pluginTools.InteractiveSimpleEllipseFit;

public class CurvatureListener implements ActionListener {

	final InteractiveSimpleEllipseFit parent;

	public CurvatureListener(final InteractiveSimpleEllipseFit parent) {

		this.parent = parent;
	}

	
	// For curvatrue
	@Override
	public void actionPerformed(ActionEvent e) {
		
		
		
		if(parent.curveautomode) {
			
			parent.emptysmooth = utility.Binarization.CreateBinaryBit(parent.originalimgsmooth, parent.lowprob, parent.highprob);
			parent.empty = utility.Binarization.CreateBinaryBit(parent.originalimg, parent.lowprob, parent.highprob);
		
			parent.StartCurvatureComputing();
		}
			
			if(parent.curvesupermode) {
				
				parent.empty = utility.Binarization.CreateBinaryBit(parent.originalimg, parent.lowprob, parent.highprob);
				
				parent.StartCurvatureComputing();
			}
				
				
		
		
		
		
		

	}

	
	
}
