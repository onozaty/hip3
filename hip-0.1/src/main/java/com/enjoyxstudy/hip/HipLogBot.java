/*
 * Copyright (c) 2008 onozaty (http://www.enjoyxstudy.com)
 *
 *  The software is licensed under the GNU General Public License (GPL)
 *  For details, see http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 */
package com.enjoyxstudy.hip;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jibble.logbot.LogBot;
import org.jibble.pircbot.Colors;

/**
 * @author onozaty
 */
public class HipLogBot extends LogBot {

    /** url pattern */
    private static final Pattern URL_PATTERN = Pattern
            .compile("(?i:\\b((http|https|ftp|irc)://[^\\s]+))");

    /** date format */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd");

    /** time format */
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat(
            "HH:mm");

    /** log file encoding */
    private static final String FILE_ENCODING = "UTF-8";

    /** output directory */
    private File outDir;

    /**
     * @param name
     * @param outDir
     * @param joinMessage
     */
    public HipLogBot(String name, File outDir, String joinMessage) {
        super(name, outDir, joinMessage);
        this.outDir = outDir;
    }

    /**
     * @see org.jibble.logbot.LogBot#append(java.lang.String, java.lang.String)
     */
    public void append(String color, String line) {
        line = Colors.removeFormattingAndColors(line);

        line = line.replaceAll("&", "&amp;");
        line = line.replaceAll("<", "&lt;");
        line = line.replaceAll(">", "&gt;");

        Matcher matcher = URL_PATTERN.matcher(line);
        line = matcher.replaceAll("<a href=\"$1\">$1</a>");

        Date now = new Date();
        String date = DATE_FORMAT.format(now);
        String time = TIME_FORMAT.format(now);
        File file = new File(outDir, date + ".log");
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, true), FILE_ENCODING));
            String entry = "<span class=\"irc-date\">[" + time
                    + "]</span> <span class=\"" + color + "\">" + line
                    + "</span><br />";
            writer.write(entry);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Could not write to log: " + e);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                System.out.println("Could not write to log: " + e);
            }
        }
    }
}
