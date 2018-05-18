package pluginTools;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

public class ComputeManual extends SwingWorker<Void, Void> {
	
	final InteractiveSimpleEllipseFit parent;
	final JProgressBar jpb;
	
	public ComputeManual(final InteractiveSimpleEllipseFit parent, final JProgressBar jpb) {
		
		this.parent = parent;
		
		this.jpb = jpb;
		
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		
		
		EllipseTrack newtrack = new EllipseTrack(parent, jpb);
		newtrack.ManualIntervention();
	
		return null;
		
	}
	

}
