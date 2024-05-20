package com.pg85.otg.fabric.materials;

import com.pg85.otg.exceptions.InvalidConfigException;
import com.pg85.otg.interfaces.IMaterialReader;
import com.pg85.otg.util.materials.LocalMaterialBase;
import com.pg85.otg.util.materials.LocalMaterialData;
import com.pg85.otg.util.materials.LocalMaterialTag;

public class FabricMaterialReader implements IMaterialReader {
    @Override
    public LocalMaterialData readMaterial(String material) throws InvalidConfigException {
        return null;
    }

    @Override
    public LocalMaterialTag readTag(String tag) throws InvalidConfigException {
        return null;
    }
}
