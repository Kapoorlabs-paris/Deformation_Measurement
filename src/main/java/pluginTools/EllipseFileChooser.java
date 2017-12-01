package pluginTools;

import java.io.File;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

public class EllipseFileChooser extends JPanel {

	
	
	 /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
	  JPanel panelCont = new JPanel();
	  JPanel panelIntro = new JPanel();
	  JFileChooser chooserA;
	  String choosertitleA;
	  File[] AllMovies;
	  public NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
	  
	public EllipseFileChooser() {
		
		new InteractiveEllipseFit().run(null);
	}
	
}
