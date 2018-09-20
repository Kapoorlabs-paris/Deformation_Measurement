package imageAxis;

import ij.ImagePlus;

public class AxisRendering {

	
	
	public static void Reshape(ImagePlus imp) {
		
		int channels, frames;
		
		
		
		if(imp.getNChannels() > imp.getNFrames()) {
			channels = imp.getNFrames();
		    frames = imp.getNChannels();
		    
		}
		
		else {
			
			channels = imp.getNChannels();
		    frames = imp.getNFrames();
			
		}
		imp.setDimensions(channels, imp.getNSlices(), frames);
		
		
	}
	
}
