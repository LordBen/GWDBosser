package org.harrynoob.api;

public enum Priority {

	VERY_HIGH(0),
	HIGH(1),
	NORMAL(2),
	LOW(3),
	VERY_LOW(4);
	
	final int id;
	
	Priority(int num) {
		this.id = num;
	}
	
	public int getId() {
		return id;
	}
}
