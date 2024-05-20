package com.pg85.otg.util.materials;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pg85.otg.config.yaml.MaterialSetDeserializer;
import com.pg85.otg.config.yaml.MaterialSetSerializer;
import com.pg85.otg.exceptions.InvalidConfigException;
import com.pg85.otg.util.OTGMaterialReader;
import com.pg85.otg.util.helpers.StringHelper;

/**
 * A material set that accepts special values such as "All" or "Solid". These
 * special values make it almost impossible to know which materials are in
 * this set, and as such, this set can't be iterated over and its size remains
 * unknown.
 */
@JsonDeserialize(using = MaterialSetDeserializer.class)
@JsonSerialize(using = MaterialSetSerializer.class)
public class MaterialSet
{
	/**
	 * Keyword that adds all materials to the set when used in
	 * {@link #parseAndAdd(String)}.
	 */
	private static final String ALL_MATERIALS = "All";

	/**
	 * Keyword that adds all solid materials to the set when used in
	 * {@link #parseAndAdd(String)}.
	 */
	public static final String SOLID_MATERIALS = "Solid";

	/**
	 * Keyword that adds all non solid materials to the set when used in
	 * {@link #parseAndAdd(String)}.
	 */
	private static final String NON_SOLID_MATERIALS = "NonSolid";

	private boolean allMaterials = false;
	private boolean allSolidMaterials = false;
	private boolean allNonSolidMaterials = false;

	private int[] materialIntSet = new int[0];
	private final Set<LocalMaterialData> materials = new LinkedHashSet<>();
	private Set<LocalMaterialTag> tags = new LinkedHashSet<>();
	private boolean intSetUpToDate = true;

	/**
	 * Adds the given material to the list.
	 *
	 * <p>If the material is "All", all
	 * materials in existence are added to the list. If the material is
	 * "Solid", all solid materials are added to the list. Otherwise,
	 * {@link OTG#readMaterial(String)} is used to read the
	 * material.
	 *
	 * <p>If the material {@link StringHelper#specifiesBlockData(String)
	 * specifies block data}, it will match only materials with exactly that
	 * block data. If the material doesn't specify block data, it will match
	 * materials with any block data.
	 *
	 * @param input The name of the material to add.
	 * @throws InvalidConfigException If the name is invalid.
	 */
	public void parseAndAdd(String input) throws InvalidConfigException
	{
		if (input.equalsIgnoreCase(ALL_MATERIALS))
		{
			this.allMaterials = true;
			return;
		}
		if (input.equalsIgnoreCase(SOLID_MATERIALS))
		{
			this.allSolidMaterials = true;
			return;
		}
		if (input.equalsIgnoreCase(NON_SOLID_MATERIALS))
		{
			this.allNonSolidMaterials = true;
			return;
		}

		LocalMaterialBase base = OTGMaterialReader.get().read(input);

		if (base instanceof LocalMaterialTag tag) {
			addTag(tag);
		} else if (base instanceof LocalMaterialData data) {
			addMaterial(data);
		} else {
			throw new InvalidConfigException("Invalid block check, material \"" + input + "\" could not be found.");
		}
	}
	
	private void addMaterial(LocalMaterialData entry)
	{
		// Add the appropriate hashCode
		this.intSetUpToDate = false;
		this.materials.add(entry);
	}
	
	private void addTag(LocalMaterialTag entry)
	{
		this.tags.add(entry);
	} 
	
	/**
	 * Updates the int (hashCode) set, so that is is up to date again with the
	 * material set.
	 */
	private void updateIntSet()
	{
		if (this.intSetUpToDate)
		{
			// Already up to date
			return;
		}

		// Update the int set
		this.materialIntSet = new int[this.materials.size()];
		int i = 0;
		for (LocalMaterialData entry : this.materials)
		{
			// If the material has no data, it should match all with the same registry name
			if(!entry.isDefaultState())
			{
				this.materialIntSet[i] = entry.getRegistryName().hashCode();
			} else {
				this.materialIntSet[i] = entry.hashCode();
			}
			i++;
		}
		// Sort int set so that we can use Arrays.binarySearch
		Arrays.sort(this.materialIntSet);
		this.intSetUpToDate = true;
	}

	/**
	 * Gets whether the specified material is in this collection. Returns
	 * false if the material is null.
	 *
	 * @param material The material to check.
	 * @return True if the material is in this set.
	 */
	public boolean contains(LocalMaterialData material)
	{
		if (material == null || material.isEmpty())
		{
			return false;
		}
		if (this.allMaterials)
		{
			return true;
		}
		if (this.allSolidMaterials && material.isSolid())
		{
			return true;
		}
		if (this.allNonSolidMaterials && !material.isSolid())
		{
			return true;
		}

		// Try to update int set
		updateIntSet();

		// Check if the material is included
		if (Arrays.binarySearch(this.materialIntSet, material.hashCode()) >= 0)
		{
			return true;
		}
		// Check if the material is included without data (matches all of the same registry name)		
		if (Arrays.binarySearch(this.materialIntSet, material.getRegistryName().hashCode()) >= 0)
		{
			return true;
		}
		for(LocalMaterialTag entry : this.tags)
		{
			if(material.isBlockTag(entry))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns a comma (",") seperated list of all materials in this set.
	 * Keywords are left intact. No brackets ("[" or "]") are used at the
	 * begin and end of the string.
	 *
	 * @return The string.
	 */
	@Override
	public String toString()
	{
		// Check if all materials are included
		if (this.allMaterials)
		{
			return ALL_MATERIALS;
		}

		StringBuilder builder = new StringBuilder();
		// Check for solid materials
		if (this.allSolidMaterials)
		{
			builder.append(SOLID_MATERIALS).append(',');
		}
		// Check for non-solid materials
		if (this.allNonSolidMaterials)
		{
			builder.append(NON_SOLID_MATERIALS).append(',');
		}
		// Add all tags
		for (LocalMaterialTag tag : this.tags)
		{
			builder.append(tag.toString()).append(',');
		}
		// Add all other materials
		for (LocalMaterialData material : this.materials)
		{
			builder.append(material.toString()).append(',');
		}

		// Remove last ','
		if (!builder.isEmpty())
		{
			builder.deleteCharAt(builder.length() - 1);
		}

		return builder.toString();
	}

	/**
	 * Gets a new material set where all blocks are rotated.
	 *
	 * @return The new material set.
	 */
	public MaterialSet rotate()
	{
		MaterialSet rotated = new MaterialSet();
		if (this.allMaterials)
		{
			rotated.allMaterials = true;
		}
		if (this.allSolidMaterials)
		{
			rotated.allSolidMaterials = true;
		}
		if (this.allNonSolidMaterials)
		{
			rotated.allNonSolidMaterials = true;
		}
		rotated.intSetUpToDate = false;
		for (LocalMaterialData material : this.materials)
		{
			rotated.materials.add(material.rotate());
		}
		rotated.tags = this.tags; 
		return rotated;
	}
}
