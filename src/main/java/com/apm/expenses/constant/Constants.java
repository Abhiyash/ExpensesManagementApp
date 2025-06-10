package com.apm.expenses.constant;

import java.util.List;

public class Constants {
    public static final String APP_FILES_PATH = "C:\\Users\\Abhiyash\\ExpenseManagementAppFiles";
    public static final String INPUT_FILE = "input_files";
    public static final String OUTPUT_FILE = "output_files";
    public static final String[] HEADERS = { "Date", "Narration","Value Dat","Debit Amount","Credit Amount","Chq/Ref Number","Closing Balance"};
    public static final List<String> categoryName = List.of("Wants","Investments","Needs");
    public static final List<String> nextMonthExpense = List.of("NextMonthExpenses");
}
