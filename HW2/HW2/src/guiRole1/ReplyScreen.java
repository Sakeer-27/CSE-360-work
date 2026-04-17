/*******
 * <p> Title: ReplyScreen Class. </p>
 *
 * <p> Description: JavaFX boundary class for viewing and managing replies 
 * to a selected post. Supports adding, updating, and deleting replies with 
 * proper validation and confirmation dialogs. Displays a “No replies yet” 
 * placeholder when empty. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Sakeer Abbas Ali
 * @version 1.00 2025-10-15 Initial implementation
 */


package guiRole1;

import entityClasses.Post;
import entityClasses.Reply;
import database.ReplyList;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.Parent;
import javafx.geometry.Insets;
import java.util.List;

public class ReplyScreen {
    private VBox root;
    private ReplyList replyList;
    private Post currentPost;
    private database.PostList sharedPostList;

    public ReplyScreen(Post post, database.PostList sharedPostList) {
        this.currentPost = post;
        this.sharedPostList = sharedPostList;
        this.replyList = new ReplyList(); // simple in-memory holder; we also mirror on the Post

        root = new VBox(10);
        root.setPadding(new Insets(10));

        Label postTitle = new Label("Post #" + currentPost.getPostId() + ": " + currentPost.getTitle());
        Label postBody = new Label(currentPost.getBody());

        TextField replyField = new TextField();
        replyField.setPromptText("Type your reply here...");

        Button addReplyBtn = new Button("Add Reply");
        Button updateReplyBtn = new Button("Update Selected Reply");
        Button deleteReplyBtn = new Button("Delete Selected Reply");

        ListView<String> replyListView = new ListView<>();

        // start disabled until a row is selected
        deleteReplyBtn.setDisable(true);
        updateReplyBtn.setDisable(true);

        // enable/disable on selection
        replyListView.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            boolean none = (n == null);
            deleteReplyBtn.setDisable(none);
            updateReplyBtn.setDisable(none);
        });

        // ADD
        addReplyBtn.setOnAction(e -> {
            String replyBody = replyField.getText().trim();
            if (replyBody.isEmpty()) {
                showAlert("Reply cannot be empty!");
                return;
            }
            int nextId = replyList.getAllReplies().size() + 1;
            Reply newReply = new Reply(nextId, replyBody, "currentUser"); // TODO: real author
            replyList.addReply(newReply);     // store in simple list
            currentPost.addReply(newReply);   // attach to the post
            refreshReplies(replyListView);
            replyField.clear();
            selectReplyInList(replyListView, newReply.getReplyId());
        });

        // UPDATE
        updateReplyBtn.setOnAction(e -> {
            String row = replyListView.getSelectionModel().getSelectedItem();
            if (row == null) { showAlert("Select a reply to update."); return; }
            int replyId = parseReplyId(row);
            if (replyId < 0) { showAlert("Could not parse reply id."); return; }

            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Update Reply");
            dialog.setHeaderText("Edit the reply text:");
            dialog.setContentText("Reply:");
            dialog.getEditor().setText(extractBodyFromRow(row));

            dialog.showAndWait().ifPresent(newText -> {
                String trimmed = newText.trim();
                if (trimmed.isEmpty()) {
                    showAlert("Reply cannot be empty!");
                    return;
                }

                // Update using Post API
                boolean ok = currentPost.updateReplyBody(replyId, trimmed);
                if (!ok) { showAlert("Reply not found."); return; }

                // Mirror in the separate ReplyList, if you keep one
                for (Reply r : replyList.getAllReplies()) {
                    if (r.getReplyId() == replyId) {
                        r.setBody(trimmed);
                        break;
                    }
                }

                refreshReplies(replyListView);
                selectReplyInList(replyListView, replyId);
            });
        });

        // Delete Reply with confirmation
        deleteReplyBtn.setOnAction(e -> {
            String row = replyListView.getSelectionModel().getSelectedItem();
            if (row == null || row.equals("<No replies yet>")) {
                showAlert("Select a reply to delete.");
                return;
            }

            int replyId = parseReplyId(row);
            if (replyId < 0) {
                showAlert("Could not parse reply id.");
                return;
            }

            // Confirm before delete
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete this reply?", ButtonType.OK, ButtonType.CANCEL);
            confirm.setHeaderText("Confirm Delete");
            confirm.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.OK) {
                    boolean removed = currentPost.removeReplyById(replyId);
                    replyList.getAllReplies().removeIf(r -> r.getReplyId() == replyId);
                    if (!removed) {
                        showAlert("Reply not found.");
                        return;
                    }
                    refreshReplies(replyListView);
                }
            });
        });

        Button backButton = new Button("Back to Dashboard");
        backButton.setOnAction(e -> {
            javafx.stage.Stage stage = (javafx.stage.Stage) root.getScene().getWindow();
            // Reuse the SAME PostList and re-select this post
            PostDashboard dashboard = new PostDashboard(sharedPostList, currentPost.getPostId());
            javafx.scene.Scene dashboardScene = new javafx.scene.Scene(
                dashboard.getRoot(),
                applicationMain.FoundationsMain.WINDOW_WIDTH,
                applicationMain.FoundationsMain.WINDOW_HEIGHT
            );
            stage.setScene(dashboardScene);
        });

        // Buttons row
        HBox buttonsRow = new HBox(10, addReplyBtn, updateReplyBtn, deleteReplyBtn);

        // Layout
        root.getChildren().addAll(
            postTitle,
            postBody,
            new Label("Replies:"),
            replyListView,
            replyField,
            buttonsRow,
            backButton
        );

        refreshReplies(replyListView);
    }

    private String extractBodyFromRow(String row) {
        // Row format: "#<id> by <author>: <body>"
        int idx = row.indexOf(":");
        return (idx >= 0 && idx + 1 < row.length()) ? row.substring(idx + 1).trim() : "";
    }

    private void selectReplyInList(ListView<String> replyListView, int replyId) {
        for (int i = 0; i < replyListView.getItems().size(); i++) {
            String row = replyListView.getItems().get(i);
            if (row != null && row.startsWith("#" + replyId + " ")) {
                replyListView.getSelectionModel().select(i);
                replyListView.scrollTo(i);
                break;
            }
        }
    }

    private void refreshReplies(ListView<String> replyListView) {
        replyListView.getItems().clear();
        List<Reply> replies = currentPost.getReplies();

        if (replies.isEmpty()) {
            replyListView.getItems().add("<No replies yet>");
            replyListView.setDisable(true);
            return;
        }

        replyListView.setDisable(false);
        for (Reply r : replies) {
            replyListView.getItems().add(
                "#" + r.getReplyId() + " by " + r.getAuthor() + ": " + r.getBody()
            );
        }
    }


    private int parseReplyId(String row) {
        if (row == null || !row.startsWith("#")) return -1;
        int space = row.indexOf(' ');
        if (space <= 1) return -1;
        try { return Integer.parseInt(row.substring(1, space)); }
        catch (NumberFormatException ex) { return -1; }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.showAndWait();
    }

    public Parent getRoot() { return root; }
}
