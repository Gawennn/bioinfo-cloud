package com.bioinfo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.bioinfo.dto.ItemDTO;
import com.bioinfo.entity.Item;
import com.bioinfo.dto.Result;
import com.bioinfo.mapper.ItemMapper;
import com.bioinfo.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 刘家雯
 * @Date 2025/5/17
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemMapper itemMapper;

    @Override
    public Result addItem(ItemDTO itemDTO) {

        Item item = new Item();
        BeanUtil.copyProperties(itemDTO, item);
        boolean isadd = itemMapper.addItem(item);
        if (isadd) {
            return Result.ok("添加项目成功！");
        }
        return Result.fail("添加商品失败");
    }
}
