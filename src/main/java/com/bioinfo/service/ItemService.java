package com.bioinfo.service;

import com.bioinfo.dto.ItemDTO;
import com.bioinfo.dto.Result;

/**
 * @author 刘家雯
 * @Date 2025/5/17
 */
public interface ItemService {

    Result addItem(ItemDTO itemDTO);
}
