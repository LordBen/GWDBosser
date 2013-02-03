package org.refined.gwdbosser;

import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.interactive.Players;

import org.sk.action.ActionBar;

import org.refined.gwdbosser.misc.Variables;

public class AbilityHandler extends Node {
	
	/**
	 * @author LordBen
	 * Credit to Strikeskids for ability bar API
	 */

	@Override
	public boolean activate() {
		// TODO Auto-generated method stub
		return Players.getLocal().isInCombat();
	}

	@Override
	public void execute() {
		for (int i = 0; i < 10; i++) {//Abilties
			if (ActionBar.isReady(i) && ActionBar.getAbilityInSlot(i).available()) {
				ActionBar.useSlot(i);
				Task.sleep(100);
				break;
			}
		}	
	}
	
	/**
	 * 
	 * @return True if successful in setting up ability bar. False if user has own ability bar or setup failed
	 * 
	 */
	
	public boolean setup() {
		if (Variables.customAbilityBar) {
			
		}
		return false;
	}

}
