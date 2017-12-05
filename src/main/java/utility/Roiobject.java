package utility;

import java.util.ArrayList;

import ij.gui.Roi;

public class Roiobject {

	
	public final Roi[] roilist;
	public final int fourthDimension;
	public final int thirdDimension;
	public final boolean isCreated;
	
	
	public Roiobject (final Roi[] roilist, final int thirdDimension, final int fourthDimension, final boolean isCreated)
	
	{
		this.roilist = roilist;
		this.fourthDimension = fourthDimension;
		this.thirdDimension= thirdDimension;
		this.isCreated = isCreated;
	}
	
	
}
