package com.droidgpt.model;

import java.time.format.DateTimeFormatter;

public class TimeFormats {

    public static final DateTimeFormatter TIME       = DateTimeFormatter.ofPattern("HH:mm");
    public static final DateTimeFormatter DATE       = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter DATE_TIME  = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
}
