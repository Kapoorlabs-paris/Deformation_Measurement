package listeners;

import java.awt.TextComponent;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import pluginTools.InteractiveEllipseFit;


public class TrackidListener  implements TextListener {

	
	final InteractiveEllipseFit parent;
	
	public TrackidListener(final InteractiveEllipseFit parent){
		
		this.parent = parent;
		
	}
	
	@Override
	public void textValueChanged(TextEvent e) {
		final TextComponent tc = (TextComponent)e.getSource();
	    String s = tc.getText();
	   
		parent.selectedID = s;
		
	}


}
