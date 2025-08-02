package com.pg85.otg.util.biome;

import java.nio.file.Path;

import com.pg85.otg.interfaces.IBiomeResourceLocation;

public class OTGBiomeResourceLocation implements IBiomeResourceLocation
{
	private static final String BIOME_RESOURCE_LOCATION_SEPARATOR = ".";

	private final String presetFolder;
	private final String presetRegistryName;
	private final String biomeName;
	private final String resourceName;

	public OTGBiomeResourceLocation(Path presetFolder, String presetRegistryName, String biomeName, String resourceName)
	{
		this.presetFolder = presetFolder.toFile().getName();
		String presetShortName = presetRegistryName != null && !presetRegistryName.trim().isEmpty() ? presetRegistryName : this.presetFolder;
		this.presetRegistryName = presetShortName.toLowerCase().trim().replaceAll("[^a-z0-9/_ -]", "_");
		this.biomeName = biomeName.toLowerCase().trim().replaceAll("[^a-z0-9/_-]", "_");
		this.resourceName = resourceName;
	}

	public OTGBiomeResourceLocation(Path presetFolder, String presetRegistryName, String biomeName)
	{
		this(presetFolder, presetRegistryName, biomeName, null);
	}
	
	@Override
	public String getPresetFolderName()
	{
		return this.presetFolder;
	}
	
	@Override
	public String toResourceLocationString()
	{
		return String.format("%s%s%s", getResourceDomain(), ":", getResourcePath());
	}	
	
	private String getResourceDomain()
	{
		return presetRegistryName;
	}

	private String getResourcePath()
	{
		if(this.resourceName != null)
		{
			return String.format("%s%s%s", this.biomeName, BIOME_RESOURCE_LOCATION_SEPARATOR, this.resourceName);
		} else {			
			return this.biomeName;
		}
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (this == other)
		{
			return true;
		}
		if (!(other instanceof OTGBiomeResourceLocation))
		{
			return false;
		}
		return ((OTGBiomeResourceLocation)other).toResourceLocationString().equals(this.toResourceLocationString());
	}

	@Override
	public int hashCode()
	{
		return toResourceLocationString().hashCode();
	}	
}
