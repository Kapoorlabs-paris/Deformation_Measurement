package curvatureUtils;

import java.awt.BorderLayout;
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
import utility.Curvatureobject;

public class CurvatureTable {

	public static void CreateTableView(final InteractiveSimpleEllipseFit parent) {

		parent.resultCurvature = new ArrayList<Pair<Integer, double[]>>();

		for (ArrayList<Curvatureobject> Allcurrentcurvature : parent.AlllocalCurvature) {
			for (int index = 0; index < Allcurrentcurvature.size(); ++index) {

				Curvatureobject currentcurvature = Allcurrentcurvature.get(index);
				parent.resultCurvature.add(new ValuePair<Integer, double[]>(currentcurvature.Label,
						new double[] { currentcurvature.cord[0], currentcurvature.cord[1],
								currentcurvature.radiusCurvature, currentcurvature.perimeter, currentcurvature.z,
								currentcurvature.t }));

			}
		}
		Object[] colnames = new Object[] { "Cell Id", "Location X", "Location Y", "Location Z", "Perimeter",
				"Curvature" };

		Object[][] rowvalues = new Object[0][colnames.length];

		rowvalues = new Object[parent.Finalcurvatureresult.size()][colnames.length];

		parent.table = new JTable(rowvalues, colnames);
		parent.row = 0;
		NumberFormat f = NumberFormat.getInstance();
		for (Map.Entry<Integer, Curvatureobject> entry : parent.Finalcurvatureresult.entrySet()) {

			Curvatureobject currentcurvature = entry.getValue();
			parent.table.getModel().setValueAt(entry.getKey(), parent.row, 0);
			parent.table.getModel().setValueAt(f.format(currentcurvature.cord[0]), parent.row, 1);
			parent.table.getModel().setValueAt(f.format(currentcurvature.cord[1]), parent.row, 2);
			parent.table.getModel().setValueAt(f.format(currentcurvature.z), parent.row, 3);
			parent.table.getModel().setValueAt(f.format(currentcurvature.perimeter), parent.row, 4);
			parent.table.getModel().setValueAt(f.format(currentcurvature.radiusCurvature), parent.row, 5);
			parent.row++;

			parent.tablesize = parent.row;
		}

		parent.PanelSelectFile.removeAll();

		parent.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		parent.scrollPane = new JScrollPane(parent.table);

		parent.scrollPane.getViewport().add(parent.table);
		parent.scrollPane.setAutoscrolls(true);
		parent.PanelSelectFile.add(parent.scrollPane, BorderLayout.CENTER);

		parent.PanelSelectFile.setBorder(parent.selectcell);

		parent.panelSecond.add(parent.PanelSelectFile, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.RELATIVE, new Insets(10, 10, 0, 10), 0, 0));

		parent.Original.add(parent.inputLabel, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		parent.Original.add(parent.inputField, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		parent.Original.add(parent.inputcellLabel, new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		parent.Original.add(parent.inputtrackField, new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		parent.Original.add(parent.ChooseDirectory, new GridBagConstraints(0, 7, 3, 1, 0.0, 0.0,
				GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		parent.Original.add(parent.Savebutton, new GridBagConstraints(0, 8, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		parent.Original.setBorder(parent.origborder);

		parent.panelSecond.add(parent.Original, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
		parent.inputField.setEnabled(true);
		parent.inputtrackField.setEnabled(true);
		parent.Savebutton.setEnabled(true);
		parent.ChooseDirectory.setEnabled(true);
		parent.Original.repaint();
		parent.Original.validate();
		parent.PanelSelectFile.repaint();
		parent.PanelSelectFile.validate();
		parent.table.repaint();
		parent.table.validate();
		parent.panelSecond.repaint();
		parent.panelSecond.validate();
		parent.Cardframe.repaint();
		parent.Cardframe.validate();

	}

}
