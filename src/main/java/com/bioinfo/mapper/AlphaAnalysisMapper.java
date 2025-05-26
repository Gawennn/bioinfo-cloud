package com.bioinfo.mapper;

import com.bioinfo.entity.AlphaDiversity;
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
public interface AlphaAnalysisMapper {

    // 插入分析结果记录
    @Insert("INSERT INTO alpha_result (task_id, created_time, updated_time, sample_name, group_name, shannon, simpson, chao1, observed_species, goods_coverage) " +
            "VALUES (#{taskId}, NOW(), NOW(), #{sampleName}, #{groupName}, #{shannon}, #{simpson}, #{chao1}, #{observedSpecies}, #{goodsCoverage})")
    void insert(AlphaDiversity alphaDiversity);

    // 展示结果
    @Select("select sample_name, group_name, shannon, simpson, chao1, observed_species, goods_coverage" +
            " from alpha_result where task_id = #{taskId}")
    List<AlphaDiversity> selectByTaskId(Long taskId);
}
