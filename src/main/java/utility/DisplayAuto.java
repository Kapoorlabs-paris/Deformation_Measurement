package utility;

import java.util.Map;

import ij.gui.EllipseRoi;
import ij.gui.Line;
import ij.gui.OvalRoi;
import pluginTools.InteractiveSimpleEllipseFit;

public class DisplayAuto {

	
	
	
	public static void Display(final InteractiveSimpleEllipseFit parent) {

		parent.overlay.clear();

		if (parent.ZTRois.size() > 0) {

			for (Map.Entry<String, Roiobject> entry : parent.ZTRois.entrySet()) {

				Roiobject currentobject = entry.getValue();
				if (currentobject.fourthDimension == parent.fourthDimension
						&& currentobject.thirdDimension == parent.thirdDimension) {

					if (currentobject.resultroi != null) {
						for (int i = 0; i < currentobject.resultroi.size(); ++i) {

							EllipseRoi ellipse = currentobject.resultroi.get(i);
							ellipse.setStrokeColor(parent.colorInChange);
							parent.overlay.add(ellipse);

						}

					}

					if (currentobject.resultovalroi != null) {
						for (int i = 0; i < currentobject.resultovalroi.size(); ++i) {

							OvalRoi ellipse = currentobject.resultovalroi.get(i);
							ellipse.setStrokeColor(parent.colorDet);
							parent.overlay.add(ellipse);

						}

					}

					if (currentobject.resultlineroi != null) {
						for (int i = 0; i < currentobject.resultlineroi.size(); ++i) {

							Line ellipse = currentobject.resultlineroi.get(i);
							ellipse.setStrokeColor(parent.colorLineA);

							parent.overlay.add(ellipse);

						}

					}

					break;
				}

			}
			parent.impOrig.setOverlay(parent.overlay);
			parent.impOrig.updateAndDraw();


		}
	}
	
	
}
