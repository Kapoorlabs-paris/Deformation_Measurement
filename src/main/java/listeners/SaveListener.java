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
import pluginTools.InteractiveSimpleEllipseFit;

public class SaveListener implements ActionListener {

	final InteractiveSimpleEllipseFit parent;

	public SaveListener(final InteractiveSimpleEllipseFit parent) {

		this.parent = parent;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		String ID = parent.selectedID;
		try {
			File fichier = new File(
					 parent.chooserA.getSelectedFile() + "//" + parent.addToName + "TrackID" +ID + ".txt");

			FileWriter fw = new FileWriter(fichier);
			BufferedWriter bw = new BufferedWriter(fw);
			
			bw.write(
					"\tTime (px)\t AngleT \n");
		System.out.println(parent.resultAngle.size() + " Size of write");
		for (int index = 0; index< parent.resultAngle.size(); ++index) {
			
			
			if (ID.equals(parent.resultAngle.get(index).getA() )) {
				
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
