package com.ulfric.core.settings;

import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.ulfric.data.scope.PlayerScopes;

public enum Settings {

	INSTANCE;

	private final Set<Setting> settings = Sets.newTreeSet();

	public List<Setting> getSettings()
	{
		return ImmutableList.copyOf(this.settings);
	}

	public void registerSetting(Setting setting)
	{
		this.settings.add(setting);

		PlayerScopes.ONLINE.addListener(setting);
	}

	public void removeSetting(Setting setting)
	{
		this.settings.remove(setting);

		PlayerScopes.ONLINE.removeListener(setting);
	}

}