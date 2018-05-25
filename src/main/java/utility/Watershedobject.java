package utility;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.RealSum;
import net.imglib2.util.Util;
import net.imglib2.view.Views;
import preProcessing.GenericFilters;
import preProcessing.Kernels;

public class Watershedobject {

	public final RandomAccessibleInterval<BitType> source;
	public final double meanIntensity;
	public final double Size;

	public Watershedobject(final RandomAccessibleInterval<BitType> source, final double meanIntensity,
			final double Size) {

		this.source = source;
		this.meanIntensity = meanIntensity;
		this.Size = Size;

	}

	public static Watershedobject CurrentLabelImage(RandomAccessibleInterval<IntType> Intimg, int currentLabel) {
		int n = Intimg.numDimensions();
		long[] position = new long[n];

		Cursor<IntType> intCursor = Views.iterable(Intimg).cursor();

		RandomAccessibleInterval<BitType> outimg = new ArrayImgFactory<BitType>().create(Intimg, new BitType());

		RandomAccess<BitType> imageRA = outimg.randomAccess();

		// Go through the whole image and add every pixel, that belongs to
		// the currently processed label
		long[] minVal = { Intimg.max(0), Intimg.max(1) };
		long[] maxVal = { Intimg.min(0), Intimg.min(1) };

		while (intCursor.hasNext()) {
			intCursor.fwd();
			imageRA.setPosition(intCursor);
			int i = intCursor.get().get();
			if (i == currentLabel) {
				intCursor.localize(position);
				for (int d = 0; d < n; ++d) {
					if (position[d] < minVal[d]) {
						minVal[d] = position[d];
					}
					if (position[d] > maxVal[d]) {
						maxVal[d] = position[d];
					}

				}

				imageRA.get().setOne();
			} else
				imageRA.get().setZero();

		}
		FinalInterval intervalsmall = new FinalInterval(minVal, maxVal);

		RandomAccessibleInterval<BitType> outimgsmall = extractImage(outimg, intervalsmall);
		double meanIntensity = computeAverage(Views.iterable(outimgsmall));
		double size = (intervalsmall.max(0) - intervalsmall.min(0)) * (intervalsmall.max(1) - intervalsmall.min(1));
		Watershedobject currentobject = new Watershedobject(outimgsmall, meanIntensity, size);

		return currentobject;

	}

	public static Watershedobject CurrentLabelBinaryImage(RandomAccessibleInterval<IntType> Intimg, int currentLabel) {
		int n = Intimg.numDimensions();
		long[] position = new long[n];

		Cursor<IntType> intCursor = Views.iterable(Intimg).cursor();

		RandomAccessibleInterval<BitType> outimg = new ArrayImgFactory<BitType>().create(Intimg, new BitType());
		RandomAccess<BitType> imageRA = outimg.randomAccess();
		RandomAccessibleInterval<BitType> currentimg = GenericFilters.GradientmagnitudeImage(Intimg);

		RandomAccess<BitType> inputRA = currentimg.randomAccess();

		// Go through the whole image and add every pixel, that belongs to
		// the currently processed label
		long[] minVal = { Intimg.max(0), Intimg.max(1) };
		long[] maxVal = { Intimg.min(0), Intimg.min(1) };

		while (intCursor.hasNext()) {
			intCursor.fwd();
			inputRA.setPosition(intCursor);
			imageRA.setPosition(inputRA);
			int i = intCursor.get().get();
			if (i == currentLabel) {
				intCursor.localize(position);
				for (int d = 0; d < n; ++d) {
					if (position[d] < minVal[d]) {
						minVal[d] = position[d];
					}
					if (position[d] > maxVal[d]) {
						maxVal[d] = position[d];
					}

				}

				imageRA.get().set(inputRA.get());
			} else
				imageRA.get().setZero();

		}
		FinalInterval intervalsmall = new FinalInterval(minVal, maxVal);

		RandomAccessibleInterval<BitType> outimgsmall = extractImage(outimg, intervalsmall);
		double meanIntensity = computeAverage(Views.iterable(outimgsmall));
		double size = (intervalsmall.max(0) - intervalsmall.min(0)) * (intervalsmall.max(1) - intervalsmall.min(1));
		Watershedobject currentobject = new Watershedobject(outimgsmall, meanIntensity, size);

		return currentobject;

	}

	/**
	 * Compute the average intensity for an {@link Iterable}.
	 *
	 * @param input
	 *            - the input data
	 * @return - the average as double
	 */
	public static <T extends RealType<T>> double computeAverage(final Iterable<T> input) {
		// Count all values using the RealSum class.
		// It prevents numerical instabilities when adding up millions of pixels
		final RealSum realSum = new RealSum();
		long count = 0;

		for (final T type : input) {
			realSum.add(type.getRealDouble());
			++count;
		}

		return realSum.getSum();
	}

	public static RandomAccessibleInterval<BitType> extractImage(final RandomAccessibleInterval<BitType> intervalView,
			final FinalInterval interval) {

		return intervalView;
	}

}
