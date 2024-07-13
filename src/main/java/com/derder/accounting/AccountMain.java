package com.derder.accounting;

import com.derder.accounting.manager.ExpenseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class AccountMain extends Application {
    static ExpenseManager manager = new ExpenseManager();

    static {
        // 關閉程式時將資料寫入txt
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("*系統* :關閉程式中...");
            try {
                manager.writeToTxt();
            } catch (IOException e) {
                System.out.println("Write error " + e.getMessage());
            }
            System.out.println("*系統* :已成功保存數據並關閉程式。");
        }));
    }

    @Override
    public void start(Stage stage) throws IOException {

        //視窗布局
        FXMLLoader fxmlLoader = new FXMLLoader(AccountMain.class.getResource("main-window.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1500, 800);

        //載入CSS
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());
        stage.setTitle("記帳程式");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/derder/accounting/window-icon.png"))));
        stage.setScene(scene);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}