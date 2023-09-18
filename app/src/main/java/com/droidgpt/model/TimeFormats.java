package com.droidgpt.model;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TimeFormats {

    public static final DateTimeFormatter TIME       = DateTimeFormatter.ofPattern("HH:mm");
    public static final DateTimeFormatter DATE       = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter DATE_TIME  = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    public static final DateTimeFormatter DATE_TXT  = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ITALIAN);
}
