package com.asolutions.scmsshd.model.security;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple matcher. '*' wildcard can be used in pattern.
 *
 * @author Oleg Ilyenko
 */
public class AntPathMatcher implements PathMatcher {

    private final Pattern pattern;

    public AntPathMatcher(String repositoryPatterns) {
        this.pattern = createPattern(Arrays.asList(repositoryPatterns.split("\\s*,\\s*")));
    }

    private Pattern createPattern(List<String> stringPatterns) {
            StringBuilder result = new StringBuilder();

            for (String p : stringPatterns) {
                String pattern = p.replace('\\', '/');

                if (result.length() > 0) {
                    result.append('|');
                }

                if (pattern.equals("**")) {
                    result.append(".*");
                    break;
                }

                Matcher m = Pattern.compile("/\\*\\*/|/\\*\\*|\\*\\*/|/\\*$|\\*|/|[^*/]+").matcher(pattern);

                while (m.find()) {
                    String t = m.group();
                    if (t.equals("/**")) {
                        result.append("/.*");
                    } else if (t.equals("**/")) {
                        result.append("(.*/|)");
                    } else if (t.equals("/**/")) {
                        result.append("(/.*/|/)");
                    } else if (t.equals("/*")) {
                        result.append("/[^/]+");
                    } else if (t.equals("*")) {
                        result.append("[^/]*");
                    } else {
                        result.append(Pattern.quote(t));
                    }
                }
            }

            return Pattern.compile(result.toString());
        }

    @Override
    public boolean matches(String repository) {
        return pattern.matcher(repository).matches();
    }
}
