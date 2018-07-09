package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ij.gui.Roi;
import ij.plugin.frame.RoiManager;
import pluginTools.InteractiveSimpleEllipseFit;
import rectangleListeners.SetRectangle;

public class ResetListener implements ActionListener {
	
	
	final RimSelectionListener parent;
	final InteractiveSimpleEllipseFit grandparent;
	
	public ResetListener(final RimSelectionListener parent, final InteractiveSimpleEllipseFit grandparent){
		
		this.parent = parent;
		this.grandparent = grandparent;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		parent.offset = 0;
		parent.offsetY = 0;
		
		   parent.heightField.setText(Integer.toString(parent.height));
		      parent.widthField.setText(Integer.toString(parent.width));
		      parent.offsetField.setText(Integer.toString(parent.offset));
		      parent.offsetYField.setText(Integer.toString(parent.offsetY));
		      
		      System.out.println(parent.offset + " " + parent.offsetY);
		      SetRectangle rect = new SetRectangle(parent, grandparent);
		      rect.setRect();

}

}
