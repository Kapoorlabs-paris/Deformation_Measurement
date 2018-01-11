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
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import utility.NearestNeighbourSearch;
import utility.NearestNeighbourSearch2D;
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
	
		
		HashMap<String, Integer> map = sortByValues(parent.Accountedframes);
		parent.Accountedframes = map;
		
		
		HashMap<String, Integer> mapZ = sortByValues(parent.AccountedZ);
		parent.AccountedZ = mapZ;
		
		EllipseTrack newtrack = new EllipseTrack(parent, jpb);
		newtrack.IntersectandTrack();

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
					(int)parent.thirdDimensionSize, parent.maxdistance, parent.AccountedZ);
			NNsearch.process();
			 SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>	simplegraph =  NNsearch.getResult();
			
			Lineage2D(simplegraph);
		}
		
	

		try {
			get();
		} catch (InterruptedException e) {

		} catch (ExecutionException e) {

		}

	}

	public void Lineage() {

		for(Map.Entry<String, SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>> entryZ : parent.parentgraphZ.entrySet()){
		
		TrackModel model = new TrackModel(entryZ.getValue());
		
		
	
		int minid = Integer.MAX_VALUE;
		int maxid = Integer.MIN_VALUE;
		
		for (final Integer id : model.trackIDs(true)) {
			
			if (id > maxid)
				maxid = id;
			
			if(id < minid)
				minid = id;
			
		}
		
		
		System.out.println("MinMax" + minid + " " + maxid + " " + entryZ.getKey());
		
		if (minid!=Integer.MAX_VALUE){
		
		for (final Integer id : model.trackIDs(true)) {

			

			
			Comparator<Pair<String,Intersectionobject>> ThirdDimcomparison = new Comparator<Pair<String,Intersectionobject>>() {

				@Override
				public int compare(final Pair<String,Intersectionobject> A, final Pair<String,Intersectionobject> B) {

					return A.getB().z - B.getB().z;

				}

			};

			Comparator<Pair<String,Intersectionobject>> FourthDimcomparison = new Comparator<Pair<String,Intersectionobject>>() {

				@Override
				public int compare(final Pair<String,Intersectionobject> A, final Pair<String,Intersectionobject> B) {

					return A.getB().t - B.getB().t;

				}

			};
			
			
			model.setName(id, "Track" + id + entryZ.getKey() );

			final HashSet<Intersectionobject> Angleset = model.trackIntersectionobjects(id);

			Iterator<Intersectionobject> Angleiter = Angleset.iterator();
			
			
			while(Angleiter.hasNext()) {
				
				Intersectionobject currentangle = Angleiter.next();
				
				parent.Tracklist.add(new ValuePair<String, Intersectionobject>( Integer.toString(id) + entryZ.getKey(), currentangle));
			}
			System.out.println("In loop" + Integer.toString(id) + entryZ.getKey());
			Collections.sort(parent.Tracklist, ThirdDimcomparison);
			if (parent.fourthDimensionSize > 1)
				Collections.sort(parent.Tracklist, FourthDimcomparison);

			
		}
		
		
		
		for (int id = minid; id <= maxid; ++id) {
			Intersectionobject bestangle = null;
			if(model.trackIntersectionobjects(id)!=null){
			
				List<Intersectionobject> sortedList = new ArrayList<Intersectionobject>(model.trackIntersectionobjects(id));
				
				
				Collections.sort(sortedList, new Comparator<Intersectionobject>() {

					@Override
					public int compare(Intersectionobject o1, Intersectionobject o2) {

						return o1.t - o2.t;
					}
					
					
					
				});
				
				Iterator<Intersectionobject> iterator = sortedList.iterator(); 
				
				int count = 0;
				while(iterator.hasNext()){
				
				
					Intersectionobject currentangle = iterator.next();
					
					    if (count == 0)
						bestangle = currentangle;
					if (bestangle.t > currentangle.t)
						bestangle = currentangle;
					System.out.println(currentangle.t + " " + bestangle.t);
					
				}
				parent.Finalresult.put(Integer.toString(id) + entryZ.getKey() , bestangle);
				
			
			}
			
		}
		}
		}
		CreateTable();

	}

	public void Lineage2D(SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge> entryZ) {

		
		TrackModel model = new TrackModel(entryZ);
		
		
	
		int minid = Integer.MAX_VALUE;
		int maxid = Integer.MIN_VALUE;
		
		for (final Integer id : model.trackIDs(true)) {
			
			if (id > maxid)
				maxid = id;
			
			if(id < minid)
				minid = id;
			
		}
		
		
		System.out.println("MinMax" + minid + " " + maxid + " " + entryZ);
		
		if (minid!=Integer.MAX_VALUE){
		
		for (final Integer id : model.trackIDs(true)) {

			

			
			Comparator<Pair<String,Intersectionobject>> ThirdDimcomparison = new Comparator<Pair<String,Intersectionobject>>() {

				@Override
				public int compare(final Pair<String,Intersectionobject> A, final Pair<String,Intersectionobject> B) {

					return A.getB().z - B.getB().z;

				}

			};

		
			
			
			model.setName(id, "Track" + id  );

			final HashSet<Intersectionobject> Angleset = model.trackIntersectionobjects(id);

			Iterator<Intersectionobject> Angleiter = Angleset.iterator();
			
			
			while(Angleiter.hasNext()) {
				
				Intersectionobject currentangle = Angleiter.next();
				
				parent.Tracklist.add(new ValuePair<String, Intersectionobject>( Integer.toString(id) , currentangle));
			}
			System.out.println("In loop" + Integer.toString(id) );
			Collections.sort(parent.Tracklist, ThirdDimcomparison);

			
		}
		
		
		
		for (int id = minid; id <= maxid; ++id) {
			Intersectionobject bestangle = null;
			if(model.trackIntersectionobjects(id)!=null){
			
				List<Intersectionobject> sortedList = new ArrayList<Intersectionobject>(model.trackIntersectionobjects(id));
				
			
				
				Iterator<Intersectionobject> iterator = sortedList.iterator(); 
				
				int count = 0;
				while(iterator.hasNext()){
				
				
					Intersectionobject currentangle = iterator.next();
					
					    if (count == 0)
						bestangle = currentangle;
					if (bestangle.z > currentangle.z)
						bestangle = currentangle;
					System.out.println(currentangle.z + " " + bestangle.z);
					
				}
				parent.Finalresult.put(Integer.toString(id), bestangle);
				
			
			}
			
		}
		
		}
		CreateTable();

	}

	
	public void CreateTable() {

	
		Object[] colnames = new Object[] { "Track Id", "Location X", "Location Y", "Last Angle", "End time",
		"End Z" };

Object[][] rowvalues = new Object[0][colnames.length];

	rowvalues = new Object[parent.Finalresult.size()][colnames.length];


	parent.table = new JTable(rowvalues, colnames);
		parent.row = 0;
	
		for (Map.Entry<String, Intersectionobject> entry : parent.Finalresult.entrySet()) {

			Intersectionobject currentangle = entry.getValue();
			parent.table.getModel().setValueAt(entry.getKey(),
					parent.row, 0);
			parent.table.getModel().setValueAt(new DecimalFormat("#.###").format(currentangle.Intersectionpoint[0]),
					parent.row, 1);
			parent.table.getModel().setValueAt(new DecimalFormat("#.###").format(currentangle.Intersectionpoint[1]),
					parent.row, 2);
			parent.table.getModel().setValueAt(new DecimalFormat("#.###").format(currentangle.angle), parent.row, 3);
			parent.table.getModel().setValueAt(new DecimalFormat("#.###").format(currentangle.t), parent.row, 4);
			parent.table.getModel().setValueAt(new DecimalFormat("#.###").format(currentangle.z), parent.row, 5);

			parent.row++;
			
			parent.tablesize = parent.row;
		}

			parent.PanelSelectFile.removeAll();
			parent.Original.removeAll();
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

			parent.panelFirst.add(parent.PanelSelectFile, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.RELATIVE, new Insets(10, 10, 0, 10), 0, 0));
			
			parent.Original.add(parent.inputLabel,  new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0) );
			parent.Original.add(parent.inputField,  new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0) );
			parent.Original.add(parent.ChooseDirectory,  new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0) );
			parent.Original.add(parent.Savebutton,  new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0) );
			
			parent.Original.setBorder(parent.origborder);
			
			
			
			parent.Original.setMinimumSize(new Dimension(parent.SizeX, parent.SizeY));
			parent.Original.setPreferredSize(new Dimension(parent.SizeX, parent.SizeY));
			parent.panelFirst.add(parent.Original, new GridBagConstraints(3, 3, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));

			parent.PanelSelectFile.repaint();
			parent.PanelSelectFile.validate();
			parent.Original.repaint();
			parent.Original.validate();
			parent.panelFirst.repaint();
			parent.panelFirst.validate();
			parent.Cardframe.repaint();
			parent.Cardframe.validate();
	}

}