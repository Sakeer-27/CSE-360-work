/*******
 * <p> Title: PostList Class. </p>
 *
 * <p> Description: Acts as a data-management class for all Post objects. 
 * Supports CRUD operations, search, and subset filtering. 
 * Enables controllers and GUIs to access, modify, and delete posts consistently. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Sakeer Abbas Ali
 * @version 1.00 2025-10-15 Initial implementation
 */

package database;

import entityClasses.Post;
import java.util.List;
import java.util.ArrayList;

public class PostList {
    private List<Post> posts;

    public PostList() {
        this.posts = new ArrayList<>();
    }

    public void addPost(Post post) {
        if(post != null) posts.add(post);
    }

    public void updatePost(int postId, String newTitle, String newBody) {
        for (Post p : posts) {
            if (p.getPostId() == postId) {
                p.setTitle(newTitle);
                p.setBody(newBody);
                break;
            }
        }
    }

    public void deletePost(int postId) {
        posts.removeIf(post -> post.getPostId() == postId);
    }

    public Post getPost(int postId) {
        for (Post p : posts) {
            if (p.getPostId() == postId) return p;
        }
        return null;
    }

    public List<Post> getAllPosts() {
        return new ArrayList<>(posts);
    }

    public List<Post> filterPosts(String keyword) {
        List<Post> results = new ArrayList<>();
        for (Post p : posts) {
            if (
                (p.getTitle() != null && p.getTitle().contains(keyword)) ||
                (p.getBody() != null && p.getBody().contains(keyword))
            ) {
                results.add(p);
            }
        }
        return results;
    }
    
 // Return all posts by a specific author
    public List<Post> findByAuthor(String author) {
        return posts.stream()
            .filter(p -> p.getAuthor().equalsIgnoreCase(author))
            .toList();
    }

    // Return all posts whose title contains a keyword
    public List<Post> searchInTitle(String keyword) {
        String s = keyword.toLowerCase();
        return posts.stream()
            .filter(p -> p.getTitle().toLowerCase().contains(s))
            .toList();
    }
}