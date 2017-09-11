package de.bg.qanda;

import android.database.Cursor;

public class QAndAAide {
	private String id;
	private int orderNo;
	private String question;
	private String answer;
	private int category;

	public QAndAAide(String question, String answer) {
		this.question = question;
		this.answer = answer;
	}

	public QAndAAide(String qaa) {
		String[] sa = new String[10];
		sa = qaa.split(qaa);
		this.id = sa[0];
		this.orderNo = new Integer(sa[1]).intValue();
		this.question = sa[2];
		this.answer = sa[3];
	}

	public QAndAAide(Cursor c) {
		id = c.getString(0);
		orderNo = c.getInt(1);
		question = c.getString(2);
		answer = c.getString(3);
		category = c.getInt(4);
	}

	public String getId() {
		return id;
	}

	public String getQuestion() {
		return question;
	}

	public String getAnswer() {
		return answer;
	}

	public int getCategory() {
		return category;
	}

	public void setId(String id) {
		this.id = id;

	}

	public String toString() {
		return id + "|" + orderNo + "|" + question + "|" + answer + "\n";
	}

	public int getOrder() {
		return orderNo;
	}

	public void setOrder(int orderNo) {
		this.orderNo = orderNo;
	}

	public void setCategory(int category) {
		this.category = category;
	}
}