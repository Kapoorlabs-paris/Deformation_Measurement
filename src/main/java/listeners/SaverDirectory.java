package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import ij.IJ;
import pluginTools.InteractiveEllipseFit;


public class SaverDirectory implements ActionListener {
	
    InteractiveEllipseFit parent;
    
	public SaverDirectory(InteractiveEllipseFit parent) {

		this.parent = parent;

	}
	
	
	

	@Override
	public void actionPerformed(final ActionEvent arg0) {
		
		parent.chooserA = new JFileChooser();
		parent.chooserA.setCurrentDirectory(new java.io.File("."));
		
		parent.chooserA.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		//
		
		//
		if (parent.chooserA.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
			System.out.println("getCurrentDirectory(): " + parent.chooserA.getCurrentDirectory());
			System.out.println("getSelectedFile() : " + parent.chooserA.getSelectedFile());
		} else {
			System.out.println("No Selection ");
		}
		
		
	}

}
