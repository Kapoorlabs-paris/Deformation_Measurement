package rectangleListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ellipsoidDetector.Distance;
import ij.gui.Roi;
import ij.plugin.frame.RoiManager;
import listeners.RimSelectionListener;
import pluginTools.InteractiveSimpleEllipseFit;

public class DoneListener implements ActionListener {

	final RimSelectionListener parent;
	final InteractiveSimpleEllipseFit grandparent;

	public DoneListener(final RimSelectionListener parent, final InteractiveSimpleEllipseFit grandparent) {

		this.parent = parent;
		this.grandparent = grandparent;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		getStrip();
		parent.Cardframe.dispose();
		grandparent.imp.getCanvas().removeMouseListener(grandparent.ovalml);
		RoiManager roimanager = RoiManager.getInstance();
		if (roimanager != null)
			roimanager.close();
		if (grandparent.imp != null) {

			grandparent.imp.getOverlay().clear();

		}
		grandparent.imp.updateAndDraw();

	}

	public void getStrip() {

		final double upperslope = parent.standardRectangle.getMinY();

		final double lowerslope = parent.standardRectangle.getMaxY();

		if (grandparent.boundarypoint != null) {
			grandparent.usedefaultrim = false;
			System.out.println(grandparent.boundarypoint[0] + " " + grandparent.boundarypoint[1] + " " + upperslope);

			final double insidedistance = Distance.DistanceSq(
					new double[] { grandparent.boundarypoint[0], grandparent.boundarypoint[1] },
					new double[] { grandparent.boundarypoint[0], upperslope });
			grandparent.insidedistance = Math.sqrt(insidedistance);

			final double outerdistance = Distance.DistanceSq(
					new double[] { grandparent.boundarypoint[0], grandparent.boundarypoint[1] },
					new double[] { grandparent.boundarypoint[0], lowerslope });
			grandparent.outsidedistance = Math.sqrt(outerdistance);

			System.out.println(grandparent.insidedistance + " " + grandparent.outsidedistance + " Dist ");
		}

		else
			grandparent.usedefaultrim = true;
	}

}
