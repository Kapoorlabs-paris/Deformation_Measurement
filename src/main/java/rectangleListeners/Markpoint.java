package rectangleListeners;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.SwingUtilities;

import ij.gui.ImageCanvas;
import ij.gui.Line;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import listeners.RimSelectionListener;
import pluginTools.InteractiveSimpleEllipseFit;

public class Markpoint {

	final RimSelectionListener parent;
	final InteractiveSimpleEllipseFit grandparent;

	public Markpoint(final RimSelectionListener parent, final InteractiveSimpleEllipseFit grandparent) {

		this.parent = parent;
		this.grandparent = grandparent;

	}

	public void markend() {
		if (grandparent.ovalml != null)
			grandparent.imp.getCanvas().removeMouseListener(grandparent.ovalml);
		grandparent.imp.getCanvas().addMouseListener(grandparent.ovalml = new MouseListener() {

			final ImageCanvas canvas = grandparent.imp.getWindow().getCanvas();

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e) && e.isShiftDown() == true) {

					int x = canvas.offScreenX(e.getX());
					int y = canvas.offScreenY(e.getY());

					Overlay o = grandparent.imp.getOverlay();

					if (o == null) {
						o = new Overlay();

						grandparent.imp.setOverlay(o);

					}

					// Make roi
					grandparent.boundarypoint = new int[] { x, y };
					OvalRoi oval = new OvalRoi(x, y, 5, 5);
					oval.setStrokeColor(Color.RED);
					o.add(oval);
					grandparent.imp.updateAndDraw();
					if (grandparent.midpoint != null) {
						Line centerline = new Line(grandparent.boundarypoint[0], grandparent.boundarypoint[1],
								grandparent.midpoint[0], grandparent.midpoint[1]);
						o.add(centerline);
						grandparent.imp.updateAndDraw();
					}

				}

			}
		});
	
	}

	public void markcenter() {

		if (grandparent.ovalml != null)
			grandparent.imp.getCanvas().removeMouseListener(grandparent.ovalml);
		grandparent.imp.getCanvas().addMouseListener(grandparent.ovalml = new MouseListener() {

			final ImageCanvas canvas = grandparent.imp.getWindow().getCanvas();

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e) && e.isShiftDown() == true) {

					int x = canvas.offScreenX(e.getX());
					int y = canvas.offScreenY(e.getY());

					Overlay o = grandparent.imp.getOverlay();

					if (o == null) {
						o = new Overlay();

						grandparent.imp.setOverlay(o);

					}

					// Make roi
					grandparent.midpoint = new int[] { x, y };
					OvalRoi oval = new OvalRoi(x, y, 5, 5);
					o.add(oval);
					oval.setStrokeColor(Color.GREEN);
					grandparent.imp.updateAndDraw();
					if (grandparent.boundarypoint != null) {
						Line centerline = new Line(grandparent.boundarypoint[0], grandparent.boundarypoint[1],
								grandparent.midpoint[0], grandparent.midpoint[1]);
						o.add(centerline);
						grandparent.imp.updateAndDraw();
					}

				}

			}
		});
		
	}
}
