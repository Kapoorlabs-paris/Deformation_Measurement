package ellipsoidDetector;

import java.util.ArrayList;

import net.imglib2.algorithm.ransac.RansacModels.Ellipsoid;
import net.imglib2.algorithm.ransac.RansacModels.GeneralEllipsoid;
import net.imglib2.util.Pair;

public class Tangentobject {

	
	final ArrayList<double[]> Intersections;
	final Pair<Ellipsoid, Ellipsoid> ellipsepair;
	
	
	public Tangentobject(final ArrayList<double[]> Intersections, final Pair<Ellipsoid, Ellipsoid> ellipsepair) {
		
		
		this.Intersections = Intersections;
		this.ellipsepair = ellipsepair;
		
	}
	
}