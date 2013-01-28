package org.harrynoob.api;

import java.awt.Point;
import java.awt.Polygon;

import org.powerbot.core.script.job.Task;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.interactive.NPCs;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.Entity;
import org.powerbot.game.api.wrappers.Locatable;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.interactive.NPC;
import org.powerbot.game.api.wrappers.widget.WidgetChild;

// TODO: Auto-generated Javadoc
/**
 * The Class Utilities.
 */
public class Utilities {

	/**
	 * Wait for condition.
	 *
	 * @param c the c
	 * @param timeout the timeout
	 * @return true, if successful
	 */
	public static boolean waitFor(final Condition c, final long timeout) {
		final Timer t = new Timer(timeout);
		while (t.isRunning() && !c.validate()) {
			Task.sleep(50);
		}
		return c.validate();
	}

	/**
	 * Checks if entity is on screen.
	 *
	 * @param e The entity
	 * @return true, if e is on screen
	 */
	public static boolean isOnScreen(Entity e) {
		ensureActionBar(true);
		WidgetChild actionbar = Widgets.get(640, 6);
		return e.isOnScreen()
				&& (actionbar == null || !(actionbar != null
						&& actionbar.isOnScreen() && actionbar
						.getBoundingRectangle().contains(e.getCentralPoint())));
	}
	
	/**
	 * Enhanced check if entity is on screen.
	 *
	 * @param e The entity
	 * @return true, if e is on screen
	 */
	public static boolean isOnScreenEnhanced(org.powerbot.game.api.wrappers.interactive.Character e) {
		WidgetChild ab = Widgets.get(640, 6);
		if(ab == null || !ab.isOnScreen() || e.getModel().getBounds().length == 0) return e.isOnScreen();
		for(Polygon p : e.getModel().getBounds()) {
			for(int i = 0; i < p.npoints; i++) {
				Point a = new Point(p.xpoints[i], p.ypoints[i]);
				if(ab.contains(a) || !e.isOnScreen()) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Ensure inventory tab is open.
	 */
	public static void ensureInventoryTab() {
		if (!Tabs.INVENTORY.isOpen())
			Tabs.INVENTORY.open();
	}

	/**
	 * Ensure action bar state equals flag.
	 *
	 * @param flag the flag
	 */
	public static void ensureActionBar(boolean flag) {
		/*if (flag) {
			WidgetChild actionbar = Widgets.get(640, 6);
			if (actionbar != null && !actionbar.isOnScreen()) {
				WidgetChild abToggle = Widgets.get(640, 28);
				if (abToggle != null) {
					abToggle.interact("Expand");
				}
			}
		} else {
			WidgetChild actionbar = Widgets.get(640, 6);
			if (actionbar != null && actionbar.isOnScreen()) {
				WidgetChild abToggle = Widgets.get(640, 30);
				if (abToggle != null) {
					abToggle.interact("Minimise");
				}
			}
		} */
		WidgetChild actionbar = Widgets.get(640, 6);
		if(flag == actionbar.isOnScreen()) return;
		if(flag ? !actionbar.isOnScreen() : actionbar.isOnScreen()) {
			WidgetChild toggle = Widgets.get(640, flag ? 28 : 30);
			if(toggle != null && toggle.isOnScreen()) {
				toggle.interact(flag ? "Expand" : "Minimise");
			}
		}
	}

	/**
	 * Gets the mid tile.
	 *
	 * @param id the id
	 * @return the mid tile
	 */
	public static Tile getMidTile(int... id) {
		TileComparator t = new TileComparator();
		for (NPC n : NPCs.getLoaded(id)) {
			if (n != null && n.validate())
				t.add(n.getLocation());
		}
		return t.getComparison();
	}

	/**
	 * Camera rotation method.
	 *
	 * @param loc A locatable entity
	 */
	public static void cameraTurnTo(final Locatable loc) {
		Thread t = new Thread() {
			public void run() {
				Camera.turnTo(loc);
				if(!isOnScreen((Entity) loc))
					Camera.setPitch(false);
			}
		};
		t.start();
	}

}
