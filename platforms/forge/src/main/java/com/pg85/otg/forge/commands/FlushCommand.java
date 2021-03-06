package com.pg85.otg.forge.commands;

import com.pg85.otg.OTG;
import com.pg85.otg.forge.gen.OTGNoiseChunkGenerator;
import com.pg85.otg.logging.LogMarker;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;

public class FlushCommand
{
	protected static int flushCache(CommandSource source)
	{
		if (!(source.getLevel().getChunkSource().generator instanceof OTGNoiseChunkGenerator))
		{
			source.sendSuccess(new StringTextComponent("OTG is not enabled in this world"), false);
			return 0;
		}
		
        OTG.log(LogMarker.INFO, "Unloading BO2/BO3/BO4 files");
        OTG.getEngine().getCustomObjectManager().reloadCustomObjectFiles();
        source.sendSuccess(new StringTextComponent("Objects unloaded."), false);
        return 0;
	}
}
