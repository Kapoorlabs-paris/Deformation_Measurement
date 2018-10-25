package batchMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import ij.IJ;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.InteractiveSimpleEllipseFit;

public class ProcessFiles {

	

	public static void process(ExecuteBatch parent, File[] directoryCh1, File[] directoryCh2, File[] directoryChSeg, String Ch1, String Ch2, String ChSeg, boolean twochannel, ExecutorService taskexecutor) {
		 List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
		 
		 
		 
		for (int fileindex = 0; fileindex < directoryCh1.length; ++fileindex) {
			
			Pair<File, File> Chfiles = StringMatching(directoryCh1[fileindex], directoryChSeg ,directoryCh2, Ch1, Ch2, ChSeg );
			
			if(Chfiles!=null) 
			tasks.add(Executors.callable(new Split(parent, Chfiles.getA(), Chfiles.getB(), directoryChSeg[fileindex], fileindex, parent.twochannel)));
	
			
			
			
			
		}
		try {
			taskexecutor.invokeAll(tasks);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
		}
		
		
		
		
	
		

		}
	
	
	public static void process(ExecuteBatch parent, File[] directoryCh1, File[] directoryChSeg, String Ch1,  String ChSeg, boolean twochannel, ExecutorService taskexecutor) {
		 List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
		 
		 
		 
		for (int fileindex = 0; fileindex < directoryCh1.length; ++fileindex) {
			
			
		
			tasks.add(Executors.callable(new Split(parent, directoryCh1[fileindex],directoryChSeg[fileindex], fileindex, parent.twochannel)));
	
			
			
			
			
		}
		try {
			taskexecutor.invokeAll(tasks);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
		}
		
		
		
		
	
		

		}
	
	public static Pair<File, File> StringMatching(File imageA, File[] dir, File[] dirSeg, String Ch1, String Ch2, String ChSeg) {
		
		File CH2pair = null;
		File CHSegpair = null;
		System.out.println(Ch1 + " " + Ch2 + " " + ChSeg);
		for (int fileindex = 0; fileindex < dir.length; ++fileindex) {
			
			
			System.out.println(dir[fileindex].getName());
			
			String Name = dir[fileindex].getName().replaceAll(Ch2, Ch1);
			
			if(imageA.getName().matches("(.*)"+ Name + "(.*)")) {
				CH2pair = dir[fileindex];
			
						break;
			}
			
		}
		
		for (int fileindex = 0; fileindex < dirSeg.length; ++fileindex) {
			
			String Name = dirSeg[fileindex].getName().replaceAll(ChSeg, Ch1);
			
			
			if(imageA.getName().matches("(.*)"+ Name + "(.*)")) {
				CHSegpair = dirSeg[fileindex];
			
						break;
			}
			
		}
		
		return new ValuePair<File, File>(CH2pair,CHSegpair);
	}
	
	
}
