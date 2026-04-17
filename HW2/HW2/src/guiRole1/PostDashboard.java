/*******
 * <p> Title: PostDashboard Class. </p>
 *
 * <p> Description: JavaFX boundary class for the Post Dashboard screen. 
 * Implements GUI-level Create, Read, Update, and Delete functionality for posts. 
 * Integrates input validation, confirmation dialogs, and navigation back 
 * to user or admin home screens. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Sakeer Abbas Ali
 * @version 1.00 2025-10-15 Initial implementation
 */

package guiRole1;

import database.PostList;

import entityClasses.Post;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import java.util.List;
import javafx.scene.Parent;
import guiAdminHome.ViewAdminHome;
import guiRole1.ViewRole1Home;
import guiRole2.ViewRole2Home;
import entityClasses.User;

public class PostDashboard {
    private VBox root;
    private PostList postList;

    // Default: new list, no pre-selection
    public PostDashboard() {
        this(new PostList(), null);
    }

    // Use shared list, no pre-selection
    public PostDashboard(PostList sharedList) {
        this(sharedList, null);
    }

    // Use shared list and re-select a specific post by id
    public PostDashboard(PostList sharedList, Integer selectPostId) {
        this.postList = sharedList;
        buildUI(selectPostId);
    }

    private void buildUI(Integer selectPostId) {
        root = new VBox(10);
        root.setPadding(new Insets(10));

        TextField titleField = new TextField();
        titleField.setPromptText("Title");

        TextArea bodyField = new TextArea();
        bodyField.setPromptText("Body");

        Button addPostBtn = new Button("Add Post");
        Button updatePostBtn = new Button("Update Selected Post");
        Button deletePostBtn = new Button("Delete Selected Post");

        ListView<String> postListView = new ListView<>();
        Button replyButton = new Button("Reply to Post");
        replyButton.setDisable(true);

        // Enable Reply when a post is selected
        postListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            replyButton.setDisable(newVal == null);
        });

        // Reply -> open ReplyScreen and pass shared PostList
        replyButton.setOnAction(e -> {
            String selected = postListView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Select a post to reply to.");
                return;
            }
            int postId = Integer.parseInt(selected.split(" ")[0].substring(1));
            Post selectedPost = postList.getPost(postId);
            guiRole1.ReplyScreen replyScreen = new guiRole1.ReplyScreen(selectedPost, postList);
            javafx.scene.Scene replyScene = new javafx.scene.Scene(
                replyScreen.getRoot(),
                applicationMain.FoundationsMain.WINDOW_WIDTH,
                applicationMain.FoundationsMain.WINDOW_HEIGHT
            );
            javafx.stage.Stage stage = (javafx.stage.Stage) root.getScene().getWindow();
            stage.setScene(replyScene);
        });

        Button quitButton = new Button("Quit to Home");
        quitButton.setOnAction(e -> {
            javafx.stage.Stage stage = (javafx.stage.Stage) root.getScene().getWindow();
            User current = applicationMain.FoundationsMain.database.getCurrentUser();
            int active = applicationMain.FoundationsMain.activeHomePage;  // 1=Admin, 2=Role1, 3=Role2

            switch (active) {
                case 1:
                    ViewAdminHome.displayAdminHome(stage, current);
                    break;
                case 2:
                    guiRole1.ViewRole1Home.displayRole1Home(stage, current);
                    break;
                case 3:
                    guiRole2.ViewRole2Home.displayRole2Home(stage, current);
                    break;
                default:
                    // Fallback: send to Role1 home if somehow unset
                    guiRole1.ViewRole1Home.displayRole1Home(stage, current);
                    break;
            }
        });

        // Add Post
        addPostBtn.setOnAction(e -> {
            String title = titleField.getText().trim();
            String body = bodyField.getText().trim();
            if (title.isEmpty() || body.isEmpty()) {
                showAlert("Title and Body cannot be empty!");
            } else {
                int nextId = postList.getAllPosts().size() + 1;
                Post newPost = new Post(nextId, title, body, "currentUser"); // TODO: real author
                postList.addPost(newPost);
                refreshPosts(postListView);
                titleField.clear();
                bodyField.clear();
                // select the new one
                selectPostInList(postListView, newPost.getPostId());
            }
        });

        // Update Post
        updatePostBtn.setOnAction(e -> {
            String selected = postListView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Select a post to update.");
                return;
            }
            int postId = Integer.parseInt(selected.split(" ")[0].substring(1));
            String newTitle = titleField.getText().trim();
            String newBody = bodyField.getText().trim();
            if (newTitle.isEmpty() || newBody.isEmpty()) {
                showAlert("Title and Body cannot be empty!");
                return;
            }
            postList.updatePost(postId, newTitle, newBody);
            refreshPosts(postListView);
            titleField.clear();
            bodyField.clear();
            selectPostInList(postListView, postId);
        });

        // Delete Post with confirmation
        deletePostBtn.setOnAction(e -> {
            String selected = postListView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Select a post to delete.");
                return;
            }
            int postId = Integer.parseInt(selected.split(" ")[0].substring(1));

            // Confirm before delete
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete this post?", ButtonType.OK, ButtonType.CANCEL);
            confirm.setHeaderText("Confirm Delete");
            confirm.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.OK) {
                    postList.deletePost(postId);
                    refreshPosts(postListView);

                    // Show message if empty
                    if (postList.getAllPosts().isEmpty()) {
                        new Alert(Alert.AlertType.INFORMATION, "No posts found").showAndWait();
                    }
                }
            });
        });
        // Layout
        root.getChildren().addAll(
            new Label("Create New Post"),
            titleField,
            bodyField,
            addPostBtn,
            updatePostBtn,
            deletePostBtn,
            new Label("All Posts:"),
            postListView,
            replyButton,
            quitButton
        );

        refreshPosts(postListView);

        // If we were asked to re-select a specific post, do it now
        if (selectPostId != null) {
            selectPostInList(postListView, selectPostId);
        }
    }

    private void selectPostInList(ListView<String> postListView, int postId) {
        for (int i = 0; i < postListView.getItems().size(); i++) {
            String row = postListView.getItems().get(i);
            if (row.startsWith("#" + postId + " ")) {
                postListView.getSelectionModel().select(i);
                postListView.scrollTo(i);
                break;
            }
        }
    }

    private void refreshPosts(ListView<String> postListView) {
        postListView.getItems().clear();
        List<Post> posts = postList.getAllPosts();

        if (posts.isEmpty()) {
            postListView.getItems().add("<No posts found>");
            postListView.setDisable(true);
            return;
        }

        postListView.setDisable(false);
        for (Post p : posts) {
            postListView.getItems().add(
                "#" + p.getPostId() + " " + p.getTitle() + ": " + p.getBody()
            );
        }
    }


    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.showAndWait();
    }
    

    public Parent getRoot() {
        return root;
    }
}
