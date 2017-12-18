package listeners;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;

import ij.io.Opener;
import pluginTools.Ellipse_FileChooser;

public class UploadMovieListener implements ActionListener {
	
	
	final Ellipse_FileChooser parent;

	public UploadMovieListener(Ellipse_FileChooser parent) {

		this.parent = parent;

	}

	@Override
	public void actionPerformed(final ActionEvent arg0) {

		int result;

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
		
		parent.impA = new Opener().openImage(parent.chooserA.getSelectedFile().getPath());
		
		parent.Done(parent.Cardframe);

	
	}
	

}
