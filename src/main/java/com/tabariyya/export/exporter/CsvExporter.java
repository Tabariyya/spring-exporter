package com.tabariyya.export.exporter;

import com.opencsv.CSVWriter;
import jakarta.servlet.http.HttpServletResponse; //changeable accordance of spring version

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CsvExporter implements Exporter {
    @Override
    public void export(List list, HttpServletResponse response) throws Exception {
        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=list.csv");

        List<Map<String, Object>> mapList = list;
        List<String> headers = new ArrayList<>(mapList.get(0).keySet());

        CSVWriter csvWriter = new CSVWriter(response.getWriter());
        csvWriter.writeNext(headers.toArray(new String[0]));

        for (Map<String, Object> row : mapList) {
            String[] values = headers.stream()
                    .map(key -> {
                        Object value = row.get(key);
                        return value != null ? value.toString() : "";
                    }).toArray(String[]::new);
            csvWriter.writeNext(values);
        }

        csvWriter.flush();
        csvWriter.close();
    }

}
