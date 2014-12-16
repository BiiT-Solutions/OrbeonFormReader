package com.biit.orbeon.form;

import java.util.List;

public interface ISubmittedObject extends Comparable<ISubmittedObject> {

	String getTag();

	void setTag(String tag);

	String getText();

	void setText(String text);

	ISubmittedObject getParent();

	void setParent(ISubmittedObject parent);

	void addChild(ISubmittedObject child);

	List<ISubmittedObject> getChildren();

	void setChildren(List<ISubmittedObject> children);

	/**
	 * Makes a deep search of an element thas is from this type and has this tag.
	 */
	ISubmittedObject getChild(Class<?> type, String tag);

	/**
	 * Return all childrens that are of this class.
	 * 
	 * @param type
	 * @return
	 */
	List<ISubmittedObject> getChildren(Class<?> type);

	/**
	 * Creates a name with all the technical names
	 */
	String getPathName();

	Integer getIndex(ISubmittedObject child);

	int compareTo(ISubmittedObject arg0);

	int getLevel();

}
