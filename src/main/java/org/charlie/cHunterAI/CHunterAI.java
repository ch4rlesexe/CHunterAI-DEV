package org.charlie.cHunterAI;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.plugin.java.JavaPlugin;

public final class CHunterAI extends JavaPlugin {

    @Override
    public void onEnable() {
        // Register the Hunter trait
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(HunterTrait.class).withName("HunterTrait"));

        // Register commands and events
        this.getCommand("spawnhunter").setExecutor(new HunterCommand(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
