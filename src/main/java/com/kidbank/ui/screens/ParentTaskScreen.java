package com.kidbank.ui.screens;

import com.kidbank.model.*;
import com.kidbank.service.BankService;
import com.kidbank.ui.KidBankApp;
import com.kidbank.ui.components.StyleUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class ParentTaskScreen {

    public static Scene build(Parent parent) {
        // This is the parent's task manager screen.
        // Parents can create tasks, then approve or reject finished ones.

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #0B1020, #121A2D);");

        HBox header = new HBox(12);
        header.setPadding(new Insets(12, 20, 12, 20));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: linear-gradient(to right, #111A2C, #1E293B);"
                + "-fx-border-color: rgba(255,255,255,0.08); -fx-border-width: 0 0 1 0;");

        Label t = new Label("📋  Task Manager");
        t.setStyle("-fx-font-size: 18px; -fx-font-weight: 800; -fx-text-fill: #EAF0FF;");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button back = new Button("← Dashboard");
        back.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-text-fill: #EAF0FF; "
                + "-fx-background-radius: 10; -fx-cursor: hand; -fx-padding: 8 14;");
        back.setOnAction(e -> KidBankApp.navigate(ParentDashboard.build(parent)));

        header.getChildren().addAll(t, sp, back);
        root.setTop(header);

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        // Top section for creating a new task.
        content.getChildren().add(StyleUtil.sectionHeader("➕  Create New Task"));
        content.getChildren().add(buildCreateForm(parent));

        // Task list below it.
        content.getChildren().add(StyleUtil.sectionHeader("📌  All Tasks"));
        content.getChildren().add(buildTaskList(parent));

        scroll.setContent(content);
        root.setCenter(scroll);

        return new Scene(root, KidBankApp.APP_WIDTH, KidBankApp.APP_HEIGHT);
    }

    private static VBox buildCreateForm(Parent parent) {
        VBox card = StyleUtil.card();
        card.setSpacing(12);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: #1A2338; -fx-background-radius: 14; "
                + "-fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 14;");

        List<Child> children = BankService.getInstance().getChildren(parent.getUsername());
        if (children.isEmpty()) {
            card.getChildren().add(StyleUtil.subtitle("Add a child account before creating tasks."));
            return card;
        }

        Label childLbl = StyleUtil.fieldLabel("Assign To");
        ComboBox<String> childBox = new ComboBox<>();
        children.forEach(ch -> childBox.getItems().add(ch.getUsername() + " – " + ch.getFullName()));
        childBox.getSelectionModel().selectFirst();
        childBox.setStyle(StyleUtil.STYLE_INPUT);
        childBox.setMaxWidth(Double.MAX_VALUE);

        Label titleLbl = StyleUtil.fieldLabel("Task Title");
        TextField titleField = StyleUtil.textField("e.g. Clean bedroom");

        Label descLbl = StyleUtil.fieldLabel("Description (optional)");
        TextField descField = StyleUtil.textField("Extra details...");

        Label rewardLbl = StyleUtil.fieldLabel("Reward Amount (£)");
        TextField rewardField = StyleUtil.textField("e.g. 2.50");

        Label errLbl = StyleUtil.errorLabel();

        Button addBtn = StyleUtil.primaryBtn("➕  Add Task");
        addBtn.setMaxWidth(200);
        addBtn.setOnAction(e -> {
            StyleUtil.clearError(errLbl);
            String sel = childBox.getValue();
            if (sel == null) {
                StyleUtil.showError(errLbl, "Select a child.");
                return;
            }
            String childUsername = sel.split(" – ")[0];
            String taskTitle = titleField.getText().trim();
            if (taskTitle.isEmpty()) {
                StyleUtil.showError(errLbl, "Task title is required.");
                return;
            }
            double reward = 0;
            try {
                String rv = rewardField.getText().trim();
                if (!rv.isEmpty()) reward = Double.parseDouble(rv);
                if (reward < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                StyleUtil.showError(errLbl, "Reward must be a valid positive number.");
                return;
            }
            try {
                BankService.getInstance().createTask(parent.getUsername(), childUsername,
                        taskTitle, descField.getText().trim(), reward);
                KidBankApp.navigate(build(parent));
            } catch (IllegalArgumentException ex) {
                StyleUtil.showError(errLbl, ex.getMessage());
            }
        });

        card.getChildren().addAll(
                childLbl, childBox,
                titleLbl, titleField,
                descLbl, descField,
                rewardLbl, rewardField,
                errLbl, addBtn
        );
        return card;
    }

    private static VBox buildTaskList(Parent parent) {
        VBox box = new VBox(10);
        List<Task> allTasks = BankService.getInstance().getTasksForParent(parent.getUsername());

        if (allTasks.isEmpty()) {
            box.getChildren().add(StyleUtil.subtitle("No tasks yet. Create one above!"));
            return box;
        }

        for (Task task : allTasks) {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-background-color: #1A2338; -fx-background-radius: 12; "
                    + "-fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 12; -fx-padding: 14;");

            String statusEmoji = switch (task.getStatus()) {
                case PENDING -> "⏳";
                case COMPLETED_BY_CHILD -> "✅";
                case APPROVED -> "✔️";
                case REJECTED -> "❌";
            };

            VBox info = new VBox(3);
            Label tl = new Label(statusEmoji + "  " + task.getTitle());
            tl.setStyle("-fx-font-weight: bold; -fx-text-fill: #EAF0FF;");
            Label sub = StyleUtil.subtitle("Child: " + task.getChildUsername() +
                    " — Reward: £" + String.format("%.2f", task.getRewardAmount()) +
                    " — Status: " + task.getStatus());
            info.getChildren().addAll(tl, sub);

            Region sp2 = new Region();
            HBox.setHgrow(sp2, Priority.ALWAYS);

            HBox actions = new HBox(8);
            if (task.getStatus() == Task.Status.COMPLETED_BY_CHILD) {
                Button appBtn = StyleUtil.successBtn("✔ Approve");
                appBtn.setMaxWidth(Region.USE_PREF_SIZE);
                appBtn.setOnAction(e -> {
                    BankService.getInstance().approveTask(task.getTaskId());
                    KidBankApp.navigate(build(parent));
                });

                Button rejBtn = StyleUtil.dangerBtn("✘ Reject");
                rejBtn.setMaxWidth(Region.USE_PREF_SIZE);
                rejBtn.setOnAction(e -> {
                    BankService.getInstance().rejectTask(task.getTaskId());
                    KidBankApp.navigate(build(parent));
                });

                actions.getChildren().addAll(appBtn, rejBtn);
            }

            row.getChildren().addAll(info, sp2, actions);
            box.getChildren().add(row);
        }
        return box;
    }
}