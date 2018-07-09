package listeners;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Label;
import java.awt.Rectangle;
import java.awt.Scrollbar;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import comboSliderTextbox.SliderBoxGUI;
import ij.gui.GenericDialog;
import ij.gui.Roi;
import ij.plugin.frame.RoiManager;
import mpicbg.imglib.image.display.Display;
import pluginTools.InteractiveSimpleEllipseFit;
import rectangleListeners.DoneListener;
import rectangleListeners.MarkCenterListener;
import rectangleListeners.MarkListener;
import rectangleListeners.RectheightListener;
import rectangleListeners.RectheightLocListener;
import rectangleListeners.RectoffsetListener;
import rectangleListeners.RectoffsetLocListener;
import rectangleListeners.RectoffsetYListener;
import rectangleListeners.RectoffsetYLocListener;
import rectangleListeners.RectwidthListener;
import rectangleListeners.RectwidthLocListener;
import utility.DisplayAuto;

public class RimSelectionListener implements ActionListener {

	final InteractiveSimpleEllipseFit parent;
	RoiManager roimanager;
	public RimSelectionListener(final InteractiveSimpleEllipseFit parent) {

		this.parent = parent;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		 roimanager = RoiManager.getInstance();

		if (roimanager == null) {
			roimanager = new RoiManager();
		}
		RectangleSelectDialog();

	}

	public int minVal = 0;
	public int minoffsetX = -100;
	public int maxoffsetX = 100;
	public int minoffsetY = -100;
	public int maxoffsetY = 100;
	public JButton Donebutton = new JButton("Done");
	public JButton Resetbutton = new JButton("Reset Offset");
	public JButton Markbutton = new JButton("Mark boundary point");
	public JButton Markcenterbutton = new JButton("Mark center point");
	
	public int scrollbarSize = 1000;
	public int height = 5;
	public int width = 5;
	public int offset = 0;
	public int offsetY = 0;
	public int maxheight = 1000;
	public int maxwidth = 1000;
	public JFrame Cardframe = new JFrame("Select Intensity measurment region");
	public JScrollBar heightslider = new JScrollBar(Scrollbar.HORIZONTAL, minVal, 10, 0, 10 + scrollbarSize);
	public JScrollBar widthslider = new JScrollBar(Scrollbar.HORIZONTAL, minVal, 10, 0, 10 + scrollbarSize);
	public JScrollBar offsetslider = new JScrollBar(Scrollbar.HORIZONTAL, minVal, 10, 0, 10 + scrollbarSize);
	public JScrollBar offsetYslider = new JScrollBar(Scrollbar.HORIZONTAL, minVal, 10, 0, 10 + scrollbarSize);
	
	public JPanel IntensityRegion = new JPanel();

	public String heightstring = "Rectangle height";
	public String widthstring = "Rectangle width";
	public String offsetstring = "Rectangle offset X";
	public String offsetYstring = "Rectangle offset Y";
	

	public TextField heightField = new TextField(5);
	public Label heightText = new Label("Rectangle height  = " + height, Label.CENTER);

	public TextField widthField = new TextField(5);
	public Label widthText = new Label("Rectangle width  = " + width, Label.CENTER);

	public TextField offsetField = new TextField(5);
	public Label offsetText = new Label("Rectangle offset X = " + offset, Label.CENTER);
	
	public TextField offsetYField = new TextField(5);
	public Label offsetYText = new Label("Rectangle offset Y = " + offsetY, Label.CENTER);
	
	public Rectangle standardRectangle;
	public void RectangleSelectDialog() {
		
		minoffsetX = -(int) parent.originalimg.dimension(0) / 2;
		 maxoffsetX = (int)parent.originalimg.dimension(0) / 2;
		 minoffsetY = -(int) parent.originalimg.dimension(1) / 2;
		maxoffsetY = (int) parent.originalimg.dimension(1) / 2;
		
	    maxheight = (int) parent.originalimg.dimension(1);
		 maxwidth = (int) parent.originalimg.dimension(0);
		
		
		
		standardRectangle = new Rectangle((int)parent.originalimg.dimension(0) / 2, (int)parent.originalimg.dimension(0) / 2, height,
				width);
		if(parent.imp!=null)
			parent.imp.setRoi(standardRectangle);
		
		
		Roi roi = parent.imp.getRoi();
		if(roi.getType() == Roi.RECTANGLE)
		roimanager.addRoi(parent.imp.getRoi());
		
		roimanager.select(0);
		IntensityRegion.setLayout(parent.layout);

		heightField.setText(Integer.toString(height));
		widthField.setText(Integer.toString(height));
		offsetslider.setValue(
				utility.Slicer.computeScrollbarPositionFromValue(offset, minoffsetX, maxoffsetX, scrollbarSize));
		offsetYslider.setValue(
				utility.Slicer.computeScrollbarPositionFromValue(offsetY, minoffsetY, maxoffsetY, scrollbarSize));
		
		heightslider
				.setValue(utility.Slicer.computeScrollbarPositionFromValue(height, minVal, maxheight, scrollbarSize));

		widthslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(width, minVal, maxwidth, scrollbarSize));

		SliderBoxGUI comboheight = new SliderBoxGUI(heightstring, heightslider, heightField, heightText, scrollbarSize,
				height, maxheight);

		IntensityRegion.add(comboheight.BuildDisplay(), new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));

		SliderBoxGUI combowidth = new SliderBoxGUI(widthstring, widthslider, widthField, widthText, scrollbarSize,
				width, maxwidth);

		IntensityRegion.add(combowidth.BuildDisplay(), new GridBagConstraints(4, 0, 3, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));

		SliderBoxGUI combooffset = new SliderBoxGUI(offsetstring, offsetslider, offsetField, offsetText, scrollbarSize,
				offset, maxoffsetX);

		IntensityRegion.add(combooffset.BuildDisplay(), new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));

		SliderBoxGUI combooffsetY = new SliderBoxGUI(offsetYstring, offsetYslider, offsetYField, offsetYText, scrollbarSize,
				offsetY, maxoffsetY);

		IntensityRegion.add(combooffsetY.BuildDisplay(), new GridBagConstraints(4, 2, 3, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
		
		IntensityRegion.add(Resetbutton,  new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
		
		IntensityRegion.add(Markbutton,  new GridBagConstraints(4, 3, 3, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
		
		//IntensityRegion.add(Markcenterbutton,  new GridBagConstraints(4, 4, 3, 1, 0.0, 0.0,
			//	GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
		
		IntensityRegion.add(Donebutton,  new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
		
		
		heightslider.addAdjustmentListener(
				new RectheightListener(this, parent, heightText, heightstring, minVal, maxheight, scrollbarSize, heightslider));
		widthslider.addAdjustmentListener(
				new RectwidthListener(this, parent, widthText, widthstring, minVal, maxwidth, scrollbarSize, widthslider));
		offsetslider.addAdjustmentListener(
				new RectoffsetListener(this, parent, offsetText, offsetstring, minoffsetX, maxoffsetX, scrollbarSize, offsetslider));
		offsetYslider.addAdjustmentListener(
				new RectoffsetYListener(this, parent, offsetYText, offsetYstring, minoffsetY, maxoffsetY, scrollbarSize, offsetYslider));
		
		
        heightField.addTextListener(new RectheightLocListener(this,parent, false)); 		
        widthField.addTextListener(new RectwidthLocListener(this, parent,  false)); 	
        offsetField.addTextListener(new RectoffsetLocListener(this, parent, false)); 	
        offsetYField.addTextListener(new RectoffsetYLocListener(this,parent,  false)); 
		Donebutton.addActionListener(new DoneListener(this, parent));
		Resetbutton.addActionListener(new ResetListener(this, parent));
		Markbutton.addActionListener(new MarkListener(this, parent));
		Markcenterbutton.addActionListener(new MarkCenterListener(this, parent));
		
		
		IntensityRegion.setPreferredSize(new Dimension(parent.SizeX, parent.SizeY));
		Cardframe.add(IntensityRegion, "Center");

		Cardframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Cardframe.pack();
		Cardframe.setVisible(true);
	}

}
