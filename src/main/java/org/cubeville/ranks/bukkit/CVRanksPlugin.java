package org.cubeville.ranks.bukkit;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.cubeville.ranks.bukkit.command.build.BrickLayerCommand;
import org.cubeville.ranks.bukkit.command.build.MasterCarpenterCommand;
import org.cubeville.ranks.bukkit.command.build.MushGardenerCommand;
import org.cubeville.ranks.bukkit.command.build.StoneMasonCommand;
import org.cubeville.ranks.bukkit.command.death.DeathHoundCommand;
import org.cubeville.ranks.bukkit.command.death.KeepsakeCommand;
import org.cubeville.ranks.bukkit.command.death.RespawnCommand;
import org.cubeville.ranks.bukkit.command.death.XpertCommand;
import org.cubeville.ranks.bukkit.command.general.RankCommand;
import org.cubeville.ranks.bukkit.command.mining.InstaSmeltCommand;
import org.cubeville.ranks.bukkit.command.mining.NightStalkerCommand;
import org.cubeville.ranks.bukkit.command.mining.ProspectorCommand;
import org.cubeville.ranks.bukkit.command.other.LeatherWorkerCommand;
import org.cubeville.ranks.bukkit.command.other.MiniRankCommand;
import org.cubeville.ranks.bukkit.command.other.ScubaCommand;
import org.cubeville.ranks.bukkit.command.other.WoodCommand;
import org.cubeville.ranks.bukkit.command.service.DoctorCommand;
import org.cubeville.ranks.bukkit.command.service.LevelCommand;
import org.cubeville.ranks.bukkit.command.service.RepairCommand;
import org.cubeville.ranks.bukkit.command.service.ShopkeeperCommand;
import org.cubeville.ranks.bukkit.listener.BlockDropListener;
import org.cubeville.ranks.bukkit.listener.BlockPlaceListener;
import org.cubeville.ranks.bukkit.listener.DeathListener;
import org.cubeville.ranks.bukkit.listener.EntityListener;
import org.cubeville.ranks.bukkit.listener.GeneralListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

public final class CVRanksPlugin extends JavaPlugin {
    
    private static final String ABILITY_READY_DOCTOR = "§bYour doctor ability is ready to use.";
    private static final String ABILITY_READY_REPAIR = "§bYour repair ability is ready to use.";
    private static final String ABILITY_READY_XPERT = "§bYou will keep your XP upon your next death.";
    private static final String ABILITY_READY_KEEPSAKE = "§bYou will keep your inventory upon your next death.";
    private static final String ABILITY_READY_DEATH_HOUND = "§bYour death hound ability is ready to use.";
    private static final String ABILITY_READY_RESPAWN = "§bYour respawn ability is ready to use.";
    
    private static final String DISABLE_NOTIFY_DOCTOR = "disable_notify_doctor";
    private static final String DISABLE_NOTIFY_COAL = "disable_notify_coal";
    private static final String DISABLE_NOTIFY_QUARTZ = "disable_notify_quartz";
    private static final String DISABLE_NOTIFY_DIAMOND = "disable_notify_diamond";
    private static final String DISABLE_NOTIFY_FLINT = "disable_notify_flint";
    private static final String DISABLE_NOTIFY_IRON = "disable_notify_iron";
    private static final String DISABLE_NOTIFY_GOLD = "disable_notify_gold";
    private static final String DISABLE_NOTIFY_COPPER = "disable_notify_copper";
    private static final String DISABLE_NOTIFY_DEATH_HOUND = "disable_notify_death_hound";
    private static final String DISABLE_NOTIFY_WOOD = "disable_notify_wood";
    private static final String DISABLE_NOTIFY_LEATHER = "disable_notify_leather";
    
    private static final String ACTIVE_INSTA_SMELT = "active_insta_smelt";
    private static final String ACTIVE_NIGHT_STALKER = "active_night_stalker";
    private static final String ACTIVE_STONE_MASON = "active_stone_mason";
    private static final String ACTIVE_MUSH_GARDENER = "active_mush_gardener";
    private static final String ACTIVE_BRICK_LAYER = "active_brick_layer";
    private static final String ACTIVE_MASTER_CARPENTER = "active_master_carpenter";
    private static final String ACTIVE_SCUBA = "active_scuba";
    private static final String ACTIVE_MINI_RANK_MYCELIUM = "active_mini_rank_mycelium";
    private static final String ACTIVE_MINI_RANK_GLASS = "active_mini_rank_glass";
    private static final String ACTIVE_MINI_RANK_OBSIDIAN = "active_mini_rank_obsidian";
    
    private Logger logger;
    
    private File dataFolder;
    private File enchantmentFile;
    private File disabledNotificationsFile;
    private File activeRanksFile;
    
    private long uptime; // Used for notifying when abilities can be used again.
    private Server server;
    private BukkitScheduler scheduler;
    
    /* EXTENDED ENCHANTMENTS */
    private Map<Enchantment, ExtendedEnchantment> byEnchantment;
    private Map<String, ExtendedEnchantment> byName;
    
    /* GENERIC / NON-PERK RELATED */
    private Map<UUID, Location> deathLocations;
    private Set<UUID> pendingDeathHoundNotifications;
    
    /* SERVICE CHAIN */
    private Map<UUID, Long> doctorLastUsed;
    private Set<UUID> notifyDoctorReset;
    private Set<UUID> notifyDoctorDisabled;
    private Map<UUID, Long> repairLastUsed;
    private Set<UUID> notifyRepairReset;
    
    /* MINING CHAIN */
    private Set<UUID> notifyCoalDisabled;
    private Set<UUID> notifyQuartzDisabled;
    private Set<UUID> notifyDiamondDisabled;
    private Set<UUID> notifyFlintDisabled;
    private Set<UUID> instaSmeltActive;
    private Set<UUID> notifyIronDisabled;
    private Set<UUID> notifyGoldDisabled;
    private Set<UUID> notifyCopperDisabled;
    private Set<UUID> nightStalkerActive;
    
    /* BUILD CHAIN */
    private Set<UUID> stoneMasonActive;
    private Set<UUID> mushGardenerActive;
    private Set<UUID> brickLayerActive;
    private Set<UUID> masterCarpenterActive;
    
    /* DEATH CHAIN */
    private Map<UUID, Long> xpertLastUsed;
    private Set<UUID> notifyXpertReset;
    private Map<UUID, Long> keepsakeLastUsed;
    private Set<UUID> notifyKeepsakeReset;
    private Map<UUID, Long> deathHoundLastUsed;
    private Set<UUID> notifyDeathHoundReset;
    private Set<UUID> notifyDeathHoundDisabled;
    private Map<UUID, Long> respawnLastUsed;
    private Set<UUID> notifyRespawnReset;
    
    /* NON-CHAIN / OTHER */
    private Set<UUID> notifyWoodDisabled;
    private Set<UUID> scubaActive;
    private Set<UUID> notifyLeatherDisabled;
    private Set<UUID> miniRankMyceliumActive;
    private Set<UUID> miniRankGlassActive;
    private Set<UUID> miniRankObsidianActive;
    
    @Override
    public void onEnable() {
        
        ////////////////////////////
        // GENERAL INITIALIZATION //
        ////////////////////////////
        
        this.logger = this.getLogger();
        
        this.uptime = 0L;
        this.server = this.getServer();
        this.scheduler = this.server.getScheduler();
        
        /*
         * Where able, use ConcurrentHashMaps instead of HashMaps as neither the
         * key nor value can be null.
         */
        
        /* EXTENDED ENCHANTMENTS */
        this.byEnchantment = new ConcurrentHashMap<Enchantment, ExtendedEnchantment>();
        this.byName = new ConcurrentHashMap<String, ExtendedEnchantment>();
        
        /* GENERIC / NON-PERK RELATED */
        this.deathLocations = new ConcurrentHashMap<UUID, Location>();
        this.pendingDeathHoundNotifications = new HashSet<UUID>();
        
        /* SERVICE CHAIN */
        this.doctorLastUsed = new ConcurrentHashMap<UUID, Long>();
        this.notifyDoctorReset = new HashSet<UUID>();
        this.notifyDoctorDisabled = new HashSet<UUID>();
        this.repairLastUsed = new ConcurrentHashMap<UUID, Long>();
        this.notifyRepairReset = new HashSet<UUID>();
        
        /* MINING CHAIN */
        this.notifyCoalDisabled = new HashSet<UUID>();
        this.notifyQuartzDisabled = new HashSet<UUID>();
        this.notifyDiamondDisabled = new HashSet<UUID>();
        this.notifyFlintDisabled = new HashSet<UUID>();
        this.instaSmeltActive = new HashSet<UUID>();
        this.notifyIronDisabled = new HashSet<UUID>();
        this.notifyGoldDisabled = new HashSet<UUID>();
        this.notifyCopperDisabled = new HashSet<UUID>();
        this.nightStalkerActive = new HashSet<UUID>();
        
        /* BUILD CHAIN */
        this.stoneMasonActive = new HashSet<UUID>();
        this.mushGardenerActive = new HashSet<UUID>();
        this.brickLayerActive = new HashSet<UUID>();
        this.masterCarpenterActive = new HashSet<UUID>();
        
        /* DEATH CHAIN */
        this.xpertLastUsed = new ConcurrentHashMap<UUID, Long>();
        this.notifyXpertReset = new HashSet<UUID>();
        this.keepsakeLastUsed = new ConcurrentHashMap<UUID, Long>();
        this.notifyKeepsakeReset = new HashSet<UUID>();
        this.deathHoundLastUsed = new ConcurrentHashMap<UUID, Long>();
        this.notifyDeathHoundReset = new HashSet<UUID>();
        this.notifyDeathHoundDisabled = new HashSet<UUID>();
        this.respawnLastUsed = new ConcurrentHashMap<UUID, Long>();
        this.notifyRespawnReset = new HashSet<UUID>();
        
        /* NON-CHAIN / OTHER */
        this.notifyWoodDisabled = new HashSet<UUID>();
        this.scubaActive = new HashSet<UUID>();
        this.notifyLeatherDisabled = new HashSet<UUID>();
        this.miniRankMyceliumActive = new HashSet<UUID>();
        this.miniRankGlassActive = new HashSet<UUID>();
        this.miniRankObsidianActive = new HashSet<UUID>();
        
        ///////////////////////////
        // CONFIGURATION LOADING //
        ///////////////////////////
        
        this.reloadEnchantments();
        this.reloadDisabledNotifications();
        this.reloadActiveRanks();
        
        ///////////////////////////////
        // RECURRING TASK SCHEDULING //
        ///////////////////////////////
        
        /*
         * UPTIME / NIGHTSTALKER / SCUBA TIMER
         * 
         * This runs every 40 ticks (2 seconds) after a 40 tick delay and
         * performs the following operations:
         * - Adds 40 ticks to the uptime counter
         * - Applies night vision to players with nightstalker enabled
         * - Applies underwater breathing to players with scuba enabled
         *     - These last 2 are scheduled due to conflicts with other plugins,
         *       such as vanish plugins, removing effects when the player
         *       changes states (ex: vanished -> visible).
         */
        this.scheduler.runTaskTimer(this, () -> {
            
            // Increment the uptime counter
            this.uptime += 40L;
            
            // Reactivate anyone with nightstalker active but without the potion effect
            for (final UUID playerId : this.nightStalkerActive) {
                final Player player = this.server.getPlayer(playerId);
                if (player == null || !player.isOnline() || player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                    continue;
                }
                
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1, false, false));
            }
            
            // Reactivate anyone with scuba active but without the potion effect
            for (final UUID playerId : this.scubaActive) {
                final Player player = this.server.getPlayer(playerId);
                if (player == null || !player.isOnline() || player.hasPotionEffect(PotionEffectType.WATER_BREATHING)) {
                    continue;
                }
                
                player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 1, false, false));
            }
        }, 40L, 40L);
        
        /*
         * NOTIFICATION TIMER
         * 
         * This task waits 200 ticks (10 seconds), and then runs every 200 ticks
         * (10 seconds) and notifies the following groups:
         * - Doctor - Ability ready to use again
         * - Death Hound - Ability ready to use again
         * - Respawn - Ability ready to use again
         * - Xpert - XP will be kept on next death
         * - Keepsake - Inventory will be kept on next death
         */
        this.scheduler.runTaskTimer(this, () -> {
            
            // Doctor
            Iterator<Map.Entry<UUID, Long>> iterator = this.doctorLastUsed.entrySet().iterator();
            while (iterator.hasNext()) {
                
                final UUID playerId = iterator.next().getKey();
                if (this.getDoctorWaitTime(playerId) > 0L) {
                    continue;
                }
                
                iterator.remove();
                
                final Player player = this.server.getPlayer(playerId);
                if (player != null && player.isOnline()) {
                    player.sendMessage(ABILITY_READY_DOCTOR);
                } else {
                    this.notifyDoctorReset.add(playerId);
                }
            }
            
            // Repair
            iterator = this.repairLastUsed.entrySet().iterator();
            while (iterator.hasNext()) {
                
                final UUID playerId = iterator.next().getKey();
                if (this.getRepairWaitTime(playerId) > 0L) {
                    continue;
                }
                
                iterator.remove();
                
                final Player player = this.server.getPlayer(playerId);
                if (player != null && player.isOnline()) {
                    player.sendMessage(ABILITY_READY_REPAIR);
                } else {
                    this.notifyRepairReset.add(playerId);
                }
            }
            
            // Xpert
            iterator = this.xpertLastUsed.entrySet().iterator();
            while (iterator.hasNext()) {
                
                final UUID playerId = iterator.next().getKey();
                if (this.getXpertWaitTime(playerId) > 0L) {
                    continue;
                }
                
                iterator.remove();
                
                final Player player = this.server.getPlayer(playerId);
                if (player != null && player.isOnline()) {
                    player.sendMessage(ABILITY_READY_XPERT);
                } else {
                    this.notifyXpertReset.add(playerId);
                }
            }
            
            // Keepsake
            iterator = this.keepsakeLastUsed.entrySet().iterator();
            while (iterator.hasNext()) {
                
                final UUID playerId = iterator.next().getKey();
                if (this.getKeepsakeWaitTime(playerId) > 0L) {
                    continue;
                }
                
                iterator.remove();
                
                final Player player = this.server.getPlayer(playerId);
                if (player != null && player.isOnline()) {
                    player.sendMessage(ABILITY_READY_KEEPSAKE);
                } else {
                    this.notifyKeepsakeReset.add(playerId);
                }
            }
            
            // Death Hound
            iterator = this.deathHoundLastUsed.entrySet().iterator();
            while (iterator.hasNext()) {
                
                final UUID playerId = iterator.next().getKey();
                if (this.getDeathHoundWaitTime(playerId) > 0L) {
                    continue;
                }
                
                iterator.remove();
                
                final Player player = this.server.getPlayer(playerId);
                if (player != null && player.isOnline()) {
                    player.sendMessage(ABILITY_READY_DEATH_HOUND);
                } else {
                    this.notifyDeathHoundReset.add(playerId);
                }
            }
            
            // Respawn
            iterator = this.respawnLastUsed.entrySet().iterator();
            while (iterator.hasNext()) {
                
                final UUID playerId = iterator.next().getKey();
                if (this.getRespawnWaitTime(playerId) > 0L) {
                    continue;
                }
                
                iterator.remove();
                
                final Player player = this.server.getPlayer(playerId);
                if (player != null && player.isOnline()) {
                    player.sendMessage(ABILITY_READY_RESPAWN);
                } else {
                    this.notifyRespawnReset.add(playerId);
                }
            }
        }, 200L, 200L);
        
        //////////////////////////
        // COMMAND REGISTRATION //
        //////////////////////////
        
        this.registerCommand("rank", new RankCommand(this));
        this.registerCommand("shopkeeper", new ShopkeeperCommand(this));
        this.registerCommand("doctor", new DoctorCommand(this));
        this.registerCommand("level", new LevelCommand(this));
        this.registerCommand("repair", new RepairCommand(this));
        this.registerCommand("prospector", new ProspectorCommand(this));
        this.registerCommand("instasmelt", new InstaSmeltCommand(this));
        this.registerCommand("nightstalker", new NightStalkerCommand(this));
        this.registerCommand("stonemason", new StoneMasonCommand(this));
        this.registerCommand("mushgardener", new MushGardenerCommand(this));
        this.registerCommand("bricklayer", new BrickLayerCommand(this));
        this.registerCommand("mastercarpenter", new MasterCarpenterCommand(this));
        this.registerCommand("xpert", new XpertCommand(this));
        this.registerCommand("keepsake", new KeepsakeCommand(this));
        this.registerCommand("deathhound", new DeathHoundCommand(this));
        this.registerCommand("respawn", new RespawnCommand(this));
        this.registerCommand("wood", new WoodCommand(this));
        this.registerCommand("scuba", new ScubaCommand(this));
        this.registerCommand("leatherworker", new LeatherWorkerCommand(this));
        this.registerCommand("minirank", new MiniRankCommand(this));
    
        /////////////////////////////////
        // EVENT LISTENER REGISTRATION //
        /////////////////////////////////
        
        final PluginManager pluginManager = this.server.getPluginManager();
        pluginManager.registerEvents(new BlockDropListener(this), this);
        pluginManager.registerEvents(new BlockPlaceListener(this), this);
        pluginManager.registerEvents(new DeathListener(this), this);
        pluginManager.registerEvents(new EntityListener(this), this);
        pluginManager.registerEvents(new GeneralListener(this), this);
        
        //////////////////////////////////
        // CRAFTING RECIPE REGISTRATION //
        //////////////////////////////////
    
        this.server.addRecipe(new ShapedRecipe(NamespacedKey.minecraft(Material.SADDLE.name().toLowerCase()), new ItemStack(Material.SADDLE)).shape("XXX", "XXX").setIngredient('X', Material.LEATHER));
    }
    
    public void reloadEnchantments() throws RuntimeException {
        
        this.reloadDataFolder();
        this.enchantmentFile = this.reloadFile("enchantments");
        final ConfigurationSection config = this.loadConfig(this.enchantmentFile);
        
        this.logger.log(Level.INFO, "Load Enchantments for Leveling - STARTING");
        
        for (final String enchantmentName : config.getKeys(false)) {
            
            Enchantment enchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(NamespacedKey.minecraft(enchantmentName.toLowerCase()));
            if (enchantment == null) {
                // Backwards compatibility
                enchantment = Enchantment.getByName(enchantmentName);
            }
            if (enchantment == null) {
                this.logger.log(Level.WARNING, "Unable to find enchantment with name " + enchantmentName + ", skipping.");
                continue;
            }
            
            final ConfigurationSection enchantmentConfig = config.getConfigurationSection(enchantmentName);
            if (enchantmentConfig == null) {
                this.logger.log(Level.WARNING, "No enchantment configuration defined for enchantment " + enchantmentName + ", skipping.");
                continue;
            }
            
            final ExtendedEnchantment extendedEnchantment;
            try {
                extendedEnchantment = new ExtendedEnchantment(enchantment, enchantmentConfig);
            } catch (final IllegalArgumentException e) {
                this.logger.log(Level.WARNING, e.getMessage());
                continue;
            }
            
            ExtendedEnchantment check = this.byEnchantment.put(enchantment, extendedEnchantment);
            if (check != null && !check.getEnchantment().getKey().getKey().equals(enchantment.getKey().getKey())) {
                this.logger.log(Level.WARNING, "Duplicate registered by enchantment for " + enchantmentName + ", overwriting with the new one.");
                this.logger.log(Level.WARNING, "Already-registered: " + check.toString());
                this.logger.log(Level.WARNING, "Newly-registered: " + extendedEnchantment.toString());
            }
            
            check = this.byName.put(enchantment.getKey().getKey().toLowerCase(), extendedEnchantment);
            if (check != null && !check.getEnchantment().getKey().getKey().equals(enchantment.getKey().getKey())) {
                logger.log(Level.WARNING, "Duplicate registered by name for " + enchantmentName + ", overwriting with the new one.");
                logger.log(Level.WARNING, "Already-registered: " + check.toString());
                logger.log(Level.WARNING, "Newly-registered: " + extendedEnchantment.toString());
            }
            
            check = this.byName.put(enchantment.getName().toLowerCase(), extendedEnchantment);
            if (check != null && !check.getEnchantment().getKey().getKey().equals(enchantment.getKey().getKey())) {
                logger.log(Level.WARNING, "Duplicate registered by name for " + enchantmentName + ", overwriting with the new one.");
                logger.log(Level.WARNING, "Already-registered: " + check.toString());
                logger.log(Level.WARNING, "Newly-registered: " + extendedEnchantment.toString());
            }
            
            for (final String name : extendedEnchantment.getNames()) {
                
                check = this.byName.put(name.toLowerCase(), extendedEnchantment);
                if (check != null && !check.getEnchantment().getKey().getKey().equals(enchantment.getKey().getKey())) {
                    logger.log(Level.WARNING, "Duplicate registered by name for " + enchantmentName + ", overwriting with the new one.");
                    logger.log(Level.WARNING, "Already-registered: " + check.toString());
                    logger.log(Level.WARNING, "Newly-registered: " + extendedEnchantment.toString());
                }
            }
            
            this.logger.log(Level.INFO, "Successfully registered enchantment " + enchantmentName + ".");
        }
        
        this.logger.log(Level.INFO, "Load Enchantments for Leveling - FINISHED");
    }
    
    public void reloadDisabledNotifications() throws RuntimeException {
        
        this.reloadDataFolder();
        this.disabledNotificationsFile = this.reloadFile("disabled_notifications");
        final ConfigurationSection config = this.loadConfig(this.disabledNotificationsFile);
        
        this.notifyDoctorDisabled.clear();
        this.notifyFlintDisabled.clear();
        this.notifyCoalDisabled.clear();
        this.notifyQuartzDisabled.clear();
        this.notifyDiamondDisabled.clear();
        this.notifyIronDisabled.clear();
        this.notifyGoldDisabled.clear();
        this.notifyCopperDisabled.clear();
        this.notifyDeathHoundDisabled.clear();
        this.notifyWoodDisabled.clear();
        this.notifyLeatherDisabled.clear();
        
        this.notifyDoctorDisabled.addAll(this.getUniqueIds(config.getStringList(DISABLE_NOTIFY_DOCTOR)));
        this.notifyFlintDisabled.addAll(this.getUniqueIds(config.getStringList(DISABLE_NOTIFY_FLINT)));
        this.notifyCoalDisabled.addAll(this.getUniqueIds(config.getStringList(DISABLE_NOTIFY_COAL)));
        this.notifyQuartzDisabled.addAll(this.getUniqueIds(config.getStringList(DISABLE_NOTIFY_QUARTZ)));
        this.notifyDiamondDisabled.addAll(this.getUniqueIds(config.getStringList(DISABLE_NOTIFY_DIAMOND)));
        this.notifyIronDisabled.addAll(this.getUniqueIds(config.getStringList(DISABLE_NOTIFY_IRON)));
        this.notifyGoldDisabled.addAll(this.getUniqueIds(config.getStringList(DISABLE_NOTIFY_GOLD)));
        this.notifyCopperDisabled.addAll(this.getUniqueIds(config.getStringList(DISABLE_NOTIFY_COPPER)));
        this.notifyDeathHoundDisabled.addAll(this.getUniqueIds(config.getStringList(DISABLE_NOTIFY_DEATH_HOUND)));
        this.notifyWoodDisabled.addAll(this.getUniqueIds(config.getStringList(DISABLE_NOTIFY_WOOD)));
        this.notifyLeatherDisabled.addAll(this.getUniqueIds(config.getStringList(DISABLE_NOTIFY_LEATHER)));
    }
    
    public void reloadActiveRanks() throws RuntimeException {
        
        this.reloadDataFolder();
        this.activeRanksFile = this.reloadFile("active_ranks");
        final ConfigurationSection config = this.loadConfig(this.activeRanksFile);
        
        this.instaSmeltActive.clear();
        this.nightStalkerActive.clear();
        this.stoneMasonActive.clear();
        this.mushGardenerActive.clear();
        this.brickLayerActive.clear();
        this.masterCarpenterActive.clear();
        this.scubaActive.clear();
        this.miniRankMyceliumActive.clear();
        this.miniRankGlassActive.clear();
        this.miniRankObsidianActive.clear();
        
        this.instaSmeltActive.addAll(this.getUniqueIds(config.getStringList(ACTIVE_INSTA_SMELT)));
        this.nightStalkerActive.addAll(this.getUniqueIds(config.getStringList(ACTIVE_NIGHT_STALKER)));
        this.stoneMasonActive.addAll(this.getUniqueIds(config.getStringList(ACTIVE_STONE_MASON)));
        this.mushGardenerActive.addAll(this.getUniqueIds(config.getStringList(ACTIVE_MUSH_GARDENER)));
        this.brickLayerActive.addAll(this.getUniqueIds(config.getStringList(ACTIVE_BRICK_LAYER)));
        this.masterCarpenterActive.addAll(this.getUniqueIds(config.getStringList(ACTIVE_MASTER_CARPENTER)));
        this.scubaActive.addAll(this.getUniqueIds(config.getStringList(ACTIVE_SCUBA)));
        this.miniRankMyceliumActive.addAll(this.getUniqueIds(config.getStringList(ACTIVE_MINI_RANK_MYCELIUM)));
        this.miniRankGlassActive.addAll(this.getUniqueIds(config.getStringList(ACTIVE_MINI_RANK_GLASS)));
        this.miniRankObsidianActive.addAll(this.getUniqueIds(config.getStringList(ACTIVE_MINI_RANK_OBSIDIAN)));
    }
    
    private void reloadDataFolder() throws RuntimeException {
        
        this.dataFolder = this.getDataFolder();
        try {
            if (this.dataFolder.exists()) {
                if (!this.dataFolder.isDirectory()) {
                    throw new RuntimeException("Data folder is not a directory: " + this.dataFolder.getPath());
                }
            } else if (!this.dataFolder.mkdirs()) {
                throw new RuntimeException("Data folder not created at " + this.dataFolder.getPath());
            }
        } catch (final SecurityException e) {
            throw new RuntimeException("Unable to validate data folder at " + this.dataFolder.getPath(), e);
        }
    }
    
    @NotNull
    private File reloadFile(@NotNull final String fileName) throws RuntimeException {
        
        final File file = new File(this.dataFolder, fileName + ".yml");
        try {
            if (file.exists()) {
                if (!file.isFile()) {
                    throw new RuntimeException("Config file is not a file: " + file.getPath());
                }
            } else {
                if (!file.createNewFile()) {
                    throw new RuntimeException("Config file not created at " + file.getPath());
                }
                
                final InputStream defaultConfig = this.getResource(file.getName());
                final FileOutputStream outputStream = new FileOutputStream(file);
                final byte[] buffer = new byte[4096];
                int bytesRead;
                
                if (defaultConfig == null) {
                    throw new RuntimeException("No default " + file.getName() + " packaged with CVRanks, possible compilation/build issue.");
                }
                
                while ((bytesRead = defaultConfig.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                
                outputStream.flush();
                outputStream.close();
                defaultConfig.close();
            }
        } catch (final IOException | SecurityException e) {
            throw new RuntimeException("Unable to load config file at" + file.getPath(), e);
        }
        
        return file;
    }
    
    @NotNull
    private YamlConfiguration loadConfig(@NotNull final File file) throws RuntimeException {
        
        final YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (final IOException | InvalidConfigurationException | IllegalArgumentException e) {
            throw new RuntimeException("Unable to load configuration from file at " + file.getPath(), e);
        }
        
        return config;
    }
    
    private void saveConfig(@NotNull final File file, @NotNull final YamlConfiguration config) throws RuntimeException {
        
        try {
            config.save(file);
        } catch (final IOException | IllegalArgumentException e) {
            throw new RuntimeException("Unable to save configuration to file at " + file.getPath(), e);
        }
    }
    
    private void saveUpdatedPlayers(@NotNull final Set<UUID> uniqueIds, @NotNull final File file, @NotNull final String key, final boolean enable, @NotNull final UUID playerId) throws RuntimeException {
        
        final List<String> rawUniqueIds = new ArrayList<String>();
        for (final UUID uniqueId : uniqueIds) {
            rawUniqueIds.add(uniqueId.toString());
        }
        
        final YamlConfiguration config = this.loadConfig(file);
        config.set(key, rawUniqueIds);
        try {
            this.saveConfig(file, config);
        } catch (final RuntimeException e) {
            
            final StringBuilder builder = new StringBuilder();
            builder.append("Unable to ");
            builder.append(enable ? "remove" : "add");
            builder.append(" UUID ").append(playerId.toString()).append(" ");
            builder.append(enable ? "from" : "to");
            builder.append(" ").append(key);
            
            throw new RuntimeException(builder.toString(), e);
        }
    }
    
    @NotNull
    private Set<UUID> getUniqueIds(@NotNull final List<String> rawUniqueIds) throws RuntimeException {
        
        final Set<UUID> uniqueIds = new HashSet<UUID>();
        for (final String rawUniqueId : rawUniqueIds) {
            
            try {
                uniqueIds.add(UUID.fromString(rawUniqueId));
            } catch (final IllegalArgumentException e) {
                throw new RuntimeException("Unable to parse UUID from string value: " + rawUniqueId, e);
            }
        }
        
        return uniqueIds;
    }
    
    private void registerCommand(@NotNull final String commandName, @NotNull final TabExecutor tabExecutor) throws RuntimeException {
        
        final PluginCommand command = this.getCommand(commandName);
        if (command == null) {
            throw new RuntimeException("Cannot find the command /" + commandName);
        }
        command.setExecutor(tabExecutor);
        command.setTabCompleter(tabExecutor);
    }
    
    @NotNull
    public String formatWaitTime(final long waitTime) {
        
        final StringBuilder builder = new StringBuilder();
        
        long hours = waitTime / 1000L;
        if (hours == 0L) {
            builder.append("Less than 1 hour");
            return builder.toString();
        }
        
        final long days = hours / 24L;
        hours = hours % 24L;
        if (days > 0L) {
            builder.append(days).append(days == 1L ? " day, " : " days, ");
        }
        builder.append(hours).append(hours == 1L ? " hour" : " hours");
        
        return builder.toString();
    }
    
    @NotNull
    public String formatRealWaitTime(final long waitTime) {
        
        final StringBuilder builder = new StringBuilder();
        
        long seconds = waitTime / 20L;
        if (seconds == 0L) {
            builder.append("A few seconds");
            return builder.toString();
        }
        
        long minutes = seconds / 60L;
        seconds = seconds % 60L;
        final long hours = minutes / 60L;
        minutes = minutes % 60L;
        
        if (hours > 0L) {
            builder.append(String.format("%02d", hours)).append("h");
            
            if (minutes > 0L || seconds > 0L) {
                builder.append(" ");
            }
        }
        
        if (minutes > 0L) {
            builder.append(String.format("%02d", minutes)).append("m");
            
            if (seconds > 0L) {
                builder.append(" ");
            }
        }
        builder.append(String.format("%02d", seconds)).append("s");
        
        return builder.toString();
    }
    
    @NotNull
    public Collection<String> onPlayerCommandSend(@NotNull final Player player) {
        
        final Collection<String> removals = new HashSet<String>();
        for (final String commandName : this.getDescription().getCommands().keySet()) {
            
            removals.add("/" + this.getName().toLowerCase() + ":" + commandName);
            final PluginCommand command = this.getCommand(commandName);
            if (command != null && !command.testPermissionSilent(player)) {
                removals.add(commandName);
            }
        }
        
        return removals;
    }
    
    ///////////////////////////
    // EXTENDED ENCHANTMENTS //
    ///////////////////////////
    
    @Nullable
    public ExtendedEnchantment getExtendedEnchantment(@NotNull final Enchantment enchantment) {
        return this.byEnchantment.get(enchantment);
    }
    
    @Nullable
    public ExtendedEnchantment getExtendedEnchantment(@NotNull final String name) {
        return this.byName.get(name.toLowerCase());
    }
    
    ////////////////////////////////
    // GENERIC / NON-PERK RELATED //
    ////////////////////////////////
    
    @Nullable
    public Location getDeathLocation(@NotNull final UUID playerId) {
        return this.deathLocations.get(playerId);
    }
    
    @NotNull
    @UnmodifiableView
    public Set<UUID> getDeathLocationIds() {
        return Collections.unmodifiableSet(this.deathLocations.keySet());
    }
    
    public void addDeathLocation(@NotNull final UUID playerId, @NotNull final Location location) {
        this.deathLocations.put(playerId, location);
    }
    
    public void removeDeathLocation(@NotNull final UUID playerId) {
        this.deathLocations.remove(playerId);
    }
    
    public boolean isPendingDeathHoundNotification(@NotNull final UUID playerId) {
        return this.pendingDeathHoundNotifications.contains(playerId);
    }
    
    public void addPendingDeathHoundNotification(@NotNull final UUID playerId) {
        this.pendingDeathHoundNotifications.add(playerId);
    }
    
    public void removePendingDeathHoundNotification(@NotNull final UUID playerId) {
        this.pendingDeathHoundNotifications.remove(playerId);
    }
    
    ///////////////////
    // SERVICE CHAIN //
    ///////////////////
    
    public long getDoctorWaitTime(@NotNull final UUID playerId) {
        
        if (!this.doctorLastUsed.containsKey(playerId)) {
            return 0L;
        }
        
        final Player player = this.server.getPlayer(playerId);
        final long waitTime;
        if (player != null && player.hasPermission("cvranks.service.dr.master")) {
            waitTime = 12000L;
        } else {
            waitTime = 24000L;
        }
        
        final long endTime = this.doctorLastUsed.get(playerId) + waitTime;
        return Math.max(0L, endTime - this.uptime);
    }
    
    public void doctorUsed(@NotNull final UUID playerId) {
        this.doctorLastUsed.put(playerId, this.uptime);
    }
    
    public void checkDoctorResetNotification(@NotNull final UUID playerId) {
        
        if (this.notifyDoctorReset.remove(playerId)) {
            this.scheduler.runTaskLater(this, () -> {
                
                final Player player = this.server.getPlayer(playerId);
                if (player != null && player.isOnline()) {
                    player.sendMessage(ABILITY_READY_DOCTOR);
                } else {
                    this.notifyDoctorReset.add(playerId);
                }
            }, 60L);
        }
    }
    
    public boolean isNotifyDoctorDisabled(@NotNull final UUID playerId) {
        return this.notifyDoctorDisabled.contains(playerId);
    }
    
    public boolean disableNotifyDoctor(@NotNull final UUID playerId) {
        
        if (!this.notifyDoctorDisabled.add(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.notifyDoctorDisabled, this.disabledNotificationsFile, DISABLE_NOTIFY_DOCTOR, false, playerId);
        return true;
    }
    
    public boolean enableNotifyDoctor(@NotNull final UUID playerId) {
        
        if (!this.notifyDoctorDisabled.remove(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.notifyDoctorDisabled, this.disabledNotificationsFile, DISABLE_NOTIFY_DOCTOR, true, playerId);
        return true;
    }
    
    public long getRepairWaitTime(@NotNull final UUID playerId) {
        
        if (!this.repairLastUsed.containsKey(playerId)) {
            return 0L;
        }
        
        final Player player = this.server.getPlayer(playerId);
        final long waitTime;
        if (player != null && player.hasPermission("cvranks.service.repairman.master")) {
            waitTime = 36000L;
        } else {
            waitTime = 48000L;
        }
        
        final long endTime = this.repairLastUsed.get(playerId) + waitTime;
        return Math.max(0L, endTime - this.uptime);
    }
    
    public void repairUsed(@NotNull final UUID playerId) {
        this.repairLastUsed.put(playerId, this.uptime);
    }
    
    public void checkRepairResetNotification(@NotNull final UUID playerId) {
        
        if (this.notifyRepairReset.remove(playerId)) {
            this.scheduler.runTaskLater(this, () -> {
                
                final Player player = this.server.getPlayer(playerId);
                if (player != null && player.isOnline()) {
                    player.sendMessage(ABILITY_READY_REPAIR);
                } else {
                    this.notifyRepairReset.add(playerId);
                }
            }, 60L);
        }
    }
    
    //////////////////
    // MINING CHAIN //
    //////////////////
    
    public boolean isNotifyCoalDisabled(@NotNull final UUID playerId) {
        return this.notifyCoalDisabled.contains(playerId);
    }
    
    public boolean disableNotifyCoal(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.notifyCoalDisabled.add(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.notifyCoalDisabled, this.disabledNotificationsFile, DISABLE_NOTIFY_COAL, false, playerId);
        return true;
    }
    
    public boolean enableNotifyCoal(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.notifyCoalDisabled.remove(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.notifyCoalDisabled, this.disabledNotificationsFile, DISABLE_NOTIFY_COAL, true, playerId);
        return true;
    }
    
    public boolean isNotifyQuartzDisabled(@NotNull final UUID playerId) {
        return this.notifyQuartzDisabled.contains(playerId);
    }
    
    public boolean disableNotifyQuartz(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.notifyQuartzDisabled.add(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.notifyQuartzDisabled, this.disabledNotificationsFile, DISABLE_NOTIFY_QUARTZ, false, playerId);
        return true;
    }
    
    public boolean enableNotifyQuartz(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.notifyQuartzDisabled.remove(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.notifyQuartzDisabled, this.disabledNotificationsFile, DISABLE_NOTIFY_QUARTZ, true, playerId);
        return true;
    }
    
    public boolean isNotifyDiamondDisabled(@NotNull final UUID playerId) {
        return this.notifyDiamondDisabled.contains(playerId);
    }
    
    public boolean disableNotifyDiamond(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.notifyDiamondDisabled.add(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.notifyDiamondDisabled, this.disabledNotificationsFile,  DISABLE_NOTIFY_DIAMOND, false, playerId);
        return true;
    }
    
    public boolean enableNotifyDiamond(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.notifyDiamondDisabled.remove(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.notifyDiamondDisabled, this.disabledNotificationsFile, DISABLE_NOTIFY_DIAMOND, true, playerId);
        return true;
    }
    
    public boolean isNotifyFlintDisabled(@NotNull final UUID playerId) {
        return this.notifyFlintDisabled.contains(playerId);
    }
    
    public boolean disableNotifyFlint(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.notifyFlintDisabled.add(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.notifyFlintDisabled, this.disabledNotificationsFile, DISABLE_NOTIFY_FLINT, false, playerId);
        return true;
    }
    
    public boolean enableNotifyFlint(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.notifyFlintDisabled.remove(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.notifyFlintDisabled, this.disabledNotificationsFile, DISABLE_NOTIFY_FLINT, true, playerId);
        return true;
    }
    
    public boolean isInstaSmeltEnabled(@NotNull final UUID playerId) {
        return this.instaSmeltActive.contains(playerId);
    }
    
    public boolean enableInstaSmelt(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.instaSmeltActive.add(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.instaSmeltActive, this.activeRanksFile, ACTIVE_INSTA_SMELT, true, playerId);
        return true;
    }
    
    public boolean disableInstaSmelt(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.instaSmeltActive.remove(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.instaSmeltActive, this.activeRanksFile, ACTIVE_INSTA_SMELT, false, playerId);
        return true;
    }
    
    public boolean isNotifyIronDisabled(@NotNull final UUID playerId) {
        return this.notifyIronDisabled.contains(playerId);
    }
    
    public boolean disableNotifyIron(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.notifyIronDisabled.add(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.notifyIronDisabled, this.disabledNotificationsFile, DISABLE_NOTIFY_IRON, false, playerId);
        return true;
    }
    
    public boolean enableNotifyIron(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.notifyIronDisabled.remove(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.notifyIronDisabled, this.disabledNotificationsFile, DISABLE_NOTIFY_IRON, true, playerId);
        return true;
    }
    
    public boolean isNotifyGoldDisabled(@NotNull final UUID playerId) {
        return this.notifyGoldDisabled.contains(playerId);
    }
    
    public boolean disableNotifyGold(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.notifyGoldDisabled.add(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.notifyGoldDisabled, this.disabledNotificationsFile, DISABLE_NOTIFY_GOLD, false, playerId);
        return true;
    }
    
    public boolean enableNotifyGold(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.notifyGoldDisabled.remove(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.notifyGoldDisabled, this.disabledNotificationsFile, DISABLE_NOTIFY_GOLD, true, playerId);
        return true;
    }
    
    public boolean isNotifyCopperDisabled(@NotNull final UUID playerId) {
        return this.notifyCopperDisabled.contains(playerId);
    }
    
    public boolean disableNotifyCopper(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.notifyCopperDisabled.add(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.notifyCopperDisabled, this.disabledNotificationsFile, DISABLE_NOTIFY_COPPER, false, playerId);
        return true;
    }
    
    public boolean enableNotifyCopper(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.notifyCopperDisabled.remove(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.notifyCopperDisabled, this.disabledNotificationsFile, DISABLE_NOTIFY_COPPER, true, playerId);
        return true;
    }
    
    public boolean isNightStalkerEnabled(@NotNull final UUID playerId) {
        return this.nightStalkerActive.contains(playerId);
    }
    
    public boolean enableNightStalker(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.nightStalkerActive.add(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.nightStalkerActive, this.activeRanksFile, ACTIVE_NIGHT_STALKER, true, playerId);
        return true;
    }
    
    public boolean disableNightStalker(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.nightStalkerActive.remove(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.nightStalkerActive, this.activeRanksFile, ACTIVE_NIGHT_STALKER, false, playerId);
        return true;
    }
    
    /////////////////
    // BUILD CHAIN //
    /////////////////
    
    public boolean isStoneMasonEnabled(@NotNull final UUID playerId) {
        return this.stoneMasonActive.contains(playerId);
    }
    
    public boolean enableStoneMason(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.stoneMasonActive.add(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.stoneMasonActive, this.activeRanksFile, ACTIVE_STONE_MASON, true, playerId);
        return true;
    }
    
    public boolean disableStoneMason(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.stoneMasonActive.remove(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.stoneMasonActive, this.activeRanksFile, ACTIVE_STONE_MASON, false, playerId);
        return true;
    }
    
    public boolean isMushGardenerEnabled(@NotNull final UUID playerId) {
        return this.mushGardenerActive.contains(playerId);
    }
    
    public boolean enableMushGardener(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.mushGardenerActive.add(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.mushGardenerActive, this.activeRanksFile, ACTIVE_MUSH_GARDENER, true, playerId);
        return true;
    }
    
    public boolean disableMushGardener(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.mushGardenerActive.remove(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.mushGardenerActive, this.activeRanksFile, ACTIVE_MUSH_GARDENER, false, playerId);
        return true;
    }
    
    public boolean isBrickLayerEnabled(@NotNull final UUID playerId) {
        return this.brickLayerActive.contains(playerId);
    }
    
    public boolean enableBrickLayer(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.brickLayerActive.add(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.brickLayerActive, this.activeRanksFile, ACTIVE_BRICK_LAYER, true, playerId);
        return true;
    }
    
    public boolean disableBrickLayer(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.brickLayerActive.remove(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.brickLayerActive, this.activeRanksFile, ACTIVE_BRICK_LAYER, false, playerId);
        return true;
    }
    
    public boolean isMasterCarpenterEnabled(@NotNull final UUID playerId) {
        return this.masterCarpenterActive.contains(playerId);
    }
    
    public boolean enableMasterCarpenter(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.masterCarpenterActive.add(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.masterCarpenterActive, this.activeRanksFile, ACTIVE_MASTER_CARPENTER, true, playerId);
        return true;
    }
    
    public boolean disableMasterCarpenter(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.masterCarpenterActive.remove(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.masterCarpenterActive, this.activeRanksFile, ACTIVE_MASTER_CARPENTER, false, playerId);
        return true;
    }
    
    /////////////////
    // DEATH CHAIN //
    /////////////////
    
    public long getXpertWaitTime(@NotNull final UUID playerId) {
        
        if (!this.xpertLastUsed.containsKey(playerId)) {
            return 0L;
        }
        
        final long endTime = this.xpertLastUsed.get(playerId) + 24000L;
        return Math.max(0L, endTime - this.uptime);
    }
    
    public void xpertUsed(@NotNull final UUID playerId) {
        this.xpertLastUsed.put(playerId, this.uptime);
    }
    
    public void checkXpertResetNotification(@NotNull final UUID playerId) {
        
        if (this.notifyXpertReset.remove(playerId)) {
            this.scheduler.runTaskLater(this, () -> {
                
                final Player player = this.server.getPlayer(playerId);
                if (player != null && player.isOnline()) {
                    player.sendMessage(ABILITY_READY_XPERT);
                } else {
                    this.notifyXpertReset.add(playerId);
                }
            }, 60L);
        }
    }
    
    public long getKeepsakeWaitTime(@NotNull final UUID playerId) {
        
        if (!this.keepsakeLastUsed.containsKey(playerId)) {
            return 0L;
        }
        
        final long endTime = this.keepsakeLastUsed.get(playerId) + 72000L;
        return Math.max(0L, endTime - this.uptime);
    }
    
    public void keepsakeUsed(@NotNull final UUID playerId) {
        this.keepsakeLastUsed.put(playerId, this.uptime);
    }
    
    public void checkKeepsakeResetNotification(@NotNull final UUID playerId) {
        
        if (this.notifyKeepsakeReset.remove(playerId)) {
            this.scheduler.runTaskLater(this, () -> {
                
                final Player player = this.server.getPlayer(playerId);
                if (player != null && player.isOnline()) {
                    player.sendMessage(ABILITY_READY_KEEPSAKE);
                } else {
                    this.notifyKeepsakeReset.add(playerId);
                }
            }, 60L);
        }
    }
    
    public long getDeathHoundWaitTime(@NotNull final UUID playerId) {
        
        if (!this.deathHoundLastUsed.containsKey(playerId)) {
            return 0L;
        }
        
        final long endTime = this.deathHoundLastUsed.get(playerId) + 24000L;
        return Math.max(0L, endTime - this.uptime);
    }
    
    public void deathHoundUsed(@NotNull final UUID playerId) {
        this.deathHoundLastUsed.put(playerId, this.uptime);
    }
    
    public void checkDeathHoundResetNotification(@NotNull final UUID playerId) {
        
        if (this.notifyDeathHoundReset.remove(playerId)) {
            this.scheduler.runTaskLater(this, () -> {
                
                final Player player = this.server.getPlayer(playerId);
                if (player != null && player.isOnline()) {
                    player.sendMessage(ABILITY_READY_DEATH_HOUND);
                } else {
                    this.notifyDeathHoundReset.add(playerId);
                }
            }, 60L);
        }
    }
    
    public boolean isNotifyDeathHoundDisabled(@NotNull final UUID playerId) {
        return this.notifyDeathHoundDisabled.contains(playerId);
    }
    
    public boolean disableNotifyDeathHound(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.notifyDeathHoundDisabled.add(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.notifyDeathHoundDisabled, this.disabledNotificationsFile, DISABLE_NOTIFY_DEATH_HOUND, false, playerId);
        return true;
    }
    
    public boolean enableNotifyDeathHound(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.notifyDeathHoundDisabled.remove(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.notifyDeathHoundDisabled, this.disabledNotificationsFile, DISABLE_NOTIFY_DEATH_HOUND, true, playerId);
        return true;
    }
    
    public long getRespawnWaitTime(@NotNull final UUID playerId) {
    
        if (!this.respawnLastUsed.containsKey(playerId)) {
            return 0L;
        }
        
        final long endTime = this.respawnLastUsed.get(playerId) + 72000L;
        return Math.max(0L, endTime - this.uptime);
    }
    
    public void respawnUsed(@NotNull final UUID playerId) {
        this.respawnLastUsed.put(playerId, this.uptime);
    }
    
    public void checkRespawnResetNotification(@NotNull final UUID playerId) {
        
        if (this.notifyRespawnReset.remove(playerId)) {
            this.scheduler.runTaskLater(this, () -> {
                
                final Player player = this.server.getPlayer(playerId);
                if (player != null && player.isOnline()) {
                    player.sendMessage(ABILITY_READY_RESPAWN);
                } else {
                    this.notifyRespawnReset.add(playerId);
                }
            }, 60L);
        }
    }
    
    ///////////////////////
    // NON-CHAIN / OTHER //
    ///////////////////////
    
    public boolean isNotifyWoodDisabled(@NotNull final UUID playerId) {
        return this.notifyWoodDisabled.contains(playerId);
    }
    
    public boolean disableNotifyWood(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.notifyWoodDisabled.add(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.notifyWoodDisabled, this.disabledNotificationsFile, DISABLE_NOTIFY_WOOD, false, playerId);
        return true;
    }
    
    public boolean enableNotifyWood(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.notifyWoodDisabled.remove(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.notifyWoodDisabled, this.disabledNotificationsFile, DISABLE_NOTIFY_WOOD, true, playerId);
        return true;
    }
    
    public boolean isScubaEnabled(@NotNull final UUID playerId) {
        return this.scubaActive.contains(playerId);
    }
    
    public boolean enableScuba(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.scubaActive.add(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.scubaActive, this.activeRanksFile, ACTIVE_SCUBA, true, playerId);
        return true;
    }
    
    public boolean disableScuba(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.scubaActive.remove(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.scubaActive, this.activeRanksFile, ACTIVE_SCUBA, false, playerId);
        return true;
    }
    
    public boolean isNotifyLeatherDisabled(@NotNull final UUID playerId) {
        return this.notifyLeatherDisabled.contains(playerId);
    }
    
    public boolean disableNotifyLeather(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.notifyLeatherDisabled.add(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.notifyLeatherDisabled, this.disabledNotificationsFile, DISABLE_NOTIFY_LEATHER, false, playerId);
        return true;
    }
    
    public boolean enableNotifyLeather(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.notifyLeatherDisabled.remove(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.notifyLeatherDisabled, this.disabledNotificationsFile, DISABLE_NOTIFY_LEATHER, true, playerId);
        return true;
    }
    
    public boolean isMiniRankMyceliumEnabled(@NotNull final UUID playerId) {
        return this.miniRankMyceliumActive.contains(playerId);
    }
    
    public boolean enableMiniRankMycelium(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.miniRankMyceliumActive.add(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.miniRankMyceliumActive, this.activeRanksFile, ACTIVE_MINI_RANK_MYCELIUM, true, playerId);
        return true;
    }
    
    public boolean disableMiniRankMycelium(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.miniRankMyceliumActive.remove(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.miniRankMyceliumActive, this.activeRanksFile, ACTIVE_MINI_RANK_MYCELIUM, false, playerId);
        return true;
    }
    
    public boolean isMiniRankGlassEnabled(@NotNull final UUID playerId) {
        return this.miniRankGlassActive.contains(playerId);
    }
    
    public boolean enableMiniRankGlass(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.miniRankGlassActive.add(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.miniRankGlassActive, this.activeRanksFile, ACTIVE_MINI_RANK_GLASS, true, playerId);
        return true;
    }
    
    public boolean disableMiniRankGlass(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.miniRankGlassActive.remove(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.miniRankGlassActive, this.activeRanksFile, ACTIVE_MINI_RANK_GLASS, false, playerId);
        return true;
    }
    
    public boolean isMiniRankObsidianEnabled(@NotNull final UUID playerId) {
        return this.miniRankObsidianActive.contains(playerId);
    }
    
    public boolean enableMiniRankObsidian(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.miniRankObsidianActive.add(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.miniRankObsidianActive, this.activeRanksFile, ACTIVE_MINI_RANK_OBSIDIAN, true, playerId);
        return true;
    }
    
    public boolean disableMiniRankObsidian(@NotNull final UUID playerId) throws RuntimeException {
        
        if (!this.miniRankObsidianActive.remove(playerId)) {
            return false;
        }
        this.saveUpdatedPlayers(this.miniRankObsidianActive, this.activeRanksFile, ACTIVE_MINI_RANK_OBSIDIAN, false, playerId);
        return true;
    }
}
