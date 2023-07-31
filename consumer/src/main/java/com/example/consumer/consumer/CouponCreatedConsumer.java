package com.example.consumer.consumer;

import com.example.consumer.domain.Coupon;
import com.example.consumer.domain.FailedEvent;
import com.example.consumer.repository.CouponRepository;
import com.example.consumer.repository.FailedEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CouponCreatedConsumer {

    private final CouponRepository couponRepository;
    private final FailedEventRepository failedEventRepository;

    private final Logger logger = LoggerFactory.getLogger(CouponCreatedConsumer.class);

    public CouponCreatedConsumer(CouponRepository couponRepository, FailedEventRepository failedEventRepository) {
        this.couponRepository = couponRepository;
        this.failedEventRepository = failedEventRepository;
    }

    @KafkaListener(topics = "coupon_create", groupId = "group_1")
    public void listener(Long userId){
        try{
            couponRepository.save(new Coupon(userId));
        }catch (Exception e){
            logger.error("failed to create coupon::" + userId);
            failedEventRepository.save(new FailedEvent(userId));
        }
    }


    /*
    * Consumer에서 에러가 발생했을 때
    * 컨슈머에서 토픽에 있는 데이터를 가져간 후에 쿠폰을 발급하는 과정에서 에러가 생긴다면
    * 쿠폰은 발급되지 않고 발급된 count만 증가하는 오류가 생길 수 있다.
    *
    * */

}
