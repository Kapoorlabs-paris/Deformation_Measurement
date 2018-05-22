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

import ellipsoidDetector.Intersectionobject;
import kalmanTracker.ETrackCostFunction;
import kalmanTracker.IntersectionobjectCollection;
import kalmanTracker.KFsearch;
import kalmanTracker.NearestNeighbourSearch;
import kalmanTracker.NearestNeighbourSearch2D;
import kalmanTracker.TrackModel;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import utility.CreateTable;

public class ComputeAnglesCurrent extends SwingWorker<Void, Void> {

	final InteractiveSimpleEllipseFit parent;
	final JProgressBar jpb;

	public ComputeAnglesCurrent(final InteractiveSimpleEllipseFit parent, final JProgressBar jpb) {

		this.parent = parent;

		this.jpb = jpb;
	}

	@Override
	protected Void doInBackground() throws Exception {
		parent.table.removeAll();
		HashMap<String, Integer> map = sortByValues(parent.Accountedframes);
		parent.Accountedframes = map;

		HashMap<String, Integer> mapZ = sortByValues(parent.AccountedZ);
		parent.AccountedZ = mapZ;
		EllipseTrack newtrack = new EllipseTrack(parent, jpb);
		newtrack.IntersectandTrackCurrent();

		return null;

	}
	private static HashMap<String, Integer> sortByValues(HashMap<String, Integer> map) { 
	       List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(map.entrySet());
	       // Defined Custom Comparator here
	       Collections.sort(list, new Comparator<Entry<String, Integer>>() {
	        

				@Override
				public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
					 return (o1.getValue())
			                  .compareTo(o2.getValue());
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

	
		if (parent.ndims > 3){
		Iterator<Map.Entry<String, Integer>> itZ = parent.AccountedZ.entrySet().iterator();
		
		
        		
		
		while (itZ.hasNext()) {

		int	z = itZ.next().getValue();
	
		
		NearestNeighbourSearch NNsearch = new NearestNeighbourSearch(parent.ALLIntersections, z,
				(int)parent.fourthDimensionSize, parent.maxdistance, parent.Accountedframes);
		NNsearch.process();
		parent.parentgraphZ.put(Integer.toString(z), NNsearch.getResult());
		
		
		
		}
		Lineage();
		}
		
		else {
			
			NearestNeighbourSearch2D NNsearch = new NearestNeighbourSearch2D(parent.ALLIntersections, 
					(int)parent.thirdDimensionSize, parent.maxdistance, parent.AccountedZ, parent.mindistance);
			NNsearch.process();
			 SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>	simplegraph =  NNsearch.getResult();
			 parent.parentgraphZ.put(Integer.toString(1), simplegraph);
			Lineage();
		}
		
	

		try {
			get();
		} catch (InterruptedException e) {

		} catch (ExecutionException e) {

		}

	}

	public   void Lineage() {

		for (Map.Entry<String, SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>> entryZ : parent.parentgraphZ
				.entrySet()) {

			TrackModel model = new TrackModel(entryZ.getValue());

			int minid = Integer.MAX_VALUE;
			int maxid = Integer.MIN_VALUE;

			for (final Integer id : model.trackIDs(true)) {

				if (id > maxid)
					maxid = id;

				if (id < minid)
					minid = id;

			}


			if (minid != Integer.MAX_VALUE) {

				for (final Integer id : model.trackIDs(true)) {

					Comparator<Pair<String, Intersectionobject>> ThirdDimcomparison = new Comparator<Pair<String, Intersectionobject>>() {

						@Override
						public int compare(final Pair<String, Intersectionobject> A,
								final Pair<String, Intersectionobject> B) {

							return A.getB().z - B.getB().z;

						}

					};

					Comparator<Pair<String, Intersectionobject>> FourthDimcomparison = new Comparator<Pair<String, Intersectionobject>>() {

						@Override
						public int compare(final Pair<String, Intersectionobject> A,
								final Pair<String, Intersectionobject> B) {

							return A.getB().t - B.getB().t;

						}

					};

					model.setName(id, "Track" + id + entryZ.getKey());

					final HashSet<Intersectionobject> Angleset = model.trackIntersectionobjects(id);

					Iterator<Intersectionobject> Angleiter = Angleset.iterator();

					while (Angleiter.hasNext()) {

						Intersectionobject currentangle = Angleiter.next();
						parent.Tracklist.add(new ValuePair<String, Intersectionobject>(
								Integer.toString(id) + entryZ.getKey(), currentangle));
					}
					Collections.sort(parent.Tracklist, ThirdDimcomparison);
					if (parent.fourthDimensionSize > 1)
						Collections.sort(parent.Tracklist, FourthDimcomparison);

				}

				for (int id = minid; id <= maxid; ++id) {
					Intersectionobject bestangle = null;
					if (model.trackIntersectionobjects(id) != null) {

						List<Intersectionobject> sortedList = new ArrayList<Intersectionobject>(
								model.trackIntersectionobjects(id));

						Collections.sort(sortedList, new Comparator<Intersectionobject>() {

							@Override
							public int compare(Intersectionobject o1, Intersectionobject o2) {

								return o1.t - o2.t;
							}

						});

						Iterator<Intersectionobject> iterator = sortedList.iterator();

						int count = 0;
						while (iterator.hasNext()) {

							Intersectionobject currentangle = iterator.next();

							if (count == 0)
								bestangle = currentangle;
							if(parent.originalimg.numDimensions() > 3) {
							if (currentangle.t  == parent.fourthDimension) {
								bestangle = currentangle;
								count++;
							    break;	
							}
							}
							else if (parent.originalimg.numDimensions()<= 3){
								if (currentangle.z  == parent.thirdDimension) {
									bestangle = currentangle;
									count++;
								    break;	
								 
								}
								
								
							}

							
						}
						parent.Finalresult.put(Integer.toString(id) + entryZ.getKey(), bestangle);

					}

				}
			}
		}
		CreateTable.CreateTableView(parent);

	}

	public SimpleWeightedGraph< Intersectionobject, DefaultWeightedEdge > Trackfunction() {
		
		parent.UserchosenCostFunction = new ETrackCostFunction(1, 0);
		
		IntersectionobjectCollection coll = new IntersectionobjectCollection();
		
		for(Map.Entry<String, ArrayList<Intersectionobject>> entry : parent.ALLIntersections.entrySet()) {
			
			String ID = entry.getKey();
			ArrayList<Intersectionobject> bloblist = entry.getValue();
			
			for (Intersectionobject blobs: bloblist) {
				
				coll.add(blobs, ID);
				
				
			}
			
			
		}
		
		KFsearch Tsearch = new KFsearch(coll, parent.UserchosenCostFunction, parent.maxSearchradius, parent.initialSearchradius, parent.maxframegap, parent.AccountedZ, parent.jpb);
		Tsearch.process();
		SimpleWeightedGraph< Intersectionobject, DefaultWeightedEdge > simplegraph = Tsearch.getResult();
		
		return simplegraph;
		
		
	}


}