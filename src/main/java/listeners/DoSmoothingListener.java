package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.imglib2.algorithm.gauss3.Gauss3;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.view.Views;
import pluginTools.InteractiveSimpleEllipseFit;
import pluginTools.InteractiveSimpleEllipseFit.ValueChange;

public class DoSmoothingListener implements ActionListener {
	
	
	public final InteractiveSimpleEllipseFit parent;
	
	public DoSmoothingListener(final InteractiveSimpleEllipseFit parent) {
		
		this.parent = parent;
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		
	try {
			
			Gauss3.gauss(parent.gaussradius, Views.extendBorder(parent.originalimg), parent.originalimgsmooth);
			parent.updatePreview(ValueChange.SEG);

		} catch (IncompatibleTypeException es) {

			es.printStackTrace();
		}
		
		
	}

}
