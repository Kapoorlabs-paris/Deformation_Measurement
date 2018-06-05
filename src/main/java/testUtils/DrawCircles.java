package testUtils;

import java.awt.Dimension;

import ij.ImageJ;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class DrawCircles {

	
	
	public static void drawCircle(RandomAccessibleInterval<FloatType> imgout, double[] center, double radius) {
		
		int n = imgout.numDimensions();
		
		double[] location = new double[n];
		double[] size = new double[n];
		final RandomAccess<FloatType> outbound = imgout.randomAccess();
		double stepsize = 0.1;
		int[] setpos = new int[n];
		double[] newpos = new double[n];
		double[] sigma = new double[n];
		
		for (int d = 0; d < n; ++d)
			size[d] = imgout.dimension(d);
		sigma[0] = 1;
		sigma[1] = 1;
		
		for (int theta = 0; theta < 360; ++theta) {
			
			
				newpos[0] = center[0] + radius * Math.cos(Math.toRadians(theta));
				newpos[1] = center[1] + radius * Math.sin(Math.toRadians(theta));
				addGaussian(imgout, newpos, sigma);
			
		}
		
		
	}

	final public static void addGaussian( final RandomAccessibleInterval< FloatType > image, final double[] location, final double[] sigma)
	{
	final int numDimensions = image.numDimensions();
	final int[] size = new int[ numDimensions ];

	final long[] min = new long[ numDimensions ];
	final long[] max = new long[ numDimensions ];

	final double[] two_sq_sigma = new double[ numDimensions ];

	for ( int d = 0; d < numDimensions; ++d )
	{
	size[ d ] = 2 * getSuggestedKernelDiameter( sigma[ d ] );
	min[ d ] = (int)Math.round( location[ d ] ) - size[ d ] / 2;
	max[ d ] = min[ d ] + size[ d ] - 1;
	two_sq_sigma[ d ] =  sigma[ d ] * sigma[ d ];
	}

	final RandomAccessible< FloatType > infinite = Views.extendZero( image );
	final RandomAccessibleInterval< FloatType > interval = Views.interval( infinite, min, max );
	final IterableInterval< FloatType > iterable = Views.iterable( interval );
	final Cursor< FloatType > cursor = iterable.localizingCursor();
	
	
	
	while ( cursor.hasNext() )
	{
	cursor.fwd();

	double value = 1;

	for ( int d = 0; d < numDimensions; ++d )
	{
	final double x = location[ d ] - cursor.getDoublePosition( d );
	value *= Math.exp( -(x * x) / two_sq_sigma[ d ] );
	
	
	}
	
	
	cursor.get().set( cursor.get().get() + (float)value );
	
	
	}
	
	
	
	
	}
	
	
	
	
	
	
	final public static void addGaussian( final IterableInterval< FloatType > image, final double Amplitude,
			final double[] location, final double[] sigma)
	{
	final int numDimensions = image.numDimensions();
	final int[] size = new int[ numDimensions ];

	final long[] min = new long[ numDimensions ];
	final long[] max = new long[ numDimensions ];


	for ( int d = 0; d < numDimensions; ++d )
	{
	size[ d ] = getSuggestedKernelDiameter( sigma[ d ] ) * 2;
	min[ d ] = (int)Math.round( location[ d ] ) - size[ d ]/2;
	max[ d ] = min[ d ] + size[ d ] - 1;
	
	}

	
	final Cursor< FloatType > cursor = image.localizingCursor();
	while ( cursor.hasNext() )
	{
	cursor.fwd();

	double value = Amplitude;

	for ( int d = 0; d < numDimensions; ++d )
	{
	final double x = location[ d ] - cursor.getIntPosition( d );
	value *= Math.exp( -(x * x) / (sigma[ d ] * sigma[ d ] ) );
	}
	
	
	cursor.get().set( cursor.get().get() + (float)value );
	
	
	}
	
	}

	public static int getSuggestedKernelDiameter( final double sigma )
	{
	int size = 0;
    int cutoff = 5; // This number means cutoff is chosen to be cutoff times sigma. 
    if ( sigma > 0 )
	size = Math.max( cutoff, ( 2 * ( int ) ( cutoff * sigma + 0.5 ) + 1 ) );

	return size;
	}
	
	public static void main(String[] args) {
		
		new ImageJ();
		
		RandomAccessibleInterval<FloatType> img = new ArrayImgFactory<FloatType>().create(new long[] {512, 512}, new FloatType());
		double[] center = {256, 256};
		double radius = 100;
		drawCircle(img, center, radius);
		ImageJFunctions.show(img);
		
		img = new ArrayImgFactory<FloatType>().create(new long[] {512, 512}, new FloatType());
		drawCircle(img, center, radius);
		
		ImageJFunctions.show(img);
		
	}
	
	
	
}
