package rectangleListeners;

import java.awt.TextComponent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import listeners.RimSelectionListener;
import pluginTools.InteractiveSimpleEllipseFit;


public class RectwidthLocListener implements TextListener {

	final RimSelectionListener parent;
    final InteractiveSimpleEllipseFit grandparent;
	boolean pressed;

	public RectwidthLocListener(final RimSelectionListener parent, final InteractiveSimpleEllipseFit grandparent, final boolean pressed) {

		this.parent = parent;
		this.pressed = pressed;
		this.grandparent = grandparent;

	}

	@Override
	public void textValueChanged(TextEvent e) {
		final TextComponent tc = (TextComponent) e.getSource();
		String s = tc.getText();
		if (s.length() > 0) {
			parent.width = Integer.parseInt(s);

			parent.widthText.setText(parent.widthstring + " = " + parent.width);
			parent.maxwidth = Math.max(parent.width, parent.maxwidth	);
			parent.widthslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.width,
					parent.minVal, parent.maxwidth, parent.scrollbarSize));
			parent.widthslider.repaint();
			parent.widthslider.validate();

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
