package listeners;

import java.awt.TextComponent;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import pluginTools.InteractiveSimpleEllipseFit;

public class RegionInteriorListener implements TextListener {

	final InteractiveSimpleEllipseFit parent;

	public RegionInteriorListener(final InteractiveSimpleEllipseFit parent) {

		this.parent = parent;

	}

	@Override
	public void textValueChanged(TextEvent e) {
		final TextComponent tc = (TextComponent) e.getSource();

		
		
				String s = tc.getText();
			
				if(s.length() > 0)
						parent.regiondistance  = Integer.parseInt(s);
				
				

			
		};
		
		

	}

