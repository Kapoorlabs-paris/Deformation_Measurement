package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import ellipsoidDetector.Intersectionobject;
import hashMapSorter.SortCoordinates;
import net.imglib2.util.Pair;
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
		if (!parent.curveautomode && !parent.curvesupermode) {
		try {
			File fichier = new File(
					parent.saveFile + "//" + parent.addToName + "TrackID" +ID + ".txt");

			FileWriter fw = new FileWriter(fichier);
			BufferedWriter bw = new BufferedWriter(fw);
			
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
			
		bw.close();
		fw.close();
		}
		catch (IOException te) {
		}
		}
			
			else {
				for (int z = parent.AutostartTime; z <= parent.AutoendTime; ++z) {
				try {
					File fichier = new File(
							parent.saveFile + "//" + parent.addToName + "CellID" +ID + "Tposition" + z + ".txt");

					FileWriter fw = new FileWriter(fichier);
					BufferedWriter bw = new BufferedWriter(fw);
				
				ArrayList<Pair<String, double[]>> currentresultPeri = new ArrayList<Pair<String, double[]>>();
				for (Pair<String, double[]> currentperi : parent.resultAngle) {

					if (ID.equals(currentperi.getA())) {

						currentresultPeri.add(currentperi);

					}

				}
				for (int index = 0; index < currentresultPeri.size(); ++index) {
					int time = (int)currentresultPeri.get(index).getB()[0];
					if(time == z) {
					bw.write("\tTrackID" + "\t" + "\t" + ID);
					bw.write("\tTimepoint" + "\t" + "\t" + z);
					bw.write("\tPerimeter" + "\t" + "\t" + currentresultPeri.get(index).getB()[1] + 
								
								"\n");	
					break;
					}
				}
				bw.write("\tX-coordinates\tY-coordinates\tCurvature\t \t Intensity\n");
				ArrayList<Pair<String, Pair< Integer,ArrayList<double[]>>>> currentresultCurv = new ArrayList<Pair<String, Pair< Integer,ArrayList<double[]>>>>();
				for(Pair<String, Pair< Integer,ArrayList<double[]>>> currentCurvature : parent.resultCurvature) {
					
					
					if (ID.equals(currentCurvature.getA())) {
						
						currentresultCurv.add(currentCurvature);
						
						
					}
					
				}
				
				for (int index = 0; index < currentresultCurv.size(); ++index) {

					Pair<String, Pair<Integer, ArrayList<double[]>>> currentpair = currentresultCurv.get(index);

					int time = currentpair.getB().getA();

					double[] X = new double[currentpair.getB().getB().size()];
					double[] Y = new double[currentpair.getB().getB().size()];
					double[] I = new double[currentpair.getB().getB().size()];
					
					
					double[] Intensity = new double[currentpair.getB().getB().size()];

					if(time == z ) {
						
					
						for (int i = 0; i < currentpair.getB().getB().size(); ++i) {

							X[i] = currentpair.getB().getB().get(i)[0];
							Y[i] = currentpair.getB().getB().get(i)[1];
							I[i] = currentpair.getB().getB().get(i)[2];
							Intensity[i] = currentpair.getB().getB().get(i)[3];
						
						bw.write("\t"+ X[i] +  "\t" + "\t" + Y[i] + "\t" + "\t" + I[i] + "\t"  + "\t"+  "\t" + "\t" + Intensity[i] + "\t" + "\t" + 
								"\n");
						
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
			
		
		
	}
	
}
