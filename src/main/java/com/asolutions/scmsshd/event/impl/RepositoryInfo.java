package com.asolutions.scmsshd.event.impl;

import org.eclipse.jgit.lib.Repository;

/**
 * @author Oleg Ilyenko
 */
public class RepositoryInfo {
    private final Repository repository;

    private final String repositoryPath;

    private final boolean exists;

    public RepositoryInfo(Repository repository, String repositoryPath, boolean exists) {
        this.repository = repository;
        this.repositoryPath = repositoryPath;
        this.exists = exists;
    }

    public boolean isExists() {
        return exists;
    }

    public Repository getRepository() {
        return repository;
    }

    public String getRepositoryPath() {
        return repositoryPath;
    }
}
