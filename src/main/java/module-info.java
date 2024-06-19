module com.derder.accounting {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.derder.accounting.GUI to javafx.fxml;
    exports com.derder.accounting;
    exports com.derder.accounting.GUI;
}