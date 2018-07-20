package listeners;


import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.JComboBox;

import ij.WindowManager;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import pluginTools.IlastikEllipseFileChooser;

public class ChooseOrigMap implements ActionListener {

	
	final IlastikEllipseFileChooser parent;
	final JComboBox<String> choice;
	
	public ChooseOrigMap(final IlastikEllipseFileChooser parent, final JComboBox<String> choice) {
		
		this.parent = parent;
		this.choice = choice;
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {

		String imagename = (String) choice.getSelectedItem();
		
	
		
	    	parent.impOrig = WindowManager.getImage(imagename);
	    	
	    	if(parent.impOrig==null)
	    	 	parent.impOrig = parent.impA;
			
			
			parent.calibration = parent.impOrig.getCalibration().pixelWidth;
			
			parent.inputFieldcalX.setText(String.valueOf(new DecimalFormat("#.###").format(parent.calibration)));
	}
	
	
	
}
