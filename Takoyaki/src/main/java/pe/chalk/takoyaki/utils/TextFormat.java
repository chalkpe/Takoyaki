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
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2015-04-15
 */
public enum TextFormat {
    ESCAPE ("§", Integer.MIN_VALUE, ""),

    RESET         ("r",  0, ""),
    OBFUSCATED    ("k", -1, ""),
    BOLD          ("l",  1, "font-weight: bold;"),
    STRIKETHROUGH ("m",  9, "text-decoration: line-through;"),
    UNDERLINE     ("n",  4, "text-decoration: underline;"),
    ITALIC        ("o",  3, "font-style: italic;"),

    BLACK        ("0", 30, "color: #000000"),
    DARK_BLUE    ("1", 34, "color: #5394EC"),
    DARK_GREEN   ("2", 32, "color: #007F00"),
    DARK_AQUA    ("3", 36, "color: #33CCCC"),
    DARK_RED     ("4", 31, "color: #FF6B68"),
    DARK_PURPLE  ("5", 35, "color: #FF70FF"),
    GOLD         ("6", 33, "color: #CDCD00"),
    GRAY         ("7", 37, "color: #999999"),

    DARK_GRAY    ("8", 90, "color: #555555"),
    BLUE         ("9", 94, "color: #7EAEF1"),
    GREEN        ("a", 92, "color: #70FF70"),
    AQUA         ("b", 96, "color: #6CDADA"),
    RED          ("c", 91, "color: #FF8785"),
    LIGHT_PURPLE ("d", 95, "color: #FF99FF"),
    YELLOW       ("e", 93, "color: #FFFF00"),
    WHITE        ("f", 97, "color: #BBBBBB"),

    BACKGROUND_BLACK        ("_0",  40, "background-color: #000000"),
    BACKGROUND_DARK_BLUE    ("_1",  44, "background-color: #5394EC"),
    BACKGROUND_DARK_GREEN   ("_2",  42, "background-color: #007F00"),
    BACKGROUND_DARK_AQUA    ("_3",  46, "background-color: #33CCCC"),
    BACKGROUND_DARK_RED     ("_4",  41, "background-color: #FF6B68"),
    BACKGROUND_DARK_PURPLE  ("_5",  45, "background-color: #FF70FF"),
    BACKGROUND_GOLD         ("_6",  43, "background-color: #CDCD00"),
    BACKGROUND_GRAY         ("_7",  47, "background-color: #999999"),

    BACKGROUND_DARK_GRAY    ("_8", 100, "background-color: #555555"),
    BACKGROUND_BLUE         ("_9", 104, "background-color: #7EAEF1"),
    BACKGROUND_GREEN        ("_a", 102, "background-color: #70FF70"),
    BACKGROUND_AQUA         ("_b", 106, "background-color: #6CDADA"),
    BACKGROUND_RED          ("_c", 101, "background-color: #FF8785"),
    BACKGROUND_LIGHT_PURPLE ("_d", 105, "background-color: #FF99FF"),
    BACKGROUND_YELLOW       ("_e", 103, "background-color: #FFFF00"),
    BACKGROUND_WHITE        ("_f", 107, "background-color: #BBBBBB");

    public static final String FORMAT_MINECRAFT  = "§%s";
    public static final String FORMAT_ANSI       = "\u001B[%dm";
    public static final String FORMAT_HTML_OPEN  = "<span style=\"%s\">";
    public static final String FORMAT_HTML_CLOSE = "</span>";

    public static final Pattern PATTERN_MINECRAFT = Pattern.compile("(?<!§)(§_?[0-9a-f]|§[k-or])");

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.KOREA);
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.KOREA);

    public static final Map<String, TextFormat> MAP_BY_STRING = Arrays.stream(TextFormat.values()).collect(Collectors.toMap(TextFormat::toString, format -> format));

    private final String minecraftCode;
    private final int ansiCode;
    private final String styleAttribute;

    TextFormat(String minecraftCode, int ansiCode, String styleAttribute){
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
        return this.ansiCode < 0 ? "" : String.format(FORMAT_ANSI, ansiCode);
    }

    public String getHtmlTag(){
        return String.format(FORMAT_HTML_OPEN, this.styleAttribute);
    }

    public enum Type {
        NONE, MINECRAFT, ANSI, HTML
    }

    public static String encode(String string){
        return string.replaceAll("§", TextFormat.ESCAPE.toString());
    }

    public static String decode(String minecraftString, Type type){
        if(minecraftString == null || minecraftString.equals("")){
            return "";
        }

        switch(type){
            default:
            case MINECRAFT:
                return minecraftString;

            case NONE:
                return replaceFormats(minecraftString, format -> "");

            case ANSI:
                return replaceFormats(minecraftString, TextFormat::getAnsiCode);

            case HTML:
                final Function<TextFormat, String> htmlReplacer = new Function<TextFormat, String>(){
                    private int indentLevel = 0;

                    @Override
                    public String apply(TextFormat textFormat){
                        if(textFormat == TextFormat.RESET){
                            String tag = TextFormat.getCloseTags(indentLevel); indentLevel = 0;
                            return tag;
                        }else{
                            indentLevel++;
                            return textFormat.getHtmlTag();
                        }
                    }
                }.andThen(str -> str.replaceAll("\n", "<br>"));
                return String.format("<span style=\"vertical-align: middle\">%s%s</span>", replaceFormats(minecraftString, htmlReplacer), htmlReplacer.apply(TextFormat.RESET));
        }
    }

    private static String replaceFormats(String minecraftString, Function<TextFormat, String> replacer){
        StringBuffer buffer = new StringBuffer(minecraftString.length());
        Matcher matcher = TextFormat.PATTERN_MINECRAFT.matcher(minecraftString);

        while(matcher.find()) matcher.appendReplacement(buffer, replacer.apply(TextFormat.MAP_BY_STRING.get(matcher.group(1))));
        matcher.appendTail(buffer);

        return buffer.toString().replaceAll(TextFormat.ESCAPE.toString(), "§");
    }

    private static String getCloseTags(int indentLevel){
        if(indentLevel <= 0){
            return "";
        }

        return String.join("", Collections.nCopies(indentLevel, FORMAT_HTML_CLOSE));
    }
}