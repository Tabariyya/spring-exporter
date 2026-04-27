package com.tabariyya.export.exporter;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExcelExporter implements Exporter {

    @Override
    public void export(List list, HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=list.xlsx");

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();

        List<Map<String, Object>> mapList = list;
        List<String> keys = new ArrayList<>(mapList.get(0).keySet());

        Row header = sheet.createRow(0);
        for (int i = 0; i < keys.size(); i++) {
            header.createCell(i).setCellValue(keys.get(i));
        }

        int rowNo = 1;
        for (Map<String, Object> rowMap : mapList) {
            Row row = sheet.createRow(rowNo++);
            for (int i = 0; i < keys.size(); i++) {
                Object val = rowMap.get(keys.get(i));
                row.createCell(i).setCellValue(val != null ? val.toString() : "");
            }
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

}

