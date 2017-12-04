package ellipsoidDetector;

import java.awt.TextComponent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import ij.IJ;
import pluginTools.InteractiveEllipseFit;
import pluginTools.InteractiveEllipseFit.ValueChange;
import utility.ShowView;

public class TlocListener implements TextListener {
	
	
	final InteractiveEllipseFit parent;
	
	
	public TlocListener(final InteractiveEllipseFit parent) {
		
		this.parent = parent;
		
	}
	
	@Override
	public void textValueChanged(TextEvent e) {
		final TextComponent tc = (TextComponent)e.getSource();
	   
		 tc.addKeyListener(new KeyListener(){
			 @Override
			    public void keyTyped(KeyEvent arg0) {
				   
			    }

			    @Override
			    public void keyReleased(KeyEvent arg0) {
			    	

			    }

			    @Override
			    public void keyPressed(KeyEvent arg0) {
			    	String s = tc.getText();
			    	if (arg0.getKeyChar() == KeyEvent.VK_ENTER)
					 {
			    		if (parent.thirdDimension > parent.thirdDimensionSize) {
							IJ.log("Max frame number exceeded, moving to last frame instead");
							parent.thirdDimension = parent.thirdDimensionSize;
						} else
							parent.thirdDimension = Integer.parseInt(s);
			    		ShowView show = new ShowView(parent);
					show.shownewT();
					// parent.timeslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.thirdDimension, parent.thirdDimensionsliderInit, parent.thirdDimensionSize, parent.scrollbarSize));
					parent.timeText.setText("Current time point = " + parent.thirdDimension);
					    parent.panelFirst.validate();
					    parent.panelFirst.repaint();
			    		parent.updatePreview(ValueChange.THIRDDIM);
					 }

			    }
			});
	

	

}

}
