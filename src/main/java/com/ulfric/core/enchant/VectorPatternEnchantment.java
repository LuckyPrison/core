package com.ulfric.core.enchant;

import java.util.List;
import java.util.SortedMap;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.text.WordUtils;

import com.ulfric.lib.coffee.location.VectorPattern;
import com.ulfric.lib.coffee.string.Patterns;
import com.ulfric.lib.craft.inventory.item.enchant.Enchantment;

public final class VectorPatternEnchantment extends Enchantment {

	public static VectorPatternEnchantment newEnchantment(String name, int id, int max, SortedMap<Integer, VectorPattern> patterns, List<Integer> conflicts)
	{
		Validate.notBlank(name);
		Validate.isTrue(id <= 256);
		Validate.isTrue(max > 0);
		Validate.notEmpty(patterns);

		String enchName = WordUtils.capitalizeFully(Patterns.D_WHITESPACE.matcher(name.trim()).replaceAll(" ")).replace('_', ' ');

		VectorPatternEnchantment ench = new VectorPatternEnchantment(enchName, id, max, patterns, conflicts);

		return ench;
	}

	private VectorPatternEnchantment(String name, int id, int max, SortedMap<Integer, VectorPattern> patterns, List<Integer> conflicts)
	{
		super(name, id, max, conflicts, item -> item.getType().isTool());

		this.patterns = patterns;
	}

	private final SortedMap<Integer, VectorPattern> patterns;

	public VectorPattern getPattern(int level)
	{
		VectorPattern pattern = this.patterns.get(level);

		if (pattern != null) return pattern;

		return this.patterns.get(this.patterns.lastKey());
	}

}