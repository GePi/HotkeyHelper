package com.hotkeyhelper;

import com.hotkeyhelper.model.WindowInfo;
import com.hotkeyhelper.service.ShortcutRepository;
import com.hotkeyhelper.service.WindowTracker;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.nio.file.Path;

public class App extends Application {

    private static final int WINDOW_WIDTH = 500;
    private static final int WINDOW_HEIGHT = 700;

    private WindowTracker windowTracker;
    private ShortcutRepository shortcutRepository;
    private Label processNameLabel;
    private Label windowTitleLabel;
    private WebView shortcutsWebView;

    @Override
    public void start(Stage primaryStage) {
        // Initialize repository
        shortcutRepository = new ShortcutRepository();

        // Create root layout
        VBox root = new VBox();
        root.setStyle("-fx-background-color: #1e1e2e;");

        // WebView for shortcuts
        shortcutsWebView = new WebView();
        shortcutsWebView.getEngine().setUserStyleSheetLocation(
                "data:text/css," +
                "body { " +
                "  background-color: #1e1e2e; " +
                "  color: #cdd6f4; " +
                "  font-family: 'Segoe UI', Arial, sans-serif; " +
                "  margin: 10px; " +
                "  font-size: 14px; " +
                "}" +
                "::-webkit-scrollbar { width: 8px; } " +
                "::-webkit-scrollbar-track { background: #313244; } " +
                "::-webkit-scrollbar-thumb { background: #585b70; border-radius: 4px; }"
        );
        shortcutsWebView.setContextMenuEnabled(false);
        VBox.setVgrow(shortcutsWebView, Priority.ALWAYS);

        // Default message
        shortcutsWebView.getEngine().loadContent(
                "<html><body><p style='text-align:center; color:#6c7086; padding-top:50px;'>" +
                "No shortcuts available for this window</p></body></html>"
        );

        // Status bar at the bottom
        processNameLabel = createInfoLabel("Process: ", "N/A");
        windowTitleLabel = createInfoLabel("Title: ", "N/A");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox statusBar = new HBox(15);
        statusBar.setStyle("-fx-background-color: #181825; -fx-padding: 4 10 4 10;");
        statusBar.setAlignment(Pos.CENTER_LEFT);
        statusBar.getChildren().addAll(processNameLabel, spacer, windowTitleLabel);

        root.getChildren().addAll(shortcutsWebView, statusBar);

        // Create scene
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.setFill(Color.web("#1e1e2e"));

        // Configure stage (DECORATED = resizable + standard min/max/close buttons)
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setScene(scene);
        primaryStage.setTitle("HotKey Helper");

        // Position on second monitor (or primary if only one exists)
        positionOnSecondMonitor(primaryStage);

        // Setup window tracker
        windowTracker = new WindowTracker(this::onWindowChange);
        windowTracker.start();

        // Handle cleanup on close
        primaryStage.setOnCloseRequest(event -> {
            if (windowTracker != null) {
                windowTracker.stop();
            }
        });

        primaryStage.show();
    }

    private Label createInfoLabel(String prefix, String value) {
        Label label = new Label(prefix + value);
        label.setFont(Font.font("Consolas", 11));
        label.setStyle("-fx-text-fill: #a6adc8;");
        label.setMaxWidth(Double.MAX_VALUE);
        return label;
    }

    private void onWindowChange(WindowInfo info) {
        Platform.runLater(() -> {
            processNameLabel.setText("Process: " + info.getProcessName());
            windowTitleLabel.setText("Title: " + (info.getWindowTitle().isEmpty() ? "N/A" : info.getWindowTitle()));

            // Load shortcuts HTML
            String html = shortcutRepository.getShortcutsHtml(info);
            if (html != null && !html.isEmpty()) {
                shortcutsWebView.getEngine().loadContent(html);
            } else {
                shortcutsWebView.getEngine().loadContent(
                        "<html><body><p style='text-align:center; color:#6c7086; padding-top:50px;'>" +
                        "No shortcuts available for " + info.getProcessName() + "</p></body></html>"
                );
            }
        });
    }

    private void positionOnSecondMonitor(Stage stage) {
        var screens = Screen.getScreens();

        if (screens.size() > 1) {
            // Position on second monitor
            Screen secondScreen = screens.get(1);
            Rectangle2D bounds = secondScreen.getVisualBounds();

            // Position in top-right corner of second monitor
            stage.setX(bounds.getMaxX() - WINDOW_WIDTH - 20);
            stage.setY(bounds.getMinY() + 20);
        } else {
            // Position on primary monitor (top-right corner)
            Screen primaryScreen = Screen.getPrimary();
            Rectangle2D bounds = primaryScreen.getVisualBounds();

            stage.setX(bounds.getMaxX() - WINDOW_WIDTH - 20);
            stage.setY(bounds.getMinY() + 20);
        }
    }

    public static void main(String[] args) {
        try {
            // Redirect stderr to log file for jpackage diagnostics
            Path logFile = Path.of(System.getProperty("user.dir"), "hotkeyhelper.log");
            System.setErr(new java.io.PrintStream(logFile.toFile()));
            System.setOut(new java.io.PrintStream(logFile.toFile()));
            System.out.println("Starting HotKey Helper...");
            System.out.println("Working dir: " + System.getProperty("user.dir"));
            System.out.println("App path: " + System.getProperty("jpackage.app-path"));
            launch(args);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
