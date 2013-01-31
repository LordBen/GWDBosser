package org.harrynoob.api;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.core.script.job.LoopTask;
import org.powerbot.core.script.job.state.Node;

public class NodeQueue {

	private List<Node> queue = new ArrayList<Node>();
	private int[] insertionPoints;
	private boolean running;
	
	public NodeQueue() {
		insertionPoints = new int[5];
	}
	
	public void add(Node n, Priority p) {
		insertionPoints[p.getId()]++;
	}
	
	public void stop() {
		running = true;
	}
	
	public boolean isHandling() {
		return running;
	}
	
}

class EventInvoker extends LoopTask {

	private final NodeQueue queue;
	
	public EventInvoker(NodeQueue nq) {
		this.queue = nq;
	}
	
	@Override
	public int loop() {
		if(!queue.isHandling()) {
			getContainer().shutdown();
		}
		return 100;
	}
	
}
