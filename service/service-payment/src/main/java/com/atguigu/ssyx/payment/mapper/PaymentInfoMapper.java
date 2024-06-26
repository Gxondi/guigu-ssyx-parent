package com.atguigu.ssyx.payment.mapper;

import com.atguigu.ssyx.model.order.PaymentInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentInfoMapper extends BaseMapper<PaymentInfo> {
}
