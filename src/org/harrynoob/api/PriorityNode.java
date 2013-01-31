package org.harrynoob.api;

import org.powerbot.core.script.job.state.Node;

public abstract class PriorityNode extends Node {

	protected Priority p;
	
	public PriorityNode(Priority p2) {
		this.p = p2;
	}
	
	public Priority getPriority() {
		return p;
	}
	
}
