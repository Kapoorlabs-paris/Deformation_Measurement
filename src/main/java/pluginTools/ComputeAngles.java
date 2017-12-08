package pluginTools;

import java.util.concurrent.ExecutionException;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import net.imglib2.img.display.imagej.ImageJFunctions;

public class ComputeAngles extends SwingWorker<Void, Void> {

	final InteractiveEllipseFit parent;
	final JProgressBar jpb;

	public ComputeAngles(final InteractiveEllipseFit parent, final JProgressBar jpb) {

		this.parent = parent;

		this.jpb = jpb;
	}

	@Override
	protected Void doInBackground() throws Exception {
		double percent = 0;
		for (int t = 1; t < parent.fourthDimensionSize; ++t) {

			percent++;

			for (int z = 1; z < parent.thirdDimensionSize; ++z) {

				percent++;

				utility.ProgressBar.SetProgressBar(jpb, 100 * percent/ (parent.thirdDimensionSize + parent.fourthDimensionSize), "Fitting ellipses and computing angles T = " + t + "/"
						+ parent.fourthDimensionSize + " Z = " + z + "/" + parent.fourthDimensionSize);
				EllipseTrack newtrack = new EllipseTrack(parent, jpb);
				newtrack.IntersectandTrack(t, z);

			}

		}

		return null;

	}

	@Override
	protected void done() {

		parent.jpb.setIndeterminate(false);
		parent.Cardframe.validate();
		try {
			get();
		} catch (InterruptedException e) {

		} catch (ExecutionException e) {

		}

	}

}
