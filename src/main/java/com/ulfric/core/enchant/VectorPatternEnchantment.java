package com.ulfric.core.enchant;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.text.WordUtils;

import com.ulfric.lib.coffee.location.VectorPattern;
import com.ulfric.lib.coffee.string.Patterns;
import com.ulfric.lib.craft.inventory.item.enchant.Enchantment;

public final class VectorPatternEnchantment extends Enchantment {

	public static VectorPatternEnchantment newEnchantment(String name, int id, int max, VectorPattern pattern, List<Integer> conflicts)
	{
		Validate.notBlank(name);
		Validate.isTrue(id <= 256);
		Validate.isTrue(max > 0);
		Validate.notNull(pattern);

		String enchName = WordUtils.capitalizeFully(Patterns.D_WHITESPACE.matcher(name.trim()).replaceAll(" ")).replace('_', ' ');

		VectorPatternEnchantment ench = new VectorPatternEnchantment(enchName, id, max, pattern, conflicts);

		ench.register();

		return ench;
	}

	private VectorPatternEnchantment(String name, int id, int max, VectorPattern pattern, List<Integer> conflicts)
	{
		super(name, id, max, conflicts);

		this.pattern = pattern;
	}

	private final VectorPattern pattern;

	public VectorPattern getPattern()
	{
		return this.pattern;
	}

}