package com.asolutions.scmsshd.event.listener.impl;

import org.apache.log4j.Category;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

/**
 * @author Oleg Ilyenko
 */
public abstract class BaseLogListener {

    protected Category log;

    private String path;

    private String maxFileSize = "20MB";

    private int maxBackupIndex = 10;

    private String pattern = "%d %m%n";

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(String maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public int getMaxBackupIndex() {
        return maxBackupIndex;
    }

    public void setMaxBackupIndex(int maxBackupIndex) {
        this.maxBackupIndex = maxBackupIndex;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setupLogger() {
        RollingFileAppender appender = new RollingFileAppender();

        if (path != null && !path.trim().equals("")) {
            appender.setFile(path);
        } else {
            appender.setFile(getDefaultPath());
        }

        appender.setName(getCategory() + "-appender");
        appender.setMaxFileSize(maxFileSize);
        appender.setMaxBackupIndex(maxBackupIndex);
        appender.setAppend(true);
        appender.setLayout(new PatternLayout(pattern));

        appender.activateOptions();

        log = org.apache.log4j.Logger.getLogger(getCategory());
        log.addAppender(appender);
        log.setAdditivity(false);
    }

    protected abstract String getCategory();
    protected abstract String getDefaultPath();
}
