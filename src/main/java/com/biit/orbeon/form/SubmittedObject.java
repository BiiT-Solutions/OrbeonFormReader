package com.biit.orbeon.form;

import java.util.ArrayList;
import java.util.List;

public class SubmittedObject implements ISubmittedObject {
	// Tags of the Orbeon form
	private String tag;
	// The real name of the element
	private String text;

	private ISubmittedObject parent;
	private List<ISubmittedObject> children;

	public SubmittedObject() {
		children = new ArrayList<>();
	}

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public void setTag(String tag) {
		this.tag = tag;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public void setText(String text) {
		this.text = text;
	}

	public ISubmittedObject getParent() {
		return parent;
	}

	@Override
	public void setParent(ISubmittedObject parent) {
		this.parent = parent;
	}

	@Override
	public void addChild(ISubmittedObject child) {
		children.add(child);
	}

	@Override
	public List<ISubmittedObject> getChildren() {
		return children;
	}

	@Override
	public void setChildren(List<ISubmittedObject> children) {
		this.children = children;
	}

	/**
	 * Makes a deep search of an element thas is from this type and has this tag.
	 */
	@Override
	public ISubmittedObject getChildren(Class<?> type, String tag) {
		// Check first level.
		for (ISubmittedObject child : getChildren()) {
			if (type.isInstance(child)) {
				if (child.getTag().equals(tag)) {
					return child;
				}
			}
			ISubmittedObject returnedChild = child.getChildren(type, tag);
			if (returnedChild != null) {
				return returnedChild;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		if (getChildren() != null && !getChildren().isEmpty()) {
			return getTag() + " " + getChildren();
		}
		return getTag();
	}
}
