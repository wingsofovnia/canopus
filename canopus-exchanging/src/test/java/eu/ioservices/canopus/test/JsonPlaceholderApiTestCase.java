package eu.ioservices.canopus.test;

import eu.ioservices.canopus.exchanging.Interaction;
import eu.ioservices.canopus.exchanging.ServiceExchangingException;
import eu.ioservices.canopus.exchanging.annotations.Repeat;
import eu.ioservices.canopus.exchanging.annotations.Timeout;
import feign.Param;
import feign.RequestLine;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public class JsonPlaceholderApiTestCase {
    private static final String BASE_API_URL = "http://jsonplaceholder.typicode.com/";
    private static final int API_POST_ID = 1;
    private static final int API_MIN_POSTS_IN_LIST = 10;

    public static class Post {
        int userId;
        int id;
        String title;
        String body;
    }

    public interface API {
        @RequestLine("GET /posts/{id}")
        Post getPost(@Param("id") int id);

        @RequestLine("GET /posts")
        List<Post> allPosts();
    }

    public interface APIwithCircuitBreaker {
        @Repeat(2)
        @Timeout(2L)
        @RequestLine("GET /posts/{id}")
        Post getPost(@Param("id") int id);
    }

    @Test
    public void getPostTest() {
        API jsonPlaceholderAPI = new Interaction().with(BASE_API_URL).via(API.class);
        Post post = jsonPlaceholderAPI.getPost(API_POST_ID);

        assertEquals(API_POST_ID, post.id);
        assertTrue(post.body.length() > 0);
        assertTrue(post.title.length() > 0);
        assertTrue(post.userId != 0);
    }

    @Test
    public void getAllPostsTest() {
        API jsonPlaceholderAPI = new Interaction().with(BASE_API_URL).via(API.class);
        List<Post> allPosts = jsonPlaceholderAPI.allPosts();

        Post apiPostById = allPosts.stream().filter(post -> post.id == API_POST_ID).findFirst().get();

        assertTrue(allPosts.size() > API_MIN_POSTS_IN_LIST);
        assertTrue(apiPostById.body.length() > 0);
        assertTrue(apiPostById.title.length() > 0);
        assertTrue(apiPostById.userId != 0);
    }

    @Test(expected = ServiceExchangingException.class)
    public void getPostWithCircuitBreakerTest() {
        APIwithCircuitBreaker jsonPlaceholderAPI = new Interaction().with(BASE_API_URL).via(APIwithCircuitBreaker.class);
        jsonPlaceholderAPI.getPost(API_POST_ID);
    }

}
