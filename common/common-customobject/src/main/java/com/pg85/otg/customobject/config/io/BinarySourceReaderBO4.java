package com.pg85.otg.customobject.config.io;

import com.pg85.otg.config.settingtype.Setting;
import com.pg85.otg.customobject.config.CustomObjectConfigFunction;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A SettingsReaderBO4 for configs loaded from binary sources (.bopack, .bo4data).
 * Holds the object name and source file; returns defaults for all settings queries
 * since binary-loaded configs never use text settings.
 */
public class BinarySourceReaderBO4 implements SettingsReaderBO4
{
	private final String name;
	private final File file;

	public BinarySourceReaderBO4(String name, File file)
	{
		this.name = name;
		this.file = file;
	}

	@Override public File getFile()   { return file; }
	@Override public String getName() { return name; }

	@Override public <T> void addConfigFunction(CustomObjectConfigFunction<T> function) {}
	@Override public <T> List<CustomObjectConfigFunction<T>> getConfigFunctions(T holder, boolean useFallback) { return Collections.emptyList(); }
	@Override public Iterable<Map.Entry<String, String>> getRawSettings() { return Collections.emptyList(); }
	@Override public <S> S getSetting(Setting<S> setting, S defaultValue) { return defaultValue; }
	@Override public boolean hasSetting(Setting<?> setting) { return false; }
	@Override public boolean isNewConfig() { return false; }
	@Override public <S> void putSetting(Setting<S> setting, S value) {}
	@Override public void renameOldSetting(String oldName, Setting<?> newSetting) {}
	@Override public void setFallbackReader(SettingsReaderBO4 reader) {}
	@Override public void flushCache() {}
}