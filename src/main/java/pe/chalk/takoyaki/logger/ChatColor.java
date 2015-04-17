package pe.chalk.takoyaki.logger;

import java.util.regex.Pattern;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-15
 */
public enum ChatColor {
    RESET('r', 0),
    BOLD('l', 1),
    STRIKETHROUGH('m', 9),
    UNDERLINE('n', 4),
    ITALIC('o', 3),

    BLACK('0', 30),
    DARK_BLUE('1', 34),
    DARK_GREEN('2', 32),
    DARK_AQUA('3', 36),
    DARK_RED('4', 31),
    DARK_PURPLE('5', 35),
    GOLD('6', 33),
    GRAY('7', 37),

    DARK_GRAY('8', 90),
    BLUE('9', 94),
    GREEN('a', 92),
    AQUA('b', 96),
    RED('c', 91),
    LIGHT_PURPLE('d', 95),
    YELLOW('e', 93),
    WHITE('f', 97);

    public static final String FORMAT_MINECRAFT = "§%c";
    public static final String FORMAT_ANSI = "\u001B[%sm";

    public static final Pattern PATTERN_MINECRAFT = Pattern.compile("§[0-9A-FL-OR]");

    private final char minecraftCode;
    private final int ansiCode;

    ChatColor(char minecraftCode, int ansiCode){
        this.minecraftCode = minecraftCode;
        this.ansiCode = ansiCode;
    }

    public char getMinecraftCode(){
        return this.minecraftCode;
    }

    public String getAnsiCode(){
        return String.format(FORMAT_ANSI, ansiCode);
    }

    @Override
    public String toString(){
        return String.format(ChatColor.FORMAT_MINECRAFT, this.getMinecraftCode());
    }

    public enum ReplaceType {
        NONE, MINECRAFT, ANSI
    }

    public static String replaceTo(ReplaceType replaceType, String minecraftString){
        if(minecraftString == null || minecraftString.equals("")){
            return "";
        }

        switch(replaceType){
            default:
            case MINECRAFT:
                return minecraftString;

            case NONE:
                return PATTERN_MINECRAFT.matcher(minecraftString).replaceAll("");

            case ANSI:
                for(ChatColor color : ChatColor.values()){
                    minecraftString = minecraftString.replaceAll(color.toString(), color.getAnsiCode());
                }
                return minecraftString;
        }
    }
}