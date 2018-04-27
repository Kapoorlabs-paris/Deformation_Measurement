package utility;

import java.util.ArrayList;

import ij.gui.EllipseRoi;
import ij.gui.Line;
import ij.gui.OvalRoi;
import ij.gui.Roi;

public class Roiobject {

	public final Roi[] roilist;
	public ArrayList<EllipseRoi> resultroi;
	public ArrayList<OvalRoi> resultovalroi;
	public ArrayList<Line> resultlineroi;
	public final int fourthDimension;
	public final int thirdDimension;
	public final boolean isCreated;

	public Roiobject(final Roi[] roilist, final int thirdDimension, final int fourthDimension, final boolean isCreated)

	{
		this.resultroi = null;
		this.resultovalroi = null;
		this.resultlineroi = null;
		this.roilist = roilist;
		this.fourthDimension = fourthDimension;
		this.thirdDimension = thirdDimension;
		this.isCreated = isCreated;
	}

	public Roiobject(final ArrayList<EllipseRoi> resultroi, ArrayList<OvalRoi> resultovalroi,
			ArrayList<Line> resultlineroi, final int thirdDimension, final int fourthDimension, final boolean isCreated)

	{
		this.resultroi = resultroi;
		this.resultovalroi = resultovalroi;
		this.resultlineroi = resultlineroi;
		this.roilist = null;
		this.fourthDimension = fourthDimension;
		this.thirdDimension = thirdDimension;
		this.isCreated = isCreated;
	}
	
	

}
