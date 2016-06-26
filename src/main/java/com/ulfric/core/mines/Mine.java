package com.ulfric.core.mines;

import com.ulfric.lib.coffee.region.Region;
import com.ulfric.lib.coffee.tuple.Weighted;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.inventory.item.Material;

import java.util.Set;

class Mine {

	private String name;
	private Set<Weighted<Material>> contents;
	private Region region;
	private Set<String> permissions;

	protected Mine(String name, Set<Weighted<Material>> contents, Region region, Set<String> permissions)
	{
		this.name = name;
		this.contents = contents;
		this.region = region;
		this.permissions = permissions;
	}

	public String getName()
	{
		return name;
	}

	public void setContents(Set<Weighted<Material>> contents)
	{
		this.contents = contents;
	}

	public Set<Weighted<Material>> getContents()
	{
		return contents;
	}

	public void setRegion(Region region)
	{
		this.region = region;
	}

	public Region getRegion()
	{
		return region;
	}

	public void setPermissions(Set<String> permissions)
	{
		this.permissions = permissions;
	}

	public Set<String> getPermissions() {
		return permissions;
	}

	public boolean hasPermission(Player player)
	{
		for (String permission : permissions) {
			if (player.hasPermission(permission)) return true;
		}
		return false;
	}
}
