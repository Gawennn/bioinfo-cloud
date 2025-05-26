package com.bioinfo.utils;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.io.InputStream;
import java.util.*;

/**
 * @author 刘家雯
 * @version 1.0
 * @Date 2025/4/14
 */
public class EasyExcelUtils {

    public static List<Map<String, String>> readDynamicExcel(InputStream inputStream) {
        List<Map<String, String>> result = new ArrayList<>();

        EasyExcel.read(inputStream)
                .sheet()
                .headRowNumber(1) // 指定第一行为表头
                .registerReadListener(new AnalysisEventListener<Map<Integer, String>>() {
                    private List<String> headers;

                    @Override
                    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
                        // 记录表头
                        headers = new ArrayList<>();
                        for (int i = 0; i < headMap.size(); i++) {
                            headers.add(headMap.get(i));
                        }
                    }

                    @Override
                    public void invoke(Map<Integer, String> data, AnalysisContext context) {
                        Map<String, String> row = new HashMap<>();
                        for (Map.Entry<Integer, String> entry : data.entrySet()) {
                            String key = headers.get(entry.getKey());
                            row.put(key, entry.getValue());
                        }
                        result.add(row);
                    }

                    @Override
                    public void doAfterAllAnalysed(AnalysisContext context) {}
                })
                .doRead();

        return result;
    }
}