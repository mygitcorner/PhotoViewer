package gf.photoviewer.gui;

import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import gf.photoviewer.PhotoViewerUtils;

public class ListPopupMenu extends JPopupMenu {
	
	public ListPopupMenu(JList<?> list) {
		addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				int index = PhotoViewerUtils.getIndexUnderMouse(list);
				if (index != -1 && !list.getSelectionModel().isSelectedIndex(index))
					list.setSelectedIndex(index);
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}

			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {}
		});
	}
}
