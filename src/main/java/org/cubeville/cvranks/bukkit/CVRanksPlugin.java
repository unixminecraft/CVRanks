package org.cubeville.cvranks.bukkit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.cubeville.cvranks.bukkit.command.build.BrickLayerCommand;
import org.cubeville.cvranks.bukkit.command.build.MasterCarpenterCommand;
import org.cubeville.cvranks.bukkit.command.build.MushGardenerCommand;
import org.cubeville.cvranks.bukkit.command.build.StoneMasonCommand;
import org.cubeville.cvranks.bukkit.command.death.DeathHoundCommand;
import org.cubeville.cvranks.bukkit.command.death.KeepsakeCommand;
import org.cubeville.cvranks.bukkit.command.death.RespawnCommand;
import org.cubeville.cvranks.bukkit.command.death.XpertCommand;
import org.cubeville.cvranks.bukkit.command.mining.InstaSmeltCommand;
import org.cubeville.cvranks.bukkit.command.mining.NightStalkerCommand;
import org.cubeville.cvranks.bukkit.command.other.MiniRankCommand;
import org.cubeville.cvranks.bukkit.command.other.ScubaCommand;
import org.cubeville.cvranks.bukkit.command.service.DoctorCommand;
import org.cubeville.cvranks.bukkit.command.service.LevelCommand;
import org.cubeville.cvranks.bukkit.command.service.RepairCommand;
import org.cubeville.cvranks.bukkit.listener.CVRanksEventListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CVRanksPlugin extends JavaPlugin {
    
    public static final String DEFAULT_PERMISSION_MESSAGE = "§cYou do not have permission to execute this command.";
    
    public static final String ABILITY_READY_DOCTOR = "§bYour doctor ability is ready to use again.";
    public static final String ABILITY_READY_REPAIR = "§bYour repair ability is ready to use again.";
    public static final String ABILITY_READY_XPERT = "§bYou will keep your XP upon your next death.";
    public static final String ABILITY_READY_KEEPSAKE = "§bYou will keep your inventory upon your next death.";
    public static final String ABILITY_READY_DEATH_HOUND = "§bYour death hound ability is ready to use again.";
    public static final String ABILITY_READY_RESPAWN = "§bYour respawn ability is ready to use again.";
    
    private File dataFolder;
    private File enchantmentFile;
    private File disabledNotificationsFile;
    private File activeRanksFile;
    
    private long uptime; // Used for notifying when abilities can be used again.
    private Server server;
    private BukkitScheduler scheduler;
    
    /* GENERIC / NON-PERK RELATED */
    private Map<UUID, Location> deathLocations;
    private Set<UUID> pendingDeathHoundNotifications;
    
    /* PERK RESET NOTIFICATIONS */
    private Set<UUID> notifyDoctorReset;
    private Set<UUID> notifyRepairReset;
    private Set<UUID> notifyXpertReset;
    private Set<UUID> notifyKeepsakeReset;
    private Set<UUID> notifyDeathHoundReset;
    private Set<UUID> notifyRespawnReset;
    
    /* BONUS BLOCKS NOTIFICATIONS */
    private Set<UUID> notifyWoodDisabled;
    private Set<UUID> notifyFlintDisabled;
    private Set<UUID> notifyCoalDisabled;
    private Set<UUID> notifyQuartzDisabled;
    private Set<UUID> notifyDiamondDisabled;
    
    /* SERVICE CHAIN */
    private Map<UUID, Long> doctorLastUsed;
    private Map<UUID, Long> repairLastUsed;
    
    /* MINING CHAIN */
    private Set<UUID> instaSmeltActive;
    private Set<UUID> nightStalkerActive;
    
    /* BUILD CHAIN */
    private Set<UUID> stoneMasonActive;
    private Set<UUID> mushGardenerActive;
    private Set<UUID> brickLayerActive;
    private Set<UUID> masterCarpenterActive;
    
    /* DEATH CHAIN */
    private Map<UUID, Long> xpertLastUsed;
    private Map<UUID, Long> keepsakeLastUsed;
    private Map<UUID, Long> deathHoundLastUsed;
    private Map<UUID, Long> respawnLastUsed;
    
    /* NON-CHAIN / OTHER */
    private Set<UUID> scubaActive;
    private Set<UUID> miniRankMyceliumActive;
    private Set<UUID> miniRankGlassActive;
    private Set<UUID> miniRankObsidianActive;
    
    @Override
    public void onEnable() {
        
        ////////////////////////////
        // GENERAL INITIALIZATION //
        ////////////////////////////
        
        this.uptime = 0L;
        this.server = this.getServer();
        this.scheduler = this.server.getScheduler();
        
        /*
         * Where able, use ConcurrentHashMaps instead of HashMaps as neither the
         * key nor value can be null.
         */
        
        /* GENERIC / NON-PERK RELATED */
        this.deathLocations = new ConcurrentHashMap<UUID, Location>();
        this.pendingDeathHoundNotifications = new HashSet<UUID>();
        
        /* PERK RESET NOTIFICATIONS */
        this.notifyDoctorReset = new HashSet<UUID>();
        this.notifyRepairReset = new HashSet<UUID>();
        this.notifyXpertReset = new HashSet<UUID>();
        this.notifyKeepsakeReset = new HashSet<UUID>();
        this.notifyDeathHoundReset = new HashSet<UUID>();
        this.notifyRespawnReset = new HashSet<UUID>();
        
        /* BONUS BLOCKS NOTIFICATIONS */
        this.notifyWoodDisabled = new HashSet<UUID>();
        this.notifyFlintDisabled = new HashSet<UUID>();
        this.notifyCoalDisabled = new HashSet<UUID>();
        this.notifyQuartzDisabled = new HashSet<UUID>();
        this.notifyDiamondDisabled = new HashSet<UUID>();
        
        /* SERVICE CHAIN */
        this.doctorLastUsed = new ConcurrentHashMap<UUID, Long>();
        this.repairLastUsed = new ConcurrentHashMap<UUID, Long>();
        
        /* MINING CHAIN */
        this.instaSmeltActive = new HashSet<UUID>();
        this.nightStalkerActive = new HashSet<UUID>();
        
        /* BUILD CHAIN */
        this.stoneMasonActive = new HashSet<UUID>();
        this.mushGardenerActive = new HashSet<UUID>();
        this.brickLayerActive = new HashSet<UUID>();
        this.masterCarpenterActive = new HashSet<UUID>();
        
        /* DEATH CHAIN */
        this.xpertLastUsed = new ConcurrentHashMap<UUID, Long>();
        this.keepsakeLastUsed = new ConcurrentHashMap<UUID, Long>();
        this.deathHoundLastUsed = new ConcurrentHashMap<UUID, Long>();
        this.respawnLastUsed = new ConcurrentHashMap<UUID, Long>();
        
        /* NON-CHAIN / OTHER */
        this.scubaActive = new HashSet<UUID>();
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
         * This runs every 40 ticks (2 seconds) after a 40 tick (2 second)
         * delay and performs the following operations:
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
                    player.sendMessage(CVRanksPlugin.ABILITY_READY_DOCTOR);
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
                    player.sendMessage(CVRanksPlugin.ABILITY_READY_REPAIR);
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
                    player.sendMessage(CVRanksPlugin.ABILITY_READY_XPERT);
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
                    player.sendMessage(CVRanksPlugin.ABILITY_READY_KEEPSAKE);
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
                    player.sendMessage(CVRanksPlugin.ABILITY_READY_DEATH_HOUND);
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
                    player.sendMessage(CVRanksPlugin.ABILITY_READY_RESPAWN);
                } else {
                    this.notifyRespawnReset.add(playerId);
                }
            }
        }, 200L, 200L);
        
        //////////////////////////
        // COMMAND REGISTRATION //
        //////////////////////////
        
        this.registerCommand("doctor", new DoctorCommand(this));
        this.registerCommand("level", new LevelCommand(this));
        this.registerCommand("repair", new RepairCommand(this));
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
        this.registerCommand("scuba", new ScubaCommand(this));
        this.registerCommand("minirank", new MiniRankCommand(this));
    
        /////////////////////////////////
        // EVENT LISTENER REGISTRATION //
        /////////////////////////////////
        
        this.server.getPluginManager().registerEvents(new CVRanksEventListener(this), this);
        
        //////////////////////////////////
        // CRAFTING RECIPE REGISTRATION //
        //////////////////////////////////
    
        this.server.addRecipe(new ShapedRecipe(NamespacedKey.minecraft(Material.SADDLE.name().toLowerCase()), new ItemStack(Material.SADDLE)).shape("XXX", "XXX").setIngredient('X', Material.LEATHER));
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
    
    public void reloadEnchantments() throws RuntimeException {
        
        this.reloadDataFolder();
        
        final File dataDirectory = this.getDataFolder();
        try {
            if (dataDirectory.exists()) {
                if (!dataDirectory.isDirectory()) {
                    throw new RuntimeException("Data directory is not a directory: " + dataDirectory.getPath());
                }
            } else if (!dataDirectory.mkdirs()) {
                throw new RuntimeException("Data directory not created at " + dataDirectory.getPath());
            }
        } catch (final SecurityException e) {
            throw new RuntimeException("Unable to validate data directory at " + dataDirectory.getPath(), e);
        }
        
        final File enchantmentsFile = new File(this.getDataFolder(), "config.yml");
        try {
            if (enchantmentsFile.exists()) {
                if (!enchantmentsFile.isFile()) {
                    throw new IllegalArgumentException("Config file is not a file: " + enchantmentsFile.getPath());
                }
            } else {
                if (!enchantmentsFile.createNewFile()) {
                    throw new IllegalArgumentException("Config file not created at " + enchantmentsFile.getPath());
                }
            
                final InputStream defaultConfig = this.getResource(enchantmentsFile.getName());
                final FileOutputStream outputStream = new FileOutputStream(enchantmentsFile);
                final byte[] buffer = new byte[4096];
                int bytesRead;
            
                if (defaultConfig == null) {
                    throw new IllegalArgumentException("No default config.yml packaged with CVRanks, possible compilation/build issue.");
                }
            
                while ((bytesRead = defaultConfig.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            
                outputStream.flush();
                outputStream.close();
                defaultConfig.close();
            }
        } catch (final IOException | SecurityException e) {
            throw new IllegalArgumentException("Unable to load config file at " + enchantmentsFile.getPath(), e);
        }
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
            builder.append("less than 1 hour");
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
    public String formatRealTimeWait(final long waitTime) {
        
        final StringBuilder builder = new StringBuilder();
        
        long seconds = waitTime / 20L;
        if (seconds == 0L) {
            builder.append("a few seconds");
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
            
            removals.add(this.getName().toLowerCase() + ":" + commandName);
            final PluginCommand command = this.getCommand(commandName);
            if (command != null && !command.testPermissionSilent(player)) {
                removals.add(commandName);
            }
        }
        
        return removals;
    }
    
    ////////////////////////////////
    // GENERIC / NON-PERK RELATED //
    ////////////////////////////////
    
    @Nullable
    public Location getDeathLocation(@NotNull final UUID playerId) {
        return this.deathLocations.get(playerId);
    }
    
    @NotNull
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
    
    //////////////////////////////
    // PERK RESET NOTIFICATIONS //
    //////////////////////////////
    
    public void checkResetNotifications(@NotNull final UUID playerId) {
        
        // Doctor
        if (this.notifyDoctorReset.remove(playerId)) {
            this.scheduler.runTaskLater(this, () -> {
                
                final Player player = this.server.getPlayer(playerId);
                if (player != null && player.isOnline()) {
                    player.sendMessage(CVRanksPlugin.ABILITY_READY_DOCTOR);
                } else {
                    this.notifyDoctorReset.add(playerId);
                }
            }, 60L);
        }
        
        // Repair
        if (this.notifyRepairReset.remove(playerId)) {
            this.scheduler.runTaskLater(this, () -> {
                
                final Player player = this.server.getPlayer(playerId);
                if (player != null && player.isOnline()) {
                    player.sendMessage(CVRanksPlugin.ABILITY_READY_REPAIR);
                } else {
                    this.notifyRepairReset.add(playerId);
                }
            }, 60L);
        }
        
        // Xpert
        if (this.notifyXpertReset.remove(playerId)) {
            this.scheduler.runTaskLater(this, () -> {
                
                final Player player = this.server.getPlayer(playerId);
                if (player != null && player.isOnline()) {
                    player.sendMessage(CVRanksPlugin.ABILITY_READY_XPERT);
                } else {
                    this.notifyXpertReset.add(playerId);
                }
            }, 60L);
        }
        
        // Keepsake
        if (this.notifyKeepsakeReset.remove(playerId)) {
            this.scheduler.runTaskLater(this, () -> {
                
                final Player player = this.server.getPlayer(playerId);
                if (player != null && player.isOnline()) {
                    player.sendMessage(CVRanksPlugin.ABILITY_READY_KEEPSAKE);
                } else {
                    this.notifyKeepsakeReset.add(playerId);
                }
            }, 60L);
        }
        
        // Death Hound
        if (this.notifyDeathHoundReset.remove(playerId)) {
            this.scheduler.runTaskLater(this, () -> {
                
                final Player player = this.server.getPlayer(playerId);
                if (player != null && player.isOnline()) {
                    player.sendMessage(CVRanksPlugin.ABILITY_READY_DEATH_HOUND);
                } else {
                    this.notifyDeathHoundReset.add(playerId);
                }
            }, 60L);
        }
        
        // Respawn
        if (this.notifyRespawnReset.remove(playerId)) {
            this.scheduler.runTaskLater(this, () -> {
                
                final Player player = this.server.getPlayer(playerId);
                if (player != null && player.isOnline()) {
                    player.sendMessage(CVRanksPlugin.ABILITY_READY_RESPAWN);
                } else {
                    this.notifyRespawnReset.add(playerId);
                }
            }, 60L);
        }
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
    
    //////////////////
    // MINING CHAIN //
    //////////////////
    
    public boolean isInstaSmeltEnabled(@NotNull final UUID playerId) {
        return this.instaSmeltActive.contains(playerId);
    }
    
    public boolean enableInstaSmelt(@NotNull final UUID playerId) {
        return this.instaSmeltActive.add(playerId);
    }
    
    public boolean disableInstaSmelt(@NotNull final UUID playerId) {
        return this.instaSmeltActive.remove(playerId);
    }
    
    public boolean isNightStalkerEnabled(@NotNull final UUID playerId) {
        return this.nightStalkerActive.contains(playerId);
    }
    
    public boolean enableNightStalker(@NotNull final UUID playerId) {
        return this.nightStalkerActive.add(playerId);
    }
    
    public boolean disableNightStalker(@NotNull final UUID playerId) {
        return this.nightStalkerActive.remove(playerId);
    }
    
    /////////////////
    // BUILD CHAIN //
    /////////////////
    
    public boolean isStoneMasonEnabled(@NotNull final UUID playerId) {
        return this.stoneMasonActive.contains(playerId);
    }
    
    public boolean enableStoneMason(@NotNull final UUID playerId) {
        return this.stoneMasonActive.add(playerId);
    }
    
    public boolean disableStoneMason(@NotNull final UUID playerId) {
        return this.stoneMasonActive.remove(playerId);
    }
    
    public boolean isMushGardenerEnabled(@NotNull final UUID playerId) {
        return this.mushGardenerActive.contains(playerId);
    }
    
    public boolean enableMushGardener(@NotNull final UUID playerId) {
        return this.mushGardenerActive.add(playerId);
    }
    
    public boolean disableMushGardener(@NotNull final UUID playerId) {
        return this.mushGardenerActive.remove(playerId);
    }
    
    public boolean isBrickLayerEnabled(@NotNull final UUID playerId) {
        return this.brickLayerActive.contains(playerId);
    }
    
    public boolean enableBrickLayer(@NotNull final UUID playerId) {
        return this.brickLayerActive.add(playerId);
    }
    
    public boolean disableBrickLayer(@NotNull final UUID playerId) {
        return this.brickLayerActive.remove(playerId);
    }
    
    public boolean isMasterCarpenterEnabled(@NotNull final UUID playerId) {
        return this.masterCarpenterActive.contains(playerId);
    }
    
    public boolean enableMasterCarpenter(@NotNull final UUID playerId) {
        return this.masterCarpenterActive.add(playerId);
    }
    
    public boolean disableMasterCarpenter(@NotNull final UUID playerId) {
        return this.masterCarpenterActive.remove(playerId);
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
    
    ///////////////////////
    // NON-CHAIN / OTHER //
    ///////////////////////
    
    public boolean isScubaEnabled(@NotNull final UUID playerId) {
        return this.scubaActive.contains(playerId);
    }
    
    public boolean enableScuba(@NotNull final UUID playerId) {
        return this.scubaActive.add(playerId);
    }
    
    public boolean disableScuba(@NotNull final UUID playerId) {
        return this.scubaActive.remove(playerId);
    }
    
    public boolean isMiniRankMyceliumEnabled(@NotNull final UUID playerId) {
        return this.miniRankMyceliumActive.contains(playerId);
    }
    
    public boolean enableMiniRankMycelium(@NotNull final UUID playerId) {
        return this.miniRankMyceliumActive.add(playerId);
    }
    
    public boolean disableMiniRankMycelium(@NotNull final UUID playerId) {
        return this.miniRankMyceliumActive.remove(playerId);
    }
    
    public boolean isMiniRankGlassEnabled(@NotNull final UUID playerId) {
        return this.miniRankGlassActive.contains(playerId);
    }
    
    public boolean enableMiniRankGlass(@NotNull final UUID playerId) {
        return this.miniRankGlassActive.add(playerId);
    }
    
    public boolean disableMiniRankGlass(@NotNull final UUID playerId) {
        return this.miniRankGlassActive.remove(playerId);
    }
    
    public boolean isMiniRankObsidianEnabled(@NotNull final UUID playerId) {
        return this.miniRankObsidianActive.contains(playerId);
    }
    
    public boolean enableMiniRankObsidian(@NotNull final UUID playerId) {
        return this.miniRankObsidianActive.add(playerId);
    }
    
    public boolean disableMiniRankObsidian(@NotNull final UUID playerId) {
        return this.miniRankObsidianActive.remove(playerId);
    }
}
