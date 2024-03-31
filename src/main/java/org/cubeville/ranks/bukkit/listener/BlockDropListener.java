package org.cubeville.ranks.bukkit.listener;

import java.util.Random;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.cubeville.ranks.bukkit.CVRanksPlugin;
import org.jetbrains.annotations.NotNull;

public final class BlockDropListener implements Listener {
    
    private final CVRanksPlugin plugin;
    private final Random random;
    
    public BlockDropListener(@NotNull final CVRanksPlugin plugin) {
        
        this.plugin = plugin;
        this.random = new Random();
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockDropItem(@NotNull final BlockDropItemEvent event) {
        
        final BlockState blockState = event.getBlockState();
        final Material blockType = blockState.getType();
        
        switch (blockType) {
            
            // Wood
            case OAK_LOG:
            case BIRCH_LOG:
            case SPRUCE_LOG:
            case DARK_OAK_LOG:
            case ACACIA_LOG:
            case JUNGLE_LOG:
            case CRIMSON_STEM:
            case WARPED_STEM:
            case CHERRY_LOG:
            case MANGROVE_LOG:
            case STRIPPED_OAK_LOG:
            case STRIPPED_BIRCH_LOG:
            case STRIPPED_SPRUCE_LOG:
            case STRIPPED_DARK_OAK_LOG:
            case STRIPPED_ACACIA_LOG:
            case STRIPPED_JUNGLE_LOG:
            case STRIPPED_CRIMSON_STEM:
            case STRIPPED_WARPED_STEM:
            case STRIPPED_CHERRY_LOG:
            case STRIPPED_MANGROVE_LOG:
                this.onBlockDropItemWood(event);
                break;
            
            // "Normal" Ore & Gravel
            case DIAMOND_ORE:
            case DEEPSLATE_DIAMOND_ORE:
            case COAL_ORE:
            case DEEPSLATE_COAL_ORE:
            case NETHER_QUARTZ_ORE:
            case GRAVEL:
                this.onBlockDropItemOreNormal(event);
                break;
            
            // "Smeltable" Ore
            case IRON_ORE:
            case DEEPSLATE_IRON_ORE:
            case GOLD_ORE:
            case DEEPSLATE_GOLD_ORE:
            case COPPER_ORE:
            case DEEPSLATE_COPPER_ORE:
                this.onBlockDropItemOreSmeltable(event);
                break;
            
            // Crops
            case WHEAT:
            case CARROTS:
            case POTATOES:
            case BEETROOTS:
            case NETHER_WART:
                this.onBlockDropItemCrops(event);
                break;
            
            // No further processing
            default:
                // Welp, no processing now.
        }
    }
    
    /**
     * Performs the event handling for the {@link BlockDropItemEvent} for
     * wood-based {@link Block Blocks}.
     * 
     * @param event The {@link BlockDropItemEvent}.
     */
    private void onBlockDropItemWood(@NotNull final BlockDropItemEvent event) {
        
        for (final Item item : event.getItems()) {
            
            switch (item.getItemStack().getType()) {
                case OAK_LOG:
                case BIRCH_LOG:
                case SPRUCE_LOG:
                case DARK_OAK_LOG:
                case ACACIA_LOG:
                case JUNGLE_LOG:
                case CRIMSON_STEM:
                case WARPED_STEM:
                case CHERRY_LOG:
                case MANGROVE_LOG:
                case STRIPPED_OAK_LOG:
                case STRIPPED_BIRCH_LOG:
                case STRIPPED_SPRUCE_LOG:
                case STRIPPED_DARK_OAK_LOG:
                case STRIPPED_ACACIA_LOG:
                case STRIPPED_JUNGLE_LOG:
                case STRIPPED_CRIMSON_STEM:
                case STRIPPED_WARPED_STEM:
                case STRIPPED_CHERRY_LOG:
                case STRIPPED_MANGROVE_LOG:
                    break;
                default:
                    continue;
            }
            
            // Found a matching Item drop (wood) if this point has been reached.
            // The attempt to drop an extra piece will only occur 1 time per event call (per block break)
            
            final Player player = event.getPlayer();
            final UUID playerId = player.getUniqueId();
            final Material toolType = player.getInventory().getItemInMainHand().getType();
            
            if (player.hasPermission("cvranks.mining.ps.logs") && this.random.nextInt(100) < this.getNormalChance(toolType, "AXE")) {
                final ItemStack drop = item.getItemStack();
                drop.setAmount(drop.getAmount() + 1);
                if (!this.plugin.isNotifyWoodDisabled(playerId)) {
                    player.sendMessage("§aYou found an extra piece of wood.");
                }
            }
            
            break;
        }
    }
    
    /**
     * Performs the event handling for the {@link BlockDropItemEvent} for
     * normal ore-based {@link Block Blocks} and gravel.
     * 
     * @param event The {@link BlockDropItemEvent}.
     */
    private void onBlockDropItemOreNormal(@NotNull final BlockDropItemEvent event) {
        
        final Player player = event.getPlayer();
        final UUID playerId = player.getUniqueId();
        final ItemStack tool = player.getInventory().getItemInMainHand();
        final Material toolType = tool.getType();
        
        if (tool.containsEnchantment(Enchantment.SILK_TOUCH)) {
            return;
        }
        
        final BlockState blockState = event.getBlockState();
        final Material blockType = blockState.getType();
        
        boolean processedDiamond = false;
        boolean processedCoal = false;
        boolean processedQuartz = false;
        boolean processedGravel = false;
        boolean addBonusDiamond = false;
        
        for (final Item item : event.getItems()) {
            
            final ItemStack drop = item.getItemStack();
            final Material dropType = drop.getType();
            
            boolean addedDrop = false;
            String dropMessage = "";
            boolean messageDisabled = false;
            
            if ((blockType == Material.DIAMOND_ORE || blockType == Material.DEEPSLATE_DIAMOND_ORE) && dropType == Material.DIAMOND && !processedDiamond) {
                
                processedDiamond = true;
                if ((player.hasPermission("cvranks.mining.ps") || player.hasPermission("cvranks.mining.ps.ore")) && this.random.nextInt(100) < this.getDiamondChance(toolType)) {
                    addedDrop = true;
                    dropMessage = "§aYou found an extra diamond.";
                    messageDisabled = this.plugin.isNotifyDiamondDisabled(playerId);
                }
                
            } else if ((blockType == Material.COAL_ORE || blockType == Material.DEEPSLATE_COAL_ORE) && dropType == Material.COAL && !processedCoal) {
                
                processedCoal = true;
                if ((player.hasPermission("cvranks.mining.ps") || player.hasPermission("cvranks.mining.ps.ore")) && this.random.nextInt(100) < this.getNormalChance(toolType, "PICKAXE")) {
                    addedDrop = true;
                    dropMessage = "§aYou found an extra piece of coal.";
                    messageDisabled = this.plugin.isNotifyCoalDisabled(playerId);
                }
                if (player.hasPermission("cvranks.mining.mp") && this.random.nextInt(100) < 2) {
                    addBonusDiamond = true;
                    messageDisabled = this.plugin.isNotifyCoalDisabled(playerId);
                }
                
            } else if (blockType == Material.NETHER_QUARTZ_ORE && dropType == Material.QUARTZ && !processedQuartz) {
                
                processedQuartz = true;
                if ((player.hasPermission("cvranks.mining.ps") || player.hasPermission("cvranks.mining.ps.ore")) && this.random.nextInt(100) < this.getNormalChance(toolType, "PICKAXE")) {
                    addedDrop = true;
                    dropMessage = "§aYou found an extra piece of quartz.";
                    messageDisabled = this.plugin.isNotifyQuartzDisabled(playerId);
                }
                
            } else if (blockType == Material.GRAVEL && (dropType == Material.FLINT || dropType == Material.GRAVEL) && !processedGravel) {
                
                processedGravel = true;
                if ((player.hasPermission("cvranks.mining.ps") || player.hasPermission("cvranks.mining.ps.flint")) && this.random.nextInt(100) < this.getNormalChance(toolType, "SHOVEL")) {
                    addedDrop = true;
                    dropMessage = "§aYou found a piece of flint.";
                    messageDisabled = this.plugin.isNotifyFlintDisabled(playerId);
                }
                
            } else {
                continue;
            }
            
            if (addedDrop) {
                
                if (!messageDisabled) {
                    player.sendMessage(dropMessage);
                }
                
                if (blockType == Material.GRAVEL) {
                    // Gotta specifically drop flint, can't just add 1, otherwise it may drop extra gravel
                    final World world = blockState.getWorld();
                    final Location location = blockState.getLocation();
                    this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> world.dropItemNaturally(location, new ItemStack(Material.FLINT)), 1L);
                } else {
                    // Just add 1 of the drop
                    drop.setAmount(drop.getAmount() + 1);
                }
            }
        }
        
        if (addBonusDiamond) {
            final World world = blockState.getWorld();
            final Location location = blockState.getLocation();
            this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                if (!this.plugin.isNotifyCoalDisabled(playerId)) {
                    player.sendMessage("§aYou found a diamond.");
                }
                world.dropItemNaturally(location, new ItemStack(Material.DIAMOND));
            }, 1L);
        }
    }
    
    /**
     * Performs the event handling for the {@link BlockDropItemEvent} for
     * smeltable ore-based {@link Block Blocks}.
     * 
     * @param event The {@link BlockDropItemEvent}.
     */
    private void onBlockDropItemOreSmeltable(@NotNull final BlockDropItemEvent event) {
        
        final Player player = event.getPlayer();
        final UUID playerId = player.getUniqueId();
        final ItemStack tool = player.getInventory().getItemInMainHand();
        final String[] toolData = tool.getType().name().split("_");
        
        if (toolData.length != 2 || !toolData[1].equals("PICKAXE")) {
            return;
        }
        if (tool.containsEnchantment(Enchantment.SILK_TOUCH)) {
            return;
        }
        
        final BlockState blockState = event.getBlockState();
        final Material blockType = blockState.getType();
        
        boolean processedIron = false;
        boolean processedGold = false;
        boolean processedCopper = false;
        final boolean smelt = this.plugin.isInstaSmeltEnabled(playerId);
        
        for (final Item item : event.getItems()) {
            
            final ItemStack drop = item.getItemStack();
            final Material dropType = drop.getType();
            
            boolean addedDrop = false;
            boolean messageDisabled = false;
            final Material newDropType;
            final String newDropName;
            
            if ((blockType == Material.IRON_ORE || blockType == Material.DEEPSLATE_IRON_ORE) && dropType == Material.RAW_IRON) {
                
                if (player.hasPermission("cvranks.mining.mp") && this.random.nextInt(100) < 15 && !processedIron) {
                    addedDrop = true;
                    messageDisabled = this.plugin.isNotifyIronDisabled(playerId);
                }
                
                processedIron = true;
                newDropType = smelt ? Material.IRON_INGOT : Material.RAW_IRON;
                newDropName = smelt ? "iron ingot" : "piece of raw iron";
                
            } else if ((blockType == Material.GOLD_ORE || blockType == Material.DEEPSLATE_GOLD_ORE) && dropType == Material.RAW_GOLD) {
                
                if (player.hasPermission("cvranks.mining.mp") && this.random.nextInt(100) < 15 && !processedGold) {
                    addedDrop = true;
                    messageDisabled = this.plugin.isNotifyGoldDisabled(playerId);
                }
                
                processedGold = true;
                newDropType = smelt ? Material.GOLD_INGOT : Material.RAW_GOLD;
                newDropName = smelt ? "gold ingot" : "piece of raw gold";
                
            } else if ((blockType == Material.COPPER_ORE || blockType == Material.DEEPSLATE_COPPER_ORE) && dropType == Material.RAW_COPPER) {
                
                if (player.hasPermission("cvranks.mining.mp") && this.random.nextInt(100) < 15 && !processedCopper) {
                    addedDrop = true;
                    messageDisabled = this.plugin.isNotifyCopperDisabled(playerId);
                }
                
                processedCopper = true;
                newDropType = smelt ? Material.COPPER_INGOT : Material.RAW_COPPER;
                newDropName = smelt ? "copper ingot" : "piece of raw copper";
                
            } else {
                continue;
            }
            
            if (addedDrop) {
                if (!messageDisabled) {
                    player.sendMessage("§aYou found an extra " + newDropName + ".");
                }
                drop.setAmount(drop.getAmount() + 1);
            }
            
            if (dropType != newDropType) {
                drop.setType(newDropType);
                player.giveExp(drop.getAmount());
            }
        }
    }
    
    /**
     * Performs the event handling for the {@link BlockDropItemEvent} for
     * crop-based {@link Block Blocks}.
     * 
     * @param event The {@link BlockDropItemEvent}.
     */
    private void onBlockDropItemCrops(@NotNull final BlockDropItemEvent event) {
        
        final Player player = event.getPlayer();
        final UUID playerId = player.getUniqueId();
        
        if (!this.plugin.isMushGardenerEnabled(playerId)) {
            return;
        }
        
        final BlockState blockState = event.getBlockState();
        final Material blockType = blockState.getType();
        final Material blockBelowType = blockState.getWorld().getBlockAt(blockState.getX(), blockState.getY() - 1, blockState.getZ()).getType();
        
        if (blockBelowType != Material.FARMLAND && blockBelowType != Material.SOUL_SAND) {
            return;
        }
        
        boolean replant = false;
        
        for (final Item item : event.getItems()) {
            
            final ItemStack drop = item.getItemStack();
            final Material dropType = drop.getType();
            
            if (blockType == Material.WHEAT && dropType == Material.WHEAT_SEEDS && blockBelowType == Material.FARMLAND) {
                replant = true;
            } else if (blockType == Material.CARROTS && dropType == Material.CARROT && blockBelowType == Material.FARMLAND) {
                replant = true;
            } else if (blockType == Material.POTATOES && dropType == Material.POTATO && blockBelowType == Material.FARMLAND) {
                replant = true;
            } else if (blockType == Material.BEETROOTS && dropType == Material.BEETROOT_SEEDS && blockBelowType == Material.FARMLAND) {
                replant = true;
            } else if (blockType == Material.NETHER_WART && dropType == Material.NETHER_WART && blockBelowType == Material.SOUL_SAND) {
                replant = true;
            } else {
                continue;
            }
            
            drop.setAmount(drop.getAmount() - 1);
            break;
        }
        
        if (replant) {
            event.getBlock().setType(blockType);
        }
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
