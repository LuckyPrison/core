package com.ulfric.core.enchant;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.text.WordUtils;

import com.ulfric.lib.coffee.location.VectorPattern;
import com.ulfric.lib.craft.inventory.item.enchant.Enchantment;

public final class VectorPatternEnchantment extends Enchantment {

	public static VectorPatternEnchantment newEnchantment(String name, int id, int max, VectorPattern pattern)
	{
		Validate.notBlank(name);
		Validate.isTrue(id <= 256);
		Validate.isTrue(max > 0);
		Validate.notNull(pattern);

		String enchName = WordUtils.capitalizeFully(name.trim()).replaceAll("[\\s]{2,}", " ").replace('_', ' ');

		VectorPatternEnchantment ench = new VectorPatternEnchantment(enchName, id, max, pattern);

		Validate.isTrue(Enchantment.ENCHANTS.putIfAbsent(id, ench) == null);

		return ench;
	}

	private VectorPatternEnchantment(String name, int id, int max, VectorPattern pattern)
	{
		super(name, id, max);

		this.pattern = pattern;
	}

	private final VectorPattern pattern;

	public VectorPattern getPattern()
	{
		return this.pattern;
	}

}