/*
 * Copyright 2014-2015 ChalkPE
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pe.chalk.takoyaki.utils;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2015-04-15
 */
public enum TextFormat {
    RESET         ('r',  0, ""),
    OBFUSCATED    ('k', -1, ""),
    BOLD          ('l',  1, "font-weight: bold;"),
    STRIKETHROUGH ('m',  9, "text-decoration: line-through;"),
    UNDERLINE     ('n',  4, "text-decoration: underline;"),
    ITALIC        ('o',  3, "font-style: italic;"),
    BLACK         ('0', 30, "color: #000000"),
    DARK_BLUE     ('1', 34, "color: #5394EC"),
    DARK_GREEN    ('2', 32, "color: #007F00"),
    DARK_AQUA     ('3', 36, "color: #33CCCC"),
    DARK_RED      ('4', 31, "color: #FF6B68"),
    DARK_PURPLE   ('5', 35, "color: #FF70FF"),
    GOLD          ('6', 33, "color: #CDCD00"),
    GRAY          ('7', 37, "color: #999999"),
    DARK_GRAY     ('8', 90, "color: #555555"),
    BLUE          ('9', 94, "color: #7EAEF1"),
    GREEN         ('a', 92, "color: #70FF70"),
    AQUA          ('b', 96, "color: #6CDADA"),
    RED           ('c', 91, "color: #FF8785"),
    LIGHT_PURPLE  ('d', 95, "color: #FF99FF"),
    YELLOW        ('e', 93, "color: #FFFF00"),
    WHITE         ('f', 97, "color: #BBBBBB");

    public static final String FORMAT_MINECRAFT  = "§%c";
    public static final String FORMAT_ANSI       = "\u001B[%sm";
    public static final String FORMAT_HTML_OPEN  = "<span style=\"%s\">";
    public static final String FORMAT_HTML_CLOSE = "</span>";

    public static final Pattern PATTERN_MINECRAFT = Pattern.compile("(§[0-9a-fl-or])");

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.KOREA);
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.KOREA);

    @SuppressWarnings("serial")
    public static final Map<String, TextFormat> MAP_BY_STRING = new HashMap<String, TextFormat>(){{
        for(TextFormat textFormat : TextFormat.values()){
            put(textFormat.toString(), textFormat);
        }
    }};

    private final char minecraftCode;
    private final int ansiCode;
    private final String styleAttribute;

    TextFormat(char minecraftCode, int ansiCode, String styleAttribute){
        this.minecraftCode = minecraftCode;
        this.ansiCode = ansiCode;
        this.styleAttribute = styleAttribute;
    }

    @Override
    public String toString(){
        return this.getMinecraftCode();
    }

    public String getMinecraftCode(){
        return String.format(TextFormat.FORMAT_MINECRAFT, this.minecraftCode);
    }

    public String getAnsiCode(){
        return this.ansiCode == -1 ? "" : String.format(FORMAT_ANSI, ansiCode);
    }

    public String getHtmlTag(){
        return String.format(FORMAT_HTML_OPEN, this.styleAttribute);
    }

    public enum Type {
        NONE, MINECRAFT, ANSI, HTML
    }

    public static String replaceTo(Type type, String minecraftString){
        if(minecraftString == null || minecraftString.equals("")){
            return "";
        }

        switch(type){
            case NONE:
                return PATTERN_MINECRAFT.matcher(minecraftString).replaceAll("");

            default:
            case MINECRAFT:
                return minecraftString;

            case ANSI:
                for(TextFormat color : TextFormat.values()){
                    minecraftString = minecraftString.replaceAll(color.toString(), color.getAnsiCode());
                }
                return minecraftString;

            case HTML:
                int indentLevel = 0;
                StringBuffer buffer = new StringBuffer(minecraftString.length());
                Matcher matcher = PATTERN_MINECRAFT.matcher(minecraftString);
                while(matcher.find()){
                    TextFormat textFormat = MAP_BY_STRING.get(matcher.group(1));
                    if(textFormat == TextFormat.RESET){
                        matcher.appendReplacement(buffer, TextFormat.getCloseTags(indentLevel));
                        indentLevel = 0;
                    }else{
                        matcher.appendReplacement(buffer, textFormat.getHtmlTag());
                        indentLevel++;
                    }
                }
                matcher.appendTail(buffer);
                return "<span style=\"vertical-align: middle\">" + buffer.toString().replaceAll("\n", "<br>").concat(getCloseTags(indentLevel)) + "</span>";
        }
    }

    private static String getCloseTags(int indentLevel){
        if(indentLevel == 0){
            return "";
        }
        StringBuilder closeTagBuilder = new StringBuilder(FORMAT_HTML_CLOSE.length() * indentLevel);
        for(int i = 0; i < indentLevel; i++){
            closeTagBuilder.append(FORMAT_HTML_CLOSE);
        }
        return closeTagBuilder.toString();
    }
}