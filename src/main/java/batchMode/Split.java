package batchMode;

import java.io.File;

import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import net.imglib2.type.numeric.real.FloatType;


public class Split implements Runnable {

	
	public final ExecuteBatch parent;
	public final File ChA;
	public final File ChB;
	public boolean twochannel;
	public int fileindex;
	
	public Split(final ExecuteBatch parent, final File ChA,final File ChB, int fileindex, boolean twochannel  ) {
		
		this.parent = parent;
		this.ChA = ChA;
		this.ChB = ChB;
		this.twochannel = twochannel;
		this.fileindex = fileindex;
	}

	@Override
	public void run() {
		
		
		// TODO Auto-generated method stub
		
		
		try {
			parent.parent.originalimg = new ImgOpener().openImgs(ChA.getAbsolutePath(), new FloatType()).get(0);
			
			
		} catch (ImgIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
