package pe.chalk.takoyaki.logger;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-15
 */
public enum ChatColor {
    RESET('r', "0"),

    BLACK('0', "30"),
    DARK_BLUE('1', "34"),
    DARK_GREEN('2', "32"),
    DARK_AQUA('3', "36"),
    DARK_RED('4', "31"),
    DARK_PURPLE('5', "35"),
    DARK_YELLOW('6', "33"),
    GRAY('7', "37"),

    DARK_GRAY('8', "90"),
    BLUE('9', "94"),
    GREEN('a', "92"),
    AQUA('b', "96"),
    RED('c', "91"),
    LIGHT_PURPLE('d', "95"),
    YELLOW('e', "93"),
    WHITE('f', "97");

    public static final char MINECRAFT_BEGIN = '§';
    public static final String ANSI_ESCAPE = "\u001B[%sm";

    private final char minecraftCode;
    private final String ansiCode;

    private final String string;

    private ChatColor(char minecraftCode, String ansiCode){
        this.minecraftCode = minecraftCode;
        this.ansiCode = ansiCode;

        this.string = new String(new char[]{ChatColor.MINECRAFT_BEGIN, this.getMinecraftCode()});
    }

    public char getMinecraftCode(){
        return this.minecraftCode;
    }

    public String getAnsiCode(){
        return String.format(ANSI_ESCAPE, ansiCode);
    }

    @Override
    public String toString(){
        return this.string;
    }

    public static String replaceToAnsi(String minecraftString){
        if(minecraftString == null || minecraftString.equals("")){
            return "";
        }

        for(ChatColor color : ChatColor.values()){
            minecraftString = minecraftString.replaceAll(color.toString(), color.getAnsiCode());
        }
        return minecraftString;
    }
}
