package com.asolutions.scmsshd.event.listener;

import com.asolutions.scmsshd.event.*;
import com.asolutions.scmsshd.model.security.AntPathMatcher;
import com.asolutions.scmsshd.model.security.Group;
import com.asolutions.scmsshd.model.security.PathMatcher;
import com.asolutions.scmsshd.model.security.User;
import org.eclipse.jgit.lib.PersonIdent;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import static com.asolutions.scmsshd.event.listener.EventDispatcher.Stage;

/**
 * @author Oleg Ilyenko
 */
public class SupportBuilder {

    private AggregateSupport mainSupport = new AggregateSupport();

    private boolean not = false;
    
    private SupportBuilder() {}

    public static SupportBuilder builder() {
        return new SupportBuilder(); 
    }

    public SupportBuilder repo(String includes, String excludes) {
        return eventClass(RepositoryEvent.class).support(new RepoSupport(includes, excludes));
    }

    public SupportBuilder file(String includes, String excludes) {
        return eventClass(RepositoryEvent.class).support(new FileSupport(includes, excludes));
    }

    public SupportBuilder eventClass(Class<? extends Event> clazz) {
        return support(new ClassSupport(clazz));
    }

    public SupportBuilder stage(Stage stage) {
        return support(new StageSupport(stage));
    }
    
    public SupportBuilder user(String userNameOrEmail) {
        return eventClass(UserEvent.class).support(new UserSupport(userNameOrEmail));
    }

    public SupportBuilder group(String name) {
        return eventClass(UserEvent.class).support(new GroupSupport(name));
    }
    
    public SupportBuilder author(String userNameOrEmail) {
        return eventClass(RepositoryEvent.class).support(new AuthorSupport(userNameOrEmail));
    }

    public SupportBuilder not() {
        not = true;
        return this;
    }

    public SupportBuilder stage(Annotation... annotations) {
        Stage stage = Stage.Post;

        for (Annotation a : annotations) {
            if (a.annotationType().equals(Pre.class)) {
                stage = Stage.Pre;
                break;
            }
        }

        return stage(stage);
    }

    public SupportBuilder support(Support support) {
        if (support != null) {
            if (not) {
                mainSupport.addSupport(new NotSupport(support));
                not = false;
            } else {
                mainSupport.addSupport(support);
            }
        }
        
        return this;
    }

    public Support build() {
        return mainSupport;
    }

    public boolean hasSupports() {
        return mainSupport.hasSupport();
    }

    public static class AggregateSupport implements Support {
        
        private final List<Support> delegates;

        public AggregateSupport() {
            delegates = new ArrayList<Support>();
        }

        public AggregateSupport(List<Support> delegates) {
            this.delegates = delegates;
        }

        public boolean supports(Stage stage, Event event) {
            for (Support s : delegates) {
                if (!s.supports(stage, event)) {
                    return false;
                }
            }

            return true;
        }

        public void addSupport(Support... supports) {
            for (Support s : supports) {
                if (!delegates.contains(s)) {
                    delegates.add(s);
                }
            }
        }

        public boolean hasSupport() {
            return !delegates.isEmpty();
        }
    }

    public static class ClassSupport implements Support {

        private final Class<? extends Event> clazz;

        public ClassSupport(Class<? extends Event> clazz) {
            this.clazz = clazz;
        }

        public boolean supports(Stage stage, Event event) {
            return supports(event.getClass());
        }

        public boolean supports(Class<? extends Event> c) {
            return clazz.isAssignableFrom(c);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ClassSupport that = (ClassSupport) o;

            return clazz.equals(that.clazz);
        }

        @Override
        public int hashCode() {
            return 189 + clazz.hashCode();
        }
    }

    public static class StageSupport implements Support {

        private final Stage stage;

        public StageSupport(Stage stage) {
            this.stage = stage;
        }

        public boolean supports(Stage stage, Event event) {
            return supports(stage);
        }

        public boolean supports(Stage stage) {
            return this.stage == stage;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            StageSupport that = (StageSupport) o;

            return stage == that.stage;

        }

        @Override
        public int hashCode() {
            return 127 + stage.hashCode();
        }
    }

    public static abstract class BasePathSupport implements Support {

        private final String includes;
        private final String excludes;

        private final PathMatcher includesMatcher;
        private final PathMatcher excludesMatcher;

        protected BasePathSupport(String includes, String excludes, PathMatcher includesMatcher, PathMatcher excludesMatcher) {
            this.includes = includes;
            this.excludes = excludes;
            this.includesMatcher = includesMatcher;
            this.excludesMatcher = excludesMatcher;
        }

        public boolean supports(String path) {
            if (excludesMatcher != null && excludesMatcher.matches(path)) {
                return false;
            } else if (includesMatcher != null && includesMatcher.matches(path)) {
                return true;
            } else {
                return false;
            }
        }

        public String getIncludes() {
            return includes;
        }

        public String getExcludes() {
            return excludes;
        }
    }

    public static class FileSupport extends BasePathSupport {

        public FileSupport(String includes, String excludes) {
            super(includes, excludes,
                    includes != null ? new AntPathMatcher(includes) : null,
                    excludes != null ? new AntPathMatcher(excludes) : null);
        }

        public boolean supports(Stage stage, Event event) {
            if (event instanceof FileChangeEvent) {
                return supports((FileChangeEvent) event);
            } else if (event instanceof CommitEvent) {
                return supports((CommitEvent) event);
            } else if (event instanceof PushEvent) {
                return supports((PushEvent) event);
            } else {
                return false;
            }
        }

        private boolean supports(FileChangeEvent event) {
            return supports(event.getPath());
        }

        private boolean supports(CommitEvent event) {
            for (FileChangeEvent fce : event.getFileChanges()) {
                if (supports(fce)) {
                    return true;
                }
            }

            return false;
        }

        private boolean supports(PushEvent event) {
            for (CommitEvent ce : event.getCommits()) {
                if (supports(ce)) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FileSupport that = (FileSupport) o;

            if (getExcludes() != null ? !getExcludes().equals(that.getExcludes()) : that.getExcludes() != null) return false;
            if (getIncludes() != null ? !getIncludes().equals(that.getIncludes()) : that.getIncludes() != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = getIncludes() != null ? getIncludes().hashCode() : 0;
            result = 123 * result + (getExcludes() != null ? getExcludes().hashCode() : 0);
            return result;
        }
    }

    public static class RepoSupport extends BasePathSupport {

        public RepoSupport(String includes, String excludes) {
            super(includes, excludes,
                    includes != null ? new AntPathMatcher(includes) : null,
                    excludes != null ? new AntPathMatcher(excludes) : null);
        }

        public boolean supports(Stage stage, Event event) {
            return supports(((RepositoryEvent) event).getRepositoryInfo().getRepositoryPath());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            RepoSupport that = (RepoSupport) o;

            if (getExcludes() != null ? !getExcludes().equals(that.getExcludes()) : that.getExcludes() != null) return false;
            if (getIncludes() != null ? !getIncludes().equals(that.getIncludes()) : that.getIncludes() != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = getIncludes() != null ? getIncludes().hashCode() : 0;
            result = 31 * result + (getIncludes() != null ? getIncludes().hashCode() : 0);
            return result;
        }
    }
    
    public static class NotSupport implements Support {

        private Support delegate;

        public NotSupport(Support delegate) {
            this.delegate = delegate;
        }

        public boolean supports(Stage stage, Event event) {
            return !delegate.supports(stage, event);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            NotSupport that = (NotSupport) o;

            return delegate.equals(that.delegate);
        }

        @Override
        public int hashCode() {
            return 346 + delegate.hashCode();
        }
    }
    
    public static class UserSupport implements Support {

        private String userNameOrEmail;

        public UserSupport(String userNameOrEmail) {
            this.userNameOrEmail = userNameOrEmail;
        }

        public boolean supports(Stage stage, Event event) {
            User user = ((UserEvent) event).getUser();
            return user.getName().equals(userNameOrEmail) ||
                    (user.getEmail() != null && user.getEmail().equals(userNameOrEmail));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            UserSupport that = (UserSupport) o;

            return userNameOrEmail.equals(that.userNameOrEmail);
        }

        @Override
        public int hashCode() {
            return 334 + userNameOrEmail.hashCode();
        }
    }

    public static class GroupSupport implements Support {

        private String name;

        public GroupSupport(String name) {
            this.name = name;
        }

        public boolean supports(Stage stage, Event event) {
            User user = ((UserEvent) event).getUser();

            for (Group g : user.getGroups()) {
                if (g.getName().equals(name)) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            GroupSupport that = (GroupSupport) o;

            return name.equals(that.name);
        }

        @Override
        public int hashCode() {
            return 336 + name.hashCode();
        }
    }

    public static class AuthorSupport implements Support {

        private String userNameOrEmail;

        public AuthorSupport(String userNameOrEmail) {
            this.userNameOrEmail = userNameOrEmail;
        }

        public boolean supports(Stage stage, Event event) {
            if (event instanceof CommitEvent) {
                return supports((CommitEvent) event);
            } else if (event instanceof PushEvent) {
                return supports((PushEvent) event);
            } else {
                return false;
            }
        }

        private boolean supports(CommitEvent event) {
            PersonIdent ident = event.getRevCommit().getAuthorIdent();

            return (ident.getName() != null && ident.getName().equals(userNameOrEmail)) ||
                    (ident.getEmailAddress() != null && ident.getEmailAddress().equals(userNameOrEmail));
        }

        private boolean supports(PushEvent event) {
            for (CommitEvent ce : event.getCommits()) {
                if (!supports(ce)) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            AuthorSupport that = (AuthorSupport) o;

            return userNameOrEmail.equals(that.userNameOrEmail);
        }

        @Override
        public int hashCode() {
            return 444 + userNameOrEmail.hashCode();
        }
    }

    public static final Support ALWAYS_SUPPORTS = new Support() {
        public boolean supports(Stage stage, Event event) {
            return true;
        }
    };
}
