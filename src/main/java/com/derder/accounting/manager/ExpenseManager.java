package com.derder.accounting.manager;

import com.derder.accounting.type.ExpenseEntry;
import com.derder.accounting.type.FoodExpense;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ExpenseManager {
    private static Map<String, List<ExpenseEntry>> allExpense = new LinkedHashMap<>();
    private final LocalDate date = LocalDate.now();
    File dataPath = new File("src/main/java/com/derder/accounting/type/Data.txt");
    // 載入檔案所有支出資料
    public void loadToTxt() throws IOException {
        ObjectInputStream ois = null;
        try {
            if (dataPath.length() != 0) {
                ois = new ObjectInputStream(new FileInputStream(dataPath));
                allExpense = (Map<String, List<ExpenseEntry>>) ois.readObject();
            } else {
                // 初始化資料
                allExpense.put("food", new ArrayList<>());
                allExpense.put("clothes", new ArrayList<>());
                allExpense.put("education", new ArrayList<>());
                allExpense.put("entertainment", new ArrayList<>());
                allExpense.put("housing", new ArrayList<>());
                allExpense.put("tax", new ArrayList<>());
                allExpense.put("transportation", new ArrayList<>());
                allExpense.put("other", new ArrayList<>());
            }
        } catch (EOFException eof) {
            System.out.println("Reached end of file unexpectedly: " + eof.getMessage());
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error reading object from file: " + e.getMessage());
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    System.out.println("Error closing ObjectInputStream: " + e.getMessage());
                }
            }
        }
    }

    // 將支出資料寫入txt檔
    public void writeToTxt() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataPath))) {
            oos.writeObject(allExpense);
        }
    }

    // 添加當天日期的支出資料
    public void addExpense(String type, String description, double amount) {
        LocalDate date = LocalDate.now();
        ExpenseEntry expense = ExpenseEntryFactory.createExpenseEntry(
                type, date.getYear(), date.getMonthValue(), date.getDayOfMonth(), description, amount);
        allExpense.get(type).add(expense);
        sortExpenseDate();
    }

    // Overidding 添加指定日期的支出資料
    public void addExpense(String type, int year, int month, int day, String description, double amount) {
        ExpenseEntry expense = ExpenseEntryFactory.createExpenseEntry(
                type, year, month, day, description, amount);
        allExpense.get(type).add(expense);
        sortExpenseDate();
    }

    // 移除指定的支出資料
    public void removeExpense(String type, int index) {
        allExpense.get(type).remove(index);
    }

    //輸出所有支出資料
    public List<String> show(String type) {
        List<String> temp = new ArrayList<>();
        if (allExpense.get(type) != null) {
            AtomicInteger index = new AtomicInteger(1);
            for (ExpenseEntry expense : allExpense.get(type).stream().toList()) {
                temp.add(String.format("%d.| \uD83D\uDCC5: %04d/%02d/%02d  💲: %.2f  💬: %s ",
                        index.getAndIncrement(),
                        expense.getYear(),
                        expense.getMonuth(),
                        expense.getDay(),
                        expense.getAmount(),
                        expense.getDescription()));
            }
            return temp;
        }

        return Collections.emptyList();
    }

    // 依照日期對支出資料進行排序
    public void sortExpenseDate() {
        allExpense.forEach((category, expenseEntries) -> {
            if (!expenseEntries.isEmpty()) {
                //Comparator.comparing 對每一值進行比較，thanComparing是前面比較完跟著比較，先比較年再來月最後是日
                expenseEntries.sort(Comparator.comparing(ExpenseEntry::getYear)
                        .thenComparing(ExpenseEntry::getMonuth).thenComparing(ExpenseEntry::getDay));
            }
        });
    }

    // 對該支出資料進行設定
    public void reSetExpense(String type, int index, String description, double amount) {
        allExpense.get(type).get(index).setDescriptionAndAmount(description, amount);
    }

    //輸出各類型總和金額返回一個List
    public List<Double> getTotalExpenses() {
        List sumOfExpenseOfTotal = new ArrayList();
        AtomicLong temp = new AtomicLong();

        allExpense.forEach((String s, List<ExpenseEntry> expenseEntries) -> {
                    expenseEntries.forEach((ExpenseEntry expenseEntry) -> {
                                temp.addAndGet((long) expenseEntry.getAmount());
                            }
                    );
                    sumOfExpenseOfTotal.add(temp.doubleValue());
                    temp.set(0);
                }
        );
        return sumOfExpenseOfTotal;
    }
}
