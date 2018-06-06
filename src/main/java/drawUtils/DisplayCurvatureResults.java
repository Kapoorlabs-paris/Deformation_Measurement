package drawUtils;

import java.awt.Color;
import java.util.ArrayList;

import ij.ImagePlus;
import ij.gui.Line;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.gui.TextRoi;
import ij.process.LUT;
import net.imagej.display.ColorTables;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.stats.Normalize;
import net.imglib2.display.ColorTable8;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.view.Views;
import pluginTools.InteractiveSimpleEllipseFit;
import script.imglib.color.RGBA;

public class DisplayCurvatureResults {

	static LUT lut = LUT.createLutFromColor(Color.BLUE);

	public static RandomAccessibleInterval<FloatType> Display(InteractiveSimpleEllipseFit parent,
			RandomAccessibleInterval<FloatType> originalimg,
			ArrayList<Pair<String, Pair<Integer, ArrayList<double[]>>>> currentresultCurv) {

		RandomAccessibleInterval<FloatType> probImg = new ArrayImgFactory<FloatType>().create(originalimg,
				new FloatType());

		for (int index = 0; index < currentresultCurv.size(); ++index) {

			Pair<String, Pair<Integer, ArrayList<double[]>>> currentpair = currentresultCurv.get(index);

			int time = currentpair.getB().getA();

			double[] X = new double[currentpair.getB().getB().size()];
			double[] Y = new double[currentpair.getB().getB().size()];
			double[] I = new double[currentpair.getB().getB().size()];

			for (int i = 0; i < currentpair.getB().getB().size(); ++i) {

				X[i] = currentpair.getB().getB().get(i)[0];
				Y[i] = currentpair.getB().getB().get(i)[1];
				I[i] = currentpair.getB().getB().get(i)[2];

			}

			RandomAccessibleInterval<FloatType> CurrentViewprobImg = utility.Slicer.getCurrentView(probImg, time,
					parent.thirdDimensionSize, 1, parent.fourthDimensionSize);

			final Cursor<FloatType> cursor = Views.iterable(CurrentViewprobImg).localizingCursor();

			while (cursor.hasNext()) {

				cursor.fwd();

				for (int i = 0; i < X.length; ++i) {

					if ((Math.abs(cursor.getFloatPosition(0) - X[i])) < 2
							&& (Math.abs(cursor.getFloatPosition(1) - Y[i])) < 2) {

						cursor.get().setReal(I[i]);

					}

				}

			}

		}

		return probImg;

	}

	public static void makeColorBar(final int lutMaxIndex, final ImagePlus originalImagePlus) {

		final int barHeight = 24, barPad = 5;
		Overlay overlay = new Overlay();

		final int barY = originalImagePlus.getHeight() - barHeight - barPad;

		final TextRoi labelGood = new TextRoi(barPad, barY, "Low Curvature");
		labelGood.setStrokeColor(Color.white);
		overlay.add(labelGood);

		final int barOffset = 2 * barPad + (int) labelGood.getBounds().getWidth();

		final TextRoi labelBad = new TextRoi(barOffset + lutMaxIndex + barPad, barY, "High Curvature");
		labelBad.setStrokeColor(Color.white);
		overlay.add(labelBad);

		for (int i = 0; i < lutMaxIndex; ++i) {

			final int barX = barOffset + i;
			final Roi line = new Line(barX, barY, barX, barY + barHeight);
			final int r = lut.getRed(i);
			final int g = lut.getGreen(i);
			final int b = lut.getBlue(i);
			line.setStrokeColor(new Color(r, g, b));
			overlay.add(line);

		}

		originalImagePlus.setOverlay(overlay);

	}

}
