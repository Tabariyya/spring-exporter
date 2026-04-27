package com.tabariyya.export;

import com.fasterxml.jackson.databind.JsonNode;
import com.tabariyya.export.exporter.Exporter;
import com.waleed.utils.JsonUtility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Aspect
public class ExportableAspect {
    private final ExporterResolver exporterResolver = new ExporterResolver();

    @Around("@annotation(exportable)")
    public Object export(ProceedingJoinPoint joinPoint, Exportable exportable) throws Throwable {
        Object result = joinPoint.proceed();
        JsonNode resultPath;
        List<Map<String, Object>> dataList;

        if (result instanceof List) {
            resultPath = JsonUtility.toJson(result);
            dataList = JsonUtility.fromJson(resultPath.toString(), List.class);
        }
        else if (result !=null) {
            resultPath = JsonUtility.toJson(result).at(exportable.path());
            Map<String, Object> singleMap = JsonUtility.fromJson(resultPath.toString(), Map.class);
            dataList = Collections.singletonList(singleMap);
        } else {
            dataList = Collections.emptyList();
        }

        List<Map<String, Object>> flattenList = dataList.stream()
                .map(ExportableAspect::flatten)
                .collect(Collectors.toList());

        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attrs.getRequest();
        HttpServletResponse response = attrs.getResponse();

        String header = request.getHeader(HttpHeaders.ACCEPT);

        if (header == null || header.equals("*/*") || header.contains("application/json")) {
            return result;
        }

        if (flattenList == null || flattenList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Export list is empty.");
        }

        Optional<ExportType> requestFormat = ExportType.match(header);

        if (!requestFormat.isPresent()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        ExportType exportType = requestFormat.get();

        Exporter exporter = exporterResolver.getExporter(exportType);
        exporter.export(flattenList, response);
        return null;

    }

    private static Map<String, Object> flatten(Map<String, Object> values) {
        Map<String, Object> flattened = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map<?, ?>) {
                Map<String, Object> nestedMap = flatten((Map<String, Object>) value);
                for (Map.Entry<String, Object> nestedEntry : nestedMap.entrySet()) {
                    flattened.put(key + "." + nestedEntry.getKey(), nestedEntry.getValue());
                }
            } else {
                flattened.put(key, value);
            }
        }
        return flattened;
    }

}
