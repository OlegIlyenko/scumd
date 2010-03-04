package com.asolutions.scmsshd.commands.git;

import org.eclipse.jgit.lib.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class GitSCMRepositoryProvider {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final HashMap<String, Repository> repositoryCache = new HashMap<String, Repository>();

    public GitSCMRepositoryProvider(String hello) {
    }

    public synchronized Repository provide(File base, String argument) throws IOException {
        if (repositoryCache.containsKey(argument)) {
            log.info("Using Cached Repo " + argument);
            return repositoryCache.get(argument);
        }

        File pathToRepo = new File(base, argument);

        log.info("Accessing Repository: " + pathToRepo.getAbsolutePath());

        Repository repo = new Repository(pathToRepo);

        if (!pathToRepo.exists()) {
            log.info("Repository does not exists, creating new bare repository: " + pathToRepo.getAbsolutePath());
            repo.create(true);
            log.info("New bare repository created: " + pathToRepo.getAbsolutePath());
        }

        repositoryCache.put(argument, repo);

        return repo;
    }

    public synchronized boolean exists(File base, String argument) {
        if (repositoryCache.containsKey(argument)) {
            return true;
        }

        File pathToRepo = new File(base, argument);
        return pathToRepo.exists();
    }

}
