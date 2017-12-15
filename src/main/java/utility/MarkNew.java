package utility;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;

import ellipsoidDetector.Distance;
import ij.gui.ImageCanvas;
import ij.gui.OvalRoi;
import ij.gui.Roi;
import pluginTools.InteractiveEllipseFit;
import pluginTools.InteractiveEllipseFit.ValueChange;

public class MarkNew {

	

	
	
	public static void mark(final InteractiveEllipseFit parent) {
		

		final ImageCanvas canvas = parent.imp.getWindow().getCanvas();
		
		
		canvas.removeMouseListener(parent.mvl);
		canvas.addMouseListener(parent.mvl = new MouseListener() {


			

			


			@Override
			public void mouseClicked(MouseEvent e) {
	
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
	        	  int x = canvas.offScreenX(e.getX());
		          int y = canvas.offScreenY(e.getY());
		          
		          parent.Clickedpoints[0] = x;
		          parent.Clickedpoints[1] = y;
		      	if(SwingUtilities.isLeftMouseButton(e) && !e.isShiftDown()  ){
		      		
		      		int index =	parent.roimanager.getRoiIndex(parent.nearestRoiCurr);
		      		parent.roimanager.select(index);
		      		parent.nearestRoiCurr.setStrokeColor(parent.colorChange);
		      		parent.nearestRoiCurr.setStrokeWidth(2);
		      		
		      		parent.imp.updateAndDraw();
		      		
		      		parent.updatePreview(ValueChange.DISPLAYROI);
		      		
		      		
		      		

		      	}
					
					if(SwingUtilities.isLeftMouseButton(e) && e.isShiftDown()  ){
						System.out.println("pressed");
						if (!parent.jFreeChartFrame.isVisible())
							parent.jFreeChartFrame = utility.ChartMaker.display(parent.chart, new Dimension(500, 500));
						
						parent.displayclicked(parent.rowchoice);
					}
				
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
		
		canvas.addMouseMotionListener(new MouseMotionListener() {
		
			Roi lastnearest = null;
			OvalRoi lastIntersectionnearest = null;
			
	
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
                if(currentobject.resultovalroi!=null) {
                parent.nearestIntersectionRoiCurr = NearestRoi.getNearestIntersectionRois(currentobject, loc.get(0), parent);
                
                if(parent.nearestIntersectionRoiCurr!=null) {
                	
                	parent.nearestIntersectionRoiCurr.setStrokeColor(Color.ORANGE);
                	
                	if(lastIntersectionnearest!=parent.nearestIntersectionRoiCurr && lastIntersectionnearest!=null)
                		lastIntersectionnearest.setStrokeColor(parent.colorDet);
                		lastIntersectionnearest = parent.nearestIntersectionRoiCurr;
                	
                	
                }
                }
            
                if(parent.nearestRoiCurr!=null) {
                parent.nearestRoiCurr.setStrokeColor(Color.ORANGE);
               
                if (lastnearest!=parent.nearestRoiCurr && lastnearest!= null)
                	lastnearest.setStrokeColor(roicolor);

                
                lastnearest = parent.nearestRoiCurr;
                
                parent.imp.updateAndDraw();
                }
        		
                double distmin = Double.MAX_VALUE;
                  if (parent.tablesize > 0) {
                	  
                	  
                       for (int row = 0; row < parent.tablesize; ++row  ) {
                       String CordX = (String) parent.table.getValueAt(row, 1);
                       String CordY = (String) parent.table.getValueAt(row, 2);
                       
                       double dCordX = Double.parseDouble(CordX);
                       double dCordY = Double.parseDouble(CordY);
                    
                       double dist = Distance.DistanceSq(new double[] {dCordX,  dCordY} , new double[] {x, y}) ;
                       if (Distance.DistanceSq(new double[] {dCordX,  dCordY} , new double[] {x, y}) < distmin) {
                       	
                       	parent.rowchoice = row;
                       	distmin = dist;
                       	
                       }
                       
                       }
                       
                   	parent.table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
           				@Override
           				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
           						boolean hasFocus, int row, int col) {

           					super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
           					if (row == parent.rowchoice) {
           						setBackground(Color.green);

           					} else {
           						setBackground(Color.white);
           					}
           					return this;
           				}
           			});
                   	
           			parent.table.validate();
           			parent.scrollPane.validate();
           			parent.panelFirst.repaint();
           			parent.panelFirst.validate();
                  }
        		
                
			}
			
			

			@Override
			public void mouseDragged(MouseEvent e) {

			}
			
			
		});
		
	

		
		
		
	}
	
	
}
