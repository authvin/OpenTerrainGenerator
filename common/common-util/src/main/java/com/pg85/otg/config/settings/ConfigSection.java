package com.pg85.otg.config.settings;

import com.pg85.otg.config.settingtype.Setting;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public abstract class ConfigSection {

    public abstract String getSectionName();

    public static Map<String, Setting<?>> getSettings(Class<? extends ConfigSection> clazz) {
        Map<String, Setting<?>> settingsMap = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) && Setting.class.isAssignableFrom(field.getType())) {
                try {
                    field.setAccessible(true);
                    Setting<?> setting = (Setting<?>) field.get(null);
                    settingsMap.put(field.getName(), setting);
                } catch (IllegalAccessException e) {
                    // Handle exception
                    e.printStackTrace();
                }
            }
        }
        return settingsMap;
    }

    public Map<String, Setting<?>> getSettings() {
        return getSettings(this.getClass());
    }



}
