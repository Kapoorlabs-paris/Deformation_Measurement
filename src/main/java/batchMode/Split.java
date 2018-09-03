package batchMode;

import java.io.File;

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
		
	}
}
