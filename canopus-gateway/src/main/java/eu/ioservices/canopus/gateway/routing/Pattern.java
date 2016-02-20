package eu.ioservices.canopus.gateway.routing;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public class Pattern {
    private static final String PATTERN_TRAILING_SLASHES_REGEXP = "/*$";
    private static final String PATTERN_ALLOWED_SYMBOLS_REGEXP = "[A-Za-z0-9-._:/*@?&=\\{\\}]+";
    private static final String PARAMETER_REGEXP = "\\{([a-zA-Z0-9]*?)\\}";
    private static final String PARAMETER_PLACEHOLDER_REGEXP = "([a-zA-Z0-9]*?)";

    private final String pattern;
    private final List<String> parameters;

    public Pattern(String pattern) {
        Objects.requireNonNull(pattern);

        this.pattern = requireValidPattern(pattern);
        this.parameters = parseParameterKeys(this.pattern);
    }

    public static Pattern of(String path) {
        return new Pattern(path);
    }

    public boolean matches(String str) {
        Objects.requireNonNull(str);
        final String matcher = getPatternMatcher(true);
        return removeTrailingSlashes(str).matches(matcher);
    }

    public Map<String, String> match(String valueSource) {
        final String matcherStr = getPatternMatcher(false);
        final Matcher matcher = java.util.regex.Pattern.compile(matcherStr).matcher(valueSource);

        List<String> paramValues = new ArrayList<>();
        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                final String value = matcher.group(i);
                if (value.trim().length() > 0)
                    paramValues.add(value);
            }
        }

        if (paramValues.size() != this.parameters.size())
            throw new IllegalArgumentException("Source string doesn't have enough param values");

        Map<String, String> paramsMap = new HashMap<>(paramValues.size());
        for (int i = 0; i < paramValues.size(); i++)
            paramsMap.put(this.parameters.get(i), paramValues.get(i));

        return paramsMap;
    }


    public List<String> params() {
        return parameters;
    }

    public String pattern() {
        return pattern;
    }

    public String compile(Map<String, String> params) {
        String filledPath = this.pattern;
        params.forEach(filledPath::replaceFirst);
        return filledPath;
    }

    @Deprecated
    public URL path(URL valuesSourcePath) throws MalformedURLException {
        String filledPath = valuesSourcePath.toString();
        final Map<String, String> params = match(filledPath);
        params.forEach(filledPath::replaceFirst);
        return new URL(filledPath);
    }

    private String getPatternMatcher(boolean fullString) {
        String matcher = this.pattern.replaceAll(PARAMETER_REGEXP, PARAMETER_PLACEHOLDER_REGEXP)  + "(/|$)";
        if (fullString)
            matcher = "(.*?)" + matcher;
        return matcher;
    }

    private List<String> parseParameterKeys(String text) {
        List<String> params = new ArrayList<>();
        final Matcher matches = java.util.regex.Pattern.compile(PARAMETER_REGEXP).matcher(text);
        while (matches.find())
            params.add(matches.group(1));

        return params;
    }

    private String requireValidPattern(String pattern) {
        if (!pattern.matches(PATTERN_ALLOWED_SYMBOLS_REGEXP))
            throw new IllegalArgumentException("Bad pattern string " + pattern + ". Valid characters are:" + PATTERN_ALLOWED_SYMBOLS_REGEXP);

        boolean wasParamStartBracket = false;
        for (char c : pattern.toCharArray()) {
            if (c == '{') {
                if (wasParamStartBracket)
                    throw new IllegalArgumentException("Nested params in patterns are not supported");
                wasParamStartBracket = true;
            } else if (c == '}') {
                wasParamStartBracket = false;
            }
        }

        if (pattern.contains("{}"))
            throw new IllegalArgumentException("Empty parameter {} is unexpected in :" + pattern);

        return removeTrailingSlashes(pattern);
    }

    private String removeTrailingSlashes(String str) {
        return str.replaceFirst(PATTERN_TRAILING_SLASHES_REGEXP, "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Pattern))
            return false;

        Pattern pattern1 = (Pattern) o;
        return pattern.equals(pattern1.pattern);
    }

    @Override
    public int hashCode() {
        return pattern.hashCode();
    }
}
