package com.bioinfo.mapper;

import com.bioinfo.entity.DiscussionComment;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author 刘家雯
 * @Date 2025/5/17
 */
@Mapper
public interface DiscussionCommentMapper {

    @Insert("INSERT INTO discussion_comment (post_id, user_id, content, liked, status, user_name) " +
            "VALUES (#{postId}, #{userId}, #{content}, 0, 0, #{userName})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(DiscussionComment comment);

    @Select("SELECT * FROM discussion_comment WHERE post_id = #{postId} ORDER BY create_time ASC")
    List<DiscussionComment> selectByPostId(@Param("postId") Long postId);

    @Update("UPDATE discussion_comment SET liked = liked + 1 WHERE id = #{id}")
    void incrementLike(@Param("id") Long id);
}
