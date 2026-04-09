package com.pg85.otg.config.settingtype;

import com.pg85.otg.config.settings.ConfigSection;
import com.pg85.otg.exceptions.InvalidConfigException;
import com.pg85.otg.util.helpers.StringHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class StringSetSetting extends Setting<Set<String>>
{

    private final String[] defaultValue;

    protected StringSetSetting(String name, String... defaultValue)
    {
        super(name);
        this.defaultValue = defaultValue;
    }

    protected StringSetSetting(String name, String[] defaultValue, Function<ConfigSection, Set<String>> getter, String... description)
    {
        super(name, getter, description);
        this.defaultValue = defaultValue;
    }

    @Override
    public Set<String> getDefaultValue()
    {
        return Set.of(defaultValue);
    }

    @Override
    public Set<String> read(String string) throws InvalidConfigException
    {
        return new HashSet<>(List.of(StringHelper.readCommaSeperatedString(string)));
    }

    @Override
    public String write(Set<String> value) {
        return StringHelper.join(value, ", ");
    }

    @Override
    public String getTypeAsString()
    {
        return "array";
    }

    @Override
    public String getComplexTypeSchema() {
        return "string";
    }

    @Override
    public String getDefaultValueAsString() {
        if (defaultValue.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String s : defaultValue) {
            sb.append('"');
            sb.append(s);
            sb.append("\", ");
        }
        sb.setLength(sb.length() - 2);
        return sb.toString();
    }
}
