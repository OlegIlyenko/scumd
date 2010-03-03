package com.asolutions.scmsshd.model.security;

/**
 * @author Oleg Ilyenko
 */
public interface RepositoryMatcher {

    boolean matches(String repository);

}
