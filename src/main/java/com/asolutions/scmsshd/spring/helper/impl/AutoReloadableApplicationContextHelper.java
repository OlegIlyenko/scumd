package com.asolutions.scmsshd.spring.helper.impl;

import com.asolutions.scmsshd.spring.helper.ApplicationContextHelper;
import com.asolutions.scmsshd.util.SpringUtil;
import org.springframework.context.support.AbstractRefreshableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.File;

/**
 * @author Oleg Ilyenko
 */
public class AutoReloadableApplicationContextHelper implements ApplicationContextHelper {

    private File applicationContextFile;

    protected AbstractRefreshableApplicationContext applicationContext;

    private long lastModified = -1;

    public AutoReloadableApplicationContextHelper(String applicationContextPath) {
        this.applicationContextFile = new File(applicationContextPath);
    }

    @Override
    public synchronized <T> T getBean(Class<T> desiredClass) {
        return getApplicationContext().getBean(desiredClass);
    }

    protected AbstractRefreshableApplicationContext getApplicationContext() {
        if (lastModified < 0) {
            applicationContext = createApplicationContext();
            lastModified = applicationContextFile.lastModified();
        } else if (lastModified != applicationContextFile.lastModified()) {
            doReload();
            lastModified = applicationContextFile.lastModified();
        }

        return applicationContext;
    }

    protected AbstractRefreshableApplicationContext createApplicationContext() {
        return new FileSystemXmlApplicationContext(SpringUtil.fixConfigLocation(applicationContextFile.getAbsolutePath()));
    }

    protected void doReload() {
        applicationContext.refresh();
    }
}
