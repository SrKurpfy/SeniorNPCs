package com.gmail.kurpfy.seniorteam.npc.versions.v1_8_R1;

import com.gmail.kurpfy.seniorteam.npc.NPC;
import com.gmail.kurpfy.seniorteam.npc.NPCPacketController;
import net.minecraft.server.v1_8_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R1.*;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NPCPacketControllerImpl extends NPCPacketController {

    private EntityPlayer entityPlayer;

    public NPCPacketControllerImpl(NPC parent) {
        super(parent);
    }

    @Override
    public void sendPacket(Player player, Object packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket((Packet) packet);
    }

    @Override
    public void spawn() {
        Location location = getParent().getLocation();

        MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();

        this.entityPlayer = new EntityPlayer(minecraftServer, worldServer, getParent().getProfile(), new PlayerInteractManager(worldServer));

        entityPlayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        entityPlayer.getBukkitEntity().getPlayer().setPlayerListName("");

        getParent().getVisibleTo().forEach(uniqueId -> {
            Player player = Bukkit.getPlayer(uniqueId);
            if(player == null) return;

            sendPacket(player, new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));
            sendPacket(player, new PacketPlayOutNamedEntitySpawn(entityPlayer));
            sendPacket(player, new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer));
        });
    }

    @Override
    public void destroy() {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entityPlayer.getId());

        getParent().getVisibleTo().forEach(uniqueId -> {
            Player player = Bukkit.getPlayer(uniqueId);
            if(player == null) return;

            sendPacket(player, packet);
        });
    }

    @Override
    public void move(double x, double y, double z) {
        PacketPlayOutRelEntityMoveLook movePacket = new PacketPlayOutRelEntityMoveLook(
          entityPlayer.getId(), parseNMSValue(x), parseNMSValue(y), parseNMSValue(z), (byte) 0, (byte) 0, true
        );

        PacketPlayOutEntityHeadRotation headPacket = new PacketPlayOutEntityHeadRotation(
          entityPlayer, (byte) 0
        );

        getParent().getVisibleTo().forEach(uniqueId -> {
            Player player = Bukkit.getPlayer(uniqueId);
            if(player == null) return;

            sendPacket(player, movePacket);
            sendPacket(player, headPacket);
        });
    }

    @Override
    public void hit() {
        PacketPlayOutAnimation packet = new PacketPlayOutAnimation(entityPlayer, 0);

        getParent().getVisibleTo().forEach(uniqueId -> {
            Player player = Bukkit.getPlayer(uniqueId);
            if(player == null) return;

            sendPacket(player, packet);
        });
    }

    @Override
    public void jump(boolean b) {
        if(b) {
            move(0, 1,0);
        } else {
            move(0, -1, 0);
        }
    }

    @Override
    public void crouch(byte data) {
        DataWatcher dataWatcher = new DataWatcher(entityPlayer);
        dataWatcher.a(0, data);

        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(entityPlayer.getId(), dataWatcher, true);

        getParent().getVisibleTo().forEach(uniqueId -> {
            Player player = Bukkit.getPlayer(uniqueId);
            if(player == null) return;

            sendPacket(player, packet);
        });
    }
}
