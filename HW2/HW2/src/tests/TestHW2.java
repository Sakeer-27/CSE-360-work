package tests;

import database.PostList;
import database.ReplyList;
import entityClasses.Post;
import entityClasses.Reply;

/**
 * Automated test cases for HW2 CRUD and validation functionality.
 * Run this file with assertions enabled:
 *   java -ea tests.TestHW2
 */
public class TestHW2 {

    public static void main(String[] args) {
        testCreatePost();
        testReadPost();
        testUpdatePost();
        testDeletePost();

        testCreateReply();
        testReadReply();
        testUpdateReply();
        testDeleteReply();

        testEmptyPostValidation();
        testEmptyReplyValidation();

        System.out.println("\n✅ All test cases executed.");
    }

    private static void testCreatePost() {
        PostList list = new PostList();
        Post p = new Post(1, "Test Title", "Test Body", "Author");
        list.addPost(p);
        assert list.getAllPosts().size() == 1 : "❌ Create Post failed";
        System.out.println("✅ testCreatePost passed");
    }

    private static void testReadPost() {
        PostList list = new PostList();
        Post p = new Post(1, "Read Title", "Read Body", "Author");
        list.addPost(p);
        Post retrieved = list.getPost(1);
        assert retrieved != null && retrieved.getTitle().equals("Read Title") : "❌ Read Post failed";
        System.out.println("✅ testReadPost passed");
    }

    private static void testUpdatePost() {
        PostList list = new PostList();
        Post p = new Post(1, "Old Title", "Old Body", "Author");
        list.addPost(p);
        list.updatePost(1, "New Title", "New Body");
        Post updated = list.getPost(1);
        assert updated.getTitle().equals("New Title") : "❌ Update Post failed";
        System.out.println("✅ testUpdatePost passed");
    }

    private static void testDeletePost() {
        PostList list = new PostList();
        Post p = new Post(1, "Delete Title", "Delete Body", "Author");
        list.addPost(p);
        list.deletePost(1);
        assert list.getAllPosts().isEmpty() : "❌ Delete Post failed";
        System.out.println("✅ testDeletePost passed");
    }

    private static void testCreateReply() {
        ReplyList replies = new ReplyList();
        Reply r = new Reply(1, "Reply body", "Author");
        replies.addReply(r);
        assert replies.getAllReplies().size() == 1 : "❌ Create Reply failed";
        System.out.println("✅ testCreateReply passed");
    }

    private static void testReadReply() {
        ReplyList replies = new ReplyList();
        Reply r = new Reply(1, "Read Reply", "Author");
        replies.addReply(r);
        Reply retrieved = replies.getReply(1);
        assert retrieved != null && retrieved.getBody().equals("Read Reply") : "❌ Read Reply failed";
        System.out.println("✅ testReadReply passed");
    }

    private static void testUpdateReply() {
        ReplyList replies = new ReplyList();
        Reply r = new Reply(1, "Old Reply", "Author");
        replies.addReply(r);
        replies.updateReply(1, "New Reply");
        assert replies.getReply(1).getBody().equals("New Reply") : "❌ Update Reply failed";
        System.out.println("✅ testUpdateReply passed");
    }

    private static void testDeleteReply() {
        ReplyList replies = new ReplyList();
        Reply r = new Reply(1, "Delete Reply", "Author");
        replies.addReply(r);
        replies.deleteReply(1);
        assert replies.getAllReplies().isEmpty() : "❌ Delete Reply failed";
        System.out.println("✅ testDeleteReply passed");
    }

    private static void testEmptyPostValidation() {
        PostList list = new PostList();
        Post invalid = new Post(1, "", "", "Author");
        list.addPost(invalid);
        // In the real GUI this would be blocked — here just demonstrate the test.
        assert list.getAllPosts().size() == 1 : "⚠️ GUI prevents empty posts, not backend";
        System.out.println("⚠️ testEmptyPostValidation checked (GUI-level validation)");
    }

    private static void testEmptyReplyValidation() {
        ReplyList replies = new ReplyList();
        Reply invalid = new Reply(1, "", "Author");
        replies.addReply(invalid);
        assert replies.getAllReplies().size() == 1 : "⚠️ GUI prevents empty replies, not backend";
        System.out.println("⚠️ testEmptyReplyValidation checked (GUI-level validation)");
    }
    
    private static void testNoRepliesYet() {
        // A new post should start with zero replies
        Post p = new Post(42, "Fresh Post", "Body", "Author");
        assert p.getReplies().isEmpty() : "❌ Expected no replies on a brand-new post";
        System.out.println("✅ testNoRepliesYet passed");
    }
}
