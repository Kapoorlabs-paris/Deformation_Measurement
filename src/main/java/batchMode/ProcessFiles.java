package batchMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

	

	public static void process(File[] directoryCh1, File[] directoryCh2, File[] directoryChSeg, String Ch1, String Ch2, String ChSeg, boolean twochannel, ExecutorService taskexecutor) {
		 
			
		for (int fileindex = 0; fileindex < directoryCh1.length; ++fileindex) {
			
			Pair<File, File> Chfiles = StringMatching(directoryCh1[fileindex], directoryChSeg ,directoryCh2, Ch1, Ch2, ChSeg );
			
		
			ExecuteBatch parent = new ExecuteBatch(directoryCh1, directoryCh2,directoryChSeg, Ch1, Ch2, ChSeg, new InteractiveSimpleEllipseFit(), directoryCh1[0], twochannel);
		
			if(Chfiles!=null) 
			taskexecutor.execute(new Split(parent, Chfiles.getA(), Chfiles.getB(), directoryChSeg[fileindex], fileindex, parent.twochannel));
			
			
			
		}
		
		
		
		
		
	
		

		}
	
	
	public static void processSingle(File[] directoryCh1, File[] directoryChSeg, String Ch1,  String ChSeg, boolean twochannel, ExecutorService taskexecutor) {
		 
		 
	
		for (int fileindex = 0; fileindex < directoryCh1.length; ++fileindex) {
			
			File Segfile = SingleStringMatching(directoryCh1[fileindex], directoryChSeg , Ch1, ChSeg );
		
		  ExecuteBatch parent = new ExecuteBatch(directoryCh1, directoryChSeg, Ch1, ChSeg, new InteractiveSimpleEllipseFit(), directoryCh1[0], twochannel);
	               taskexecutor.execute(new Split(parent, directoryCh1[fileindex],Segfile, fileindex, parent.twochannel));
			
			
			
			
	}

		
		
		
	
		

		}
	
	public static Pair<File, File> StringMatching(File imageA, File[] dir, File[] dirSeg, String Ch1, String Ch2, String ChSeg) {
		
		File CH2pair = null;
		File CHSegpair = null;
		for (int fileindex = 0; fileindex < dir.length; ++fileindex) {
			
			
			
			String Name = dir[fileindex].getName().replaceAll(Ch2, Ch1);
			
			if(imageA.getName().matches("(.*)"+ Name + "(.*)")) {
				CH2pair = dir[fileindex];
			
						break;
			}
			
		}
		
		for (int fileindex = 0; fileindex < dirSeg.length; ++fileindex) {
			
			String Name = dirSeg[fileindex].getName();
			
			
			if(imageA.getName().matches("(.*)"+ Name + "(.*)")) {
				CHSegpair = dirSeg[fileindex];
			
						break;
			}
			
		}
		
		return new ValuePair<File, File>(CH2pair,CHSegpair);
	}
	public static File SingleStringMatching(File imageA,  File[] dirSeg, String Ch1,  String ChSeg) {
		
		
		File CHSegpair = null;
	
	
		
		for (int fileindex = 0; fileindex < dirSeg.length; ++fileindex) {
			
		
			String Name = dirSeg[fileindex].getName();
			System.out.println(Name + " " + imageA.getName());
			if(imageA.getName().matches("(.*)"+ Name + "(.*)")) {
				CHSegpair = dirSeg[fileindex];
			System.out.println(CHSegpair.getName());
						break;
			}
			
		}
		
		return CHSegpair;
	}
	
}
