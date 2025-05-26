package com.bioinfo.service;

import com.bioinfo.dto.VoucherDTO;
import com.bioinfo.dto.Result;
import com.bioinfo.entity.VoucherOrder;

/**
 * @author 刘家雯
 * @Date 2025/5/17
 */
public interface VoucherService {

    Result addVoucher(VoucherDTO voucherDTO);

    Result seckillVoucher(Long voucherId);

    void createVoucherOrder(VoucherOrder voucherOrder);
}
