/*******
 * <p> Title: Post Class. </p>
 *
 * <p> Description: Represents an individual discussion post entity. 
 * Stores post ID, title, body, author, timestamp, and associated replies. 
 * Provides methods to add, update, and remove replies while preserving 
 * encapsulation through unmodifiable lists. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Sakeer Abbas Ali
 * @version 1.00 2025-10-15 Initial implementation
 */

package entityClasses;

import java.util.*;
  
public class Post {
    private int postId;
    private String title;
    private String body;
    private String author;
    private Date timestamp;
    private List<Reply> replies;

    public Post(int postId, String title, String body, String author) {
        this.postId = postId;
        this.title = title;
        this.body = body;
        this.author = author;
        this.timestamp = new Date();
        this.replies = new ArrayList<>();
    }

    public int getPostId() { return postId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public String getAuthor() { return author; }
    public Date getTimestamp() { return timestamp; }

    public void addReply(Reply reply) {
        if (reply != null) replies.add(reply);
    }
    /** Remove by id; returns true if something was removed */
    public boolean removeReplyById(int replyId) {
        for (int i = 0; i < replies.size(); i++) {
            if (replies.get(i).getReplyId() == replyId) {
                replies.remove(i);
                return true;
            }
        }
        return false;
    }

    /** Update a reply’s body by id; returns true if updated */
    public boolean updateReplyBody(int replyId, String newBody) {
        for (Reply r : replies) {
            if (r.getReplyId() == replyId) {
                r.setBody(newBody);
                return true;
            }
        }
        return false;
    }
    
    
    public List<Reply> getReplies() {
        return Collections.unmodifiableList(replies);
    }

    @Override
    public String toString() {
        return "Post{" +
                "postId=" + postId +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", author='" + author + '\'' +
                ", timestamp=" + timestamp +
                ", replies=" + replies +
                '}';
    }
}