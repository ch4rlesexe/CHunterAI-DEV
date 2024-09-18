package org.charlie.cHunterAI;

import net.citizensnpcs.api.event.NPCDamageEvent;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.util.PlayerAnimation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class HunterTrait extends Trait implements Listener {
    private Player target;
    private int health = 20; // Hunter's health

    public HunterTrait() {
        super("HunterTrait");
        Bukkit.getPluginManager().registerEvents(this, Bukkit.getPluginManager().getPlugin("CHunterAI"));
    }

    public void setTarget(Player target) {
        this.target = target;
        startHunting();
    }

    private void startHunting() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (npc == null || npc.getEntity() == null || target == null || !target.isOnline()) {
                    this.cancel();
                    return;
                }

                // Set the NPC's health
                if (npc.getEntity() instanceof LivingEntity) {
                    LivingEntity livingNPC = (LivingEntity) npc.getEntity();
                    livingNPC.setHealth(health); // Update the NPC's health
                }

                // Persistent aggro: Continuously navigate towards the player
                npc.getNavigator().getDefaultParameters().speedModifier(1.5f);
                npc.getNavigator().setTarget(target.getLocation());

                // Elevation difference handling
                double heightDifference = target.getLocation().getY() - npc.getEntity().getLocation().getY();

                if (heightDifference > 2) {
                    // Player is above, build upwards
                    buildUpwards();
                } else if (heightDifference < -2) {
                    // Player is below, break blocks downward
                    breakBlocksTowardsPlayer();
                } else {
                    // Handle obstacles if on roughly the same level
                    navigateThroughObstacles();
                }

                // Custom attack range (3 blocks)
                if (npc.getEntity().getLocation().distance(target.getLocation()) < 3) {
                    target.damage(2.0); // Adjust damage as needed
                    PlayerAnimation.ARM_SWING.play((Player) npc.getEntity()); // Visual attack animation
                }
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("CHunterAI"), 0L, 20L); // Runs every second
    }

    private void buildUpwards() {
        // Place a block below the hunter's feet to build upwards
        Block blockBelow = npc.getEntity().getLocation().add(0, -1, 0).getBlock();
        if (blockBelow.getType() == Material.AIR) {
            blockBelow.setType(Material.COBBLESTONE); // Place a block below
            Vector jumpVector = new Vector(0, 1, 0);
            npc.getEntity().setVelocity(jumpVector); // Make the NPC jump onto the block
        }
    }

    private void breakBlocksTowardsPlayer() {
        // Use ray tracing to find blocks between the NPC and the target player
        Vector direction = target.getLocation().toVector().subtract(npc.getEntity().getLocation().toVector()).normalize();
        for (int i = 1; i < 3; i++) { // Check the next few blocks in the direction of the player
            Block legBlock = npc.getEntity().getLocation().add(direction.clone().multiply(i)).getBlock();
            Block headBlock = npc.getEntity().getLocation().add(direction.clone().multiply(i)).add(0, 1, 0).getBlock();
            // Break both leg and head blocks to clear the path
            if (!legBlock.isPassable() && legBlock.getType() != Material.AIR) {
                legBlock.breakNaturally();
            }
            if (!headBlock.isPassable() && headBlock.getType() != Material.AIR) {
                headBlock.breakNaturally();
            }
        }
    }

    private void navigateThroughObstacles() {
        // Check for blocks directly in front (leg level and head level)
        Block blockInFrontLeg = npc.getEntity().getLocation().add(npc.getEntity().getLocation().getDirection()).getBlock();
        Block blockInFrontHead = npc.getEntity().getLocation().add(npc.getEntity().getLocation().getDirection()).add(0, 1, 0).getBlock();

        // Break the blocks in front if they are obstacles
        if (!blockInFrontLeg.isPassable() && blockInFrontLeg.getType() != Material.AIR) {
            blockInFrontLeg.breakNaturally();
            return; // Stop further execution this tick to avoid multiple block actions simultaneously
        }
        if (!blockInFrontHead.isPassable() && blockInFrontHead.getType() != Material.AIR) {
            blockInFrontHead.breakNaturally();
            return;
        }

        // Check if there is a gap to cross, place blocks to reach the player
        Block blockBelow = npc.getEntity().getLocation().add(0, -1, 0).getBlock();
        if (blockBelow.getType() == Material.AIR) {
            blockBelow.setType(Material.COBBLESTONE); // Place a block below to bridge a gap
        }
    }

    @EventHandler
    public void onNPCDamage(NPCDamageEvent event) {
        if (event.getNPC() != this.getNPC()) return;

        // Handle NPC taking damage
        health -= event.getDamage();
        if (health <= 0) {
            // NPC dies
            npc.despawn();
            npc.getEntity().getWorld().dropItemNaturally(npc.getEntity().getLocation(), new ItemStack(Material.STONE));
        }
    }

    @EventHandler
    public void onNPCDeath(NPCDeathEvent event) {
        if (event.getNPC() != this.getNPC()) return;

        // Additional death logic if needed
    }
}
