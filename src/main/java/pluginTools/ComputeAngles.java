package pluginTools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;

import ellipsoidDetector.Intersectionobject;
import net.imglib2.img.display.imagej.ImageJFunctions;
import utility.NearestNeighbourSearch;
import utility.TrackModel;

public class ComputeAngles extends SwingWorker<Void, Void> {

	final InteractiveEllipseFit parent;
	final JProgressBar jpb;

	public ComputeAngles(final InteractiveEllipseFit parent, final JProgressBar jpb) {

		this.parent = parent;

		this.jpb = jpb;
	}

	@Override
	protected Void doInBackground() throws Exception {

		EllipseTrack newtrack = new EllipseTrack(parent, jpb);
		newtrack.IntersectandTrack();

		return null;

	}

	@Override
	protected void done() {

		parent.jpb.setIndeterminate(false);
		parent.Cardframe.validate();

		NearestNeighbourSearch NNsearch = new NearestNeighbourSearch(parent.ALLIntersections, parent.thirdDimension,
				parent.fourthDimensionSize, parent.maxdistance);
		NNsearch.process();
		parent.parentgraph = NNsearch.getResult();

		Lineage();

		System.out.println("Size of graph" + parent.parentgraph.vertexSet().size());
		try {
			get();
		} catch (InterruptedException e) {

		} catch (ExecutionException e) {

		}

	}

	public void Lineage() {

		TrackModel model = new TrackModel(parent.parentgraph);

		
		parent.Finalresult = new HashMap<Integer, Intersectionobject>();
	
		
		
		for (final Integer id : model.trackIDs(true)) {

			
			parent.Tracklist = new ArrayList<Intersectionobject>();

			
			Comparator<Intersectionobject> ThirdDimcomparison = new Comparator<Intersectionobject>() {

				@Override
				public int compare(final Intersectionobject A, final Intersectionobject B) {

					return A.z - B.z;

				}

			};

			Comparator<Intersectionobject> FourthDimcomparison = new Comparator<Intersectionobject>() {

				@Override
				public int compare(final Intersectionobject A, final Intersectionobject B) {

					return A.t - B.t;

				}

			};
			
			
			model.setName(id, "Track" + id);

			final HashSet<Intersectionobject> Angleset = model.trackIntersectionobjects(id);

			Iterator<Intersectionobject> Angleiter = Angleset.iterator();
			
			
			while(Angleiter.hasNext()) {
				
				Intersectionobject currentangle = Angleiter.next();
				
				parent.Tracklist.add(currentangle);
			}
			
			Collections.sort(parent.Tracklist, ThirdDimcomparison);
			if (parent.fourthDimensionSize > 1)
				Collections.sort(parent.Tracklist, FourthDimcomparison);

			parent.Finalresult.put(id, parent.Tracklist.get(0));
			
			System.out.println(id + " " + parent.Tracklist.get(0).t + " " + parent.Tracklist.get(0).Intersectionpoint[0]);
		}

		CreateTable();

	}

	public void CreateTable() {

		parent.PanelSelectFile.removeAll();
		parent.Cardframe.repaint();
		parent.Cardframe.validate();
		parent.row = 0;
		parent.Card();
		for (Map.Entry<Integer, Intersectionobject> entry : parent.Finalresult.entrySet()) {

			Intersectionobject currentangle = entry.getValue();

			parent.table.getModel().setValueAt(new DecimalFormat("#.###").format(currentangle.Intersectionpoint[0]),
					parent.row, 1);
			parent.table.getModel().setValueAt(new DecimalFormat("#.###").format(currentangle.Intersectionpoint[1]),
					parent.row, 2);
			parent.table.getModel().setValueAt(new DecimalFormat("#.###").format(currentangle.angle), parent.row, 3);
			parent.table.getModel().setValueAt(new DecimalFormat("#.###").format(currentangle.t), parent.row, 4);
			parent.table.getModel().setValueAt(new DecimalFormat("#.###").format(currentangle.z), parent.row, 5);

			++parent.row;
		}

		
	

	}

}
