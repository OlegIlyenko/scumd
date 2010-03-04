package com.asolutions.scmsshd.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Oleg Ilyenko
 */
public abstract class BaseDatabaseDao implements InitializingBean {

    private static final String DEFAULT_SQL_STATEMENTS_FILE = "/com/asolutions/scmsshd/dao/impl/sql-statements.properties";

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private String sqlStatementsFile;

    protected NamedParameterJdbcTemplate jdbcTemplate;

    protected Properties sqlSource;

    public String getSqlStatementsFile() {
        return sqlStatementsFile;
    }

    public void setSqlStatementsFile(String sqlStatementsFile) {
        this.sqlStatementsFile = sqlStatementsFile;
    }

    public NamedParameterJdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initSqlSource();
        createTables();
    }

    private void initSqlSource() {
        sqlSource = new Properties();

        try {
            if (sqlStatementsFile == null || sqlStatementsFile.trim().equals("")) {
                sqlSource.load(this.getClass().getResourceAsStream(DEFAULT_SQL_STATEMENTS_FILE));
            } else {
                sqlSource.load(new FileInputStream(sqlStatementsFile));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void createTables();
}
