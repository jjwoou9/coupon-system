package com.example.api.service;

import com.example.api.domain.Coupon;
import com.example.api.producer.CouponCreateProducer;
import com.example.api.repository.AppliedUserRepository;
import com.example.api.repository.CouponCountRepository;
import com.example.api.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplyService {

    private final CouponRepository couponRepository;
    private final CouponCountRepository couponCountRepository;
    private final CouponCreateProducer couponCreateProducer;
    private final AppliedUserRepository appliedUserRepository;

    public void apply(Long userId){
        Long apply = appliedUserRepository.add(userId);

        if(apply != 1){
            return;
        }

        long count = couponCountRepository.increment();
        //redis는 single thread 이기 때문에 동시 접속에도 값이 하나씩 증가한다.
        //but 사이즈가 커지면 RDB에 부하가 생기기 때문에 이것만으로 사용하기에는 무리무리. 시간도 오래걸림. 다른 서비스 요청도 느려짐.

        if(count > 100){
            return;
        }

        couponCreateProducer.create(userId);

    }
}
