package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import pluginTools.InteractiveEllipseFit;

public class DrawListener implements ActionListener {
	
	final InteractiveEllipseFit parent;
	final JComboBox<String> choice;
	
	public DrawListener(final InteractiveEllipseFit parent, final JComboBox<String> choice ) {
		
		
		this.parent = parent;
		this.choice = choice;
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		
		int selectedindex = choice.getSelectedIndex();
		
		if (selectedindex == 1) {
			parent.minpercent = (float) (parent.minpercentINI / 2.0);
			parent.inputFieldminpercent.setText(Float.toString(parent.minpercent));	
		}
		else {
			parent.minpercent = parent.minpercentINI;
			parent.inputFieldminpercent.setText(Float.toString(parent.minpercent));	
			
		}
		parent.panelFirst.repaint();
		parent.panelFirst.validate();
		
		
	}
	
	
	
	
	
	

}
