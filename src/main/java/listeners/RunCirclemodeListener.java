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
			parent.polynomialfits = true;

		}

		else if (arg0.getStateChange() == ItemEvent.SELECTED) {

			parent.circlefits = true;
			parent.polynomialfits = false;

			parent.Angleselect.removeAll();

			parent.Angleselect.add(parent.incrementText, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));

			parent.Angleselect.add(parent.incrementField, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0,
					GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));

			parent.Angleselect.add(parent.maxsizeText, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0,
					GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));

			parent.Angleselect.add(parent.maxSizeField, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0,
					GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));

		//	SliderBoxGUI combominInlier = new SliderBoxGUI(parent.mininlierstring, parent.minInlierslider,
		//			parent.minInlierField, parent.minInlierText, parent.scrollbarSize, parent.minNumInliers,
		//			parent.minNumInliersmax);

		//	parent.Angleselect.add(combominInlier.BuildDisplay(), new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0,
		//			GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
			parent.Angleselect.add(parent.CurrentCurvaturebutton, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
			parent.Angleselect.add(parent.Curvaturebutton, new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0,
					GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));

			parent.Angleselect.add(parent.displayCircle, new GridBagConstraints(3, 0, 3, 1, 0.0, 0.0,
					GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
			parent.Angleselect.add(parent.displaySegments, new GridBagConstraints(3, 1, 3, 1, 0.0, 0.0,
					GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));

			parent.Angleselect.add(parent.minsizeText, new GridBagConstraints(3, 2, 3, 1, 0.0, 0.0,
					GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));

			parent.Angleselect.add(parent.minSizeField, new GridBagConstraints(3, 3, 3, 1, 0.0, 0.0,
					GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
			
			parent.Angleselect.add(parent.ClearDisplay, new GridBagConstraints(3, 4, 3, 1, 0.0, 0.0,
					GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
			
			parent.Angleselect.add(parent.SelectRim, new GridBagConstraints(3, 5, 3, 1, 0.0, 0.0,
					GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
			
		
		
		

			parent.Angleselect.setBorder(parent.circletools);
			parent.Angleselect.setPreferredSize(new Dimension(parent.SizeX , parent.SizeY ));
			parent.panelFirst.add(parent.Angleselect, new GridBagConstraints(5, 1, 5, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
			parent.Angleselect.validate();
			parent.Angleselect.repaint();
		}

	}

}