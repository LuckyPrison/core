package com.ulfric.core.serverstats

import com.ulfric.lib.coffee.module.Module

class ModuleServerStats : Module("serverstats", "/serverstats command", "1.0.0", "Amaranth")
{
    override fun onModuleEnable()
    {
        this.addCommand(CommandServerStats(this))
    }
}
