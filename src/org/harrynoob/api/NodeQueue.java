package org.harrynoob.api;

import java.util.LinkedList;

import org.powerbot.core.script.job.LoopTask;
import org.powerbot.core.script.job.state.Node;

public class NodeQueue {

	private LinkedList<Node> queue = new LinkedList<Node>();
	private int[] insertionPoints;
	private boolean running;
	
	public NodeQueue() {
		insertionPoints = new int[5];
	}
	
	public void add(Node n, Priority p) {
		queue.add(insertionPoints[p.getId()], n);
		insertionPoints[p.getId()]++;
	}
	
	public void handle() {
		if(queue.peek().activate()) {
			queue.poll().execute();
			queue.remove();
		}
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
