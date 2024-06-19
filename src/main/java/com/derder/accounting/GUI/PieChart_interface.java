package com.derder.accounting.GUI;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;

import java.util.List;

public class PieChart_interface {
    @FXML
    private PieChart pieChart;
    public void setChartData(List<Double> total) {
        //圓餅圖設定
        System.out.println(total.toString());
        pieChart.getData().add(new PieChart.Data("Food", total.get(0)));
        pieChart.getData().add(new PieChart.Data("Clothes", total.get(1)));
        pieChart.getData().add(new PieChart.Data("Education", total.get(2)));
        pieChart.getData().add(new PieChart.Data("Entertainment", total.get(3)));
        pieChart.getData().add(new PieChart.Data("Housing", total.get(4)));
        pieChart.getData().add(new PieChart.Data("Tax", total.get(5)));
        pieChart.getData().add(new PieChart.Data("Transportation", total.get(6)));
        pieChart.getData().add(new PieChart.Data("Other", total.get(7)));
    }
}
