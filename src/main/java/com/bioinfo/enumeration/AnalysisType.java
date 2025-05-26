package com.bioinfo.enumeration;

/**
 * @author 刘家雯
 * @Date 2025/5/18
 */
public enum AnalysisType {
    ALPHA("alpha多样性分析"),
    BETA("beta多样性分析");

    private final String desc;

    AnalysisType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public static AnalysisType fromDesc(String desc) {
        for (AnalysisType type : values()) {
            if (type.getDesc().equals(desc)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的分析类型: " + desc);
    }
}
