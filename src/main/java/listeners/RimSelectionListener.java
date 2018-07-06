package listeners;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import comboSliderTextbox.SliderBoxGUI;
import ij.gui.GenericDialog;
import mpicbg.imglib.image.display.Display;
import pluginTools.InteractiveSimpleEllipseFit;
import rectangleListeners.RectheightListener;
import rectangleListeners.RectoffsetListener;
import rectangleListeners.RectwidthListener;
import utility.DisplayAuto;

public class RimSelectionListener implements ActionListener {

	final InteractiveSimpleEllipseFit parent;

	public RimSelectionListener(final InteractiveSimpleEllipseFit parent) {

		this.parent = parent;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		RectangleSelectDialog();

	}

	public int minVal = 0;
	public int minoffset = -100;
	public int maxoffset = 100;
	public int scrollbarSize = 1000;
	public int height = 5;
	public int width = 5;
	public int offset = 0;
	public int maxheight = 1000;
	public int maxwidth = 1000;
	public JFrame Cardframe = new JFrame("Select Intensity measurment region");
	public JScrollBar heightslider = new JScrollBar(Scrollbar.HORIZONTAL, minVal, 10, 0, 10 + scrollbarSize);
	public JScrollBar widthslider = new JScrollBar(Scrollbar.HORIZONTAL, minVal, 10, 0, 10 + scrollbarSize);
	public JScrollBar offsetslider = new JScrollBar(Scrollbar.HORIZONTAL, minVal, 10, 0, 10 + scrollbarSize);
	public JPanel IntensityRegion = new JPanel();

	public String heightstring = "Rectangle height";
	public String widthstring = "Rectangle width";
	public String offsetstring = "Rectangle offset";

	public TextField heightField = new TextField(5);
	public Label heightText = new Label("Rectangle height  = " + height, Label.CENTER);

	public TextField widthField = new TextField(5);
	public Label widthText = new Label("Rectangle width  = " + width, Label.CENTER);

	public TextField offsetField = new TextField(5);
	public Label offsetText = new Label("Rectangle offset  = " + offset, Label.CENTER);

	public void RectangleSelectDialog() {
		IntensityRegion.setLayout(parent.layout);

		heightField.setText(Integer.toString(height));
		widthField.setText(Integer.toString(height));
		offsetslider.setValue(
				utility.Slicer.computeScrollbarPositionFromValue(offset, minoffset, maxoffset, scrollbarSize));
		heightslider
				.setValue(utility.Slicer.computeScrollbarPositionFromValue(height, minVal, maxheight, scrollbarSize));

		widthslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(width, minVal, maxwidth, scrollbarSize));

		SliderBoxGUI comboheight = new SliderBoxGUI(heightstring, heightslider, heightField, heightText, scrollbarSize,
				height, maxheight);

		IntensityRegion.add(comboheight.BuildDisplay(), new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));

		SliderBoxGUI combowidth = new SliderBoxGUI(widthstring, widthslider, widthField, widthText, scrollbarSize,
				width, maxwidth);

		IntensityRegion.add(combowidth.BuildDisplay(), new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));

		SliderBoxGUI combooffset = new SliderBoxGUI(offsetstring, offsetslider, offsetField, offsetText, scrollbarSize,
				offset, maxoffset);

		IntensityRegion.add(combooffset.BuildDisplay(), new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));

		
		
		heightslider.addAdjustmentListener(
				new RectheightListener(this, heightText, heightstring, minVal, maxheight, scrollbarSize, heightslider));
		widthslider.addAdjustmentListener(
				new RectwidthListener(this, widthText, widthstring, minVal, maxwidth, scrollbarSize, widthslider));
		offsetslider.addAdjustmentListener(
				new RectoffsetListener(this, offsetText, offsetstring, minoffset, maxoffset, scrollbarSize, offsetslider));
		
		IntensityRegion.setPreferredSize(new Dimension(parent.SizeX, parent.SizeY));
		Cardframe.add(IntensityRegion, "Center");

		Cardframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Cardframe.pack();
		Cardframe.setVisible(true);
	}

}
