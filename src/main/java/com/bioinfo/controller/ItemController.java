package com.bioinfo.controller;

import com.bioinfo.dto.ItemDTO;
import com.bioinfo.dto.Result;
import com.bioinfo.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



/**
 * @author 刘家雯
 * @Date 2025/5/17
 *
 * 项目管理
 */
@RestController
@RequestMapping("/admin/item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @PostMapping("/add")
    public Result addItem(@RequestBody ItemDTO itemDTO) {
        return itemService.addItem(itemDTO);
    }
}
