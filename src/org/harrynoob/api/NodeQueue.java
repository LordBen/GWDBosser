package org.harrynoob.api;

import java.util.LinkedList;

import org.powerbot.core.script.job.LoopTask;

public class NodeQueue {

	private LinkedList<PriorityNode> queue = new LinkedList<PriorityNode>();
	private int[] insertionPoints;
	private boolean running;
	
	public NodeQueue() {
		insertionPoints = new int[5];
	}
	
	public void add(PriorityNode n, Priority p) {
		queue.add(getInsertionPoint(p.getId()), n);
		insertionPoints[p.getId()]++;
	}
	
	public void handle() {
		if(!queue.isEmpty()) {
			if(queue.peek().activate()) {
				insertionPoints[queue.peek().getPriority().getId()]--;
				queue.remove().execute();
			}
		}
	}
	
	private int getInsertionPoint(int id) {
		int temp = 0;
		for(int i = 0; i < id; i++) {
			temp += insertionPoints[i];
		}
		return temp;
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
		} else {
			queue.handle();
		}
		return 100;
	}
	
}
