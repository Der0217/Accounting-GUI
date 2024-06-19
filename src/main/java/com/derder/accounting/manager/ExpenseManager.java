package com.derder.accounting.manager;

import com.derder.accounting.type.ExpenseEntry;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ExpenseManager {
    private static final Map<String,List<ExpenseEntry>> allExpense= new LinkedHashMap<>();
    private final LocalDate date = LocalDate.now();
    String dataPath = "src/main/java/com/derder/accounting/type/Data.txt";

    static {
        // 初始化所有支出類型列表
        allExpense.put("food", new ArrayList<>());
        allExpense.put("clothes", new ArrayList<>());
        allExpense.put("education", new ArrayList<>());
        allExpense.put("entertainment", new ArrayList<>());
        allExpense.put("housing", new ArrayList<>());
        allExpense.put("tax", new ArrayList<>());
        allExpense.put("transportation", new ArrayList<>());
        allExpense.put("other", new ArrayList<>());
    }
    // 載入檔案所有支出資料
    public  void loadToTxt() throws IOException {
        File file = new File(dataPath);
        BufferedReader br = new BufferedReader(new FileReader(file));
        try {
            String line;
            while ((line = br.readLine()) != null) {
                String []spiltPart=line.split("\\\\");
                String type =spiltPart[0];
                int year = Integer.parseInt(spiltPart[1]);
                int month = Integer.parseInt(spiltPart[2]);
                int day = Integer.parseInt(spiltPart[3]);
                String description = spiltPart[4];
                double amount = Double.parseDouble(spiltPart[5]);
                addExpense(type,year,month,day,description,amount);
            }
        }catch (IOException w){
            w.printStackTrace();
        }
        sortExpenseDate(); // 載入後進行排序
    }

    // 將支出資料寫入txt檔
    public void writeToTxt() throws IOException {
        File file = new File(dataPath);
        FileWriter fileWriter = new FileWriter(file);
        allExpense.forEach((category, expenseEntries)-> {
            StringBuilder sb = new StringBuilder();
            if(!expenseEntries.isEmpty()){
                expenseEntries.forEach(expenseEntry->{
                        sb.append(category).append("\\").append(expenseEntry.getYear()).append("\\").append(expenseEntry.getMonuth()).append("\\").append(expenseEntry.getDay()).append("\\").append(expenseEntry.getDescription()).append("\\").append(expenseEntry.getAmount()).append("\n");
                });
                try {
                    fileWriter.write(sb.toString());
                    fileWriter.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        });
        fileWriter.close();
    }
    // 添加當天日期的支出資料
    public void addExpense(String type,String description, double amount){
        LocalDate date = LocalDate.now();
        ExpenseEntry expense = ExpenseEntryFactory.createExpenseEntry(
                type, date.getYear(), date.getMonthValue(), date.getDayOfMonth(), description, amount);
        allExpense.get(type).add(expense);
        sortExpenseDate();
    }
    // Overidding 添加指定日期的支出資料
    public void addExpense(String type,int year,int month,int day,String description, double amount){
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
    public  List<String> show(String type) {
        List<String> temp =new ArrayList<>();
        if(allExpense.get(type)!=null){
            AtomicInteger index =new AtomicInteger(1);
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
    public void reSetExpense(String type , int index , String description,double amount){
        allExpense.get(type).get(index).setDescriptionAndAmount(description,amount);
    }

    //輸出各類型總和金額返回一個List
    public List<Double> getTotalExpenses(){
        List sumOfExpenseOfTotal = new ArrayList();
        AtomicLong temp = new AtomicLong();

        allExpense.forEach((String s, List<ExpenseEntry> expenseEntries)->{
                expenseEntries.forEach((ExpenseEntry expenseEntry)->{
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
