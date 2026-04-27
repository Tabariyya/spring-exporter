package com.tabariyya.export.exporter;

import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface Exporter{
    void export (List list, HttpServletResponse response) throws Exception;
}
