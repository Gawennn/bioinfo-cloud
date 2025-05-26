package com.bioinfo.controller;

import com.bioinfo.dto.VoucherDTO;
import com.bioinfo.dto.Result;
import com.bioinfo.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 刘家雯
 * @Date 2025/5/17
 *
 * 秒杀券管理接口
 *
 */
@RestController
@RequestMapping("/admin/voucher")
public class VoucherController {

    @Autowired
    private VoucherService voucherService;

    /**
     * 新增优惠券
     * @param voucherDTO
     * @return
     */
    @PostMapping("/add")
    public Result addVoucer(@RequestBody VoucherDTO voucherDTO) {
        return voucherService.addVoucher(voucherDTO);
    }

    /**
     * 用户秒杀券
     * @param voucherId
     * @return
     */
    @PostMapping("/seckill/{voucherId}")
    public Result seckillVoucher(@PathVariable("voucherId") Long voucherId){
        return voucherService.seckillVoucher(voucherId);
    }
}
