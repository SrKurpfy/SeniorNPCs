package com.gmail.kurpfy.seniorteam.npc;

import com.mojang.authlib.GameProfile;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class NPC {

    @Getter
    private final UUID id;

    @Getter
    private final int entityId;

    @Getter
    private final Location location;

    @Getter
    private final Set<UUID> visibleTo;

    private GameProfile profile;

    @Getter @Setter
    private NPCPacketController packetController;

    public NPC(int entityId, Location location) {
        this.id = UUID.randomUUID();
        this.entityId = entityId;
        this.location = location;
        this.visibleTo = new HashSet<>();
    }

    public GameProfile getProfile() {
        if (profile == null)
            profile = new GameProfile(UUID.randomUUID(), StringUtils.EMPTY);

        return profile;
    }

    public void spawn(Player player) {
        visibleTo.add(player.getUniqueId());
    }

    public void destroy(Player player) {
        visibleTo.remove(player.getUniqueId());
    }
}
