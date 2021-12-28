package gf.photoviewer.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;

import gf.photoviewer.io.LabeledIndentityIO;
import gf.photoviewer.resources.LabeledIdentity;

@SuppressWarnings("serial")
public class LabeledIdentityModel<E extends LabeledIdentity,
				T extends LabeledIndentityIO<E>> extends AbstractListModel<E> {
	private T ioObj;
	private List<E> elements;
	
	@SuppressWarnings("unchecked")
	public LabeledIdentityModel(Class<T> ioObjClass) throws Exception {
		elements = new ArrayList<>();
		ioObj = (T) ioObjClass.getMethod("getInstance").invoke(null);
		updateElements();
	}

	@Override
	public int getSize() {
		return elements.size();
	}

	@Override
	public E getElementAt(int index) {
		return elements.get(index);
	}
	
	public List<E> getElements() {
		return elements;
	}

	protected void updateElements() throws Exception {
		elements = ioObj.getAllRecords();
		fireContentsChanged(this, 0, elements.size());
	}

	public E add(E element) throws Exception {
		element = ioObj.add(element);
		elements.add(element);
		fireIntervalAdded(this, elements.size() - 1, elements.size() - 1);
		return element;
	}
	
	public void remove(E element) throws Exception {
		ioObj.remove(element);
		
		for (int i = 0; i < getSize(); i++) {
			if (getElementAt(i).equals(element)) {
				elements.remove(i);
				fireIntervalRemoved(this, i, i);
				break;
			}
		}
	}
	
	public void rename(E element, String newName) throws Exception {
		ioObj.rename(element, newName);
		
		for (int i = 0; i < getSize(); i++) {
			if (getElementAt(i).equals(element)) {
				getElementAt(i).setLabel(newName);
				fireContentsChanged(this, i, i);
				break;
			}
		}
		
	}
	
	protected T getIOObj() {
		return ioObj;
	}
	
	@Override
	public String toString() {
		return elements.toString();
	}
}
