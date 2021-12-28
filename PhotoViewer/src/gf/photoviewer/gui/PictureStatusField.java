package gf.photoviewer.gui;

import java.awt.Color;
import java.util.stream.Collectors;

import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import gf.photoviewer.model.AlbumModel;
import gf.photoviewer.model.CurrentResource;
import gf.photoviewer.model.PictureModel;
import gf.photoviewer.model.TagModel;
import gf.photoviewer.resources.Album;
import gf.photoviewer.resources.Tag;

public class PictureStatusField extends JTextField {
	private PictureModel pictureModel;
	
	public PictureStatusField(PictureModel pictureModel) {
		setEditable(false);
		setBackground(Color.WHITE);
		
		pictureModel.addListDataListener(new ListDataListener() {
			@Override
			public void intervalAdded(ListDataEvent e) { setStatus(); }

			@Override
			public void intervalRemoved(ListDataEvent e) { setStatus(); }

			@Override
			public void contentsChanged(ListDataEvent e) { setStatus(); }
			
			public void setStatus() { 
				CurrentResource currentResource = pictureModel.getCurrentResource();
				
				if (currentResource.currentIsAlbum()) {
					setText("Album: " + currentResource.getCurrentAlbum().getLabel());
				} else if (currentResource.currentIsTags()) {
					String text = "Tags: ";
					text += pictureModel
							.getCurrentResource()
							.getCurrentTags()
							.stream()
							.map(Tag::getLabel)
							.collect(Collectors.joining(", "));
					setText(text);
				} else {
					setText("");
				}
			}
		});
	}
}
