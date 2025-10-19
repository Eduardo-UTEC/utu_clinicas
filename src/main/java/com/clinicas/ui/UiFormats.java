package com.clinicas.ui;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class UiFormats {
    private UiFormats(){}

    // Formatos centrales (dd/MM/yyyy y dd/MM/yyyy HH:mm)
    public static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            .withLocale(new Locale("es", "UY"));
    public static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            .withLocale(new Locale("es", "UY"));

    // Renderer para LocalDateTime / Timestamp en JTables
    public static TableCellRenderer dateTimeRenderer(){
        return new DefaultTableCellRenderer(){
            @Override
            protected void setValue(Object value) {
                if (value == null) { setText(""); return; }
                if (value instanceof LocalDateTime) {
                    setText(((LocalDateTime) value).format(DATETIME_FMT));
                } else if (value instanceof Timestamp) {
                    setText(((Timestamp) value).toLocalDateTime().format(DATETIME_FMT));
                } else {
                    // Fallback: intentar parseo rápido si viene como String ISO
                    try {
                        LocalDateTime ldt = LocalDateTime.parse(value.toString().replace(' ', 'T'));
                        setText(ldt.format(DATETIME_FMT));
                    } catch(Exception ex){
                        setText(value.toString());
                    }
                }
            }
        };
    }
}
