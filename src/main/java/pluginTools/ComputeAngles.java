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
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
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
	
		int minid = Integer.MAX_VALUE;
		int maxid = Integer.MIN_VALUE;
		for (final Integer id : model.trackIDs(true)) {
			
			if (id > maxid)
				maxid = id;
			
			if(id < minid)
				minid = id;
			
		}
		for (final Integer id : model.trackIDs(true)) {

			

			
			Comparator<Pair<Integer,Intersectionobject>> ThirdDimcomparison = new Comparator<Pair<Integer,Intersectionobject>>() {

				@Override
				public int compare(final Pair<Integer,Intersectionobject> A, final Pair<Integer,Intersectionobject> B) {

					return A.getB().z - B.getB().z;

				}

			};

			Comparator<Pair<Integer,Intersectionobject>> FourthDimcomparison = new Comparator<Pair<Integer,Intersectionobject>>() {

				@Override
				public int compare(final Pair<Integer,Intersectionobject> A, final Pair<Integer,Intersectionobject> B) {

					return A.getB().t - B.getB().t;

				}

			};
			
			
			model.setName(id, "Track" + id);

			final HashSet<Intersectionobject> Angleset = model.trackIntersectionobjects(id);

			Iterator<Intersectionobject> Angleiter = Angleset.iterator();
			
			
			while(Angleiter.hasNext()) {
				
				Intersectionobject currentangle = Angleiter.next();
				
				parent.Tracklist.add(new ValuePair<Integer, Intersectionobject>( id, currentangle));
			}
			
			Collections.sort(parent.Tracklist, ThirdDimcomparison);
			if (parent.fourthDimensionSize > 1)
				Collections.sort(parent.Tracklist, FourthDimcomparison);

			
		}
		
		
		
		for (int id = minid; id <= maxid; ++id) {
			
			if(model.trackIntersectionobjects(id)!=null)
			parent.Finalresult.put(id, model.trackIntersectionobjects(id).iterator().next());
			
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
			parent.table.getModel().setValueAt(new DecimalFormat("#.###").format(entry.getKey()),
					parent.row, 0);
			parent.table.getModel().setValueAt(new DecimalFormat("#.###").format(currentangle.Intersectionpoint[0]),
					parent.row, 1);
			parent.table.getModel().setValueAt(new DecimalFormat("#.###").format(currentangle.Intersectionpoint[1]),
					parent.row, 2);
			parent.table.getModel().setValueAt(new DecimalFormat("#.###").format(currentangle.angle), parent.row, 3);
			parent.table.getModel().setValueAt(new DecimalFormat("#.###").format(currentangle.t), parent.row, 4);
			parent.table.getModel().setValueAt(new DecimalFormat("#.###").format(currentangle.z), parent.row, 5);

			parent.row++;
		}

		
	

	}

}
