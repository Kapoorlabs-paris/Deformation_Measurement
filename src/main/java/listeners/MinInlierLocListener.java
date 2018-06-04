package listeners;

import java.awt.TextComponent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import ij.IJ;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.logic.BitType;
import pluginTools.InteractiveSimpleEllipseFit;
import pluginTools.InteractiveSimpleEllipseFit.ValueChange;
import utility.ShowView;

public class MinInlierLocListener implements TextListener {
	
	
	final InteractiveSimpleEllipseFit parent;
	
	boolean pressed;
	public MinInlierLocListener(final InteractiveSimpleEllipseFit parent, final boolean pressed) {
		
		this.parent = parent;
		this.pressed = pressed;
		
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
			    	
			    	if (arg0.getKeyChar() == KeyEvent.VK_ENTER ) {
						
						
						pressed = false;
						
					}

			    }

			    @Override
			    public void keyPressed(KeyEvent arg0) {
			    	String s = tc.getText();
			    	if (arg0.getKeyChar() == KeyEvent.VK_ENTER&& !pressed) {
						pressed = true;
			    		
							parent.minNumInliers = Integer.parseInt(s);
			  
					parent.minInlierText.setText("Min Inliers = " + parent.minNumInliers);
					parent.minNumInliersmax = Math.max(parent.minNumInliers, parent.minNumInliersmax);
					parent.minInlierslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.minNumInliers, parent.minNumInliersmin, parent.minNumInliersmax, parent.scrollbarSize));
					parent.StartCurvatureComputingCurrent();
					parent.minInlierslider.repaint();
					parent.minInlierslider.validate();
					
					
					
			    		
					 }

			    }
			});
	

	

}

}
