package com.pg85.otg.gen.resource;

import com.google.common.collect.Lists;
import com.pg85.otg.config.biome.Resource;
import com.pg85.otg.exception.InvalidConfigException;
import com.pg85.otg.gen.resource.util.CoralHelper;
import com.pg85.otg.logging.ILogger;
import com.pg85.otg.util.ChunkCoordinate;
import com.pg85.otg.util.OTGDirection;
import com.pg85.otg.util.interfaces.IBiomeConfig;
import com.pg85.otg.util.interfaces.IMaterialReader;
import com.pg85.otg.util.interfaces.IWorldGenRegion;
import com.pg85.otg.util.materials.LocalMaterialData;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CoralClawGen extends Resource
{
    private static final OTGDirection[] HORIZONTAL = {OTGDirection.NORTH, OTGDirection.EAST, OTGDirection.SOUTH, OTGDirection.WEST};

    public CoralClawGen(IBiomeConfig biomeConfig, List<String> args, ILogger logger, IMaterialReader materialReader) throws InvalidConfigException
    {
        super(biomeConfig, args, logger, materialReader);
        this.frequency = readInt(args.get(0), 1, 500);
        this.rarity = readRarity(args.get(1));
    }

    @Override
    public String toString()
    {
        return "CoralClaw(" + this.frequency + "," + this.rarity + ")";
    }

    @Override
    public void spawn(IWorldGenRegion world, Random random, boolean villageInChunk, int x, int z, ChunkCoordinate chunkBeingPopulated)
    {
        int y = world.getBlockAboveSolidHeight(x, z, chunkBeingPopulated);
        LocalMaterialData coral = CoralHelper.getRandomCoralBlock(random);

        // Return if we don't have enough space to place the claw
        if (!CoralHelper.placeCoralBlock(world, random, chunkBeingPopulated, x, y, z, coral))
        {
            return;
        }

        OTGDirection initial = randomDirection(random);
        List<OTGDirection> list = Lists.newArrayList(initial, initial.getClockWise(), initial.getCounterClockWise());
        Collections.shuffle(list, random);

        int dirEnd = random.nextInt(2) + 2;

        for (OTGDirection direction : list.subList(0, dirEnd))
        {
            int dx = x + direction.getX();
            int dy = y;
            int dz = z + direction.getZ();

            int branchLength = random.nextInt(2) + 1;

            int clawLength;
            OTGDirection finalDir;
            if (direction == initial) {
                finalDir = direction;
                clawLength = random.nextInt(3) + 2;
            } else {
                dy++;
                finalDir = randomDirection(new OTGDirection[]{direction, OTGDirection.UP}, random);
                clawLength = random.nextInt(3) + 3;
            }

            for(int i = 0; i < branchLength && CoralHelper.placeCoralBlock(world, random, chunkBeingPopulated, dx, dy, dz, coral); ++i) {
                dx += finalDir.getX();
                dy += finalDir.getY();
                dz += finalDir.getZ();
            }

            // Go backwards 1
            dx -= finalDir.getX();
            dy -= finalDir.getY();
            dz -= finalDir.getZ();

            // Go up 1
            dy++;

            for(int i = 0; i < clawLength; ++i) {
                dx += initial.getX();
                dy += initial.getY();
                dz += initial.getZ();

                if (!CoralHelper.placeCoralBlock(world, random, chunkBeingPopulated, dx, dy, dz, coral)) {
                    break;
                }

                if (random.nextFloat() < 0.25) {
                    dy++;
                }
            }
        }
    }

    private static OTGDirection randomDirection(OTGDirection[] array, Random random)
    {
        return array[random.nextInt(array.length)];
    }

    private static OTGDirection randomDirection(Random random)
    {
        return HORIZONTAL[random.nextInt(3)];
    }
}
