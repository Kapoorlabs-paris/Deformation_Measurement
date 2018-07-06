package rectangleListeners;

import java.awt.Rectangle;
import java.awt.TextComponent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import ij.plugin.frame.RoiManager;
import listeners.RimSelectionListener;
import pluginTools.InteractiveSimpleEllipseFit;


public class RectoffsetYLocListener implements TextListener {

	final RimSelectionListener parent;
    final InteractiveSimpleEllipseFit grandparent;
	boolean pressed;

	public RectoffsetYLocListener(final RimSelectionListener parent,InteractiveSimpleEllipseFit grandparent, final boolean pressed) {

		this.parent = parent;
		this.pressed = pressed;
		this.grandparent = grandparent;

	}

	@Override
	public void textValueChanged(TextEvent e) {
		final TextComponent tc = (TextComponent) e.getSource();
		String s = tc.getText();
		if (s.length() > 0) {
			parent.offsetY = Integer.parseInt(s);

			parent.offsetYText.setText(parent.offsetYstring + " = " + parent.offsetY);
			parent.maxoffsetY = Math.max(parent.offsetY, parent.maxoffsetY	);
			parent.offsetYslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.offsetY,
					parent.minoffsetY, parent.maxoffsetY, parent.scrollbarSize));
			parent.offsetYslider.repaint();
			parent.offsetYslider.validate();

		}
		tc.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent arg0) {

			}

			@Override
			public void keyReleased(KeyEvent arg0) {

				if (arg0.getKeyChar() == KeyEvent.VK_ENTER) {

					pressed = false;

				}

			}

			@Override
			public void keyPressed(KeyEvent arg0) {

				if (arg0.getKeyChar() == KeyEvent.VK_ENTER && !pressed) {
					pressed = true;

					Setroi.Setup(parent, grandparent);
				}

			}
		});

	}

}
