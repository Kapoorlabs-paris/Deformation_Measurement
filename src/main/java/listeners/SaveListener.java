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
import ij.IJ;
import kalmanForSegments.Segmentobject;
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
		
		
	
			
				NewSave();
				
				IJ.log("All trackes saved in: " + parent.saveFile.getAbsolutePath());
			
			
		
		
	}
	
	
	public void NewSave() {
		
	
		
		String ID = parent.selectedID;
		
		
			try {
				File fichier = new File(
						parent.saveFile + "//" + parent.addToName + "SegmentID" +ID + ".txt");

				FileWriter fw = new FileWriter(fichier);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write("\tTrackID" + "\t" + "\t" + ID + "\n");
				bw.write("\tX-coordinates\tY-coordinates\tCurvature\t Perimeter\t \t Intensity \t \t IntensitySec\n");
				for (Pair<String, Segmentobject> currentangle : parent.SegmentTracklist) {
					
					String currentID = currentangle.getA();
					
					if(currentID.equals(ID)) {
						
						
						bw.write("\t"+ parent.nf.format(currentangle.getB().centralpoint.getDoublePosition(0)) +  "\t" + "\t" + parent.nf.format(currentangle.getB().centralpoint.getDoublePosition(1))
								+ "\t" + "\t" +parent.nf.format(currentangle.getB().Curvature) + "\t"  + "\t"+  "\t" + "\t" + parent.nf.format(currentangle.getB().Perimeter) + "\t" + "\t"  
								+ parent.nf.format(currentangle.getB().IntensityA) +
								
								"\t" + "\t"  + parent.nf.format(currentangle.getB().IntensityB) + 
								"\n");
						
						
					}
				
				
			}
			
			
	    bw.close();
		fw.close();
		}
		catch (IOException te) {
		}
		
		
	}
	
	public void OldSave() {
		
		
		String ID = parent.selectedID;
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
			bw.write("\tX-coordinates\tY-coordinates\tCurvature\t \t Intensity \t \t IntensitySec\n");
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
				double[] IntensitySec = new double[currentpair.getB().getB().size()];
				if(time == z ) {
					
				
					for (int i = 0; i < currentpair.getB().getB().size(); ++i) {

						X[i] = currentpair.getB().getB().get(i)[0];
						Y[i] = currentpair.getB().getB().get(i)[1];
						I[i] = currentpair.getB().getB().get(i)[2];
						Intensity[i] = currentpair.getB().getB().get(i)[3];
						IntensitySec[i] = currentpair.getB().getB().get(i)[4];
					bw.write("\t"+ X[i] +  "\t" + "\t" + Y[i] + "\t" + "\t" + I[i] + "\t"  + "\t"+  "\t" + "\t" + Intensity[i] + "\t" + "\t"  + IntensitySec[i] +
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
	
	

