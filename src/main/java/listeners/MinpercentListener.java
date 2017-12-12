package listeners;

import java.awt.TextComponent;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import pluginTools.InteractiveEllipseFit;

public class MinpercentListener implements TextListener {

	final InteractiveEllipseFit parent;

	public MinpercentListener(final InteractiveEllipseFit parent) {

		this.parent = parent;

	}

	@Override
	public void textValueChanged(TextEvent e) {
		final TextComponent tc = (TextComponent) e.getSource();

		
		
				String s = tc.getText();
			
				if(s.length() > 0)
						parent.minpercent = Float.parseFloat(s);
				
				

			
		};
		
		

	}

