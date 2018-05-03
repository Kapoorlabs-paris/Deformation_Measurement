package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import ellipsoidDetector.Intersectionobject;
import pluginTools.InteractiveSimpleEllipseFit;
import utility.Curvatureobject;

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
					parent.saveFile + "//" + parent.addToName + "TrackID" +ID + ".txt");

			FileWriter fw = new FileWriter(fichier);
			BufferedWriter bw = new BufferedWriter(fw);
			if (!parent.curveautomode && !parent.curvesupermode) {
			bw.write(
					"\tTime (px)\t AngleT \n");
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
			}
			
			else {
				
				bw.write("\tCellLabel\tX-coordinates\tY-coordinates\tCurvature\tPerimeter\tZposition\tTposition\n");
				for (ArrayList<Curvatureobject> Allcurrentcurvature : parent.AlllocalCurvature) {
					for (int index = 0; index < Allcurrentcurvature.size(); ++index) {
						Curvatureobject currentcurvature = Allcurrentcurvature.get(index);

						if (ID.equals(Integer.toString(currentcurvature.Label)) && parent.thirdDimension == currentcurvature.z
								&& parent.fourthDimension == currentcurvature.t) {
							
							
							int Label = currentcurvature.Label;
							int t = currentcurvature.t;
							int z = currentcurvature.z;
							double curvature = currentcurvature.radiusCurvature;
							double perimeter = currentcurvature.perimeter;
							double X = currentcurvature.cord[0];
							double Y = currentcurvature.cord[1];
							bw.write("\t" + Label + "\t" + "\t"
									+ X +  "\t" + "\t" + Y + "\t" + "\t" + parent.nf.format(curvature) + "\t" + "\t" + parent.nf.format(perimeter) +"\t" + "\t" + z + "\t" + "\t" + t + 
									
									"\n");
							
							
						}
					
						
						
					}
					}
				
				
			}
			
		bw.close();
		fw.close();
		}
		catch (IOException te) {
		}
		
	}
	
}
