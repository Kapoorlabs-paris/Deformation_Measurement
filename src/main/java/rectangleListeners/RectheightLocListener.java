package rectangleListeners;

import java.awt.TextComponent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import listeners.RimSelectionListener;
import pluginTools.InteractiveSimpleEllipseFit;


public class RectheightLocListener implements TextListener {

	final RimSelectionListener parent;
    final InteractiveSimpleEllipseFit grandparent;
	boolean pressed;

	public RectheightLocListener(final RimSelectionListener parent, final InteractiveSimpleEllipseFit grandparent,  final boolean pressed) {

		this.parent = parent;
		this.pressed = pressed;
		this.grandparent = grandparent;

	}

	@Override
	public void textValueChanged(TextEvent e) {
		final TextComponent tc = (TextComponent) e.getSource();
		String s = tc.getText();
		if (s.length() > 0) {
			parent.height = Integer.parseInt(s);

			parent.heightText.setText(parent.heightstring + " = " + parent.height);
			parent.maxheight = Math.max(parent.height, parent.maxheight);
			parent.heightslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.height,
					parent.minVal, parent.maxheight, parent.scrollbarSize));
			parent.heightslider.repaint();
			parent.heightslider.validate();

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

					// Some value is remembered
					Setroi.Setup(parent, grandparent);
				}

			}
		});

	}

}
