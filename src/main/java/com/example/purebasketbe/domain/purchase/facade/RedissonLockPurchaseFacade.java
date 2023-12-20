package com.example.purebasketbe.domain.purchase.facade;

import com.example.purebasketbe.domain.member.entity.Member;
import com.example.purebasketbe.domain.purchase.PurchaseService;
import com.example.purebasketbe.domain.purchase.dto.PurchaseRequestDto.PurchaseDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedissonLockPurchaseFacade {

    private final RedissonClient redissonClient;
    private final PurchaseService purchaseService;

    public void purchaseProducts(List<PurchaseDetail> purchaseRequestDto, Member member) {
        List<RLock> lockList = new ArrayList<>();

        for (PurchaseDetail purchaseDetail : purchaseRequestDto) {
            RLock lock = redissonClient.getLock("product_lock:"+purchaseDetail.productId());
            lockList.add(lock);
        }

        try {
            boolean isLocked;
            for (RLock lock : lockList) {
                isLocked = lock.tryLock(15, 3, TimeUnit.SECONDS);
                if(!isLocked) {
                    log.info("락 획득 실패");
                    return;
                }
            }

            purchaseService.purchaseProducts(purchaseRequestDto, member);

        } catch (InterruptedException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        } finally {
            lockList.forEach(Lock::unlock);
        }
    }
}
