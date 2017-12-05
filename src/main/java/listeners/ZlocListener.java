package listeners;

import java.awt.TextComponent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import ij.IJ;
import pluginTools.InteractiveEllipseFit;
import pluginTools.InteractiveEllipseFit.ValueChange;
import utility.ShowView;

public class ZlocListener implements TextListener {

	final InteractiveEllipseFit parent;

	boolean pressed;
	public ZlocListener(final InteractiveEllipseFit parent, boolean pressed) {

		this.parent = parent;

		this.pressed = pressed;
	}

	@Override
	public void textValueChanged(TextEvent e) {
		final TextComponent tc = (TextComponent) e.getSource();

		
		tc.addKeyListener(new KeyListener() {
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
				if (arg0.getKeyChar() == KeyEvent.VK_ENTER && !pressed) {
					pressed = true;
					if (parent.thirdDimension > parent.thirdDimensionSize) {
						IJ.log("Max frame number exceeded, moving to last frame instead");
						parent.thirdDimension = parent.thirdDimensionSize;
					} else
						parent.thirdDimension = Integer.parseInt(s);
					ShowView show = new ShowView(parent);
					show.shownewZ();
					
					parent.zText.setText("Current Z location = " + parent.thirdDimension);
					parent.updatePreview(ValueChange.THIRDDIMmouse);
					
				}

			}
		});
		
		

	}
}