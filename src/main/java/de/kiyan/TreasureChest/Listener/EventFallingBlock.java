package de.kiyan.TreasureChest.Listener;

import de.kiyan.TreasureChest.TChest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class EventFallingBlock implements Listener {
    @EventHandler
    public void EntityChangeBlock(EntityChangeBlockEvent event) {
        if (TChest.tchestList == null) return;

        for (TChest tChest : TChest.tchestList) {
            for (FallingBlock ent : tChest.getFallBlocks().values()) {
                if (ent.getEntityId() == event.getEntity().getEntityId()) {
                    event.setCancelled(true);
                }
            }
        }
    }
}

