package batchMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import ij.IJ;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.InteractiveSimpleEllipseFit;

public class ProcessFiles {

	public static void process(File[] directoryCh1, File[] directoryCh2, File[] directoryChSeg, String Ch1, String Ch2,
			String ChSeg, boolean twochannel, ExecutorService taskexecutor) throws InterruptedException {
		 List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
		for (int fileindex = 0; fileindex < directoryCh1.length; ++fileindex) {

			Pair<File, File> Chfiles = StringMatching(directoryCh1[fileindex], directoryChSeg, directoryCh2, Ch1, Ch2,
					ChSeg);

			ExecuteBatch parent = new ExecuteBatch(directoryCh1, directoryCh2, directoryChSeg, Ch1, Ch2, ChSeg,
					new InteractiveSimpleEllipseFit(), directoryCh1[0], twochannel);
		

			if (Chfiles != null)
           tasks.add(Executors.callable(new Split(parent, Chfiles.getA(), Chfiles.getB(), directoryChSeg[fileindex], fileindex, parent.twochannel)));
	
		
			
			
			
			
		}
		try {
			taskexecutor.invokeAll(tasks);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
		}

	}

	public static void processSingle(File[] directoryCh1, File[] directoryChSeg, String Ch1, String ChSeg,
			boolean twochannel, ExecutorService taskexecutor) throws InterruptedException {
		List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
		for (int fileindex = 0; fileindex < directoryCh1.length; ++fileindex) {

			File Segfile = SingleStringMatching(directoryCh1[fileindex], directoryChSeg, Ch1, ChSeg);

			ExecuteBatch parent = new ExecuteBatch(directoryCh1, directoryChSeg, Ch1, ChSeg,
					new InteractiveSimpleEllipseFit(), directoryCh1[0], twochannel);
			 tasks.add(Executors.callable(new Split(parent, directoryCh1[fileindex], directoryCh1[fileindex], Segfile, fileindex, parent.twochannel)));

		}
		try {
			taskexecutor.invokeAll(tasks);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
		}

	}

	public static Pair<File, File> StringMatching(File imageA, File[] dir, File[] dirSeg, String Ch1, String Ch2,
			String ChSeg) {

		File CH2pair = null;
		File CHSegpair = null;
		for (int fileindex = 0; fileindex < dir.length; ++fileindex) {

			String Name = dir[fileindex].getName().replaceAll(Ch2, Ch1);

			if (imageA.getName().matches("(.*)" + Name + "(.*)")) {
				CH2pair = dir[fileindex];

				break;
			}

		}

		for (int fileindex = 0; fileindex < dirSeg.length; ++fileindex) {

			String Name = dirSeg[fileindex].getName().replaceAll(ChSeg, Ch1);

			if (imageA.getName().matches("(.*)" + Name + "(.*)")) {
				CHSegpair = dirSeg[fileindex];

				break;
			}

		}

		return new ValuePair<File, File>(CH2pair, CHSegpair);
	}

	public static File SingleStringMatching(File imageA, File[] dirSeg, String Ch1, String ChSeg) {

		File CHSegpair = null;

		for (int fileindex = 0; fileindex < dirSeg.length; ++fileindex) {

			
			String Name = dirSeg[fileindex].getName().replaceAll(ChSeg, Ch1);
			

			if (imageA.getName().matches("(.*)" + Name + "(.*)")) {
				CHSegpair = dirSeg[fileindex];
				break;
			}

		}

		return CHSegpair;
	}

}
