package curvatureFinder;

import java.util.List;

import javax.swing.JProgressBar;

import ij.IJ;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.algorithm.ransac.RansacModels.ConnectedComponentCoordinates;
import net.imglib2.type.numeric.real.FloatType;
import pluginTools.InteractiveSimpleEllipseFit;

public class GetCandidatePoints {
	
	
	
	public static List<RealLocalizable> ListofPoints(final InteractiveSimpleEllipseFit parent, final RandomAccessibleInterval<FloatType> ActualRoiimg ,final JProgressBar jpb, int percent, int t, int z) {
		
		parent.Allnodes.clear();
		parent.Nodemap.clear();
		parent.Listmap.clear();

		if (parent.fourthDimensionSize != 0 && parent.Accountedframes.size() != 0 && parent.Accountedframes != null)
			utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.pixellist.size()), "Computing Curvature = "
					+ t + "/" + parent.fourthDimensionSize + " Z = " + z + "/" + parent.thirdDimensionSize);
		else if (parent.thirdDimensionSize != 0 && parent.AccountedZ.size() != 0 && parent.AccountedZ != null)
			utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.pixellist.size()),
					"Computing Curvature T/Z = " + z + "/" + parent.thirdDimensionSize);
		else {

			utility.ProgressBar.SetProgressBar(jpb, 100 * (percent) / (parent.pixellist.size()),
					"Computing Curvature ");
		}
		
		
		List<RealLocalizable> truths = ConnectedComponentCoordinates.GetCoordinatesBit(ActualRoiimg);
		if(parent.thirdDimension == parent.AutostartTime )
		IJ.log("Box size for curvature calculation = " + (int) ((truths.size() / parent.minNumInliers ) * parent.calibration) + " " + " um " );
		
		return truths;
	}

}
