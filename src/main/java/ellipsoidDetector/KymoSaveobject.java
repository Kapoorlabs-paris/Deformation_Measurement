package ellipsoidDetector;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.real.FloatType;

public class KymoSaveobject {
	
	
	
	public final RandomAccessibleInterval<FloatType> CurvatureKymo;
	public final RandomAccessibleInterval<FloatType> IntensityAKymo;
	public final RandomAccessibleInterval<FloatType> IntensityBKymo;
	
	public KymoSaveobject(RandomAccessibleInterval<FloatType> CurvatureKymo, RandomAccessibleInterval<FloatType> IntensityAKymo,RandomAccessibleInterval<FloatType> IntensityBKymo ) {
		
		this.CurvatureKymo = CurvatureKymo;
		this.IntensityAKymo = IntensityAKymo;
		this.IntensityBKymo = IntensityBKymo;
		
		
	}

}
