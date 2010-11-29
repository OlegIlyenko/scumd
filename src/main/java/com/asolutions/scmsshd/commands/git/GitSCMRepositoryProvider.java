package com.asolutions.scmsshd.commands.git;

import com.asolutions.scmsshd.InteractionContext;
import com.asolutions.scmsshd.commands.handlers.CommandContext;
import com.asolutions.scmsshd.event.CancelEventException;
import com.asolutions.scmsshd.event.impl.RepositoryCreateEventImpl;
import com.asolutions.scmsshd.event.impl.RepositoryInfo;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static com.asolutions.scmsshd.event.listener.EventDispatcher.Stage.Post;
import static com.asolutions.scmsshd.event.listener.EventDispatcher.Stage.Pre;

public class GitSCMRepositoryProvider {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final HashMap<String, Repository> repositoryCache = new HashMap<String, Repository>();
    
    public synchronized Repository provide(File base, CommandContext commandContext) throws IOException {
        String argument = commandContext.getFilteredCommand().getArgument();
        InteractionContext ctx = commandContext.getInteractionContext();

        if (ctx.isAllowCaching() && repositoryCache.containsKey(argument)) {
            log.info("Using Cached Repo " + argument);
            ctx.setRepositoryInfo(new RepositoryInfo(repositoryCache.get(argument), argument, true));
            return repositoryCache.get(argument);
        }

        File pathToRepo = new File(base, argument);

        log.info("Accessing Repository: " + pathToRepo.getAbsolutePath());

        Repository repo = new FileRepository(pathToRepo);

        if (!pathToRepo.exists()) {
            log.info("Repository does not exists, creating new bare repository: " + pathToRepo.getAbsolutePath());

            try {
                ctx.getEventDispatcher().fireEvent(Pre, new RepositoryCreateEventImpl(
                        ctx.getUser(), ctx.getServer(), new RepositoryInfo(repo, argument, false)));
            } catch (CancelEventException e) {
                log.info("Listener cancelled repository creation! \n" + e.getContextInfo());
                throw e;
            }

            repo.create(true);
            log.info("New bare repository created: " + pathToRepo.getAbsolutePath());
            ctx.getEventDispatcher().fireEvent(Post, new RepositoryCreateEventImpl(
                    ctx.getUser(), ctx.getServer(), new RepositoryInfo(repo, argument, true)));
        }

        if (ctx.isAllowCaching()) {
            repositoryCache.put(argument, repo);
        }

        ctx.setRepositoryInfo(new RepositoryInfo(repo, argument, true));

        return repo;
    }

    public synchronized boolean exists(File base, String argument, InteractionContext ctx) {
        if (ctx.isAllowCaching() && repositoryCache.containsKey(argument)) {
            return true;
        }

        File pathToRepo = new File(base, argument);
        return pathToRepo.exists();
    }

}
