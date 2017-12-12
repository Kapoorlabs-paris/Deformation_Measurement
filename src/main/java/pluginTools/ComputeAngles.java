package pluginTools;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.JProgressBar;
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
		
		NearestNeighbourSearch NNsearch = new NearestNeighbourSearch(parent.ALLIntersections, parent.thirdDimension, parent.fourthDimensionSize);
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

		model.getDirectedNeighborIndex();
		
		for (final Integer id : model.trackIDs(true)) {
			
			model.setName(id, "Track" + id);
			
			
			final HashSet<Intersectionobject> Angleset = model.trackIntersectionobjects(id);

	
			Iterator<Intersectionobject> Angleiter = Angleset.iterator();

			
				Intersectionobject currentangle = Angleiter.next();

				parent.Finalresult.put(id, currentangle);
			
				
		}
		
		Object[] colnames = new Object[]{"Track Id", "Location X", "Location Y" , "Starting Angle", "Start time", 
				"Start Z"};
		
		
		Object[][] rowvalues = new Object[0][colnames.length];
		if(parent.Finalresult!=null && parent.Finalresult.size() > 0) {
			
			rowvalues = new Object[parent.Finalresult.size()][colnames.length];
			
			int count = 0;
			for (Map.Entry<Integer, Intersectionobject> entry : parent.Finalresult.entrySet()) {
				
				rowvalues[count][0] = entry.getKey();
				
				count++;
			}
			
			
		}
		
        parent.table = new JTable(rowvalues, colnames);
		CreateTable();
		
	}
	
	public void CreateTable() {
		
		for (Map.Entry<Integer, Intersectionobject> entry : parent.Finalresult.entrySet()) {
		
			Intersectionobject currentangle = entry.getValue();
			
			parent.table.getModel().setValueAt(new DecimalFormat("#.###").format(currentangle.Intersectionpoint[0]), parent.row, 1);
		parent.table.getModel().setValueAt(new DecimalFormat("#.###").format(currentangle.Intersectionpoint[1]), parent.row, 2);
		parent.table.getModel().setValueAt(new DecimalFormat("#.###").format(currentangle.angle), parent.row, 3);
		parent.table.getModel().setValueAt(new DecimalFormat("#.###").format(currentangle.t), parent.row, 4);
		parent.table.getModel().setValueAt(new DecimalFormat("#.###").format(currentangle.z), parent.row, 5);
		
		}
		
		parent.table.validate();
		parent.scrollPane.validate();
		parent.panelFirst.repaint();
		parent.panelFirst.validate();
		
		
		
	}
	
	
		
		
		
		
		
	

}
