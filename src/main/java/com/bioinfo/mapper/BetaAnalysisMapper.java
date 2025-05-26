package com.bioinfo.mapper;

import com.bioinfo.entity.AlphaDiversity;
import com.bioinfo.entity.BetaDiversity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author 刘家雯
 * @version 1.0
 * @Date 2025/4/18
 */
@Mapper
public interface BetaAnalysisMapper {

    // 插入分析结果记录
    @Insert("INSERT INTO beta_result (task_id, sample_name, poca1, pcoa2) " +
            "VALUES (#{taskId}, #{sampleName}, #{pcoa1}, #{pcoa2})")
    void insert(BetaDiversity betaDiversity);

    // 展示结果

    @Select("select sample_name, pcoa1, pcoa2" +
            " from beta_result where task_id = #{taskId}")
    List<BetaDiversity> selectByTaskId(Long taskId);
}
