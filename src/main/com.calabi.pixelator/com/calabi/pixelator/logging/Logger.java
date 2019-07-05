package com.calabi.pixelator.logging;

import java.util.Map;

import javafx.scene.input.InputEvent;

import org.apache.logging.log4j.LogManager;

import com.calabi.pixelator.util.EventUtil;

public class Logger {

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger("com.pixelator.app");

    public static final int SUBJECT_LENGTH = 15;
    public static final int WHO_LENGTH = 16;
    public static final int WHAT_LENGTH = 4000;

    public static void log(Object message) {
        LOGGER.info(message);
    }

    public static void error(Throwable e) {
        LOGGER.error("", e);
    }

    public static void log(Object subject, Object who, Object what) {
        LOGGER.info(toSubject(subject) + toWho(who) + " " + toWhat(what));
    }

    public static void log(Object subject, Object who, Object what, Object where) {
        log(subject, who, (what == null ? "" : what) + " at " + where);
    }

    public static void log(Object subject, Object what) {
        log(subject, "", what);
    }

    public static void logEvent(InputEvent e, String action) {
        StringBuilder sb = new StringBuilder(e.getClass().getSimpleName() + ": ");
        if (EventUtil.isControlDown(e)) {
            sb.append("CTRL + ");
        }
        if (EventUtil.isShiftDown(e)) {
            sb.append("SHIFT + ");
        }
        if (EventUtil.isAltDown(e)) {
            sb.append("ALT + ");
        }
        if (EventUtil.getKey(e) == null) {
            sb.append("<no key>");
        } else {
            sb.append('\'').append(EventUtil.getKey(e)).append('\'');
        }
        if (EventUtil.getVScroll(e) != null) {
            sb.append(", V-Scroll: ").append(EventUtil.getVScroll(e));
        }
        if (EventUtil.getHScroll(e) != null) {
            sb.append(", H-Scroll: ").append(EventUtil.getHScroll(e));
        }
        sb.append(" -> ").append(action);
        log(sb.toString());
    }

    private static String toSubject(Object subject) {
        return format(subject == null ? "NULL" : subject.toString().toUpperCase(), SUBJECT_LENGTH, " ");
    }

    private static String toWho(Object subject) {
        return format(subject == null ? "NULL" : subject.toString().toUpperCase(), WHO_LENGTH, ": ");
    }

    private static String toWhat(Object subject) {
        return format(subject == null ? "null" : subject.toString(), WHAT_LENGTH, ".");
    }

    private static String format(String string, int length, String separator) {
        separator = string.isEmpty() ? "" : separator;
        String s = string.trim();
        String subString = s.substring(0, Math.min(length - separator.length(), s.length()));
        StringBuilder builder = new StringBuilder(subString + separator);
        for (int i = builder.length(); i < length; i++) {
            builder.append(" ");
        }
        return builder.toString();
    }

    public static <K, V> String toString(Map<K, V> map) {
        StringBuilder out = new StringBuilder();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            String key = entry.getKey() == null ? "null" : entry.getKey().toString();
            String value = entry.getValue() == null ? "null" : entry.getValue().toString();
            out.append(String.format("[%s -> %s] ", key, value));
        }
        return out.toString();
    }
}
