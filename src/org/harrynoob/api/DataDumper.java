package org.harrynoob.api;

import java.io.File;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Iterator;

import org.powerbot.core.script.job.Container;
import org.powerbot.core.script.job.LoopTask;
import org.powerbot.game.api.methods.Environment;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.wrappers.node.SceneObject;
import org.powerbot.game.api.wrappers.node.SceneObjectDefinition;

public class DataDumper {
	
	private final HashSet<String> nameSet = new HashSet<>();
	
	public DataDumper(final Container c) {
		c.submit(new DataGatherer(this));
	}
	
	public boolean save() {
		try {
			File f = new File(Environment.getStorageDirectory(), "DataDump.txt");
			FileWriter fw = new FileWriter(f);
			Iterator<String> it = nameSet.iterator();
			while(it.hasNext()) {
				fw.write(it.next());
			}
			fw.flush();
			fw.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public void addData(final int i, final String s) {
		nameSet.add(s);
	}
	
	private class DataGatherer extends LoopTask {

		private final DataDumper instance;
		private SceneObjectDefinition sod;
		
		public DataGatherer(final DataDumper dd) {
			this.instance = dd;
		}
		
		@Override
		public int loop() {
			for(final SceneObject so : SceneEntities.getLoaded()) {
				if((sod = so.getDefinition()) != null) {
					instance.addData(sod.getId(), sod.getName());
				}
			}
			return 250;
		}
	}
}

