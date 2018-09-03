package batchMode;

import pluginTools.InteractiveSimpleEllipseFit;

public class CreateINIfile {

	
	final InteractiveSimpleEllipseFit parent;
	
	public CreateINIfile(final InteractiveSimpleEllipseFit parent) {
		
		this.parent = parent;
		
	}
	
	
	public void RecordParent() {
		
		
		LocalPrefs.set("BackgroundLabel.int", parent.background);
		LocalPrefs.set("NumberofSegments.int", parent.minNumInliers);
		LocalPrefs.set("Resolution.int", parent.resolution);
		LocalPrefs.set("IntensityRadius.double", parent.insidedistance);
		LocalPrefs.setHomeDir(parent.userfile.getParent());
        LocalPrefs.savePreferences();
		
		System.exit(1);
	}
	
	
}
