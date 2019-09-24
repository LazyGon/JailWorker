package fr.alienationgaming.jailworker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import fr.alienationgaming.jailworker.commands.JWCommand;
import fr.alienationgaming.jailworker.listner.JWBlockBreakListener;
import fr.alienationgaming.jailworker.listner.JWChatPrisonerPrevent;
import fr.alienationgaming.jailworker.listner.JWPlayerCommandProtector;
import fr.alienationgaming.jailworker.listner.JWPrisonerDieListener;
import fr.alienationgaming.jailworker.listner.JWPutBlockListener;
import fr.stevecohen.jailworker.configsign.OnConfigSignPlacedListener;

public class JailWorker extends JavaPlugin {

    /* Listeners */
    public JWBlockBreakListener jwblockbreaklistener = new JWBlockBreakListener(this);
    public JWPrisonerDieListener jwprisonerdielistener = new JWPrisonerDieListener(this);
    public JWPlayerCommandProtector jwplayercommandprotector = new JWPlayerCommandProtector(this);
    public JWChatPrisonerPrevent jwchatprisonerprevent = new JWChatPrisonerPrevent(this);
    public JWPutBlockListener jwputblocklistener = new JWPutBlockListener(this);
    public OnConfigSignPlacedListener jwconfigsignplaced = new OnConfigSignPlacedListener(this);
    /* Tmp values */
    public Map<Player, Block> blockJail1 = new HashMap<Player, Block>();
    public Map<Player, Block> blockJail2 = new HashMap<Player, Block>();
    public Map<Player, Location> JailPrisonerSpawn = new HashMap<Player, Location>();
    /* Files */
    private FileConfiguration jailConfig = null;
    private File jailConfigFile = null;
    private FileConfiguration langConfig = null;
    private File enLanguage = null;
    private File frLanguage = null;
    public UpdateFiles uf = new UpdateFiles(this);
    private Map<String, Object> lang = new HashMap<String, Object>();
    /* Other */
    public Config getdefaultvalues = new Config(this);
    public JWPlayerInteract interactWithPlayer = new JWPlayerInteract(this);
    public JWInventorySaver iv = new JWInventorySaver(this);
    public Map<String, BukkitRunnable> tasks = new HashMap<>();
    public int NumberBlockToBreak = 0;
    public Location prisonerPreviousPos = null;
    private Vector<String> allowBlocks = new Vector<String>();

    /* Binded plugins */
    public Permission perms = null;
    public WorldEditPlugin worldEdit = null;

    private static JailWorker instance;

    public static JailWorker getInstance() {
        if (instance == null) {
            instance = (JailWorker) Bukkit.getPluginManager().getPlugin("JailWorker");
        }
        return instance;
    }

    private boolean setupWorldEdit() {
        worldEdit = (WorldEditPlugin) this.getServer().getPluginManager().getPlugin("WorldEdit");
        if (worldEdit == null)
            return false;
        return true;
    }

    /*
     * JailConfig
     */
    public void reloadJailConfig() {
        if (jailConfigFile == null) {
            jailConfigFile = new File(getDataFolder(), "jails.yml");
        }
        this.jailConfig = YamlConfiguration.loadConfiguration(jailConfigFile);
        InputStream defConfigStream = this.getResource("jails.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
            jailConfig.setDefaults(defConfig);
        }
    }

    public FileConfiguration getJailConfig() {
        if (jailConfig == null) {
            this.reloadJailConfig();
        }
        return jailConfig;
    }

    public void saveJailConfig() {
        if (jailConfig == null || jailConfigFile == null) {
            return;
        }
        try {
            getJailConfig().save(jailConfigFile);
        } catch (IOException ex) {
            this.getLogger().log(Level.SEVERE, "Could not save config to " + jailConfigFile, ex);
        }
    }

    public void saveDefaultJailConfig() {
        if (jailConfigFile == null) {
            jailConfigFile = new File(getDataFolder(), "jails.yml");
        }
        if (!jailConfigFile.exists()) {
            this.saveResource("jails.yml", false);
            this.getJailConfig().createSection("Jails");
            this.getJailConfig().createSection("Prisoners");
            this.getLogger().log(Level.INFO, "Default jail.yml saved.");
        }
    }

    public void restartConfigFile() {
        File file = new File(getDataFolder(), "Config.yml");
        file.delete();
        this.saveDefaultConfig();
    }

    /* Lang Config */
    public void reloadLangConfig() {
        String language = getConfig().getString("Plugin.Language");
        File file = new File(getDataFolder(), language + ".yml");
        this.langConfig = YamlConfiguration.loadConfiguration(file);
        InputStream defConfigStream = this.getResource(language + ".yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
            langConfig.setDefaults(defConfig);
        }
    }

    public FileConfiguration getLangConfig() {
        if (langConfig == null) {
            this.reloadLangConfig();
        }
        return langConfig;
    }

    /* English Language */
    public void saveDefaultEnFile() {
        if (enLanguage == null) {
            enLanguage = new File(getDataFolder(), "en.yml");
        }
        if (!enLanguage.exists()) {
            this.saveResource("en.yml", false);
            this.getLogger().log(Level.INFO, "Language en.yml saved.");
        }
    }

    public void restartEnFile() {
        File file = new File(getDataFolder(), "en.yml");
        file.delete();
        this.saveDefaultEnFile();
    }

    /* Fr Language */
    public void saveDefaultFrFile() {
        if (frLanguage == null) {
            frLanguage = new File(getDataFolder(), "fr.yml");
        }
        if (!frLanguage.exists()) {
            this.saveResource("fr.yml", false);
            this.getLogger().log(Level.INFO, "Language fr.yml saved.");
        }
    }

    public void restartFrFile() {
        File file = new File(getDataFolder(), "fr.yml");
        file.delete();
        this.saveDefaultFrFile();
    }

    @Override
    public void onEnable() {
        boolean worldEditFound = setupWorldEdit();
        if (worldEditFound == false)
            getLogger().log(Level.INFO,
                    "WorldEdit not found, you'll not be able to use WorldEdit selection to define the jails.\nBut you can use my simple selection system.");

        /* Init Defauts Config */
        this.saveDefaultConfig();
        this.saveDefaultJailConfig();
        this.saveDefaultEnFile();
        this.saveDefaultFrFile();
        this.uf.setUpdate();
        this.saveJailConfig();
        this.saveConfig();
        this.reloadConfig();
        this.reloadJailConfig();
        this.reloadLangConfig();
        this.initLang();

        /* Events see on JailSet.java & JailSetSpawn.java */
        this.getServer().getPluginManager().registerEvents(jwblockbreaklistener, this);
        this.getServer().getPluginManager().registerEvents(jwprisonerdielistener, this);
        this.getServer().getPluginManager().registerEvents(jwplayercommandprotector, this);
        this.getServer().getPluginManager().registerEvents(jwchatprisonerprevent, this);
        this.getServer().getPluginManager().registerEvents(jwputblocklistener, this);
        this.getServer().getPluginManager().registerEvents(jwconfigsignplaced, this);

        /* Register tasks for jails enabled */
        if (jailConfig != null) {
            Set<String> jails = getJailConfig().getConfigurationSection("Jails").getKeys(false);
            int numberTaskStarted = 0;
            for (String jail : jails) {
                if (!getJailConfig().getBoolean("Jails." + jail + ".isStarted")) {
                    continue;
                }
                Jail runjailsystem = new Jail(
                        this.getServer().getWorld(this.getJailConfig().getString("Jails." + jail + ".World")), jail);
                this.tasks.put(jail, runjailsystem.getTask());
                numberTaskStarted++;
            }
            this.getLogger().info(numberTaskStarted + " of " + jails.size() + " jails started!");
        }

        allowBlocks.add("SAND");
        allowBlocks.add("DIRT");
        allowBlocks.add("STONE");
        allowBlocks.add("OBSIDIAN");

        new JWCommand();
    }

    public String colorFormat(String str) {
        return str.replaceAll("%black", "" + ChatColor.BLACK).replaceAll("%dark_blue", "" + ChatColor.DARK_BLUE)
                .replaceAll("%dark_green", "" + ChatColor.DARK_GREEN).replaceAll("%dark_aqua", "" + ChatColor.DARK_AQUA)
                .replaceAll("%dark_red", "" + ChatColor.DARK_RED).replaceAll("%dark_purple", "" + ChatColor.DARK_PURPLE)
                .replaceAll("%gold", "" + ChatColor.GOLD).replaceAll("%gray", "" + ChatColor.GRAY)
                .replaceAll("%dark_gray", "" + ChatColor.DARK_GRAY).replaceAll("%blue", "" + ChatColor.BLUE)
                .replaceAll("%green", "" + ChatColor.GREEN).replaceAll("%aqua", "" + ChatColor.AQUA)
                .replaceAll("%red", "" + ChatColor.RED).replaceAll("%light_purple", "" + ChatColor.LIGHT_PURPLE)
                .replaceAll("%yellow", "" + ChatColor.YELLOW).replaceAll("%white", "" + ChatColor.WHITE)
                .replaceAll("%magic", "" + ChatColor.MAGIC).replaceAll("%bold", "" + ChatColor.BOLD)
                .replaceAll("%strikethrough", "" + ChatColor.STRIKETHROUGH)
                .replaceAll("%underline", "" + ChatColor.UNDERLINE).replaceAll("%italic", "" + ChatColor.ITALIC)
                .replaceAll("%reset", "" + ChatColor.RESET);
    }

    public void initLang() {
        ConfigurationSection section = getLangConfig().getConfigurationSection("Messages");
        lang = section.getValues(false);
    }

    public String toLanguage(String str, Object... objects) {
        if (!lang.containsKey(str))
            return "";
        String mystr = lang.get(str).toString();
        // /* Add accent */
        // mystr = mystr.replaceAll("é", "�");
        // mystr = mystr.replaceAll("ê", "�");
        // mystr = mystr.replaceAll("�.", "�");

        String colored = colorFormat(mystr.replaceAll("\\$", "%"));
        String val = String.format(colored, objects);
        /* Cleaning val */
        val = val.replaceAll("\\s+", " ");
        val = val.trim();
        return (val);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public Vector<String> getAllowBlocks() {
        return allowBlocks;
    }

    public void setAllowBlocks(Vector<String> allowBlocks) {
        this.allowBlocks = allowBlocks;
    }

}