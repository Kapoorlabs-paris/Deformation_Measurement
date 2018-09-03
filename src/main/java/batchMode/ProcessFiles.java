package batchMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import ij.IJ;
import pluginTools.InteractiveSimpleEllipseFit;

public class ProcessFiles {

	

	public static void process(File[] directoryCh1, File[] directoryCh2, String Ch1, String Ch2, boolean twochannel, ExecutorService taskexecutor) {
		 List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
		 
		 
		 
		for (int fileindex = 0; fileindex < directoryCh1.length; ++fileindex) {
			
			File Ch2file = StringMatching(directoryCh1[fileindex],directoryCh2, Ch1, Ch2 );
			ExecuteBatch parent = new ExecuteBatch(directoryCh1, directoryCh2, Ch1, Ch2, new InteractiveSimpleEllipseFit(), directoryCh1[0], twochannel);
			if(Ch2file!=null) 
			tasks.add(Executors.callable(new Split(parent, Ch2file, directoryCh1[fileindex], fileindex, parent.twochannel)));
			else
				tasks.add(Executors.callable(new Split(parent, directoryCh1[fileindex], directoryCh1[fileindex], fileindex, parent.twochannel)));
			
			
			
			
		}
		try {
			taskexecutor.invokeAll(tasks);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
		}
		
		
		
		
	
		

		}
	
	public static File StringMatching(File imageA, File[] dir, String Ch1, String Ch2) {
		
		File CH2pair = null;
		
		for (int fileindex = 0; fileindex < dir.length; ++fileindex) {
			
			String Name = dir[fileindex].getName().replaceAll(Ch2, Ch1);
			
			if(imageA.getName().matches("(.*)"+ Name + "(.*)")) {
				CH2pair = dir[fileindex];
			
						break;
			}
			
		}
		
		return CH2pair;
	}
	
	
}
