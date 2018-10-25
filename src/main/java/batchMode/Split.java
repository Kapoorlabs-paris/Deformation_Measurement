package batchMode;

import java.io.File;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import ellipsoidDetector.Intersectionobject;
import ij.ImagePlus;
import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import kalmanForSegments.Segmentobject;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import pluginTools.InteractiveSimpleEllipseFit;


public class Split implements Runnable {

	
	public final ExecuteBatch parent;
	public final File ChA;
	public final File ChB;
	public final File ChSeg;
	public boolean twochannel;
	public int fileindex;
	public JProgressBar fileprogress;
	public Split(final ExecuteBatch parent, final File ChA,final File ChB, final File ChSeg, int fileindex, boolean twochannel , JProgressBar fileprogress ) {
		
		this.parent = parent;
		this.ChA = ChA;
		this.ChB = ChB;
		this.ChSeg = ChSeg;
		this.twochannel = twochannel;
		this.fileindex = fileindex;
		this.fileprogress = fileprogress;
	}

	
	public Split(final ExecuteBatch parent, final File ChA, final File ChSeg, int fileindex, boolean twochannel, JProgressBar fileprogress  ) {
		
		this.parent = parent;
		this.ChA = ChA;
		this.ChB = null;
		this.ChSeg = ChSeg;
		this.twochannel = twochannel;
		this.fileindex = fileindex;
		this.fileprogress = fileprogress;
	}
	
	@Override
	public void run() {
		
		
		

		fileprogress.setIndeterminate(false);

		fileprogress.setMaximum(parent.C1_AllImages.length);

	
		parent.panel.add(fileprogress);
		
			 
			try {
				parent.parent.originalimg = new ImgOpener().openImgs(ChA.getAbsolutePath(), new FloatType()).get(0);
			
			
			
			if(parent.twochannel) {
			parent.parent.originalSecimg =  new ImgOpener().openImgs(ChB.getAbsolutePath(), new FloatType()).get(0);
			
			
			}
			parent.parent.originalimgsuper=  new ImgOpener().openImgs(ChSeg.getAbsolutePath(), new IntType()).get(0);
			
			parent.parent.addToName = ChA.getName().replaceFirst("[.][^.]+$", "");
			System.out.println(parent.parent.addToName);
			System.out.println(parent.batchfolder);
			double percent = Math.round(100 * (fileindex + 1) / (parent.C1_AllImages.length - 1));

			utility.ProgressBar.SetProgressBarTime(fileprogress, percent, (fileindex + 1), (parent.C1_AllImages.length), "Processing Files (please wait)");

			
			parent.parent.background = LocalPrefs.getInt(".BackgroundLabel.int", 0);
			parent.parent.minNumInliers = LocalPrefs.getInt(".NumberofSegments", 10);
			parent.parent.resolution = LocalPrefs.getInt(".Resolution.int", 1);
			parent.parent.timecal = LocalPrefs.getDouble(".TimeCalibration", 1);
			parent.parent.calibration = LocalPrefs.getDouble(".SpaceCalibration", 1);
			parent.parent.pixelcelltrackcirclefits = LocalPrefs.getBoolean(".CurvatureViaCircle.boolean", true);
			parent.parent.distancemethod = LocalPrefs.getBoolean(".CurvatureViaDistance.boolean", false);
			
			System.out.println(parent.parent.pixelcelltrackcirclefits + " circle or not");
			
			
				
				// Execute circle curvatureloop
				
				
			
			} catch (ImgIOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
				
			
			
			
		
		
	}
	
	
	public  void ClearStuff() {
		
		parent.parent.table.removeAll();
		parent.parent.table.repaint();
		parent.parent.localCurvature.clear();
		parent.parent.AlllocalCurvature.clear();
		parent.parent.KymoFileobject.clear();
		parent.parent.overlay.clear();
		parent.parent.Tracklist.clear();
		if(parent.parent.imp!=null && parent.parent.mvl!=null)
			parent.parent.imp.getCanvas().removeMouseListener(parent.parent.mvl);
		if(parent.parent.imp!=null && parent.parent.ml!=null)
			parent.parent.imp.getCanvas().removeMouseMotionListener(parent.parent.ml);
		parent.parent.starttime = Integer.parseInt(parent.parent.startT.getText());
		parent.parent.endtime = Integer.parseInt(parent.parent.endT.getText());
		parent.parent.resolution = Integer.parseInt(parent.parent.resolutionField.getText());
		parent.parent.displayCircle.setState(false);
		parent.parent.displaySegments.setState(false);
		parent.parent.displayIntermediate = false;
		parent.parent.displayIntermediateBox = false;
		parent.parent.parentgraph = new SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>(
				DefaultWeightedEdge.class);
		parent.parent.parentgraphZ = new HashMap<String, SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>>();
		parent.parent.empty = utility.Binarization.CreateBinaryBit(parent.parent.originalimg, parent.parent.lowprob, parent.parent.highprob);
		parent.parent.parentgraphSegZ = new HashMap<String, SimpleWeightedGraph<Segmentobject, DefaultWeightedEdge>>();
		parent.parent.parentdensegraphZ = new HashMap<String, SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>>();
		parent.parent.ALLSegments.clear();
		parent.parent.SegmentFinalresult.clear();
		parent.parent.overlay.clear();
		parent.parent.AccountedZ.clear();
		parent.parent.AutostartTime = Integer.parseInt(parent.parent.startT.getText());
		if (parent.parent.AutostartTime <= 0)
			parent.parent.AutostartTime = 1;
		parent.parent.AutoendTime = Integer.parseInt(parent.parent.endT.getText());
		for(int z = parent.parent.AutostartTime; z <= parent.parent.AutoendTime; ++z)
			parent.parent.AccountedZ.put(Integer.toString(z), z);
		
	}
}
