package utility;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import ij.gui.ImageCanvas;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.plugin.frame.RoiManager;
import pluginTools.InteractiveEllipseFit;
import pluginTools.InteractiveEllipseFit.ValueChange;

public class SelectNew {

	

	
	
	public static void select(final InteractiveEllipseFit parent){
		
		
		if(parent.mvl!=null)
			 parent.imp.getCanvas().removeMouseListener(parent.mvl);
          parent.imp.getCanvas().addMouseListener(parent.mvl = new MouseListener() {


  			final ImageCanvas canvas = parent.imp.getWindow().getCanvas();
			

			


				@Override
				public void mouseClicked(MouseEvent e) {
					
		        	  int x = canvas.offScreenX(e.getX());
		          int y = canvas.offScreenY(e.getY());
		          
		          parent.Clickedpoints[0] = x;
		          parent.Clickedpoints[1] = y;
					LeftClick.LeftRightClick(x, y, e, parent);
				}

				@Override
				public void mousePressed(MouseEvent e) {

					
				
					
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					
					
					    	
						
					

				}

				@Override
				public void mouseEntered(MouseEvent e) {

				}

				@Override
				public void mouseExited(MouseEvent e) {

				}
			});

		
		
		
		
		
		
		
	}

	
}
