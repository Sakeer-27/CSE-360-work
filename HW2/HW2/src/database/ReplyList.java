/*******
 * <p> Title: ReplyList Class. </p>
 *
 * <p> Description: Manages all Reply objects across posts. 
 * Provides create, read, update, and delete operations plus subset queries. 
 * Used by PostDashboard and ReplyScreen for centralized reply management. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Sakeer Abbas Ali
 * @version 1.00 2025-10-15 Initial implementation
 */

package database;

import entityClasses.Reply;
import java.util.List;
import java.util.ArrayList;

public class ReplyList {
    private List<Reply> replies;

    public ReplyList() {
        this.replies = new ArrayList<>();
    }

    public void addReply(Reply reply) {
        if(reply != null) replies.add(reply);
    }

    public void updateReply(int replyId, String newBody) {
        for (Reply r : replies) {
            if (r.getReplyId() == replyId) {
                r.setBody(newBody);
                break;
            }
        }
    }

    public void deleteReply(int replyId) {
        replies.removeIf(reply -> reply.getReplyId() == replyId);
    }

    public Reply getReply(int replyId) {
        for (Reply r : replies) {
            if (r.getReplyId() == replyId) return r;
        }
        return null;
    }

    public List<Reply> getAllReplies() {
        return new ArrayList<>(replies);
    }
}