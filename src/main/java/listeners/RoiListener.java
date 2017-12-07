package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import ij.gui.Roi;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.logic.BitType;
import net.imglib2.view.Views;
import pluginTools.InteractiveEllipseFit;
import pluginTools.InteractiveEllipseFit.ValueChange;
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
		
		Paint(totalimg, parent.uniqueID, parent.thirdDimension, parent.fourthDimension);
		
		
		
	}
	private void Slice(RandomAccessibleInterval<BitType> current, ArrayList<int[]> pointlist, int z, int t) {

		final RandomAccess<BitType> ranac = current.randomAccess();
		for (int[] point : pointlist) {

			ranac.setPosition(point);

			
			ranac.get().setOne();

		}

		final Cursor<BitType> cursor = Views.iterable(current).localizingCursor();
		final RandomAccess<BitType> ranacsec = Views.hyperSlice(Views.hyperSlice(parent.empty, 2, z - 1), 2, t - 1).randomAccess();
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
