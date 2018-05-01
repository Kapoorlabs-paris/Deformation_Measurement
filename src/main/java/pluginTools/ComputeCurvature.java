package pluginTools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import costMatrix.PixelratiowDistCostFunction;
import curvatureUtils.DisplaySelected;
import ellipsoidDetector.Intersectionobject;
import ij.ImageStack;
import ij.gui.Line;
import kalmanTracker.ETrackCostFunction;
import kalmanTracker.IntersectionobjectCollection;
import kalmanTracker.KFsearch;
import kalmanTracker.NearestNeighbourSearch;
import kalmanTracker.NearestNeighbourSearch2D;
import kalmanTracker.TrackModel;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.InteractiveSimpleEllipseFit.ValueChange;
import utility.CreateTable;
import utility.Curvatureobject;
import utility.Roiobject;
import utility.ThreeDRoiobject;

public class ComputeCurvature extends SwingWorker<Void, Void> {

	final InteractiveSimpleEllipseFit parent;
	final JProgressBar jpb;

	public ComputeCurvature(final InteractiveSimpleEllipseFit parent, final JProgressBar jpb) {

		this.parent = parent;

		this.jpb = jpb;
	}

	@Override
	protected Void doInBackground() throws Exception {

		parent.table.removeAll();
		parent.localCurvature.clear();
		parent.AlllocalCurvature.clear();
		HashMap<String, Integer> map = sortByValues(parent.Accountedframes);
		parent.Accountedframes = map;

		HashMap<String, Integer> mapZ = sortByValues(parent.AccountedZ);
		parent.AccountedZ = mapZ;

		EllipseTrack newtrack = new EllipseTrack(parent, jpb);
		newtrack.ComputeCurvature();

		return null;

	}

	private static HashMap<String, Integer> sortByValues(HashMap<String, Integer> map) {
		List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(map.entrySet());
		// Defined Custom Comparator here
		Collections.sort(list, new Comparator<Entry<String, Integer>>() {

			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		// Here I am copying the sorted list in HashMap
		// using LinkedHashMap to preserve the insertion order
		HashMap<String, Integer> sortedHashMap = new LinkedHashMap<String, Integer>();
		for (Iterator<Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		}
		return sortedHashMap;
	}

	@Override
	protected void done() {

		parent.jpb.setIndeterminate(false);
		parent.Cardframe.validate();

		parent.prestack = new ImageStack((int) parent.originalimg.dimension(0), (int) parent.originalimg.dimension(1),
				java.awt.image.ColorModel.getRGBdefault());

          
		CurvedLineage();

		try {
			get();
		} catch (InterruptedException e) {

		} catch (ExecutionException e) {

		}

	}

	public void CurvedLineage() {

		

		DisplaySelected.mark(parent);
		DisplaySelected.select(parent);
		
		for (ArrayList<Curvatureobject> local: parent.AlllocalCurvature) {
		Iterator<Curvatureobject> iterator = local.iterator();


		while (iterator.hasNext()) {

			Curvatureobject currentcurvature = iterator.next();


			if (parent.originalimg.numDimensions() > 3) {
				if (currentcurvature.t == parent.fourthDimension) {
					parent.Finalcurvatureresult.put(currentcurvature.Label, currentcurvature);
				}
			} else if (parent.originalimg.numDimensions() <= 3) {
				if (currentcurvature.z == parent.thirdDimension) {
					parent.Finalcurvatureresult.put(currentcurvature.Label, currentcurvature);

				}

			}

		}
		}
		curvatureUtils.CurvatureTable.CreateTableView(parent);

	}

}