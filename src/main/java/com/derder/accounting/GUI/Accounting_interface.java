package com.derder.accounting.GUI;

import com.derder.accounting.AccountMain;
import com.derder.accounting.manager.ExpenseManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;


public class Accounting_interface {
    @FXML
    private ListView<String> food, clothes, education, entertainment, housing, tax, transportation, other;
    @FXML
    private Label titleLabel, foodLabel, clothesLabel, educationLabel, entertainmentLabel, housingLabel, otherLabel, taxLabel, transportationLabel, textType, textDescription;
    @FXML
    private TextField amountField, descriptionField;
    @FXML
    private List<ListView<String>> listViewData;
    @FXML
    private ChoiceBox categoryChoiceBox, operationChoiceBox;
    @FXML
    private Button addButton, showPieChartButton;
    @FXML
    private ImageView itemImageView;
    @FXML
    private TextInputDialog inputDialog;
    static ExpenseManager manager = new ExpenseManager();
    private static final Map<String, ListView<String>> allListViewMap = new HashMap<>();

    @FXML
    public void initialize() {
        // 初始化選擇框預設值和ListView映射
        operationChoiceBox.getSelectionModel().selectFirst();
        categoryChoiceBox.getSelectionModel().selectFirst();

        //放入各ListView物件
        allListViewMap.put("food", food);
        allListViewMap.put("clothes", clothes);
        allListViewMap.put("education", education);
        allListViewMap.put("entertainment", entertainment);
        allListViewMap.put("housing", housing);
        allListViewMap.put("tax", tax);
        allListViewMap.put("transportation", transportation);
        allListViewMap.put("other", other);

        //監聽ListView點擊
        allListViewMap.forEach((s, stringListView) ->
                deleteOrResetListView(stringListView));


        addNumericValidation(amountField);

        // 載入資料並更新
        try {
            manager.loadToTxt();
            updateList();
        } catch (IOException e) {
            System.out.println("讀檔錯誤!" + e.getMessage());
        }
    }

    //按鈕動作
    @FXML
    private void ButtonClick() {
        // 添加支出
        if (!amountField.getText().isEmpty() && !descriptionField.getText().isEmpty()) {
            double textField1 = Double.parseDouble(amountField.getText());
            String type = categoryChoiceBox.getValue().toString();
            String textField2 = descriptionField.getText();
            manager.addExpense(type, textField2, textField1);
            updateList();
            amountField.setText("");
            descriptionField.setText("");
        }
    }

    //操作類別選擇框
    public void classSelect(ActionEvent actionEvent) {
        boolean isAddExpense = operationChoiceBox.getValue().toString().equals("Add Expense");
        //判斷選擇的項目去做動作
        amountField.setPromptText(isAddExpense ? "Please input the amount." : "");
        descriptionField.setPromptText(isAddExpense ? "Please input the description." : "");
        addButton.setText(isAddExpense ? "ADD" : "");
        amountField.setDisable(!isAddExpense);
        descriptionField.setDisable(!isAddExpense);
        categoryChoiceBox.setDisable(!isAddExpense);
        addButton.setDisable(!isAddExpense);
        updateList();
    }


    private void updateList() {
        // 更新所有ListView資料
        for (ListView<String> a : allListViewMap.values()) {
            a.getItems().clear();
            a.getItems().addAll(manager.show(a.getId()));
        }
    }


    private void addNumericValidation(TextField textField) {
        // 限制輸入框只能輸入數字
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    //選擇刪除或修改ListView
    private void deleteOrResetListView(ListView<String> listView) {
        listView.setOnMouseClicked(event -> {
            // 設定ListView的刪除或修改功能
            if (event.getClickCount() == 2) {
                String selectMode = operationChoiceBox.getValue().toString();
                if (selectMode.equals("Delete Expense")) {
                    handleDelete(listView);
                } else if (selectMode.equals("Reset Expense")) {
                    handleReset(listView);
                }
                updateList();
            }
        });
    }

    private void handleDelete(ListView<String> listView) {
        // 刪除選定的ListView項目
        String selectedItem = listView.getSelectionModel().getSelectedItem();
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
        if (selectedItem != null && selectedIndex >= 0) {
            //出現警告視窗
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Confirmation");
            alert.setHeaderText("Delete Item");
            alert.setContentText("Are you sure you want to delete '\n" + selectedItem + "'?");

            //警告視窗中的圖片
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/derder/accounting/cross.png")));
            ImageView imageView = new ImageView(image);
            alert.setGraphic(imageView);

            //警告視窗如果點擊OK按鈕則刪除選擇的ListView
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    listView.getItems().remove(selectedItem);
                    manager.removeExpense(listView.getId(), selectedIndex);
                    updateList();
                }
            });
        }
    }

    private void handleReset(ListView<String> listView) {
        // 修改選定的ListView項目

        //獲取選擇的ListView的內容跟Index
        String selectedItem = listView.getSelectionModel().getSelectedItem();
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();

        Optional<String> amountResult = showNumberIcInputDialog("Reset Expense",
                "Enter Amount", "Amount::",
                "/com/derder/accounting/sack-dollar.png");
        amountResult.ifPresent(amount -> {
            //建立另外一個視窗用於輸入敘述，並回傳結果
            Optional<String> descriptionResult = showInputDialog("Reset Expense", "Enter Description", "Description:", "/com/derder/accounting/edit.png");
            //如果用戶都輸入則把該ListView做修改
            descriptionResult.ifPresent(description -> {
                manager.reSetExpense(listView.getId(), selectedIndex, description, Double.parseDouble(amount));
            });
        });
    }

    private Optional<String> showInputDialog(String title, String header, String content, String imagePath) {
        // 建立另一個對話視窗用來輸入敘述
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);

        //在對話視窗建立圖片
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
        ImageView imageView = new ImageView(image);
        dialog.setGraphic(imageView);

        //等待用戶輸入
        return dialog.showAndWait();
    }

    private Optional<String> showNumberIcInputDialog(String title, String header, String content, String imagePath) {
        // 建立另一個對話視窗用來輸入敘述
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);

        //在對話視窗建立圖片
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
        ImageView imageView = new ImageView(image);
        dialog.setGraphic(imageView);

        TextField textField = dialog.getEditor();
        // 限制只能輸入數字
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                textField.setText(oldValue);
            }
        });
        //等待用戶輸入
        return dialog.showAndWait();
    }

    public void ShowClick() {
        try {
            // 加載新的 FXML 文件
            FXMLLoader loader = new FXMLLoader(AccountMain.class.getResource("chart-window.fxml"));
            AnchorPane page = loader.load();

            // 創建新舞台
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Pie Chart");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            PieChart_interface controller = loader.getController();
            controller.setChartData(manager.getTotalExpenses());

            // 顯示舞台
            dialogStage.showAndWait();
        } catch (IOException e) {
            System.out.println("圓餅圖加載失敗");
        }
    }
}
