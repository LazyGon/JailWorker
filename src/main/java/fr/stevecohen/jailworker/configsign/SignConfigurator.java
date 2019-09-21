package fr.stevecohen.jailworker.configsign;

import java.util.Vector;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import fr.alienationgaming.jailworker.JailWorker;

public class SignConfigurator {
    private static final int SELECTOR_LINE_INDEX = 2;

    private JailWorker plugin;

    private MySign nextSign;
    private MySign prevSign;
    private MySign centralSign;
    private MySign upSign;
    private MySign downSign;

    private Player processingPlayer;
    private State state;
    private String jailname;
    private SignConfigurator.Util cfgSignUtil;
    private JailConfig currentConfig;

    private int nbrValue;
    private int index;

    public SignConfigurator(Player player, String jailname) {
        this.processingPlayer = player;
        this.jailname = jailname;
        this.cfgSignUtil = new SignConfigurator.Util();
        this.plugin = JailWorker.getInstance();
        this.currentConfig = new JailConfig(jailname);
    }

    public void initConfigProcess() {
        this.currentConfig.loadJailConfig();
        nextSign.setClickListener(new OnConfigSignClickedListener(nextSign, processingPlayer, new SignClickedCallback() { // Rignt clicked
            @Override
            public void onSignClicked() {
                SignConfigurator.this.state.updateCfg(currentConfig, centralSign);
                SignConfigurator.this.state.next(SignConfigurator.this);
                SignConfigurator.this.udpdateConfigState();
            }
        }));
        prevSign.setClickListener(new OnConfigSignClickedListener(prevSign, processingPlayer, new SignClickedCallback() { // Left clicked
            @Override
            public void onSignClicked() {
                SignConfigurator.this.state.previous(SignConfigurator.this);
                SignConfigurator.this.udpdateConfigState();
            }
        }));
        upSign.setClickListener(new OnConfigSignClickedListener(upSign, processingPlayer, new SignClickedCallback() { // Up clicked
            @Override
            public void onSignClicked() {
                nbrValue = nbrValue > 1 ? cfgSignUtil.getPreviousNumber(nbrValue) : 1;
                index = index - (index > 0 ? 1 : 0);
                updateCentralSign(state);
            }
        }));
        downSign.setClickListener(new OnConfigSignClickedListener(downSign, processingPlayer, new SignClickedCallback() { // Down clicked
            @Override
            public void onSignClicked() {
                nbrValue = cfgSignUtil.getNextNumber(nbrValue);
                if (state == State.MATERIAL)
                    index = index + (index < plugin.getAllowBlocks().size() - 1 ? 1 : 0);
                updateCentralSign(state);
            }
        }));
    }

    public boolean getRelativesSignsByCenterSign(Block centerSign) {
        String error = "";
        /* Check left and right */
        if (centerSign.getRelative(BlockFace.WEST).getState() instanceof Sign && centerSign.getRelative(BlockFace.EAST).getState() instanceof Sign) {
            this.prevSign = new MySign((Sign) centerSign.getRelative(BlockFace.WEST).getState());
            this.nextSign = new MySign((Sign) centerSign.getRelative(BlockFace.EAST).getState());
        } else
            /* Check orher left and right */
            if (centerSign.getRelative(BlockFace.NORTH).getState() instanceof Sign && centerSign.getRelative(BlockFace.SOUTH).getState() instanceof Sign) {
                this.prevSign = new MySign((Sign) centerSign.getRelative(BlockFace.NORTH).getState());
                this.nextSign = new MySign((Sign) centerSign.getRelative(BlockFace.SOUTH).getState());
            } else
                error = "error-listener-configsign-cross-error1";
        /* Check top and bottom */
        if (centerSign.getRelative(BlockFace.UP).getState() instanceof Sign)
            this.upSign = new MySign((Sign) centerSign.getRelative(BlockFace.UP).getState());
        else
            error = "error-listener-configsign-cross-error2";

        if (centerSign.getRelative(BlockFace.DOWN).getState() instanceof Sign)
            this.downSign = new MySign((Sign) centerSign.getRelative(BlockFace.DOWN).getState());
        else
            error = "error-listener-configsign-cross-error3";
        this.centralSign = new MySign((Sign) centerSign.getState());

        /* Check error */
        if (error != "") {
            processingPlayer.sendMessage(plugin.toLanguage(error));
            return false;
        }
        return true;
    }

    public void startProcess() {
        state = State.INTRO;
        this.udpdateConfigState();
    }

    public void updateCentralSign(State state) {
        switch (state) {
            case INTRO:
                centralSign.setCustomText(
                        "[" + jailname + "]",
                        "To start the",
                        "configuration",
                        "click GO ->")
                        .update();
                break;
            case MATERIAL:
                Vector<String> allowedBlocks = plugin.getAllowBlocks();
                String line0m = "[" + state.label + "]";
                String line1m = " " + (index - 1 >= 0 ? allowedBlocks.get(index - 1) : "");
                String line2m = ">" + (allowedBlocks.get(index));
                String line3m = " " + (index + 1 <= (allowedBlocks.size() - 1) ? allowedBlocks.get(index + 1) : "");

                centralSign.setLine(0, line0m);
                centralSign.setLine(1, line1m + "               ".substring(line1m.length()));
                centralSign.setLine(2, line2m + "               ".substring(line2m.length()));
                centralSign.setLine(3, line3m + "               ".substring(line3m.length()));
                centralSign.update();
                break;
            case CONFIRMATION:
                centralSign.setCustomText(
                        "[" + jailname + "]",
                        "M: " + currentConfig.getType(),
                        "P: " + currentConfig.getBlocks(),
                        "C: " + currentConfig.getMaxSand() + " | F: " + currentConfig.getSpeed())
                        .update();
                break;
            default:
                String line0d = "[" + state.label + "]";
                String line1d = " " + (nbrValue > 1 ? cfgSignUtil.getPreviousNumber(nbrValue) : "");
                String line2d = ">" + nbrValue;
                String line3d = " " + cfgSignUtil.getNextNumber(nbrValue);

                centralSign.setLine(0, line0d);
                centralSign.setLine(1, line1d + "               ".substring(line1d.length()));
                centralSign.setLine(2, line2d + "               ".substring(line2d.length()));
                centralSign.setLine(3, line3d + "               ".substring(line3d.length()));
                centralSign.update();
                break;
        }
    }

    public void buildIntroState() {
        prevSign.clearSign().update();
        nextSign.drawGo().update();
        upSign.clearSign().update();
        downSign.clearSign().update();
        updateCentralSign(state);
        processingPlayer.sendMessage(plugin.toLanguage("help-listener-configsign-cross-introstate"));
    }

    public void buildMaterialState() {
        prevSign.drawLeftArrow().update();
        nextSign.drawRightArrow().update();
        upSign.drawUpArrow().update();
        downSign.drawDownArrow().update();
        index = plugin.getAllowBlocks().indexOf(currentConfig.getType());
        updateCentralSign(state);
        processingPlayer.sendMessage(plugin.toLanguage("help-listener-configsign-cross-materialstate"));
    }

    public void buildDefaultPunishmentState() {
        prevSign.drawLeftArrow().update();
        nextSign.drawRightArrow().update();
        upSign.drawUpArrow().update();
        downSign.drawDownArrow().update();
        nbrValue = currentConfig.getBlocks();
        updateCentralSign(state);
        processingPlayer.sendMessage(plugin.toLanguage("help-listener-configsign-cross-punishmentstate"));
    }

    public void buildJailCapacityState() {
        prevSign.drawLeftArrow().update();
        nextSign.drawRightArrow().update();
        upSign.drawUpArrow().update();
        downSign.drawDownArrow().update();
        nbrValue = currentConfig.getMaxSand();
        updateCentralSign(state);
        processingPlayer.sendMessage(plugin.toLanguage("help-listener-configsign-cross-capacitystate"));
    }

    public void buildFrequencyState() {
        prevSign.drawLeftArrow().update();
        nextSign.drawRightArrow().update();
        upSign.drawUpArrow().update();
        downSign.drawDownArrow().update();
        nbrValue = currentConfig.getSpeed();
        updateCentralSign(state);
        processingPlayer.sendMessage(plugin.toLanguage("help-listener-configsign-cross-frequencystate"));
    }

    public void buildConfirmationState() {
        prevSign.drawLeftArrow().update();
        nextSign.drawConfirmationValid().update();
        ;
        upSign.clearSign().update();
        downSign.clearSign().update();
        updateCentralSign(state);
        processingPlayer.sendMessage(plugin.toLanguage("help-listener-configsign-cross-confirmationstate"));
    }

    public void destroySigns() {
        prevSign.location.getBlock().setType(Material.AIR);
        nextSign.location.getBlock().setType(Material.AIR);
        upSign.location.getBlock().setType(Material.AIR);
        downSign.location.getBlock().setType(Material.AIR);
        centralSign.location.getBlock().setType(Material.AIR);
    }

    private void cleanConfigurator() {
        PlayerInteractEvent.getHandlerList().unregister(nextSign.getClickListener());
        PlayerInteractEvent.getHandlerList().unregister(prevSign.getClickListener());
        PlayerInteractEvent.getHandlerList().unregister(upSign.getClickListener());
        PlayerInteractEvent.getHandlerList().unregister(downSign.getClickListener());

        processingPlayer.sendMessage(plugin.toLanguage("help-listener-configsign-cross-finish"));
    }

    public void udpdateConfigState() {
        switch (state) {
            case INTRO:
                buildIntroState();
                break;
            case MATERIAL:
                buildMaterialState();
                break;
            case DEFAULT_PUNISHMENT:
                buildDefaultPunishmentState();
                break;
            case JAIL_CAPACITY:
                buildJailCapacityState();
                break;
            case FREQUENCY:
                buildFrequencyState();
                break;
            case CONFIRMATION:
                buildConfirmationState();
                break;
            case END:
                destroySigns();
                cleanConfigurator();
                break;
        }
    }

    /********************* Inner enum ***********************/
    public enum State {

        INTRO("INTRO") {
            void next(SignConfigurator entity) {
                entity.state = MATERIAL;
            }

            void previous(SignConfigurator entity) {
            }

            void updateCfg(JailConfig cfg, MySign centralSign) {
            }
        },
        MATERIAL("MATERIAL") {
            void next(SignConfigurator entity) {
                entity.state = DEFAULT_PUNISHMENT;
            }

            void previous(SignConfigurator entity) {
                entity.state = INTRO;
            }

            void updateCfg(JailConfig cfg, MySign centralSign) {
                cfg.setType(centralSign.getLine(SELECTOR_LINE_INDEX).substring(1).replaceAll(" ", ""));
            }
        },
        DEFAULT_PUNISHMENT("DEF PUNISH") {
            void next(SignConfigurator entity) {
                entity.state = JAIL_CAPACITY;
            }

            void previous(SignConfigurator entity) {
                entity.state = MATERIAL;
            }

            void updateCfg(JailConfig cfg, MySign centralSign) {
                cfg.setBlocks(Integer.valueOf(centralSign.getLine(SELECTOR_LINE_INDEX).substring(1).replaceAll(" ", "")));
            }
        },
        JAIL_CAPACITY("CAPACITY") {
            void next(SignConfigurator entity) {
                entity.state = FREQUENCY;
            }

            void previous(SignConfigurator entity) {
                entity.state = DEFAULT_PUNISHMENT;
            }

            void updateCfg(JailConfig cfg, MySign centralSign) {
                cfg.setMaxSand(Integer.valueOf(centralSign.getLine(SELECTOR_LINE_INDEX).substring(1).replaceAll(" ", "")));
            }
        },
        FREQUENCY("FREQUENCY") {
            void next(SignConfigurator entity) {
                entity.state = CONFIRMATION;
            }

            void previous(SignConfigurator entity) {
                entity.state = JAIL_CAPACITY;
            }

            void updateCfg(JailConfig cfg, MySign centralSign) {
                cfg.setSpeed(Integer.valueOf(centralSign.getLine(SELECTOR_LINE_INDEX).substring(1).replaceAll(" ", "")));
            }
        },
        CONFIRMATION("CONFIRMATION") {
            void next(SignConfigurator entity) {
                entity.state = END;
            }

            void previous(SignConfigurator entity) {
                entity.state = FREQUENCY;
            }

            void updateCfg(JailConfig cfg, MySign centralSign) {
                cfg.saveJailConfig();
            }
        },
        END("END") {
            void next(SignConfigurator entity) {
            }

            void previous(SignConfigurator entity) {
            }

            void updateCfg(JailConfig cfg, MySign centralSign) {
            }
        };

        abstract void next(SignConfigurator entity);

        abstract void previous(SignConfigurator entity);

        abstract void updateCfg(JailConfig cfg, MySign centralSign);

        private State state;
        public String label;

        public State getState() {
            return state;
        }

        private State(String label) {
            this.label = label;
        }

    }


    /********** Inner callback class **********/
    public interface SignClickedCallback {
        public void onSignClicked();
    }

    /********** Inner util class **********/
    private class Util {

        public int getNextNumberByAlpha(int nbr, int alpha) {
            if (nbr < 20)
                return nbr + (1 * alpha);
            else if (nbr < 50)
                return nbr + (5 * alpha);
            else if (nbr < 200)
                return nbr + (10 * alpha);
            else if (nbr < 1000)
                return nbr + (50 * alpha);
            else
                return nbr + (100 * alpha);
        }

        public int getNextNumber(int nbr) {
            return getNextNumberByAlpha(nbr, 1);
        }

        public int getPreviousNumber(int nbr) {
            return getNextNumberByAlpha(nbr, -1);
        }

        //		public int getPrevNumber(int nbr) {
        //			if (nbr > 1000)
        //				return nbr-100;
        //			else if (nbr > 200)
        //				return nbr-50;
        //			else if (nbr > 50)
        //				return nbr-10;
        //			else if (nbr > 20)
        //				return nbr-5;
        //			else
        //				return nbr-1;
        //		}
    }
}
