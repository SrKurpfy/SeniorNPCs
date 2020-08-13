package com.gmail.kurpfy.seniorteam.npc;

import lombok.Getter;
import org.bukkit.entity.Player;

public abstract class NPCPacketController {

    @Getter
    private final NPC parent;

    public NPCPacketController(NPC parent) {
        this.parent = parent;
    }

    public byte parseNMSValue(double value) {
        return (byte) (value * 32);
    }

    public abstract void sendPacket(Player player, Object packet);

    public abstract void spawn();

    public abstract void destroy();

    public abstract void move(double x, double y, double z);

    public abstract void hit();

    public void jump(boolean b) {
        if(b) {
            move(0, 1,0);
        } else {
            move(0, -1, 0);
        }
    }

    public abstract void crouch(byte data);
}
