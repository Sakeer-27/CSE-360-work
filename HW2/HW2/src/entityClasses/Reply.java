/*******
 * <p> Title: Reply Class. </p>
 *
 * <p> Description: Represents an individual reply entity linked to a post. 
 * Stores reply ID, body text, author name, and timestamp. 
 * Used by the Post and ReplyList classes to maintain nested discussions. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Sakeer Abbas Ali
 * @version 1.00 2025-10-15 Initial implementation
 */

package entityClasses;

import java.util.Date;

public class Reply {
    private int replyId;
    private String body;
    private String author;
    private Date timestamp;

    public Reply(int replyId, String body, String author) {
        this.replyId = replyId;
        this.body = body;
        this.author = author;
        this.timestamp = new Date();
    }

    public int getReplyId() { return replyId; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public String getAuthor() { return author; }
    public Date getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "Reply{" +
                "replyId=" + replyId +
                ", body='" + body + '\'' +
                ", author='" + author + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}