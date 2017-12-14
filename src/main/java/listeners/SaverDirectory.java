package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;

import ij.IJ;
import pluginTools.InteractiveEllipseFit;


public class SaverDirectory implements ActionListener {
	
    InteractiveEllipseFit parent;
  
    
	public SaverDirectory(InteractiveEllipseFit parent) {

		this.parent = parent;
	

	}
	
	
	

	@Override
	public void actionPerformed(final ActionEvent arg0) {

		JFileChooser chooserA = new JFileChooser();
		if(parent.chooserA!=null)
		chooserA.setCurrentDirectory(parent.chooserA.getCurrentDirectory());
		else
		chooserA.setCurrentDirectory(new java.io.File("."));	
		chooserA.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooserA.showOpenDialog(parent.panelFirst);
		if(chooserA.getSelectedFile()!=null) {
		parent.chooserA = chooserA;
		parent.usefolder = chooserA.getSelectedFile().getAbsolutePath();
		parent.userfile = chooserA.getSelectedFile();
	
		System.out.println(parent.usefolder + " " + parent.addToName);
		}
	}

}
