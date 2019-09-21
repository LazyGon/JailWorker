package fr.alienationgaming.jailworker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import fr.alienationgaming.jailworker.commands.Clean;
import fr.alienationgaming.jailworker.commands.ConfigCmd;
import fr.alienationgaming.jailworker.commands.Create;
import fr.alienationgaming.jailworker.commands.Delete;
import fr.alienationgaming.jailworker.commands.Free;
import fr.alienationgaming.jailworker.commands.Give;
import fr.alienationgaming.jailworker.commands.Goto;
import fr.alienationgaming.jailworker.commands.Increase;
import fr.alienationgaming.jailworker.commands.Info;
import fr.alienationgaming.jailworker.commands.JailPlayer;
import fr.alienationgaming.jailworker.commands.List;
import fr.alienationgaming.jailworker.commands.OwnerManager;
import fr.alienationgaming.jailworker.commands.Reload;
import fr.alienationgaming.jailworker.commands.Restart;
import fr.alienationgaming.jailworker.commands.Save;
import fr.alienationgaming.jailworker.commands.SetSpawn;
import fr.alienationgaming.jailworker.commands.Start;
import fr.alienationgaming.jailworker.commands.Stop;
import fr.alienationgaming.jailworker.commands.WhiteCmd;
import fr.alienationgaming.jailworker.listner.JWBlockBreakListener;
import fr.alienationgaming.jailworker.listner.JWChatPrisonerPrevent;
import fr.alienationgaming.jailworker.listner.JWPlayerCommandProtector;
import fr.alienationgaming.jailworker.listner.JWPrisonerDieListener;
import fr.alienationgaming.jailworker.listner.JWPutBlockListener;
import fr.stevecohen.jailworker.configsign.OnConfigSignPlacedListener;

public class JailWorker extends JavaPlugin {

    /* Commands declarations */
    private Create jailset = new Create(this);
    private JailPlayer jailplayer = new JailPlayer(this);
    private Start jailstart = new Start(this);
    private SetSpawn jailsetspawn = new SetSpawn(this);
    private Stop jailstop = new Stop(this);
    private Clean jailclean = new Clean(this);
    private Save jailsave = new Save(this);
    private ConfigCmd jailconfigcmd = new ConfigCmd(this);
    private List jaillist = new List(this);
    private Delete jaildelete = new Delete(this);
    private Restart jailrestart = new Restart(this);
    private Info jailinfo = new Info(this);
    private Free jailfree = new Free(this);
    private Goto jailgoto = new Goto(this);
    private Give jailgive = new Give(this);
    private WhiteCmd jailwhitecmd = new WhiteCmd(this);
    private Reload jailreload = new Reload(this);
    private Increase jailincrease = new Increase(this);
    private OwnerManager jailmanageowners = new OwnerManager(this);

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
    /* Other*/
    public Config getdefaultvalues = new Config(this);
    public JWPlayerInteract interactWithPlayer = new JWPlayerInteract(this);
    public Utils utils = new Utils(this);
    public JWInventorySaver iv = new JWInventorySaver(this);
    public Map<String, Integer> tasks = new HashMap<String, Integer>();
    public int NbrBlockToBreak = 0;
    public Location prisonerPreviousPos = null;
    private Vector<String> allowBlocks = new Vector<String>();

    /* Binded plugins */
    public Permission perms = null;
    public WorldEditPlugin worldEdit = null;

    private static JailWorker instance;

    public static JailWorker getInstance() {
        return instance;
    }

    private boolean setupWorldEdit() {
        worldEdit = (WorldEditPlugin) this.getServer().getPluginManager().getPlugin("WorldEdit");
        if (worldEdit == null)
            return false;
        return true;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public boolean hasPerm(Player player, String perm) {
        if (player instanceof Player) {
            if (this.perms.has(player, perm))
                return true;
            player.sendMessage(ChatColor.RED + "You have not permission to do that. Please verify permissions in this world.");
            return false;
        }
        return true;
    }

    public boolean hasPerm(Player player, String perm, boolean quiet) {
        if (player instanceof Player) {
            if (this.perms.has(player, perm))
                return true;
            return false;
        }
        return true;
    }

    /*
     * JailConfig
     * */
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
        instance = this;
        setupPermissions();
        boolean worldEditFound = setupWorldEdit();
        if (worldEditFound == false)
            getLogger().log(Level.INFO, "WorldEdit not found, you'll not be able to use WorldEdit selection to define the jails.\nBut you can use my simple selection system.");

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
            Set<String> s = getJailConfig().getConfigurationSection("Jails").getKeys(false);
            Iterator<String> it = s.iterator();

            int nbrTaskStarted = 0;
            while (it.hasNext()) {
                String elem = (String) it.next();
                if (getJailConfig().getBoolean("Jails." + elem + ".isStarted")) {
                    Jail runjailsystem = new Jail(this, this.getServer().getWorld(this.getJailConfig().getString("Jails." + elem + ".World")), elem);
                    this.tasks.put(elem, runjailsystem.getTaskId());
                    nbrTaskStarted++;
                }
            }
            this.getLogger().log(Level.INFO, nbrTaskStarted + " of " + s.size() + " jails started!");
        }

        /* Commands */
        getCommand("jw-setjail").setExecutor((CommandExecutor) jailset);
        getCommand("jw-player").setExecutor((CommandExecutor) jailplayer);
        getCommand("jw-start").setExecutor((CommandExecutor) jailstart);
        getCommand("jw-setspawn").setExecutor((CommandExecutor) jailsetspawn);
        getCommand("jw-stop").setExecutor((CommandExecutor) jailstop);
        getCommand("jw-clean").setExecutor((CommandExecutor) jailclean);
        getCommand("jw-save").setExecutor((CommandExecutor) jailsave);
        getCommand("jw-config").setExecutor((CommandExecutor) jailconfigcmd);
        getCommand("jw-list").setExecutor((CommandExecutor) jaillist);
        getCommand("jw-delete").setExecutor((CommandExecutor) jaildelete);
        getCommand("jw-restart").setExecutor((CommandExecutor) jailrestart);
        getCommand("jw-info").setExecutor((CommandExecutor) jailinfo);
        getCommand("jw-free").setExecutor((CommandExecutor) jailfree);
        getCommand("jw-goto").setExecutor((CommandExecutor) jailgoto);
        getCommand("jw-give").setExecutor((CommandExecutor) jailgive);
        getCommand("jw-whitecmd").setExecutor((CommandExecutor) jailwhitecmd);
        getCommand("jw-reload").setExecutor((CommandExecutor) jailreload);
        getCommand("jw-increase").setExecutor((CommandExecutor) jailincrease);
        getCommand("jw-manageowners").setExecutor((CommandExecutor) jailmanageowners);

        allowBlocks.add("SAND");
        allowBlocks.add("DIRT");
        allowBlocks.add("STONE");
        allowBlocks.add("OBSIDIAN");
    }

    public String colorFormat(String str) {
        str = str.replaceAll("%black", "" + ChatColor.BLACK);
        str = str.replaceAll("%dark_blue", "" + ChatColor.DARK_BLUE);
        str = str.replaceAll("%dark_green", "" + ChatColor.DARK_GREEN);
        str = str.replaceAll("%dark_aqua", "" + ChatColor.DARK_AQUA);
        str = str.replaceAll("%dark_red", "" + ChatColor.DARK_RED);
        str = str.replaceAll("%dark_purple", "" + ChatColor.DARK_PURPLE);
        str = str.replaceAll("%gold", "" + ChatColor.GOLD);
        str = str.replaceAll("%gray", "" + ChatColor.GRAY);
        str = str.replaceAll("%dark_gray", "" + ChatColor.DARK_GRAY);
        str = str.replaceAll("%blue", "" + ChatColor.BLUE);
        str = str.replaceAll("%green", "" + ChatColor.GREEN);
        str = str.replaceAll("%aqua", "" + ChatColor.AQUA);
        str = str.replaceAll("%red", "" + ChatColor.RED);
        str = str.replaceAll("%light_purple", "" + ChatColor.LIGHT_PURPLE);
        str = str.replaceAll("%yellow", "" + ChatColor.YELLOW);
        str = str.replaceAll("%white", "" + ChatColor.WHITE);
        str = str.replaceAll("%magic", "" + ChatColor.MAGIC);
        str = str.replaceAll("%bold", "" + ChatColor.BOLD);
        str = str.replaceAll("%strikethrough", "" + ChatColor.STRIKETHROUGH);
        str = str.replaceAll("%underline", "" + ChatColor.UNDERLINE);
        str = str.replaceAll("%italic", "" + ChatColor.ITALIC);
        str = str.replaceAll("%reset", "" + ChatColor.RESET);

        return str;
    }

    public void initLang() {
        ConfigurationSection section = getLangConfig().getConfigurationSection("Messages");
        lang = section.getValues(false);
    }

    public String toLanguage(String str, Object... objects) {
        if (!lang.containsKey(str))
            return "";
        String mystr = lang.get(str).toString();
//		/* Add accent */
//		mystr = mystr.replaceAll("é", "�");
//		mystr = mystr.replaceAll("ê", "�");
//		mystr = mystr.replaceAll("�.", "�");

        String colored = colorFormat(mystr.replaceAll("\\$", "%"));
        String val = String.format(colored, objects);
        /* Cleaning val */
        val = val.replaceAll("\\s+", " ");
        val = val.trim();
        return (val);
    }


    @Override
    public void onDisable() {

    }

    public Vector<String> getAllowBlocks() {
        return allowBlocks;
    }

    public void setAllowBlocks(Vector<String> allowBlocks) {
        this.allowBlocks = allowBlocks;
    }

}