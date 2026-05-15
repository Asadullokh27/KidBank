package com.kidbank.ui.screens;

import com.kidbank.model.Child;
import com.kidbank.model.Task;
import com.kidbank.service.BankService;
import com.kidbank.ui.KidBankApp;
import com.kidbank.ui.components.StyleUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;


//bu ChildTaskScreen, bola o'z vazifalarini ko'rishi va boshqarishi mumkin bo'lgan ekran.

public class ChildTaskScreen {

    public static Scene build(Child child) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #0B1020, #121A2D);");

        HBox header = buildHeader(child);
        root.setTop(header);

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        VBox content = new VBox(18);
        content.setPadding(new Insets(20));

        Label pageTitle = new Label("My Tasks");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 800; -fx-text-fill: #EAF0FF;");

        Label pageSub = new Label("Complete chores, earn rewards, and build good money habits.");
        pageSub.setStyle("-fx-font-size: 13px; -fx-text-fill: #9AA7C7;");

        List<Task> tasks = BankService.getInstance().getTasksForChild(child.getUsername());
        List<Task> active = tasks.stream().filter(t -> t.getStatus() == Task.Status.PENDING).toList();
        List<Task> waiting = tasks.stream().filter(t -> t.getStatus() == Task.Status.COMPLETED_BY_CHILD).toList();
        List<Task> approved = tasks.stream().filter(t -> t.getStatus() == Task.Status.APPROVED).toList();

        HBox statRow = new HBox(14);
        statRow.getChildren().addAll(
                statCard("Active", String.valueOf(active.size()), "#4F8CFF"),
                statCard("Waiting", String.valueOf(waiting.size()), "#F59E0B"),
                statCard("Done", String.valueOf(approved.size()), "#10B981")
        );

        content.getChildren().addAll(pageTitle, pageSub, statRow);

        content.getChildren().add(StyleUtil.sectionHeader("⏳  Active Tasks (" + active.size() + ")"));
        if (active.isEmpty()) {
            content.getChildren().add(emptyCard("No active tasks right now.", "Ask your parent to assign some chores!"));
        } else {
            for (Task task : active) content.getChildren().add(buildActiveRow(task, child));
        }

        if (!waiting.isEmpty()) {
            content.getChildren().add(StyleUtil.sectionHeader("🔍  Awaiting Approval (" + waiting.size() + ")"));
            for (Task task : waiting) {
                content.getChildren().add(buildStatusRow(
                        task,
                        "Done — waiting for parent approval",
                        "#F59E0B"
                ));
            }
        }

        if (!approved.isEmpty()) {
            content.getChildren().add(StyleUtil.sectionHeader("✔  Completed (" + approved.size() + ")"));
            for (Task task : approved) {
                content.getChildren().add(buildStatusRow(
                        task,
                        "Reward earned: £" + String.format("%.2f", task.getRewardAmount()),
                        "#10B981"
                ));
            }
        }

        scroll.setContent(content);
        root.setCenter(scroll);

        return new Scene(root, KidBankApp.APP_WIDTH, KidBankApp.APP_HEIGHT);
    }

    private static HBox buildHeader(Child child) {
        HBox bar = new HBox(12);
        bar.setPadding(new Insets(12, 20, 12, 20));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: linear-gradient(to right, #111A2C, #1E293B);"
                + "-fx-border-color: rgba(255,255,255,0.08); -fx-border-width: 0 0 1 0;");

        Label t = new Label("📋  My Tasks");
        t.setStyle("-fx-font-size: 18px; -fx-font-weight: 800; -fx-text-fill: #EAF0FF;");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button back = new Button("← Dashboard");
        back.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-text-fill: #EAF0FF; "
                + "-fx-background-radius: 10; -fx-cursor: hand; -fx-padding: 8 14;");
        back.setOnAction(e -> KidBankApp.navigate(ChildDashboard.build(child)));

        bar.getChildren().addAll(t, sp, back);
        return bar;
    }

    private static VBox statCard(String title, String value, String accent) {
        VBox card = new VBox(4);
        card.setPadding(new Insets(14));
        card.setMinWidth(160);
        card.setStyle("-fx-background-color: #1A2338; -fx-background-radius: 14; "
                + "-fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 14;");

        Label t = new Label(title.toUpperCase());
        t.setStyle("-fx-font-size: 11px; -fx-font-weight: 800; -fx-text-fill: #9AA7C7;");

        Label v = new Label(value);
        v.setStyle("-fx-font-size: 24px; -fx-font-weight: 800; -fx-text-fill: " + accent + ";");

        card.getChildren().addAll(t, v);
        return card;
    }

    private static VBox emptyCard(String title, String subtitle) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: #1A2338; -fx-background-radius: 14; "
                + "-fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 14;");

        Label t = new Label(title);
        t.setStyle("-fx-font-size: 14px; -fx-font-weight: 800; -fx-text-fill: #EAF0FF;");

        Label s = new Label(subtitle);
        s.setStyle("-fx-font-size: 12px; -fx-text-fill: #9AA7C7;");

        card.getChildren().addAll(t, s);
        return card;
    }

    private static HBox buildActiveRow(Task task, Child child) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #1A2338; -fx-background-radius: 14; "
                + "-fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 14; -fx-padding: 14;");

        VBox info = new VBox(4);
        Label titleLbl = new Label("⏳  " + task.getTitle());
        titleLbl.setStyle("-fx-font-weight: 800; -fx-font-size: 13px; -fx-text-fill: #EAF0FF;");

        Label reward = new Label("Reward: £" + String.format("%.2f", task.getRewardAmount()));
        reward.setStyle("-fx-text-fill: #10B981; -fx-font-weight: 800;");

        info.getChildren().addAll(titleLbl, reward);

        if (!task.getDescription().isBlank()) {
            Label desc = StyleUtil.subtitle(task.getDescription());
            info.getChildren().add(desc);
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button doneBtn = StyleUtil.successBtn("✔  Mark Done");
        doneBtn.setMaxWidth(Region.USE_PREF_SIZE);
        doneBtn.setOnAction(e -> {
            BankService.getInstance().markTaskComplete(task.getTaskId());
            KidBankApp.navigate(build(child));
        });

        row.getChildren().addAll(info, spacer, doneBtn);
        return row;
    }

    private static HBox buildStatusRow(Task task, String statusText, String color) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #1A2338; -fx-background-radius: 14; "
                + "-fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 14; -fx-padding: 14;");

        VBox info = new VBox(4);

        Label titleLbl = new Label(task.getTitle());
        titleLbl.setStyle("-fx-font-weight: 800; -fx-font-size: 13px; -fx-text-fill: #EAF0FF;");

        Label status = new Label(statusText);
        status.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px; -fx-font-weight: 700;");

        info.getChildren().addAll(titleLbl, status);

        row.getChildren().add(info);
        return row;
    }
}