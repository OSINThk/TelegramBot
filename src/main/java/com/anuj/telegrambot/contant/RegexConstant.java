package com.anuj.telegrambot.contant;

public class RegexConstant {

    public static final String PRODUCT_CATEGORY_FORMAT = "prod_(\\d+)_report_(\\d+)";

    public static final String PRODUCT_SCARCITY_FORMAT = "scarcity_(\\d+)_(\\d+)";

    public static final String PRODUCT_EXPENSIVE_FORMAT = "expensive_(\\d+)_(\\d+)";

    public static final String PRODUCT_NOTE_FORMAT = "/note\\s(\\d+)\\s(.*)";

    public static final String SEND_REPORT_ACCEPT = "send_report_true_(\\d+)";
    public static final String SEND_REPORT_REJECT = "send_report_false_(\\d+)";

    public static final String LANGUAGE_CHOOSE_OPTION = "language_option_(\\d+)";

    public static final String SKIP_NOTE = "skip_note_(\\d+)";
}
