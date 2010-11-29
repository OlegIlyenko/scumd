package com.asolutions.scmsshd.event.listener.impl;

import com.asolutions.scmsshd.event.AuthenticationFailEvent;
import com.asolutions.scmsshd.event.AuthenticationSuccessEvent;
import com.asolutions.scmsshd.event.AuthorizationFailEvent;
import com.asolutions.scmsshd.event.AuthorizationSuccessEvent;
import com.asolutions.scmsshd.runner.ScumdConfigurableRunner;
import com.asolutions.scmsshd.util.GitUtil;

import java.io.File;

/**
 * @author Oleg Ilyenko
 */
public class AccessLogListener extends BaseLogListener {

    @Override
    protected String getCategory() {
        return "access";
    }

    @Override
    protected String getDefaultPath() {
        return System.getProperty("user.home") + File.separator + ScumdConfigurableRunner.DEFAULT_SCUMD_FOLDER +
                File.separator + "log" + File.separator + "access.log";
    }

    public void onAuthenticationSuccess(AuthenticationSuccessEvent e) {
        log.info(GitUtil.render(e));
    }

    public void onAuthenticationFail(AuthenticationFailEvent e) {
        log.info(GitUtil.render(e));
    }

    public void onAuthorizationSuccess(AuthorizationSuccessEvent e) {
        log.info(GitUtil.render(e));
    }

    public void onAuthorizationFail(AuthorizationFailEvent e) {
        log.info(GitUtil.render(e));
    }
}
