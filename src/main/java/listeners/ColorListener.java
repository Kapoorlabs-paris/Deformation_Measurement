package listeners;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import ij.IJ;
import pluginTools.InteractiveEllipseFit;

public class ColorListener implements ActionListener {

	final InteractiveEllipseFit parent;
	final JComboBox<String> choice;

	public ColorListener(final InteractiveEllipseFit parent, final JComboBox<String> choice) {

		this.parent = parent;
		this.choice = choice;

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		int selectedindex = choice.getSelectedIndex();

		if (selectedindex == 0) {

			parent.colorChange = Color.GRAY;
			System.out.println(selectedindex + " " + parent.colorChange) ;
		}
		if (selectedindex == 1) {

			parent.colorChange = Color.RED;
			System.out.println(selectedindex + " " + parent.colorChange) ;
		}
		if (selectedindex == 2) {

			parent.colorChange = Color.BLUE;
			System.out.println(selectedindex + " " + parent.colorChange) ;
		}
		if (selectedindex == 3) {

			parent.colorChange = Color.PINK;
			System.out.println(selectedindex + " " + parent.colorChange) ;
		}
	
		

			
		parent.Angleselect.repaint();
		parent.Angleselect.validate();
		parent.panelFirst.repaint();
		parent.panelFirst.validate();

	}

}
