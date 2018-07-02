package curvatureUtils;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.neighborhood.Neighborhood;
import net.imglib2.algorithm.neighborhood.RectangleShape;
import net.imglib2.algorithm.region.hypersphere.HyperSphere;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;
import pluginTools.InteractiveSimpleEllipseFit;

public class ExpandBorder {

	/**
	 * 
	 * This method expands the border pixel of an integer image by certain pixels
	 * 
	 * @param parent
	 * @param source
	 */
	public static  < T extends RealType< T > & NativeType< T >> void extendBorder(final InteractiveSimpleEllipseFit parent, RandomAccessibleInterval<T> source) {
		
		T type = Views.iterable(source).cursor().next().createVariable();
		RandomAccessibleInterval<T> output =  new ArrayImgFactory<T>().create(source, type);
		
		final Cursor< T > center =  Views.iterable( source ).cursor();
		
		  final RectangleShape shape = new RectangleShape( 1, true );
		
		  
	       // so that the search in the 8-neighborhood (3x3x3...x3) never goes outside
	        // of the defined interval
	        Interval interval = Intervals.expand( source, -1 );
	 
	        // create a view on the source with this interval
	        source = Views.interval( source, interval );
	        
		  for ( final Neighborhood< T > localNeighborhood : shape.neighborhoods( source ) ) 
		  {
			  
			  final T centerValue = center.next();
			  
			  boolean isBorderPixel = false;
			  
			  
			  for (final T value : localNeighborhood)
			  {
				  
				  if(centerValue.compareTo(value)!=0) {
					  
					  
					  isBorderPixel = true;
					  
					  break;
					  
					  
				  }
				  
				  
			  }
			  
			  if(isBorderPixel) {
				  
				  HyperSphere< T > hypershpere = new HyperSphere< T >(output, center, (int)parent.borderpixel );
				  
				  
				  for (T value : hypershpere)
					  value.set(centerValue);
				  
				  
			  }
			  
			  
			  
		  }
		
		
	}
	
	
}
