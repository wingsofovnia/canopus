package eu.ioservices.canopus.gateway;

import eu.ioservices.canopus.gateway.routing.Router;
import eu.ioservices.canopus.gateway.routing.Track;
import eu.ioservices.canopus.http.HttpMethod;
import javafx.util.Pair;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RouterTest {
    private static final Map<Pair<String, String>, Map<String, String>> ROUTES_TO_PARAMETERS = new HashMap<Pair<String, String>, Map<String, String>>() {{
        put(new Pair<>("/user/id/{id}", "http://12.20.20.20:231/user/id/12"),
                new HashMap<String, String>() {{
                    put("id", "12");
                }});
        put(new Pair<>("/user/id/{id}/photo/{pho}", "http://12.20.20.20:231/user/id/12/photo/true"),
                new HashMap<String, String>() {{
                    put("id", "12");
                    put("pho", "true");
                }});
    }};

    @Test
    public void testPatternValuesMatching() {
        final Router router = new Router();

        ROUTES_TO_PARAMETERS.forEach((patternUrlPair, paramsMap) -> {
            final String pattern = patternUrlPair.getKey();
            final String targetUrl = patternUrlPair.getValue();

            router.addRoute(HttpMethod.DELETE, pattern, pattern);
            final Optional<Track> trackOpt = router.route(HttpMethod.DELETE, targetUrl);
            assertTrue(trackOpt.isPresent());
            final Track track = trackOpt.get();

            assertEquals(paramsMap.size(), track.params().size());

            track.params().forEach((param, val) -> {
                assertTrue(paramsMap.containsKey(param));
                assertTrue(paramsMap.get(param).equals(val));
            });
        });
    }

}
