package com.asolutions.scmsshd.model.security;

/**
 * @author Oleg Ilyenko
 */
public interface PathMatcher {

    boolean matches(String repository);

}
