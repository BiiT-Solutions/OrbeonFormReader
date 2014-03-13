package com.biit.orbeon.form;

public interface IQuestion {

	void setAnswer(IAnswer createAnswer);

	IAnswer getAnswer();

	String getTag();

	void setTag(String tag);

}
