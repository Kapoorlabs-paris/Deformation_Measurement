package utility;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;

import ij.gui.ImageCanvas;
import ij.gui.Roi;
import pluginTools.InteractiveEllipseFit;

public class MarkNew {

	
	final InteractiveEllipseFit parent;
	
	public MarkNew(final InteractiveEllipseFit parent) {
		
		
		this.parent = parent;
	}
	
	
	public  void mark() {
		
		
		parent.imp.getCanvas().addMouseMotionListener(parent.ml = new MouseMotionListener() {
			
			final ImageCanvas canvas = parent.imp.getWindow().getCanvas();
			Roi lastnearest = null;
			
			@Override
			public void mouseMoved(MouseEvent e) {
				
				int x = canvas.offScreenX(e.getX());
                int y = canvas.offScreenY(e.getY());

              
                final HashMap<Integer,  double[] > loc = new HashMap<Integer, double[]>();
                
                loc.put(0, new double[] { x, y });
                
                Color roicolor;
                Roiobject currentobject;
                if (parent.ZTRois.get(parent.uniqueID) == null) {
                roicolor = parent.defaultRois;
                
                currentobject =  parent.DefaultZTRois.entrySet().iterator().next().getValue();
                
                }
                else {
                	roicolor = parent.confirmedRois;
                
                	currentobject = parent.ZTRois.get(parent.uniqueID);
                	
                }
                parent.nearestRoiCurr = NearestRoi.getNearestRois(currentobject, loc.get(0), parent);
            
                if(parent.nearestRoiCurr!=null) {
                parent.nearestRoiCurr.setStrokeColor(Color.ORANGE);
               
                if (lastnearest!=parent.nearestRoiCurr && lastnearest!= null)
                	lastnearest.setStrokeColor(roicolor);

                
                lastnearest = parent.nearestRoiCurr;
                
                parent.imp.updateAndDraw();
                }
				
			}
			
			

			@Override
			public void mouseDragged(MouseEvent e) {
				
			}
			
			
		});
		
		
	}
	
	
}
