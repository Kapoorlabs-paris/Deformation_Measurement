package utility;

import ij.IJ;
import pluginTools.InteractiveEllipseFit;

public class ShowResultView {

	
final InteractiveEllipseFit parent;
final int time;
final int Z;
	
	public ShowResultView(final InteractiveEllipseFit parent, final int time, final int Z) {
		
		this.parent = parent;
		this.time = time;
		this.Z = Z;
		
	}
	
	
	public void shownew() {

	

			parent.CurrentResultView = utility.Slicer.getCurrentView(parent.originalimg,(int) Z,
					(int)parent.thirdDimensionSize, time,(int) parent.fourthDimensionSize);
			
		

		
	}
	
	
	
}
