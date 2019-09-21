package fr.stevecohen.jailworker.configsign;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class MySign {

    private String line0;
    private String line1;
    private String line2;
    private String line3;

    public Location location;

    private OnConfigSignClickedListener clickListener;

    public MySign(Sign sign) {
        this.location = sign.getLocation();
        setLine(0, sign.getLine(0));
        setLine(1, sign.getLine(1));
        setLine(2, sign.getLine(2));
        setLine(3, sign.getLine(3));
    }

    public MySign drawUpArrow() {
        this.setLine(0, "/\\");
        this.setLine(1, "/ | \\");
        this.setLine(2, "|");
        this.setLine(3, "|");
        return this;
    }

    public MySign drawDownArrow() {
        this.setLine(0, "|");
        this.setLine(1, "|");
        this.setLine(2, "\\ | /");
        this.setLine(3, "\\/");
        return this;
    }

    public MySign drawLeftArrow() {
        this.setLine(0, "/                 ");
        this.setLine(1, "/==============");
        this.setLine(2, "\\==============");
        this.setLine(3, "\\                 ");
        return this;
    }

    public MySign drawRightArrow() {
        this.setLine(0, "                 \\");
        this.setLine(1, "==============\\");
        this.setLine(2, "==============/");
        this.setLine(3, "                 /");
        return this;
    }

    public MySign drawConfirmationValid() {
        this.setLine(0, "  ####       #  #  ");
        this.setLine(1, "  #   #       ###   ");
        this.setLine(2, "  #   #       #  #  ");
        this.setLine(3, "  ####       #   # ");
        return this;
    }

    public MySign drawGo() {
        this.setLine(0, "  ####      ####  ");
        this.setLine(1, "  #           #   #  ");
        this.setLine(2, "  #  ###    #   #  ");
        this.setLine(3, "  ####      ####  ");
        return this;
    }

    public MySign setCustomText(String line0, String line1, String line2, String line3) {
        this.setLine(0, line0);
        this.setLine(1, line1);
        this.setLine(2, line2);
        this.setLine(3, line3);
        return this;
    }

    public MySign clearSign() {
        this.setLine(0, "");
        this.setLine(1, "");
        this.setLine(2, "");
        this.setLine(3, "");
        return this;
    }

    public void update() {
        Block block = location.getBlock();
        Sign realSign = (Sign) block.getState();
        realSign.setLine(0, getLine(0));
        realSign.setLine(1, getLine(1));
        realSign.setLine(2, getLine(2));
        realSign.setLine(3, getLine(3));
        realSign.update();
    }

    public String getLine(int line) {
        switch (line) {
            case 0:
                return line0;
            case 1:
                return line1;
            case 2:
                return line2;
            case 3:
                return line3;
            default:
                return "";
        }
    }

    public void setLine(int index, String label) {
        switch (index) {
            case 0:
                line0 = label;
            case 1:
                line1 = label;
            case 2:
                line2 = label;
            case 3:
                line3 = label;
        }
    }

    public OnConfigSignClickedListener getClickListener() {
        return clickListener;
    }

    public void setClickListener(OnConfigSignClickedListener clickListener) {
        this.clickListener = clickListener;
    }
}
