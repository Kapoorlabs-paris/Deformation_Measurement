package listeners;

import java.awt.TextComponent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import pluginTools.InteractiveSimpleEllipseFit;

public class DegreeListener implements TextListener {
	
	
	final InteractiveSimpleEllipseFit parent;
	
	boolean pressed;
	public DegreeListener(final InteractiveSimpleEllipseFit parent, final boolean pressed) {
		
		
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
			parent.degree = Integer.parseInt(s);
		parent.StartCurvatureComputingCurrent();
		
			   	 }

			    }
			});
	
	}

}
