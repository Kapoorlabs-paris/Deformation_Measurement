package curvatureUtils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;

import angleUtils.TableDisplay;
import ellipsoidDetector.Distance;
import ij.gui.ImageCanvas;
import ij.gui.Line;
import pluginTools.InteractiveSimpleEllipseFit;
import utility.Curvatureobject;
import utility.Roiobject;

public class DisplaySelected {

	
	public static void Display(final InteractiveSimpleEllipseFit parent) {
		
		parent.overlay.clear();
		Integer ID = (Integer) parent.table.getValueAt(parent.rowchoice, 0);
		final ArrayList<Line> resultlineroi = new ArrayList<Line>();
		for (ArrayList<Curvatureobject> Allcurrentcurvature : parent.AlllocalCurvature) {
			for (int index = 0; index < Allcurrentcurvature.size(); ++index) {

				Curvatureobject currentcurvature = Allcurrentcurvature.get(index);
				
				if(currentcurvature.Label == ID) {
					
					Line currentline = new Line(currentcurvature.cord[0],currentcurvature.cord[1] ,
							currentcurvature.cord[0], currentcurvature.cord[1]); 
					resultlineroi.add(currentline);
					parent.overlay.add(currentline);
				}
				
			}
			
		}
		
		parent.imp.setOverlay(parent.overlay);
		parent.imp.updateAndDraw();

		if (parent.impOrig != null) {
			parent.impOrig.setOverlay(parent.overlay);
			parent.impOrig.updateAndDraw();
		}
		
		
		
	}
	
	
	
	public static void select(final InteractiveSimpleEllipseFit parent) {

		if(parent.impOrig == null)
			parent.impOrig = parent.imp;
		
		if (parent.mvl != null)
			parent.impOrig.getCanvas().removeMouseListener(parent.mvl);
		parent.impOrig.getCanvas().addMouseListener(parent.mvl = new MouseListener() {

			final ImageCanvas canvas = parent.impOrig.getWindow().getCanvas();

			@Override
			public void mouseClicked(MouseEvent e) {

				int x = canvas.offScreenX(e.getX());
				int y = canvas.offScreenY(e.getY());
				parent.Clickedpoints[0] = x;
				parent.Clickedpoints[1] = y;

				if (SwingUtilities.isLeftMouseButton(e) && e.isShiftDown()) {
					

					CurvatureTableDisplay.displayclicked(parent, parent.rowchoice);
					
					if (!parent.jFreeChartFrame.isVisible())
						parent.jFreeChartFrame = utility.ChartMaker.display(parent.chart, new Dimension(500, 500));
				}

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

	public static void mark(final InteractiveSimpleEllipseFit parent) {

		if (parent.impOrig == null)
			parent.impOrig = parent.imp;
		if (parent.ml != null)
			parent.impOrig.getCanvas().removeMouseMotionListener(parent.ml);
		parent.impOrig.getCanvas().addMouseMotionListener(parent.ml = new MouseMotionListener() {

			final ImageCanvas canvas = parent.impOrig.getWindow().getCanvas();

			@Override
			public void mouseMoved(MouseEvent e) {

				int x = canvas.offScreenX(e.getX());
				int y = canvas.offScreenY(e.getY());

				final HashMap<Integer, double[]> loc = new HashMap<Integer, double[]>();

				loc.put(0, new double[] { x, y });

				double distmin = Double.MAX_VALUE;
				if (parent.tablesize > 0) {
					NumberFormat f = NumberFormat.getInstance();
					for (int row = 0; row < parent.tablesize; ++row) {
						String CordX = (String) parent.table.getValueAt(row, 1);
						String CordY = (String) parent.table.getValueAt(row, 2);

						String CordZ = (String) parent.table.getValueAt(row, 3);

						double dCordX = 0, dCordZ = 0, dCordY = 0;
						try {
							dCordX = f.parse(CordX).doubleValue();

							dCordY = f.parse(CordY).doubleValue();
							dCordZ = f.parse(CordZ).doubleValue();
						} catch (ParseException e1) {

						}
						double dist = Distance.DistanceSq(new double[] { dCordX, dCordY }, new double[] { x, y });
						if (Distance.DistanceSq(new double[] { dCordX, dCordY }, new double[] { x, y }) < distmin
								&& parent.thirdDimension == (int) dCordZ && parent.ndims > 3) {

							parent.rowchoice = row;
							distmin = dist;

						}
						if (Distance.DistanceSq(new double[] { dCordX, dCordY }, new double[] { x, y }) < distmin
								&& parent.ndims <= 3) {

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
					parent.panelSecond.repaint();
					parent.panelSecond.validate();

				}

			}

			@Override
			public void mouseDragged(MouseEvent e) {

			}

		});

	}
}
