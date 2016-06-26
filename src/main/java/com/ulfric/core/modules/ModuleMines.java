package com.ulfric.core.modules;

import com.ulfric.core.mines.CommandMines;
import com.ulfric.core.mines.MineSelectListener;
import com.ulfric.core.mines.Mines;
import com.ulfric.lib.coffee.module.Module;

public class ModuleMines extends Module {

	private Mines mines;

	public ModuleMines()
	{
		super("mines", "A module to handle mines and mine accessories", "1.0.0", "rowtn");
		this.mines = new Mines(/* data */ null, /* world*/ null, 600);
		addCommand(new CommandMines(this));
		addListener(new MineSelectListener(this));
	}

	public Mines getMines() {
		return mines;
	}
}
