package com.pg85.otg.customobject.bofunctions;

import com.pg85.otg.customobject.config.CustomObjectConfigFile;
import com.pg85.otg.customobject.config.CustomObjectConfigFunction;
import com.pg85.otg.exception.InvalidConfigException;
import com.pg85.otg.logging.ILogger;
import com.pg85.otg.util.interfaces.IMaterialReader;

import java.util.List;

/**
 * Represents a block in a BO3.
 */
public abstract class ModDataFunction<T extends CustomObjectConfigFile> extends CustomObjectConfigFunction<T>
{
    public int y;
    public String modId;
    public String modData;

    @Override
    public void load(List<String> args, boolean spawnLog, ILogger logger, IMaterialReader materialReader) throws InvalidConfigException
    {
        assureSize(5, args);
        // Those limits are arbitrary, LocalWorld.setBlock will limit it
        // correctly based on what chunks can be accessed
		x = readInt(args.get(0), -100, 100);
        y = readInt(args.get(1), -1000, 1000);
        z = readInt(args.get(2), -100, 100);
        modId = args.get(3);
        modData = args.get(4);
    }

    @Override
    public String makeString()
    {
        return "ModData(" + x + ',' + y + ',' + z + ',' + modId + ',' + modData + ')';
    }

    @Override
    public boolean isAnalogousTo(CustomObjectConfigFunction<T> other)
    {
        if(!getClass().equals(other.getClass()))
        {
            return false;
        }
        ModDataFunction<T> block = (ModDataFunction<T>) other;
        return block.x == x && block.y == y && block.z == z && block.modId.equalsIgnoreCase(modId) && block.modData.equalsIgnoreCase(modData);
    }
    
    public abstract ModDataFunction<T> getNewInstance();
}
