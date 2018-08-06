package curvatureUtils;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import ellipsoidDetector.Intersectionobject;
import kalmanForSegments.Segmentobject;
import net.imglib2.RealLocalizable;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.InteractiveSimpleEllipseFit;
import utility.Curvatureobject;

public class CurvatureTable {

	public static void CreateTableTrackView(final InteractiveSimpleEllipseFit parent) {

		parent.resultAngle = new ArrayList<Pair<String, double[]>>();
		parent.resultCurvature = new ArrayList<Pair<String, Pair<Integer, ArrayList<double[]>>>>();
		for (Pair<String, Intersectionobject> currentangle : parent.denseTracklist) {
			if (parent.originalimg.numDimensions() > 3) {
				parent.resultAngle.add(new ValuePair<String, double[]>(currentangle.getA(),
						new double[] { currentangle.getB().t, currentangle.getB().perimeter }));

				Pair<Integer, ArrayList<double[]>> timelist = new ValuePair<Integer, ArrayList<double[]>>(
						currentangle.getB().t, currentangle.getB().linelist);
				parent.resultCurvature
						.add(new ValuePair<String, Pair<Integer, ArrayList<double[]>>>(currentangle.getA(), timelist));
				parent.HashresultCurvature.put(currentangle.getB().t, currentangle.getB().linelist);

			} else {
				parent.resultAngle.add(new ValuePair<String, double[]>(currentangle.getA(),
						new double[] { currentangle.getB().z, currentangle.getB().perimeter }));
				Pair<Integer, ArrayList<double[]>> timelist = new ValuePair<Integer, ArrayList<double[]>>(
						currentangle.getB().z, currentangle.getB().linelist);
				parent.resultCurvature
						.add(new ValuePair<String, Pair<Integer, ArrayList<double[]>>>(currentangle.getA(), timelist));
				parent.HashresultCurvature.put(currentangle.getB().z, currentangle.getB().linelist);
			}

		}
		Object[] colnames = new Object[] { "Track Id", "Location X", "Location Y", "Location Z/T", "Perimeter" };

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
			parent.table.getModel().setValueAt(f.format(currentangle.z), parent.row, 3);
			parent.table.getModel().setValueAt(f.format(currentangle.perimeter), parent.row, 4);

			parent.row++;

			parent.tablesize = parent.row;
		}

		makeGUI(parent);
	}

	public static void CreateSegTableTrackView(final InteractiveSimpleEllipseFit parent) {

		parent.resultSegCurvature = new ArrayList<Pair<String, Pair<Integer, Double>>>();
		parent.resultSegIntensityA = new ArrayList<Pair<String, Pair<Integer, Double>>>();
		parent.resultSegIntensityB = new ArrayList<Pair<String, Pair<Integer, Double>>>();
		parent.resultSegPerimeter = new ArrayList<Pair<String, Pair<Integer, Double>>>();
		parent.resultCurvature = new ArrayList<Pair<String, Pair<Integer, ArrayList<double[]>>>>();
		parent.resultAngle = new ArrayList<Pair<String, double[]>>();
		parent.SubresultCurvature = new ArrayList<Pair<String, Pair<Integer, List<RealLocalizable>>>>();

		for (Pair<String, Segmentobject> currentangle : parent.SegmentTracklist) {
			if (parent.originalimg.numDimensions() > 3) {

				Pair<Integer, Double> timelist = new ValuePair<Integer, Double>(currentangle.getB().t,
						currentangle.getB().Curvature);
				parent.resultSegCurvature
						.add(new ValuePair<String, Pair<Integer, Double>>(currentangle.getA(), timelist));
				parent.HashresultSegCurvature.put(currentangle.getB().t, currentangle.getB().Curvature);

			

				Pair<Integer, ArrayList<double[]>> linelist = new ValuePair<Integer, ArrayList<double[]>>(
						currentangle.getB().t, currentangle.getB().sublist);
				parent.resultCurvature
						.add(new ValuePair<String, Pair<Integer, ArrayList<double[]>>>(currentangle.getA(), linelist));

				parent.HashresultCurvature.put(currentangle.getB().t, currentangle.getB().sublist);

				Pair<Integer, Double> IntensityAtimelist = new ValuePair<Integer, Double>(currentangle.getB().t,
						currentangle.getB().IntensityA);
				Pair<Integer, Double> IntensityBtimelist = new ValuePair<Integer, Double>(currentangle.getB().t,
						currentangle.getB().IntensityB);
				Pair<Integer, Double> Perimetertimelist = new ValuePair<Integer, Double>(currentangle.getB().t,
						currentangle.getB().Perimeter);

				parent.resultSegIntensityA
						.add(new ValuePair<String, Pair<Integer, Double>>(currentangle.getA(), IntensityAtimelist));
				parent.resultSegIntensityB
						.add(new ValuePair<String, Pair<Integer, Double>>(currentangle.getA(), IntensityBtimelist));
				parent.resultSegPerimeter
						.add(new ValuePair<String, Pair<Integer, Double>>(currentangle.getA(), Perimetertimelist));

				parent.HashresultSegIntensityA.put(currentangle.getB().t, currentangle.getB().IntensityA);
				parent.HashresultSegIntensityB.put(currentangle.getB().t, currentangle.getB().IntensityB);
				parent.HashresultSegPerimeter.put(currentangle.getB().t, currentangle.getB().Perimeter);
			} else {
				
				Pair<Integer, Double> timelist = new ValuePair<Integer, Double>(currentangle.getB().z,
						currentangle.getB().Curvature);
				parent.resultSegCurvature
						.add(new ValuePair<String, Pair<Integer, Double>>(currentangle.getA(), timelist));
				parent.HashresultSegCurvature.put(currentangle.getB().z, currentangle.getB().Curvature);

			

				Pair<Integer, ArrayList<double[]>> linelist = new ValuePair<Integer, ArrayList<double[]>>(
						currentangle.getB().z, currentangle.getB().sublist);
				parent.resultCurvature
				.add(new ValuePair<String, Pair<Integer, ArrayList<double[]>>>(currentangle.getA(), linelist));

		        parent.HashresultCurvature.put(currentangle.getB().z, currentangle.getB().sublist);

				Pair<Integer, Double> IntensityAtimelist = new ValuePair<Integer, Double>(currentangle.getB().z,
						currentangle.getB().IntensityA);
				Pair<Integer, Double> IntensityBtimelist = new ValuePair<Integer, Double>(currentangle.getB().z,
						currentangle.getB().IntensityB);
				Pair<Integer, Double> Perimetertimelist = new ValuePair<Integer, Double>(currentangle.getB().z,
						currentangle.getB().Perimeter);

				parent.resultSegIntensityA
						.add(new ValuePair<String, Pair<Integer, Double>>(currentangle.getA(), IntensityAtimelist));
				parent.resultSegIntensityB
						.add(new ValuePair<String, Pair<Integer, Double>>(currentangle.getA(), IntensityBtimelist));
				parent.resultSegPerimeter
						.add(new ValuePair<String, Pair<Integer, Double>>(currentangle.getA(), Perimetertimelist));

				parent.HashresultSegIntensityA.put(currentangle.getB().z, currentangle.getB().IntensityA);
				parent.HashresultSegIntensityB.put(currentangle.getB().z, currentangle.getB().IntensityB);

			}

		}
		Object[] colnames = new Object[] { "Track Id", "Location X", "Location Y", "Location Z/T", "Curvature" };

		Object[][] rowvalues = new Object[0][colnames.length];

		rowvalues = new Object[parent.SegmentFinalresult.size()][colnames.length];

		parent.table = new JTable(rowvalues, colnames);
		parent.row = 0;
		NumberFormat f = NumberFormat.getInstance();
		for (Map.Entry<String, Segmentobject> entry : parent.SegmentFinalresult.entrySet()) {

			Segmentobject currentangle = entry.getValue();
			parent.table.getModel().setValueAt(entry.getKey(), parent.row, 0);
			parent.table.getModel().setValueAt(f.format(currentangle.centralpoint.getDoublePosition(0)), parent.row, 1);
			parent.table.getModel().setValueAt(f.format(currentangle.centralpoint.getDoublePosition(1)), parent.row, 2);
			parent.table.getModel().setValueAt(f.format(currentangle.z), parent.row, 3);
			parent.table.getModel().setValueAt(f.format(currentangle.Curvature), parent.row, 4);

			parent.row++;

			parent.tablesize = parent.row;
		}

		makeGUI(parent);
	}

	public static void CreateTableView(final InteractiveSimpleEllipseFit parent) {

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

		makeGUI(parent);

	}

	public static void makeGUI(final InteractiveSimpleEllipseFit parent) {

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
		parent.Original.add(parent.SaveAllbutton, new GridBagConstraints(0, 9, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		parent.Original.setBorder(parent.origborder);

		parent.panelSecond.add(parent.Original, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
		parent.inputField.setEnabled(true);
		parent.inputtrackField.setEnabled(true);
		parent.Savebutton.setEnabled(true);
		parent.SaveAllbutton.setEnabled(true);
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
