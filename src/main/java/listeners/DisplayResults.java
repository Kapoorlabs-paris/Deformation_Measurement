package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import ellipsoidDetector.Intersectionobject;
import net.imglib2.util.Pair;
import pluginTools.InteractiveEllipseFit;

public class DisplayResults implements ActionListener {

	
	final InteractiveEllipseFit parent;
	final String ID;
	
	public DisplayResults(final InteractiveEllipseFit parent, final String ID) {
		
		this.parent = parent;
		this.ID = ID;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {

		
		for (Pair<Integer,Intersectionobject> currentangle: parent.Tracklist) {
			
			
			ArrayList<double[]> resultlist = new ArrayList<double[]>();
			if (Integer.parseInt(ID) == currentangle.getA()) {
				
				resultlist.add(new double[] {currentangle.getB().t, currentangle.getB().z, currentangle.getB().Intersectionpoint[0], currentangle.getB().Intersectionpoint[1]  });
				parent.resultDraw.put(ID, resultlist);
				
				
			}
				
				
		}
		
		
		
		
		
	}

}
