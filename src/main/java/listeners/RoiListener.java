package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import distanceTransform.WatershedBinary;
import ij.gui.Roi;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.Type;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;
import pluginTools.InteractiveEllipseFit;
import pluginTools.InteractiveEllipseFit.ValueChange;
import preProcessing.Otsu;
import utility.Roiobject;

public class RoiListener implements ActionListener{
	
	
	final InteractiveEllipseFit parent;
	
	
	public RoiListener (final InteractiveEllipseFit parent) {
		
		this.parent = parent;
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		
		parent.updatePreview(ValueChange.ROI);
		

		RandomAccessibleInterval<BitType> totalimg = new ArrayImgFactory<BitType>()
				.create(new long[] { parent.originalimg.dimension(0), parent.originalimg.dimension(1) }, new BitType());
		
		Paint(totalimg, parent.uniqueID, (int)parent.thirdDimension, (int)parent.fourthDimension);
		
		Segment(totalimg);
		
		
		
	}
	
	
	
	private void Segment(RandomAccessibleInterval<BitType> totalimg) {
		
		
		
		
		WatershedBinary segmentimage = new WatershedBinary(totalimg);
		segmentimage.process();
		RandomAccessibleInterval<IntType> watershedimage = segmentimage.getResult();
		parent.maxlabel = segmentimage.GetMaxlabelsseeded(watershedimage);
		SliceInt(watershedimage, (int)parent.thirdDimension, (int)parent.fourthDimension);
		
	}
	private void SliceInt(RandomAccessibleInterval<IntType> current, int z, int t) {


		final Cursor<IntType> cursor = Views.iterable(current).localizingCursor();
		final RandomAccess<IntType> ranacsec;
		if(parent.originalimg.numDimensions() > 3)
		ranacsec = Views.hyperSlice(Views.hyperSlice(parent.emptyWater, 2, z - 1), 2, t - 1).randomAccess();
		else
		ranacsec = Views.hyperSlice(parent.emptyWater, 2, z - 1).randomAccess();
		while (cursor.hasNext()) {

			cursor.fwd();

			ranacsec.setPosition(cursor.getIntPosition(0), 0);
			ranacsec.setPosition(cursor.getIntPosition(1), 1);
          
			
			ranacsec.get().set(cursor.get());
		}
	}
	
	private void CreateBinary(RandomAccessibleInterval<FloatType> source, RandomAccessibleInterval<BitType> sourcebit, double lowprob, double highprob) {
		
		
		RandomAccessibleInterval<FloatType> copyoriginal = new ArrayImgFactory<FloatType>().create(source, new FloatType());
		
		final RandomAccess<FloatType> ranac =  copyoriginal.randomAccess();
		final RandomAccess<BitType> bitranac = sourcebit.randomAccess();
		final Cursor<FloatType> cursor = Views.iterable(source).localizingCursor();
		
		while(cursor.hasNext()) {
			
			cursor.fwd();
			
			ranac.setPosition(cursor);
			bitranac.setPosition(cursor);
			if(cursor.get().getRealDouble() < lowprob && cursor.get().getRealDouble() > highprob) {
				ranac.get().set(0);
			    bitranac.get().setZero();	
			}
			else {
				ranac.get().set(ranac.get());
			    bitranac.get().setOne();
			}
			
			
		}
		
		
		copy(copyoriginal, Views.iterable(source));
		
	}
	 public < T extends Type< T > > void copy( final RandomAccessibleInterval< T > source,
		        final IterableInterval< T > target )
		    {
		        // create a cursor that automatically localizes itself on every move
		        Cursor< T > targetCursor = target.localizingCursor();
		        RandomAccess< T > sourceRandomAccess = source.randomAccess();
		 
		        // iterate over the input cursor
		        while ( targetCursor.hasNext())
		        {
		            // move input cursor forward
		            targetCursor.fwd();
		 
		            // set the output cursor to the position of the input cursor
		            sourceRandomAccess.setPosition( targetCursor );
		 
		            // set the value of this pixel of the output image, every Type supports T.set( T type )
		            targetCursor.get().set( sourceRandomAccess.get() );
		        }
		    }
	private void Slice(RandomAccessibleInterval<BitType> current, ArrayList<int[]> pointlist, int z, int t) {

		final RandomAccess<BitType> ranac = current.randomAccess();
		for (int[] point : pointlist) {

			ranac.setPosition(point);

			
			ranac.get().setOne();

		}

		final Cursor<BitType> cursor = Views.iterable(current).localizingCursor();
		final RandomAccess<BitType> ranacsec;
		if (parent.originalimg.numDimensions() >3)
		ranacsec = Views.hyperSlice(Views.hyperSlice(parent.empty, 2, z - 1), 2, t - 1).randomAccess();
		else
		ranacsec = Views.hyperSlice(parent.empty, 2, z - 1).randomAccess();
		while (cursor.hasNext()) {

			cursor.fwd();

			ranacsec.setPosition(cursor.getIntPosition(0), 0);
			ranacsec.setPosition(cursor.getIntPosition(1), 1);
          
			
			ranacsec.get().set(cursor.get());
		}
	}

	private void Paint(RandomAccessibleInterval<BitType> current, String id, int z, int t) {

		Roiobject currentobject = parent.ZTRois.get(id);

		if (currentobject != null) {
			ArrayList<int[]> pointlist = new ArrayList<int[]>();

			Roi[] roilist = currentobject.roilist;

			for (int i = 0; i < roilist.length; ++i) {

				Roi currentroi = roilist[i];

				final float[] xCord = currentroi.getInterpolatedPolygon().xpoints;
				final float[] yCord = currentroi.getInterpolatedPolygon().ypoints;

				int N = xCord.length;

				for (int index = 0; index < N; ++index) {

					pointlist.add(new int[] { Math.round(xCord[index]), Math.round(yCord[index]), z, t });
				}

			}
			Slice(current, pointlist, z , t);
		}

	}
}
