package org.charlie.cHunterAI;

import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class HunterListener implements Listener {

    @EventHandler
    public void onHunterDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Villager) {
            event.getEntity().getWorld().strikeLightningEffect(event.getEntity().getLocation());
            event.getDrops().clear(); // Clear drops if needed
        }
    }
}
