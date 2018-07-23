package pluginTools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import ellipsoidDetector.Intersectionobject;
import ij.ImageStack;
import kalmanTracker.ETrackCostFunction;
import kalmanTracker.IntersectionobjectCollection;
import kalmanTracker.KFsearch;
import kalmanTracker.TrackModel;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import utility.CreateTable;

public class ComputeManual extends SwingWorker<Void, Void> {
	
	final InteractiveSimpleEllipseFit parent;
	final JProgressBar jpb;
	
	public ComputeManual(final InteractiveSimpleEllipseFit parent, final JProgressBar jpb) {
		
		this.parent = parent;
		
		this.jpb = jpb;
		
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		
		
		EllipseTrack newtrack = new EllipseTrack(parent, jpb);
		newtrack.ManualIntervention();
	
		return null;
		
	}
	
	@Override
	protected void done() {

		parent.jpb.setIndeterminate(false);
		
		parent.Cardframe.validate();

		parent.prestack =  new ImageStack((int) parent.originalimg.dimension(0), (int) parent.originalimg.dimension(1),
				java.awt.image.ColorModel.getRGBdefault());
		
		parent.resultDraw.clear();
		
		parent.Tracklist.clear();

		if (parent.ndims > 3) {
			
			Iterator<Map.Entry<String, Integer>> itZ = parent.AccountedZ.entrySet().iterator();
			
			while (itZ.hasNext()) {

				int z = itZ.next().getValue();
			
				SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge> simplegraph = Trackfunction();
				
				parent.parentgraphZ.put(Integer.toString(z), simplegraph);
			}
			Lineage();
		}

		else {
		
			SimpleWeightedGraph< Intersectionobject, DefaultWeightedEdge > simplegraph = Trackfunction();
			
			

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
		
		ArrayList<ArrayList<Intersectionobject>> colllist = new ArrayList<ArrayList<Intersectionobject>>();
		for(Map.Entry<String, ArrayList<Intersectionobject>> entry : parent.ALLIntersections.entrySet()) {
			
			ArrayList<Intersectionobject> bloblist = entry.getValue();
			colllist.add(bloblist);
			
			
		}
		
		KFsearch Tsearch = new KFsearch(colllist, parent.UserchosenCostFunction, parent.maxSearchradius, parent.maxSearchradius, parent.maxframegap, parent.AccountedZ, parent.jpb);
		Tsearch.process();
		SimpleWeightedGraph< Intersectionobject, DefaultWeightedEdge > simplegraph = Tsearch.getResult();
		
		return simplegraph;
		
		
	}
}
