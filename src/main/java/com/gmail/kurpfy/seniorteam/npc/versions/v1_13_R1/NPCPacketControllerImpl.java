package com.gmail.kurpfy.seniorteam.npc.versions.v1_13_R1;

import com.gmail.kurpfy.seniorteam.npc.NPC;
import com.gmail.kurpfy.seniorteam.npc.NPCPacketController;
import net.minecraft.server.v1_13_R1.*;
import net.minecraft.server.v1_8_R1.PacketPlayOutRelEntityMoveLook;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_13_R1.CraftServer;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
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

            sendPacket(player, new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));
            sendPacket(player, new PacketPlayOutNamedEntitySpawn(entityPlayer));
            sendPacket(player, new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer));
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
        PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook movePacket = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(
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
    public void crouch(byte data) {
        DataWatcher dataWatcher = new DataWatcher(entityPlayer);

        DataWatcherObject<Byte> basicEntityData = new DataWatcherObject<>(0, DataWatcherRegistry.a);
        byte bitMaskData = (byte) (data == (byte) 2 ? 0b0000_0010 : 0b0000_0000);

        dataWatcher.register(basicEntityData, bitMaskData);

        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(entityPlayer.getId(), dataWatcher, true);

        getParent().getVisibleTo().forEach(uniqueId -> {
            Player player = Bukkit.getPlayer(uniqueId);
            if(player == null) return;

            sendPacket(player, packet);
        });
    }
}
