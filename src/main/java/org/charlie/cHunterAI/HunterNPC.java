package org.charlie.cHunterAI;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.LookClose; // Use this import
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class HunterNPC {
    private final NPC npc;
    private final Player target;

    public HunterNPC(Location spawnLocation, Player target) {
        this.target = target;

        // Create an NPC using Citizens
        npc = CitizensAPI.getNPCRegistry().createNPC(org.bukkit.entity.EntityType.PLAYER, "Hunter");
        npc.spawn(spawnLocation);

        // Add custom trait for hunting behavior
        npc.addTrait(HunterTrait.class);
        npc.getOrAddTrait(HunterTrait.class).setTarget(target);

        // Make the NPC look at the player
        npc.getOrAddTrait(LookClose.class).lookClose(true);
    }
}
