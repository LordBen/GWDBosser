package org.refined.api;

/**
 * @author LordBen
 */

public enum Potions {

	PRAYER_POTS(143, 23253, 141, 23251, 139, 23249, 2434, 23247, 23245, 23243),
	SARA_BREWS(6691, 22361, 6689, 22359, 6687, 22357, 6685, 22355, 22353, 22351),
	OVERLOADS(15335, 23536, 15334, 23535, 15333, 23534, 15332, 23533, 23532, 23531),
	SUPER_ATKS(149, 23265, 147, 23263, 145, 23261, 2436, 23259, 23257, 23255),
	EXTREME_ATKS(15311, 23500, 15310, 23499, 15309, 23498, 15308, 23497, 23496, 23495),
	SUPER_STRS(161, 23289, 159, 23287, 157, 23285, 2440, 23283, 23281, 23279),
	EXTREME_STRS(15315, 23506, 15314, 23505, 15313, 23504, 15312, 23503, 23502, 23501);


	private final int[] itemId;

	private Potions(int... item) {
		this.itemId = item;
	}

	/**
	 * Gets the ids of a certain type of potion
	 * 
	 * @return An array containing all potion ids
	 */
	public int[] getAltar() {
		return itemId;
	}
}

