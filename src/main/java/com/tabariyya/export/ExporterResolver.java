package com.tabariyya.export;


import com.tabariyya.export.exporter.CsvExporter;
import com.tabariyya.export.exporter.ExcelExporter;
import com.tabariyya.export.exporter.Exporter;

import java.util.HashMap;
import java.util.Map;

public class ExporterResolver {
    private final Map<ExportType, Exporter>exporters= new HashMap<>();
    public ExporterResolver() {
        exporters.put(ExportType.CSV, new CsvExporter());
        exporters.put(ExportType.EXCEL, new ExcelExporter());
    }
    public Exporter getExporter(ExportType type) {
        return exporters.get(type);
    }
}