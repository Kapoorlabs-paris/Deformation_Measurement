package batchMode;

import java.io.File;

import javax.swing.JProgressBar;

import ij.IJ;
import ij.plugin.PlugIn;
import pluginTools.InteractiveSimpleEllipseFit;

public class ExecuteBatch implements PlugIn, Runnable {

	
	public int NumberofSegments = (int) LocalPrefs.getDouble(".NumberofSegments.int", 10);
	public int BackgroundLabel = (int) LocalPrefs.getDouble(".BackgroundLabel.int", 10);
	public int Resolution = (int) LocalPrefs.getDouble(".Resolution.int", 10);
	public double IntensityRadius = LocalPrefs.getDouble(".IntensityRadius.double", 10);
	public String batchfolder;
	public String userfile;
	public JProgressBar jpb;
	public JProgressBar jpbpre;
	public final File[] C1_AllImages;
	public final File[] C2_AllImages;
	public final InteractiveSimpleEllipseFit parent;
	public final String channelA;
	public final String channelB;
	
	public ExecuteBatch() {
		this.parent = null;
		this.C1_AllImages = null;
		this.C2_AllImages = null;
		this.channelA = null;
		this.channelB = null;
	};

	public ExecuteBatch(final File[] C1_AllImages, final File[] C2_AllImages, final String channelA, final String channelB, final InteractiveSimpleEllipseFit parent, final File batchdirectory) {

		this.C1_AllImages = C1_AllImages;
		this.C2_AllImages = C2_AllImages;
		this.channelA = channelA;
		this.channelB = channelB;
		this.parent = parent;
		this.batchfolder = batchdirectory.getParent();

	}
	
	@Override
	public void run() {
		jpbpre = new JProgressBar();
		jpb = new JProgressBar();
		goTrack();
		
	}
	@Override
	public void run(String arg0) {
		
		jpbpre = new JProgressBar();
		jpb = new JProgressBar();
		run();
		
	}
	
public void goTrack() {

		
		LocalPrefs.setHomeDir(C1_AllImages[0].getParent());
		parent.usefolder = batchfolder;
		LocalPrefs.load(C1_AllImages[0].getParent(), IJ.getApplet());
		ProgressBatch startbatch = new ProgressBatch(this);
		startbatch.execute();
		

	}
	
}
