package com.biit.orbeon.form.answers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.biit.orbeon.form.IAnswer;
import com.biit.orbeon.form.IQuestion;

public class AnswerFactory {

	public static IAnswer createAnswer(IQuestion question, String value) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date date = formatter.parse(value);
			return new DateAnswer(question, date);
		} catch (ParseException e) {
			// It is not a date.
		}

		try {
			Integer number = Integer.parseInt(value);
			return new NumericAnswer(question, number);
		} catch (NumberFormatException nfe) {
			// It is not a number.
		}

		return new StringAnswer(question, value);
	}
}
