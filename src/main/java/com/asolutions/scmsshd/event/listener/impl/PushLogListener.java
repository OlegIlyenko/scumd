package com.asolutions.scmsshd.event.listener.impl;

import com.asolutions.scmsshd.event.PushEvent;
import com.asolutions.scmsshd.runner.ScumdConfigurableRunner;
import com.asolutions.scmsshd.util.GitUtil;

import java.io.File;

/**
 * @author Oleg Ilyenko
 */
public class PushLogListener extends BaseLogListener {

    @Override
    protected String getCategory() {
        return "push";
    }

    @Override
    protected String getDefaultPath() {
        return System.getProperty("user.home") + File.separator + ScumdConfigurableRunner.DEFAULT_SCUMD_FOLDER +
                File.separator + "log" + File.separator + "push.log";
    }

    public void onPush(PushEvent e) {
        log.info("[" + e.getUser().getName()+"] successful push:\n" + GitUtil.render(e));
    }
}
