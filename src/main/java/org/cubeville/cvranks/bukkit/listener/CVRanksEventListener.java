package org.cubeville.cvranks.bukkit.listener;

import java.util.Iterator;
import java.util.Random;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.cubeville.cvranks.bukkit.CVRanksPlugin;
import org.jetbrains.annotations.NotNull;

public final class CVRanksEventListener implements Listener {
    
    private final CVRanksPlugin plugin;
    private final Random random;
    
    public CVRanksEventListener(@NotNull final CVRanksPlugin plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(@NotNull final BlockBreakEvent event) {
        
        final Player player = event.getPlayer();
        final ItemStack tool = player.getInventory().getItemInMainHand();
        final Material toolType = tool.getType();
        if (toolType == Material.AIR) {
            return;
        }
        
        final Block block = event.getBlock();
        final Material blockType = block.getType();
        final World world = block.getWorld();
        final Location location = block.getLocation();
        
        switch (blockType) {
            case OAK_LOG:
            case BIRCH_LOG:
            case SPRUCE_LOG:
            case DARK_OAK_LOG:
            case ACACIA_LOG:
            case JUNGLE_LOG:
            case CRIMSON_HYPHAE:
            case WARPED_HYPHAE:
            case STRIPPED_OAK_LOG:
            case STRIPPED_BIRCH_LOG:
            case STRIPPED_SPRUCE_LOG:
            case STRIPPED_DARK_OAK_LOG:
            case STRIPPED_ACACIA_LOG:
            case STRIPPED_JUNGLE_LOG:
            case STRIPPED_CRIMSON_HYPHAE:
            case STRIPPED_WARPED_HYPHAE:
                
                if (player.hasPermission("cvranks.mining.ps.logs") && this.random.nextInt(100) < this.getNormalChance(toolType, "AXE")) {
                    world.dropItemNaturally(location, new ItemStack(blockType));
                    player.sendMessage("§aYou found an extra piece of wood.");
                }
                return;
            default:
                // Fall through
        }
        
        if (tool.containsEnchantment(Enchantment.SILK_TOUCH)) {
            return;
        }
        
        switch (blockType) {
            case DIAMOND_ORE:
            case DEEPSLATE_DIAMOND_ORE:
                if ((player.hasPermission("cvranks.mining.ps") || player.hasPermission("cvranks.mining.ps.ore")) && this.random.nextInt(100) < this.getDiamondChance(toolType)) {
                    world.dropItemNaturally(location, new ItemStack(Material.DIAMOND));
                    player.sendMessage("§aYou found an extra diamond.");
                }
                break;
            case COAL_ORE:
            case DEEPSLATE_COAL_ORE:
                if ((player.hasPermission("cvranks.mining.ps") || player.hasPermission("cvranks.mining.ps.ore")) && this.random.nextInt(100) < this.getNormalChance(toolType, "PICKAXE")) {
                    world.dropItemNaturally(location, new ItemStack(Material.COAL));
                    player.sendMessage("§aYou found an extra piece of coal.");
                    
                    if (player.hasPermission("cvranks.mining.mp") && this.random.nextInt(100) < 2) {
                        world.dropItemNaturally(location, new ItemStack(Material.DIAMOND));
                        player.sendMessage("§aYou found a diamond.");
                    }
                }
                break;
            case NETHER_QUARTZ_ORE:
                if ((player.hasPermission("cvranks.mining.ps") || player.hasPermission("cvranks.mining.ps.ore")) && this.random.nextInt(100) < this.getNormalChance(toolType, "PICKAXE")) {
                    world.dropItemNaturally(location, new ItemStack(Material.QUARTZ));
                    player.sendMessage("§aYou found an extra piece of quartz.");
                }
                break;
            case GRAVEL:
                if ((player.hasPermission("cvranks.mining.ps") || player.hasPermission("cvranks.mining.ps.flint")) && this.random.nextInt(100) < this.getNormalChance(toolType, "SHOVEL")) {
                    world.dropItemNaturally(location, new ItemStack(Material.FLINT));
                    player.sendMessage("§aYou found a piece of flint.");
                }
            default:
                // Sad, no drops for you.
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockDropItem(@NotNull final BlockDropItemEvent event) {
        
        final BlockState blockState = event.getBlockState();
        final Material blockType = blockState.getType();
        
        switch (blockType) {
            case IRON_ORE:
            case DEEPSLATE_IRON_ORE:
            case GOLD_ORE:
            case DEEPSLATE_GOLD_ORE:
            case COPPER_ORE:
            case DEEPSLATE_COPPER_ORE:
                break;
            default:
                return;
        }
        
        final Player player = event.getPlayer();
        final UUID playerId = player.getUniqueId();
        final ItemStack tool = player.getInventory().getItemInMainHand();
        if (tool.containsEnchantment(Enchantment.SILK_TOUCH)) {
            return;
        }
        
        final boolean smelt = this.plugin.isInstaSmeltEnabled(playerId);
        final World world = blockState.getWorld();
        final Location location = blockState.getLocation();
        
        final Iterator<Item> iterator = event.getItems().iterator();
        while (iterator.hasNext()) {
            
            final ItemStack drop = iterator.next().getItemStack();
            int dropAmount = drop.getAmount();
            final Material dropType = drop.getType();
            final Material newDropType;
            final String newDropName;
            
            switch (dropType) {
                case RAW_IRON:
                    newDropType = smelt ? Material.IRON_INGOT : dropType;
                    newDropName = smelt ? "iron ingot" : "piece of raw iron";
                    break;
                case RAW_GOLD:
                    newDropType = smelt ? Material.GOLD_INGOT : dropType;
                    newDropName = smelt ? "gold ingot" : "piece of raw gold";
                    break;
                case RAW_COPPER:
                    newDropType = smelt ? Material.COPPER_INGOT : dropType;
                    newDropName = smelt ? "copper ingot" : "piece of raw copper";
                    break;
                default:
                    continue;
            }
            
            if (player.hasPermission("cvranks.mining.mp") && this.random.nextInt(100) < 15) {
                player.giveExp(1);
                dropAmount++;
                player.sendMessage("§aYou have found an extra " + newDropName + ".");
            }
            
            world.dropItemNaturally(location, new ItemStack(newDropType, dropAmount));
            iterator.remove();
        }
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
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(@NotNull final EntityDeathEvent event) {
        
        final LivingEntity entity = event.getEntity();
        final Player killer = entity.getKiller();
        if (killer == null || !killer.hasPermission("cvranks.leatherworker")) {
            return;
        }
        
        switch (entity.getType()) {
            case COW:
            case MUSHROOM_COW:
            case HORSE:
            case DONKEY:
            case MULE:
            case LLAMA:
                break;
            default:
                return;
        }
        
        entity.getWorld().dropItemNaturally(entity.getLocation(), new ItemStack(Material.LEATHER));
        killer.sendMessage("§aYou got an extra piece of leather.");
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerCommandSend(@NotNull final PlayerCommandSendEvent event) {
        event.getCommands().removeAll(this.plugin.onPlayerCommandSend(event.getPlayer()));
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(@NotNull final PlayerDeathEvent event) {
        
        final Player player = event.getEntity();
        final UUID playerId = player.getUniqueId();
        
        final boolean keepExperience;
        if (player.hasPermission("cvranks.death.te.admin")) {
            keepExperience = true;
        } else if (player.hasPermission("cvranks.death.te") && this.plugin.getXpertWaitTime(playerId) == 0L) {
            keepExperience = true;
            this.plugin.xpertUsed(playerId);
        } else {
            keepExperience = false;
        }
        
        if (keepExperience) {
            event.setKeepLevel(true);
            event.setDroppedExp(0);
            player.sendMessage("§aYour Xpert ability has been triggered, and you have kept your XP.");
        }
        
        final boolean keepInventory;
        if (player.hasPermission("cvranks.death.ks.admin")) {
            keepInventory = true;
        } else if (player.hasPermission("cvranks.death.ks") && this.plugin.getKeepsakeWaitTime(playerId) == 0L) {
            keepInventory = true;
            this.plugin.keepsakeUsed(playerId);
        } else {
            keepInventory = false;
        }
        
        if (keepInventory) {
            event.setKeepInventory(true);
            event.getDrops().clear();
            
            final Iterator<ItemStack> iterator = player.getInventory().iterator();
            while (iterator.hasNext()) {
                final ItemStack item = iterator.next();
                if (item != null && item.getEnchantmentLevel(Enchantment.VANISHING_CURSE) > 0) {
                    iterator.remove();
                }
            }
            
            player.sendMessage("§aYour Keepsake ability has been triggered, and you have kept your inventory.");
        }
        
        this.plugin.addPendingDeathHoundNotification(playerId);
        this.plugin.addDeathLocation(playerId, player.getLocation());
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(@NotNull final PlayerJoinEvent event) {
        this.plugin.checkResetNotifications(event.getPlayer().getUniqueId());
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(@NotNull final PlayerRespawnEvent event) {
        
        final UUID respawnPlayerId = event.getPlayer().getUniqueId();
        this.plugin.disableNightStalker(respawnPlayerId);
        this.plugin.disableScuba(respawnPlayerId);
    }
    
    private int getNormalChance(@NotNull final Material toolType, @NotNull final String toolEndsWith) {
        
        final String[] toolData = toolType.name().split("_");
        if (toolData.length != 2) {
            return 0;
        }
        
        if (!toolData[1].equalsIgnoreCase(toolEndsWith)) {
            return 0;
        }
        
        switch (toolData[0]) {
            case "STONE":
                return 4;
            case "IRON":
                return 8;
            case "DIAMOND":
                return 16;
            case "NETHERITE":
                return 20;
            case "GOLDEN":
                return 24;
            default:
                return 0;
        }
    }
    
    private int getDiamondChance(@NotNull final Material toolType) {
        switch (toolType) {
            case DIAMOND_PICKAXE:
            case NETHERITE_PICKAXE:
                return 8;
            default:
                return 0;
        }
    }
}
