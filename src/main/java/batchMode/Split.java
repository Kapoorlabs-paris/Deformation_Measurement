package batchMode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import curvatureUtils.ExpandBorder;
import ellipsoidDetector.Intersectionobject;
import ellipsoidDetector.KymoSaveobject;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.Line;
import ij.gui.Roi;
import ij.io.Opener;
import ij.measure.ResultsTable;
import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import kalmanForSegments.Segmentobject;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.algorithm.ransac.RansacModels.Ellipsoid;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.Type;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.view.Views;
import pluginTools.Computeinwater;
import pluginTools.InteractiveSimpleEllipseFit;
import pluginTools.InteractiveSimpleEllipseFit.ValueChange;
import ransacPoly.RegressionFunction;
import utility.Curvatureobject;
import utility.Roiobject;


public class Split implements Runnable {

	
	public final ExecuteBatch parent;
	public final File ChA;
	public final File ChB;
	public final File ChSeg;
	public boolean twochannel;
	public int fileindex;
	
	public Split(final ExecuteBatch parent, final File ChA,final File ChB, final File ChSeg, int fileindex, boolean twochannel ) {
		
		this.parent = parent;
		this.ChA = ChA;
		this.ChB = ChB;
		this.ChSeg = ChSeg;
		this.twochannel = twochannel;
		this.fileindex = fileindex;
	
	}

	
	public Split(final ExecuteBatch parent, final File ChA, final File ChSeg, int fileindex, boolean twochannel ) {
		
		this.parent = parent;
		this.ChA = ChA;
		this.ChB = null;
		this.ChSeg = ChSeg;
		this.twochannel = twochannel;
		this.fileindex = fileindex;
	
	}
	
	@Override
	public void run() {
		
		
		
		JProgressBar fileprogress = new JProgressBar();
		fileprogress.setIndeterminate(false);

		fileprogress.setMaximum(parent.C1_AllImages.length);

		parent.label = new JLabel("Progress..");
		parent.frame = new JFrame();
		parent.panel = new JPanel();
		parent.frame.add(parent.panel);
		parent.frame.pack();
		parent.frame.setSize(500, 200);
		parent.frame.setVisible(true);
		
		parent.panel.add(fileprogress);
		System.out.println(parent.batchfolder + " " + fileindex);
			 
			
				ImagePlus impB = new Opener().openImage(ChA.getPath());
				parent.parent.originalimgbefore = ImageJFunctions.convertFloat(impB);
			
			if(parent.twochannel) {
				ImagePlus impC = new Opener().openImage(ChB.getPath());
				parent.parent.originalSecimg =  ImageJFunctions.convertFloat(impC);
				
			}
			
			try {
				parent.parent.originalimgsuper=  new ImgOpener().openImgs(ChSeg.getAbsolutePath(), new IntType()).get(0);
			} catch (ImgIOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			parent.parent.addToName = ChA.getName().replaceFirst("[.][^.]+$", "");
			
			
			new File(parent.batchfolder + "/Results").mkdirs();
			
		
				File Savefolder = new File(parent.batchfolder + "/Results");
			
			System.out.println(Savefolder);
			
			
			double percent = Math.round(100 * (fileindex + 1) / (parent.C1_AllImages.length ));

			utility.ProgressBar.SetProgressBarTime(fileprogress, percent, (fileindex + 1), (parent.C1_AllImages.length), "Processing Files (please wait)");

			
			
			if(!parent.twochannel)
				new InteractiveSimpleEllipseFit(parent.parent.originalimgbefore, parent.parent.originalimgbefore, parent.parent.originalimgsuper,
						parent.calibration, parent.timecal, false, false, false, true, parent.batchfolder, twochannel, ChA.getName()).runBatch(Savefolder);
			if(parent.twochannel)
				new InteractiveSimpleEllipseFit(parent.parent.originalimgbefore, parent.parent.originalSecimg, parent.parent.originalimgsuper,
						parent.calibration, parent.timecal, false, false, false, true, parent.batchfolder, twochannel, ChA.getName()).runBatch(Savefolder);
			
	
			
		

			
			
		
				
			
			
			
		
		
	}
	
	

}
