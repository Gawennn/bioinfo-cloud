package com.bioinfo.mapper;

import com.bioinfo.entity.DiscussionPost;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author 刘家雯
 * @Date 2025/5/17
 */
@Mapper
public interface DiscussionPostMapper {

    @Insert("INSERT INTO discussion_post (user_id, title, content, user_name) " +
            "VALUES (#{userId}, #{title}, #{content}, #{userName})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(DiscussionPost post);

    @Select("SELECT * FROM discussion_post WHERE id = #{id}")
    DiscussionPost selectById(@Param("id") Long id);

    @Select("SELECT * FROM discussion_post ORDER BY status DESC, create_time DESC")
    List<DiscussionPost> selectAll();

    @Update("UPDATE discussion_post SET liked = liked + 1 WHERE id = #{id}")
    void incrementLike(@Param("id") Long id);

    @Update("UPDATE discussion_post SET comments = comments + 1 WHERE id = #{id}")
    void incrementComments(@Param("id") Long id);
}
