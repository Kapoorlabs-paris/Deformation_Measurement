package batchMode;

import java.io.File;

import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;


public class Split implements Runnable {

	
	public final ExecuteBatch parent;
	public final File ChA;
	public final File ChB;
	public final File ChSeg;
	public boolean twochannel;
	public int fileindex;
	
	public Split(final ExecuteBatch parent, final File ChA,final File ChB, final File ChSeg, int fileindex, boolean twochannel  ) {
		
		this.parent = parent;
		this.ChA = ChA;
		this.ChB = ChB;
		this.ChSeg = ChSeg;
		this.twochannel = twochannel;
		this.fileindex = fileindex;
	}

	
	public Split(final ExecuteBatch parent, final File ChA, final File ChSeg, int fileindex, boolean twochannel  ) {
		
		this.parent = parent;
		this.ChA = ChA;
		this.ChB = null;
		this.ChSeg = ChSeg;
		this.twochannel = twochannel;
		this.fileindex = fileindex;
	}
	
	@Override
	public void run() {
		
		
		
		
		try {

			 System.setProperty("scijava.log.level", "None");
			 
			 System.out.println("Reached the loop");
			parent.parent.originalimg = new ImgOpener().openImgs(ChA.getAbsolutePath(), new FloatType()).get(0);
			if(parent.twochannel)
			parent.parent.originalSecimg =  new ImgOpener().openImgs(ChB.getAbsolutePath(), new FloatType()).get(0);
			
			
			parent.parent.originalimgsuper=  new ImgOpener().openImgs(ChSeg.getAbsolutePath(), new IntType()).get(0);
			
			
		} catch (ImgIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
