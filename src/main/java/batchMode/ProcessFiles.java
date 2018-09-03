package batchMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import ij.IJ;
import pluginTools.InteractiveSimpleEllipseFit;

public class ProcessFiles {

	

	public static void process(File[] directory, ExecutorService taskexecutor) {
		 List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
		 
		 
		 
		for (int fileindex = 0; fileindex < directory.length; ++fileindex) {
			
			
			ExecuteBatch parent = new ExecuteBatch(directory, new InteractiveSimpleEllipseFit(), directory[0]);
			 tasks.add(Executors.callable(new Split(directory[fileindex], parent, fileindex)));
			
			
			
			
		}
		try {
			taskexecutor.invokeAll(tasks);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
		}
		
		
		
		
	
		

		}
	
	
}
