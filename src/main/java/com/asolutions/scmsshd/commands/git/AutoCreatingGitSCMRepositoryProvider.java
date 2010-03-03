package com.asolutions.scmsshd.commands.git;

import org.eclipse.jgit.lib.Repository;

import java.io.File;
import java.io.IOException;

public class AutoCreatingGitSCMRepositoryProvider extends GitSCMRepositoryProvider {

    @Override
    public Repository provide(File base, String argument) throws IOException {
        synchronized (repositoryCache) {
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
    }

    public boolean exists(File base, String argument) {
        synchronized (repositoryCache) {
            if (repositoryCache.containsKey(argument)) {
                return true;
            }

            File pathToRepo = new File(base, argument);
            return pathToRepo.exists();
        }
    }

}