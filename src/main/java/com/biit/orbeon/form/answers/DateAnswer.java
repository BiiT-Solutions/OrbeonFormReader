package com.biit.orbeon.form.answers;

import java.util.Calendar;
import java.util.Date;

import com.biit.orbeon.form.IQuestion;

public class DateAnswer extends Answer<Date> {

	public DateAnswer(IQuestion question, Date value) {
		super(question, value);
	}

	public int getPassedYears() {
		return getDiffYears(getValue(), new Date());
	}

	private static int getDiffYears(Date first, Date last) {
		Calendar firstCalendar = getCalendar(first);
		Calendar lastCalendar = getCalendar(last);
		int diff = lastCalendar.get(Calendar.YEAR) - firstCalendar.get(Calendar.YEAR);
		if (firstCalendar.get(Calendar.MONTH) > lastCalendar.get(Calendar.MONTH)
				|| (firstCalendar.get(Calendar.MONTH) == lastCalendar.get(Calendar.MONTH) && firstCalendar
						.get(Calendar.DATE) > lastCalendar.get(Calendar.DATE))) {
			diff--;
		}
		return diff;
	}

	public static Calendar getCalendar(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

}
