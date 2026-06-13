package com.pg85.otg.util.minecraft;

/**
 * Represents all tree types in Minecraft.
 *
 */
public enum TreeType
{
	Acacia,
	BigTree,
	Birch,
	CocoaTree,
	DarkOak,
	/**
	 * Soft-deprecated, renamed to Birch
	 */
	Forest("Birch"),
	CrimsonFungi,
	WarpedFungi,
	ChorusPlant,
	GroundBush,
	HugeMushroom,
	HugeRedMushroom,
	HugeBrownMushroom,
	JungleTree,
	SwampTree(true),
	Taiga1,
	Taiga2,
	HugeTaiga1,
	HugeTaiga2,
	TallBirch,
	Tree,
	Mangrove(true),
	TallMangrove(true),
	Cherry;

	private final String name;
	private final boolean spawnsInWater;

	/**
	 * Creates a new tree type.
	 */
	private TreeType()
	{
		this.name = name();
		this.spawnsInWater = false;
	}

	/**
	 * Creates a new tree type that is allowed to spawn in/over water.
	 *
	 * @param spawnsInWater Whether this tree may be placed in liquid.
	 */
	private TreeType(boolean spawnsInWater)
	{
		this.name = name();
		this.spawnsInWater = spawnsInWater;
	}

	/**
	 * Creates a new tree type. When this type is written to the configs, the
	 * provided name will be used instead. This allows for renaming tree types
	 * while still being able to read old ones.
	 *
	 * @param name The name used for writing.
	 */
	private TreeType(String name)
	{
		this.name = name;
		this.spawnsInWater = false;
	}

	/**
	 * Whether this tree type is designed to spawn in or over water (e.g.
	 * mangroves, swamp oaks). Other trees should be rejected when their
	 * placement position falls on liquid.
	 */
	public boolean spawnsInWater()
	{
		return this.spawnsInWater;
	}

	public String toString()
	{
		// Overridden so that the correct name is used when writing
		// to the config files
		return name;
	}
}