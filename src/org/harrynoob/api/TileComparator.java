package org.harrynoob.api;

import java.util.ArrayList;

import org.powerbot.game.api.wrappers.Tile;

public class TileComparator {

	private ArrayList<Tile> tileList = new ArrayList<Tile>();
	private int xTotal = 0;
	private int yTotal = 0;
	private int listSize = 0;
	
	public void clear()
	{
		tileList.clear();
	}
	
	public void add(Tile tile)
	{
		tileList.add(tile);
	}
	
	public Tile getComparison()
	{
		for(Object a : tileList.toArray())
		{
			if(a instanceof Tile)
			{
				xTotal += ((Tile) a).getX();
				yTotal += ((Tile) a).getY();
				listSize++;
			}
		}
		return new Tile(xTotal/listSize, yTotal/listSize, 0);
	}
}
