package com.biit.orbeon.form;

import java.util.ArrayList;
import java.util.List;

public class SubmittedObject implements ISubmittedObject, Comparable<ISubmittedObject> {
	protected static final String DEFAULT_PATH_SEPARATOR = "/";
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
		child.setParent(this);
	}

	@Override
	public List<ISubmittedObject> getChildren() {
		return children;
	}

	@Override
	public void setChildren(List<ISubmittedObject> children) {
		this.children = children;
	}

	@Override
	public ISubmittedObject getChild(Class<?> type, String tag) {
		// Check first level.
		for (ISubmittedObject child : getChildren()) {
			if (type.isInstance(child)) {
				if (child.getTag().equals(tag)) {
					return child;
				}
			}
			ISubmittedObject returnedChild = child.getChild(type, tag);
			if (returnedChild != null) {
				return returnedChild;
			}
		}
		return null;
	}

	@Override
	public List<ISubmittedObject> getChildren(Class<?> type) {
		List<ISubmittedObject> children = new ArrayList<>();
		for (ISubmittedObject child : getChildren()) {
			if (type.isInstance(child)) {
				children.add(child);
			}
			children.addAll(child.getChildren(type));
		}
		return children;
	}

	@Override
	public String toString() {
		if (getChildren() != null && !getChildren().isEmpty()) {
			return getPathName() + " " + getChildren();
		}
		return getPathName();
	}

	/**
	 * Creates a name with all the technical names
	 */
	@Override
	public String getPathName() {
		// Ignores the form.
		if (parent == null) {
			return getTag();
		} else {
			String parentPath = parent.getPathName();
			if (parentPath == null) {
				return getTag();
			} else {
				return parentPath + DEFAULT_PATH_SEPARATOR + getTag();
			}
		}
	}

	/**
	 * Get index of child
	 * 
	 * @param child
	 * @return
	 */
	@Override
	public Integer getIndex(ISubmittedObject child) {
		return children.indexOf(child);
	}

	@Override
	public int getLevel() {
		if (parent == null) {
			return 0;
		} else {
			return parent.getLevel() + 1;
		}
	}

	@Override
	public int compareTo(ISubmittedObject arg0) {
		// This compare will only work for members of the same tree structure
		// Both of them are root
		if (getParent() == null && arg0.getParent() == null) {
			return 0;
		}
		// If one of the elements is root
		if (getParent() == null && arg0.getParent() != null) {
			return -1;
		}
		if (getParent() != null && arg0.getParent() == null) {
			return 1;
		}
		// None of them are root.
		int levelThis = getLevel();
		int levelArg0 = arg0.getLevel();

		if (levelThis == levelArg0) {
			// Both are in the same level
			if (getParent() != null) {
				if (getParent().equals(arg0.getParent())) {
					// Same parent
					return getParent().getIndex(this).compareTo(getParent().getIndex(arg0));
				} else {
					return getParent().compareTo(arg0.getParent());
				}
			} else {
				// Two root elements
				return 0;
			}
		} else {
			// Check if they are father/son
			if (getParent().equals(arg0)) {
				return 1;
			}
			if (arg0.getParent().equals(this)) {
				return -1;
			}
			// If not, then return comparison with deepest element parent.
			if (levelThis > levelArg0) {
				return getParent().compareTo(arg0);
			} else {
				return compareTo(arg0.getParent());
			}
		}
	}

}
