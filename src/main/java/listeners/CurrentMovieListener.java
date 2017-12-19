package listeners;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ij.IJ;
import pluginTools.EllipseFileChooser;
import pluginTools.InteractiveEllipseFit;

public class CurrentMovieListener implements ActionListener {

	final EllipseFileChooser parent;

	public CurrentMovieListener(EllipseFileChooser parent) {

		this.parent = parent;

	}

	@Override
	public void actionPerformed(final ActionEvent arg0) {

		parent.impA = IJ.getImage();
		
		if(parent.impA!=null)
		parent.DoneCurr(parent.Cardframe);
	}

}
