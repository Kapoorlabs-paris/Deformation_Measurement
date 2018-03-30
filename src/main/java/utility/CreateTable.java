package utility;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import ellipsoidDetector.Intersectionobject;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.InteractiveSimpleEllipseFit;

public class CreateTable {

	
	
	
	
	
	public static void CreateTableView(final InteractiveSimpleEllipseFit parent) {

		parent.resultAngle = new ArrayList<Pair<String, double[]>>();

		for (Pair<String, Intersectionobject> currentangle : parent.Tracklist) {
			if(parent.originalimg.numDimensions() > 3)
			parent.resultAngle.add(new ValuePair<String, double[]>(currentangle.getA(),
					new double[] { currentangle.getB().t, currentangle.getB().angle }));
			else
				parent.resultAngle.add(new ValuePair<String, double[]>(currentangle.getA(),
						new double[] { currentangle.getB().z, currentangle.getB().angle }));

		}
		Object[] colnames = new Object[] { "Track Id", "Location X", "Location Y", "End Z" };

		Object[][] rowvalues = new Object[0][colnames.length];

		rowvalues = new Object[parent.Finalresult.size()][colnames.length];

		parent.table = new JTable(rowvalues, colnames);
		parent.row = 0;
		NumberFormat f = NumberFormat.getInstance();
		for (Map.Entry<String, Intersectionobject> entry : parent.Finalresult.entrySet()) {

			Intersectionobject currentangle = entry.getValue();
			parent.table.getModel().setValueAt(entry.getKey(), parent.row, 0);
			parent.table.getModel().setValueAt(f.format(currentangle.Intersectionpoint[0]), parent.row, 1);
			parent.table.getModel().setValueAt(f.format(currentangle.Intersectionpoint[1]), parent.row, 2);
			parent.table.getModel().setValueAt(f.format(currentangle.angle), parent.row, 3);
			

			parent.row++;

			parent.tablesize = parent.row;
		}

		parent.PanelSelectFile.removeAll();

		parent.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		parent.table.setMinimumSize(new Dimension(500, 300));
		parent.table.setPreferredSize(new Dimension(500, 200));

		parent.scrollPane = new JScrollPane(parent.table);
		parent.scrollPane.setMinimumSize(new Dimension(300, 200));
		parent.scrollPane.setPreferredSize(new Dimension(300, 200));

		parent.scrollPane.getViewport().add(parent.table);
		parent.scrollPane.setAutoscrolls(true);
		parent.PanelSelectFile.add(parent.scrollPane, BorderLayout.CENTER);

		parent.PanelSelectFile.setBorder(parent.selectfile);

		parent.panelFirst.add(parent.PanelSelectFile, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.RELATIVE, new Insets(10, 10, 0, 10), 0, 0));

		parent.Original.add(parent.inputLabel, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		parent.Original.add(parent.inputField, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		parent.Original.add(parent.inputtrackLabel, new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		parent.Original.add(parent.inputtrackField, new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		parent.Original.add(parent.ChooseDirectory, new GridBagConstraints(0, 7, 3, 1, 0.0, 0.0,
				GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		parent.Original.add(parent.Savebutton, new GridBagConstraints(0, 8, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		parent.Original.setBorder(parent.origborder);

		parent.Original.setMinimumSize(new Dimension(parent.SizeX + 10, parent.SizeY + 10));
		parent.Original.setPreferredSize(new Dimension(parent.SizeX + 10, parent.SizeY + 10));
		parent.panelFirst.add(parent.Original, new GridBagConstraints(3, 3, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
		parent.inputField.setEnabled(true);
		parent.inputtrackField.setEnabled(true);
		parent.Savebutton.setEnabled(true);
		parent.ChooseDirectory.setEnabled(true);
		parent.Original.repaint();
		parent.Original.validate();
		parent.PanelSelectFile.repaint();
		parent.PanelSelectFile.validate();

		parent.panelFirst.repaint();
		parent.panelFirst.validate();
		parent.Cardframe.repaint();
		parent.Cardframe.validate();
	}
	
}
