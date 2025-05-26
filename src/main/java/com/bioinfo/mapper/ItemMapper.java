package com.bioinfo.mapper;

import com.bioinfo.entity.Item;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author 刘家雯
 * @Date 2025/5/17
 */
@Mapper
public interface ItemMapper {

    @Insert("insert into items (name, price) values (#{name}, 100*#{price})")
    boolean addItem(Item item);

    @Select("select (price/100) from items where name = #{name}")
    Long getPriceByName(String name);
}
