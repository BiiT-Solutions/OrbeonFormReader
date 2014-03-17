package com.biit.orbeon.form.answers;

import com.biit.orbeon.form.IAnswer;
import com.biit.orbeon.form.IQuestion;

public abstract class Answer<T> implements IAnswer {
	private IQuestion question;
	private T value;
	private int score = Integer.MAX_VALUE;
	// Some rules can modify the score value of other answers.
	private int updateScore = 0;

	public Answer(IQuestion question, T value) {
		setQuestion(question);
		setValue(value);
	}

	public IQuestion getQuestion() {
		return question;
	}

	public void setQuestion(IQuestion question) {
		this.question = question;
	}

	@Override
	public String toString() {
		return value.toString();
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	@Override
	public int getScore() {
		return score + updateScore;
	}

	@Override
	public void setScore(int score) {
		this.score = score;
	}

	@Override
	public void setScore(Number score) {
		this.score = score.intValue();
	}

	public void setUpdateScore(int updateScore) {
		this.updateScore = updateScore;
	}
}
