package com.gmail.kurpfy.seniorteam;

import com.gmail.kurpfy.seniorteam.command.NPCCommand;
import com.gmail.kurpfy.seniorteam.npc.NPC;
import com.gmail.kurpfy.seniorteam.npc.NPCPacketController;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class SeniorTeamPlugin extends JavaPlugin {

    @Getter
    private static SeniorTeamPlugin instance;

    private Class<?> packetControllerClass;

    @Getter @Setter
    private NPC npc;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        String version = packageName.substring(packageName.lastIndexOf('.') + 1);

        try {
            this.packetControllerClass = Class.forName(
              "com.gmail.kurpfy.seniorteam.npc.versions." + version + ".NPCPacketControllerImpl"
            );

        } catch (ClassNotFoundException e) {
            getLogger().severe("Unsupported version " + version);
        }

        getCommand("npc").setExecutor(new NPCCommand(this));
    }

    public void setPacketController(NPC npc) {
        try {
            Constructor<?> constructor = packetControllerClass.getConstructor(NPC.class);

            npc.setPacketController((NPCPacketController) constructor.newInstance(npc));
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
