package ellipsoidDetector;

import net.imglib2.algorithm.ransac.RansacModels.Ellipsoid;
import net.imglib2.util.Pair;

public class Intersectionobject {

	
	
	public final double[] Intersectionpoint;
	public final Pair<Ellipsoid, Ellipsoid> ellipsepair;
	
	
	
	public Intersectionobject(final double[] Intersectionpoint, final Pair<Ellipsoid, Ellipsoid> ellipsepair) {
		
		this.Intersectionpoint = Intersectionpoint;
		this.ellipsepair = ellipsepair;
		
	}
	
}
