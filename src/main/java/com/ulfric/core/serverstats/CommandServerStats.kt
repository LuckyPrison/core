package com.ulfric.core.serverstats

import com.ulfric.lib.coffee.command.Command
import com.ulfric.lib.coffee.module.ModuleBase
import com.ulfric.lib.craft.server.ServerUtils
import com.ulfric.lib.craft.string.ChatColor
import java.lang.management.ManagementFactory

class CommandServerStats(owner: ModuleBase) : Command("serverstats", owner)
{
    init
    {
        this.addPermission("serverstats.command")
    }

    override fun run()
    {
        val green = ChatColor.of("GREEN")
        val red = ChatColor.of("RED")
        val gray = ChatColor.of("GRAY")
        val bold = "${ChatColor.of("RESET")}${ChatColor.of("BOLD")}"

        val gc = ManagementFactory.getGarbageCollectorMXBeans().first { !it.name.contains("Scavenge") }
                .name.trimStart('P', 'S', ' ')
        val max = Runtime.getRuntime().maxMemory() / 1024 / 1024
        val used = max - (Runtime.getRuntime().freeMemory() / 1024 / 1024)
        val percent = used.toDouble() / max
        val percentStr = "${if (percent < 0.8) green else red}${(percent * 100).toInt()}%"
        val uptime = ManagementFactory.getRuntimeMXBean().uptime.milliSecondsToTimespan()

        this.sender.apply {
            sendMessage("${bold}Uptime: $gray$uptime")
            sendMessage("${bold}TPS: ${formatTPS()}")
            sendMessage("${bold}GC: $gray$gc")
            sendMessage("${bold}Memory: $percentStr ${progress(percent, 25)} $gray[$red${used}MB$gray/$green${max}MB$gray]")
        }
    }

    companion object
    {
        fun formatTPS(): String
        {
            val green = ChatColor.of("GREEN")
            val gold = ChatColor.of("GOLD")
            val red = ChatColor.of("RED")
            val gray = ChatColor.of("GRAY")

            return ServerUtils.getTPS().joinToString(separator = "$gray, ") {
                val color = if (it < 10.0) red else if (it < 15.0) gold else green
                String.format("$color%.2f", it)
            }
        }

        fun progress(percent: Double, size: Int): String
        {
            val green = ChatColor.of("GREEN")
            val red = ChatColor.of("RED")
            val gray = ChatColor.of("GRAY")

            val filledSize = (size * percent).toInt()
            val filledChars = "|".repeat(filledSize)
            val emptySize = size - filledSize
            val emptyChars = "|".repeat(emptySize)

            return "$gray[$red$filledChars$green$emptyChars$gray]"
        }

        fun Long.milliSecondsToTimespan(): String
        {
            val sb = StringBuffer()
            val diffInSeconds = this / 1000
            val seconds = if (diffInSeconds >= 60) diffInSeconds % 60 else diffInSeconds
            val minutes = if ((diffInSeconds / 60) >= 60) (diffInSeconds / 60) % (60) else diffInSeconds / 60
            val hours = if ((diffInSeconds / 3600) >= 24) (diffInSeconds / 3600) % (24) else diffInSeconds / 3600
            val days = diffInSeconds / 60 / 60 / 24

            if (days > 0)
            {
                sb.append(days)
                sb.append(if (days > 1) " days" else " day")
                sb.append(" and ")
            }

            if (hours > 0 || days > 0)
            {
                sb.append(hours)
                sb.append(if (hours > 1) " hours" else " hour")
                sb.append(" and ")
            }

            if (minutes > 0 || hours > 0 || days > 0)
            {
                sb.append(minutes)
                sb.append(if (minutes > 1) " minutes" else " minute")
                sb.append(" and ")
            }

            sb.append(seconds)
            sb.append(if (seconds > 1) " seconds" else " second")

            return sb.toString()
        }
    }
}
