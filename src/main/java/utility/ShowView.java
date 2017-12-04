package utility;

import ij.IJ;
import pluginTools.InteractiveEllipseFit;

public class ShowView {

	
	final InteractiveEllipseFit parent;
	
	
	public ShowView(final InteractiveEllipseFit parent) {
		
		this.parent = parent;
		
	}
	
	
	public void shownewZ() {

		if (parent.fourthDimension > parent.fourthDimensionSize) {
			IJ.log("Max Z stack exceeded, moving to last Z instead");
			parent.fourthDimension = parent.fourthDimensionSize;
			
			
			parent.CurrentView = utility.Slicer.getCurrentView(parent.originalimg, parent.thirdDimension,
					parent.thirdDimensionSize, parent.fourthDimension, parent.fourthDimensionSize);
			
		} else {

			parent.CurrentView = utility.Slicer.getCurrentView(parent.originalimg, parent.thirdDimension,
					parent.thirdDimensionSize, parent.fourthDimension, parent.fourthDimensionSize);
			
		}

		
	}

	
	
	public void shownewT() {

		if (parent.thirdDimension > parent.thirdDimensionSize) {
			IJ.log("Max time point exceeded, moving to last time point instead");
			parent.thirdDimension = parent.thirdDimensionSize;
			
			
			parent.CurrentView = utility.Slicer.getCurrentView(parent.originalimg, parent.thirdDimension,
					parent.thirdDimensionSize, parent.fourthDimension, parent.fourthDimensionSize);
			
		} else {

			parent.CurrentView = utility.Slicer.getCurrentView(parent.originalimg, parent.thirdDimension,
					parent.thirdDimensionSize, parent.fourthDimension, parent.fourthDimensionSize);
			
		}

		
		
	

		
	}
	
}
