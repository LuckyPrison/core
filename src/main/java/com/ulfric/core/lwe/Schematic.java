package com.ulfric.core.lwe;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import com.flowpowered.nbt.ByteArrayTag;
import com.flowpowered.nbt.ShortTag;
import com.flowpowered.nbt.StringTag;
import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.stream.NBTInputStream;
import com.ulfric.lib.coffee.location.ImmutableVector;
import com.ulfric.lib.coffee.location.Vector;
import com.ulfric.lib.coffee.module.ModuleUtils;
import com.ulfric.lib.coffee.persist.FileUtils;
import com.ulfric.lib.coffee.string.NamedBase;
import com.ulfric.lib.craft.block.MaterialData;
import com.ulfric.lib.craft.block.MultiBlockChange;
import com.ulfric.lib.craft.inventory.item.Material;
import com.ulfric.lib.craft.location.Location;
import com.ulfric.lib.craft.location.LocationUtils;
import com.ulfric.lib.craft.world.World;

/**
 * Port of WorldEdit's schematic loader
 *
 * @author Adam
 */
public final class Schematic extends NamedBase {

	public static Schematic valueOf(String name)
	{
		if (StringUtils.isBlank(name)) return null;

		Path path = ModuleUtils.getModule(ModuleLWE.class).getModuleFolder().resolve("schematics").resolve(name + ".schematic");

		if (!FileUtils.isFile(path)) return null;

		return new Schematic(name, path);
	}

	private Schematic(String name, Path path)
	{
		super(name);

		this.path = path;
	}

	private final Path path;

	public Path getPath()
	{
		return this.path;
	}

	public void paste(Location location)
	{
		Validate.notNull(location);

		try (NBTInputStream nbtStream = new NBTInputStream(Files.newInputStream(this.path)))
		{
			@SuppressWarnings("unchecked")
			Tag<Map<String, Tag<?>>> tag = nbtStream.readTag();

			Validate.notNull(tag);

			Validate.isTrue(tag.getName().equals("Schematic"));

			Map<String, Tag<?>> schematic = tag.getValue();

			short width = Schematic.getTag(schematic, "Width", ShortTag.class).getValue();
			short length = Schematic.getTag(schematic, "Length", ShortTag.class).getValue();
			short height = Schematic.getTag(schematic, "Height", ShortTag.class).getValue();

			String materials = Schematic.getTag(schematic, "Materials", StringTag.class).getValue();

			Validate.isTrue(materials.equals("Alpha"));

			byte[] blockId = getTag(schematic, "Blocks", ByteArrayTag.class).getValue();
			byte[] blockData = getTag(schematic, "Data", ByteArrayTag.class).getValue();
			byte[] addId = schematic.containsKey("AddBlocks") ? Schematic.getTag(schematic, "AddBlocks", ByteArrayTag.class).getValue() : new byte[0];
			short[] blocks = new short[blockId.length];

			for (int index = 0; index < blockId.length; index++)
			{
				if ((index >> 1) >= addId.length)
				{
					blocks[index] = (short) (blockId[index] & 0xFF);
				}
				else
				{
					if ((index & 1) == 0)
					{
						blocks[index] = (short) (((addId[index >> 1] & 0x0F) << 8) + (blockId[index] & 0xFF));
					}
					else
					{
						blocks[index] = (short) (((addId[index >> 1] & 0xF0) << 4) + (blockId[index] & 0xFF));
					}
				}
			}

			MultiBlockChange change = new MultiBlockChange(100);

			World world = location.getWorld();

			Vector base = ImmutableVector.of(location);

			for (int x = 0; x < width; x++)
			{
				for (int y = 0; y < height; y++)
				{
					for (int z = 0; z < length; ++z)
					{
						int index = y * width * length + z * width + x;

						Vector set = ImmutableVector.of(x, y, z).add(base);

						MaterialData data = MaterialData.of(Material.of(blocks[index]), blockData[index]);

						change.addBlock(LocationUtils.getLocation(world, set), data);
					}
				}
			}

			change.run();
		}
		catch (IOException exception)
		{
			exception.printStackTrace();
		}
	}

	private static <T extends Tag<?>> T getTag(Map<String, Tag<?>> tags, String key, Class<T> expected)
	{
		Tag<?> tag = tags.get(key);

		Validate.notNull(tag);

		return expected.cast(tag);
	}

}