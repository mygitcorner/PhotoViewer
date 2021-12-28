package gf.photoviewer;

import java.awt.MouseInfo;
import java.awt.Point;

import javax.swing.JList;

public class PhotoViewerUtils {
	public static int getIndexUnderMouse(JList<?> list) {
		Point mousePos = MouseInfo.getPointerInfo().getLocation();
		Point loc = list.getLocationOnScreen();
		mousePos.translate((int) -loc.getX(), (int) -loc.getY());

		int index = list.locationToIndex(mousePos);

		if (index != -1 && list.getCellBounds(index, index).contains(mousePos))
			return index;
		else
			return -1;
	}
}
