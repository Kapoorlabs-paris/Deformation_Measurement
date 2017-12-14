package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import ellipsoidDetector.Intersectionobject;
import pluginTools.InteractiveEllipseFit;

public class SaveListener implements ActionListener {

	final InteractiveEllipseFit parent;

	public SaveListener(final InteractiveEllipseFit parent) {

		this.parent = parent;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
	
		String ID = (String) parent.table.getValueAt(parent.row, 0);
		
		try {
			File fichier = new File(
					parent.usefolder + "//" + parent.addToName + "TrackID" +ID + ".txt");

			FileWriter fw = new FileWriter(fichier);
			BufferedWriter bw = new BufferedWriter(fw);
			
			bw.write(
					"\tTime (px)\t AngleT \n");
		
		for (int index = 0; index< parent.resultAngle.size(); ++index) {
			
			
			if (ID == parent.resultAngle.get(index).getA() ) {
				
				
				// Save result sin file
			
				int time = (int) parent.resultAngle.get(index).getB()[0];
				double angle = parent.resultAngle.get(index).getB()[1];
				bw.write("\t" + time + "\t" + "\t"
						+ angle + 
						
						"\n");
				

				
			}
			
		}
		bw.close();
		fw.close();
		}
		catch (IOException te) {
		}
		
	}
	
}
