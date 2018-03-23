package utility;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.real.FloatType;

public class Watershedobject {
	
	public final RandomAccessibleInterval<BitType> source;
	public final double meanIntensity;
	public final double Size;
	
	public Watershedobject(final RandomAccessibleInterval<BitType> source, final double meanIntensity, final double Size) {
		
		this.source = source;
		this.meanIntensity = meanIntensity;
		this.Size = Size;
		
	}

}
