package com.gmail.kurpfy.seniorteam.command;

import com.gmail.kurpfy.seniorteam.SeniorTeamPlugin;
import com.gmail.kurpfy.seniorteam.npc.NPC;
import com.gmail.kurpfy.seniorteam.npc.NPCRunnable;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NPCCommand implements CommandExecutor {

    private final SeniorTeamPlugin plugin;

    public NPCCommand(SeniorTeamPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(!(commandSender instanceof Player)) return false;

        Player player = (Player) commandSender;

        if(plugin.getNpc() != null) {
            plugin.getNpc().getPacketController().destroy();
        }

        Location location = player.getLocation();

        NPC npc = new NPC((int) (Math.random() * Integer.MAX_VALUE), location);
        plugin.setPacketController(npc);

        npc.getVisibleTo().add(player.getUniqueId());

        npc.getPacketController().spawn();
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new NPCRunnable(npc), 10L, 10L);

        return false;
    }
}
