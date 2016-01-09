package eu.ioservices.canopus.test;

import eu.ioservices.canopus.exchanging.Interaction;
import eu.ioservices.canopus.exchanging.ServiceExchangingException;
import eu.ioservices.canopus.exchanging.annotations.Repeat;
import eu.ioservices.canopus.exchanging.annotations.Timeout;
import feign.Param;
import feign.RequestLine;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public class DirectUrlTestCase {
    private static final String GITHUB_API_URL = "https://api.github.com";
    private static final String GITHUB_REPO_USER = "netflix";
    private static final String GITHUB_REPO_PROJECT = "feign";
    private static final String GITHUB_REPO_CONTRIBUTOR = "adriancole";
    private static final int GITHUB_REPO_CONTRIBUTOR_MIN_CONTRIBUTIONS = 220;

    public static class Contributor {
        String login;
        int contributions;
    }

    public interface GitHub {
        @RequestLine("GET /repos/{owner}/{repo}/contributors")
        List<Contributor> contributors(@Param("owner") String owner, @Param("repo") String repo);
    }

    public interface GitHubWithCircuitBreaker {
        @Repeat(4)
        @Timeout(10L)
        @RequestLine("GET /repos/{owner}/{repo}/contributors")
        List<Contributor> contributors(@Param("owner") String owner, @Param("repo") String repo);
    }

    @Test
    public void gitHubClientTestWithoutCircuitBreaker() {
        final GitHub gitHubClient = new Interaction().with(GITHUB_API_URL).via(GitHub.class);
        List<Contributor> contributors = gitHubClient.contributors(GITHUB_REPO_USER, GITHUB_REPO_PROJECT);
        assertTrue(contributors.stream().anyMatch(contributor -> GITHUB_REPO_CONTRIBUTOR.equals(contributor.login) &&
                contributor.contributions > GITHUB_REPO_CONTRIBUTOR_MIN_CONTRIBUTIONS));
    }

    @Test(expected = ServiceExchangingException.class)
    public void gitHubClientTestWithCircuitBreaker() {
        final GitHubWithCircuitBreaker gitHubClient = new Interaction().with(GITHUB_API_URL).via(GitHubWithCircuitBreaker.class);
        List<Contributor> contributors = gitHubClient.contributors(GITHUB_REPO_USER, GITHUB_REPO_PROJECT);
        assertTrue(contributors.stream().anyMatch(contributor -> GITHUB_REPO_CONTRIBUTOR.equals(contributor.login) &&
                contributor.contributions > GITHUB_REPO_CONTRIBUTOR_MIN_CONTRIBUTIONS));
    }
}
