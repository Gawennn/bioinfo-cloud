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
 *
 * 将 Excel 文件内容读取为 List<Map<String, String>> 格式的数据
 * 接收一个 InputStream 输入流，返回一个包含 Excel 数据的列表，其中每个元素是一个 Map，表示一行数据
 */
public class EasyExcelUtils {

    // 此方法可以读取任意结构的 Excel 文件
    // 不需要预先定义 Java 类
    public static List<Map<String, String>> readDynamicExcel(InputStream inputStream) {
        List<Map<String, String>> result = new ArrayList<>();

        EasyExcel.read(inputStream)
                .sheet()
                .headRowNumber(1) // 指定第一行为表头
                .registerReadListener(new AnalysisEventListener<Map<Integer, String>>() {
                    private List<String> headers;

                    // 处理表头，将表头信息存储在 headers 列表中
                    // headMap 的键是列索引，值是列名
                    @Override
                    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
                        // 记录表头
                        headers = new ArrayList<>();
                        for (int i = 0; i < headMap.size(); i++) {
                            headers.add(headMap.get(i));
                        }
                    }

                    // 处理数据行
                    // 将每行数据转换为 Map 格式
                    // 使用表头信息将列索引转换为列名作为键
                    // 将转换后的行数据添加到结果列表中
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