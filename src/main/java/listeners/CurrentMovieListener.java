package listeners;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ij.IJ;
import pluginTools.Ellipse_FileChooser;
import pluginTools.InteractiveEllipseFit;

public class CurrentMovieListener implements ActionListener {

	final Ellipse_FileChooser parent;

	public CurrentMovieListener(Ellipse_FileChooser parent) {

		this.parent = parent;

	}

	@Override
	public void actionPerformed(final ActionEvent arg0) {

		parent.impA = IJ.getImage();
		
		
		parent.DoneCurr(parent.Cardframe);
	}

}
