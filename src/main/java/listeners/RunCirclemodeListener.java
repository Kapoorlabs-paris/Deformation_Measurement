package listeners;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import comboSliderTextbox.SliderBoxGUI;
import pluginTools.InteractiveSimpleEllipseFit;

public class RunCirclemodeListener implements ItemListener {

	InteractiveSimpleEllipseFit parent;

	public RunCirclemodeListener(InteractiveSimpleEllipseFit parent) {
		this.parent = parent;
	}

	@Override
	public void itemStateChanged(final ItemEvent arg0) {

		if (arg0.getStateChange() == ItemEvent.DESELECTED) {

			parent.circlefits = false;
			parent.celltrackcirclefits = false;
			parent.pixelcelltrackcirclefits = false;
			
			
			parent.polynomialfits = true;

		}

		else if (arg0.getStateChange() == ItemEvent.SELECTED) {

			parent.circlefits = true;
			parent.celltrackcirclefits = false;
			parent.pixelcelltrackcirclefits = false;
			parent.polynomialfits = false;

			parent.Angleselect.removeAll();

		 parent.resolution = 1;

			parent.Angleselect.add(parent.indistText, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,
					GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));

			parent.Angleselect.add(parent.interiorfield, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0,
					GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
			
		//	parent.Angleselect.add(outdistText, new GridBagConstraints(3, 2, 2, 1, 0.0, 0.0,
		//			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));

		//	parent.Angleselect.add(exteriorfield, new GridBagConstraints(3, 3, 2, 1, 0.0, 0.0,
		//			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));

			SliderBoxGUI combominInlier = new SliderBoxGUI(parent.mininlierstring, parent.minInlierslider,
					parent.minInlierField, parent.minInlierText, parent.scrollbarSize, parent.minNumInliers,
					parent.minNumInliersmax);

			parent.Angleselect.add(combominInlier.BuildDisplay(), new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0,
					GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
			
		//	parent.Angleselect.add(CurrentCurvaturebutton, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
		//			GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
			parent.Angleselect.add(parent.Curvaturebutton, new GridBagConstraints(3, 2, 2, 1, 0.0, 0.0,
					GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));

		//	parent.Angleselect.add(parent.displayCircle, new GridBagConstraints(3, 0, 2, 1, 0.0, 0.0,
		//			GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
	//		parent.Angleselect.add(parent.displaySegments, new GridBagConstraints(3, 1, 2, 1, 0.0, 0.0,
	//				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));

			


			parent.Angleselect.setBorder(parent.circletools);
			parent.panelFirst.add(parent.Angleselect, new GridBagConstraints(5, 1, 5, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
		parent.Angleselect.validate();
			parent.Angleselect.repaint();
		}

	}

}