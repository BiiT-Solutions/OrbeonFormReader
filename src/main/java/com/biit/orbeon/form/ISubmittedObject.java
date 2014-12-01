package com.biit.orbeon.form;

import java.util.List;

public interface ISubmittedObject {

	String getTag();

	void setTag(String tag);

	String getText();

	void setText(String text);

	ISubmittedObject getParent();

	void setParent(ISubmittedObject parent);

	void addChild(ISubmittedObject child);

	List<ISubmittedObject> getChildren();

	void setChildren(List<ISubmittedObject> children);

	ISubmittedObject getChildren(Class<?> type, String tag);

}
