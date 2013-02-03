package org.refined.gwdbosser.misc;

public enum Chambers {
	
	BANDOS(6260, new int[] {6261, 6263, 6265}, -1),
	ARMADYL(6222, new int[] {6223, 6225, 6227}, -1),
	SARADOMIN(6247, new int[] {6250, 6252, 6248}, -1),
	ZAMORAK(6203, new int[] {6204, 6206, 6208}, -1);
	
	private final int bossId;
	private final int[] bossGuardians;
	private final int[] bossFollowers;

	private Chambers(int bossId, int[] guardianIds, int... followerIds) {
		this.bossId = bossId;
		this.bossGuardians = guardianIds;
		this.bossFollowers = followerIds;
	}
	
	/**
	 * 
	 * @return Boss's follower's ids (npcs you kill to get kc)
	 */
	
	public int[] getBossFollowers() {
		return bossFollowers;
	}
	
	/**
	 * 
	 * @return Boss's guardian's ids (3 npcs in room with boss)
	 */
	
	public int[] getBossGuardians() {
		return bossGuardians;
	}

	/**
	 * 
	 * @return Boss id
	 */
	
	public int getBossId() {
		return bossId;
	}
	
}

