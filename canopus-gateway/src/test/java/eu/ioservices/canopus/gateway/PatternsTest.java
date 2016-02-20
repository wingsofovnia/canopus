package eu.ioservices.canopus.gateway;

import eu.ioservices.canopus.gateway.routing.Pattern;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PatternsTest {
    private static final Map<String, Boolean> PATTERN_VALIDITY_TEST = new HashMap<String, Boolean>() {{
        put("/user/id/{id}", true);
        put("/user/id/{xcz}/zcz/{asda}/{}", false);
        put("/user/id/{id{asda}}", false);
    }};

    private static final Map<String, String> PATTERN_ADDRESS_MATCHES = new HashMap<String, String>() {{
        put("/user/id/{id}", "http://12.20.20.20:231/user/id/12");
    }};

    private static final Map<String, List<String>> PATTERN_PARAMS_TEST = new HashMap<String, List<String>>() {{
        put("/user/id/{id}", Arrays.asList("id"));
        put("/user/id/{xcz}/zcz/{asda}", Arrays.asList("xcz", "asda"));
        put("/{notrail}/", Arrays.asList("notrail"));
    }};

    @Test
    public void routesValidityTest() {
        PATTERN_VALIDITY_TEST.forEach((pattern, expectedValidity) -> {
            boolean isPatternValid = false;
            try {
                new Pattern(pattern);
                isPatternValid = true;
            } catch (Exception e) {
                isPatternValid = false;
            }
            assertEquals(expectedValidity, isPatternValid);
        });
    }

    @Test
    public void routesMatchTest() {
        PATTERN_ADDRESS_MATCHES.forEach((pattern, url) -> {
            assertTrue(new Pattern(pattern).matches(url));
        });
    }

    @Test
    public void routesParamsTest() {
        PATTERN_PARAMS_TEST.forEach((pattern, expectedParams) -> {
            final List<String> actualParams = new Pattern(pattern).params();
            actualParams.containsAll(expectedParams);
        });
    }
}
