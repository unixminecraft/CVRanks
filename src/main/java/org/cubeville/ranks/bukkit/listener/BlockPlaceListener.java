package org.cubeville.ranks.bukkit.listener;

import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.cubeville.ranks.bukkit.CVRanksPlugin;
import org.jetbrains.annotations.NotNull;

public final class BlockPlaceListener implements Listener {
    
    private final CVRanksPlugin plugin;
    
    public BlockPlaceListener(@NotNull final CVRanksPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(@NotNull final BlockPlaceEvent event) {
        
        final UUID playerId = event.getPlayer().getUniqueId();
        final Block block = event.getBlockPlaced();
        
        switch (block.getType()) {
            case COBBLESTONE:
                if (this.plugin.isStoneMasonEnabled(playerId)) {
                    block.setType(Material.STONE);
                }
                break;
            case DIRT:
                if (this.plugin.isMushGardenerEnabled(playerId) || this.plugin.isMiniRankMyceliumEnabled(playerId)) {
                    block.setType(Material.MYCELIUM);
                }
                break;
            case CLAY:
                if (this.plugin.isBrickLayerEnabled(playerId)) {
                    block.setType(Material.BRICKS);
                }
                break;
            case SAND:
                if (this.plugin.isMasterCarpenterEnabled(playerId) || this.plugin.isMiniRankGlassEnabled(playerId)) {
                    block.setType(Material.GLASS);
                }
                break;
            case SOUL_SAND:
                if (this.plugin.isMasterCarpenterEnabled(playerId) || this.plugin.isMiniRankObsidianEnabled(playerId)) {
                    block.setType(Material.OBSIDIAN);
                }
                break;
            default:
                // Do nothing.
        }
    }
}
