package com.ulfric.core.teleport;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.IteratorUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

final class WarpSet implements Iterable<Warp> {

	WarpSet() { }

	private boolean changed;
	private final Set<Warp> warps = Sets.newTreeSet();
	private final Set<String> removedWarps = Sets.newHashSet();

	public void add(Warp warp)
	{
		if (!this.warps.add(warp)) return;

		this.removedWarps.remove(warp.getName());

		this.changed = true;
	}

	public boolean remove(Warp warp)
	{
		if (!this.warps.remove(warp)) return false;

		this.removedWarps.add(warp.getName());

		this.changed = true;

		return true;
	}

	public boolean isEmpty()
	{
		return this.warps.isEmpty();
	}

	@Override
	public Iterator<Warp> iterator()
	{
		return IteratorUtils.unmodifiableIterator(this.warps.iterator());
	}

	public int size()
	{
		return this.warps.size();
	}

	public List<Warp> getChangedWarps()
	{
		if (!this.changed) return ImmutableList.of();

		ImmutableList.Builder<Warp> builder = ImmutableList.builder();

		this.warps.parallelStream().filter(Warp::hasBeenModified).forEach(builder::add);

		return builder.build();
	}

	public boolean hasBeenChanged()
	{
		if (this.changed == true) return true;

		for (Warp warp : this.warps)
		{
			if (!warp.hasBeenModified()) continue;

			return true;
		}

		return false;
	}

	public Iterator<String> removedWarpsIterator()
	{
		return IteratorUtils.unmodifiableIterator(this.removedWarps.iterator());
	}

}