package com.example.api.service;

import com.example.api.repository.CouponRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ApplyServiceTest {

    @Autowired
    private ApplyService applyService;

    @Autowired
    private CouponRepository couponRepository;

    @Test
    public void 한번만응모(){
        applyService.apply(1L);

        long count = couponRepository.count();

        assertEquals(count, 1);
    }

    @Test
    public void 여러명응모() throws InterruptedException {
        int threadCount = 1000; //요청 수
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount); //다른 스레드에서 수행하는 작업을 기다리는것을 도와줌

        for(int i=0; i < threadCount; i++){
            long userId = i;
            executorService.submit(() -> {
               try{
                   applyService.apply(userId);
               }finally {
                   latch.countDown();
               }
            });
        }

        latch.await();

        Thread.sleep(10000);
        //모든 요청이 완료되면 생성된 쿠폰의 개수를 확인
        long count = couponRepository.count();

        assertEquals(100, count);
    }

    @Test
    public void 한명당쿠폰하나만발급() throws InterruptedException {
        int threadCount = 1000; //요청 수
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount); //다른 스레드에서 수행하는 작업을 기다리는것을 도와줌

        for(int i=0; i < threadCount; i++){
            long userId = i;
            executorService.submit(() -> {
                try{
                    applyService.apply(1L);
                }finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Thread.sleep(10000);
        //모든 요청이 완료되면 생성된 쿠폰의 개수를 확인
        long count = couponRepository.count();

        assertEquals(1, count);
    }

}