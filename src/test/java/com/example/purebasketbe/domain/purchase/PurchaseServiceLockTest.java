package com.example.purebasketbe.domain.purchase;

import com.example.purebasketbe.domain.member.MemberRepository;
import com.example.purebasketbe.domain.member.entity.Member;
import com.example.purebasketbe.domain.product.ProductRepository;
import com.example.purebasketbe.domain.product.entity.Product;
import com.example.purebasketbe.domain.purchase.dto.PurchaseRequestDto.PurchaseDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PurchaseServiceLockTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PurchaseService purchaseService;


    @BeforeEach
    void setUp() {
        productRepository.save(createProduct(1L));
        productRepository.save(createProduct(2L));
    }

    @Test
    void 주문() {
        Member member = memberRepository.findById(1L).orElseThrow();
        List<PurchaseDetail> purchaseRequestDto = createPurchaseRequestDto();

        purchaseService.purchaseProducts(purchaseRequestDto, member);

        Product product1 = productRepository.findById(1L).orElseThrow();
        Product product2 = productRepository.findById(2L).orElseThrow();
        assertThat(product1.getStock()).isEqualTo(79);
        assertThat(product2.getStock()).isEqualTo(79);
    }

    @Test
    void 동시에_100개요청() throws InterruptedException {
        List<PurchaseDetail> purchaseRequestDto = createPurchaseRequestDto();

        final int threadCount = 100;
        final ExecutorService executorService = Executors.newFixedThreadPool(32);
        final CountDownLatch latch = new CountDownLatch(threadCount);

        final AtomicInteger successCount = new AtomicInteger();
        final AtomicInteger failCount = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            Member member = memberRepository.findById((long) (i + 1)).orElseThrow();
            executorService.submit(() -> {
                try {
                    purchaseService.purchaseProducts(purchaseRequestDto, member);
                    successCount.getAndIncrement();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    failCount.getAndIncrement();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        System.out.println("구매 성공 횟수: " + successCount.get());
        System.out.println("구매 실패 횟수: " + failCount.get());

        Product product1 = productRepository.findById(1L).orElseThrow();
        Product product2 = productRepository.findById(2L).orElseThrow();

        assertThat(successCount.get() + failCount.get()).isEqualTo(threadCount);
        assertThat(product1.getStock()).isEqualTo(0);
        assertThat(product2.getStock()).isEqualTo(0);
    }

    private Product createProduct(Long id) {
        Product product = Product.builder().price(1000).stock(80).discountRate(0).build();

        ReflectionTestUtils.setField(product, "id", id);
        ReflectionTestUtils.setField(product, "name", "test name" + id);
        ReflectionTestUtils.setField(product, "deleted", false);

        return product;
    }

    private List<PurchaseDetail> createPurchaseRequestDto() {
        PurchaseDetail purchaseDetail1 = PurchaseDetail.builder().productId(1L).amount(1).build();
        PurchaseDetail purchaseDetail2 = PurchaseDetail.builder().productId(2L).amount(1).build();
        return List.of(purchaseDetail1, purchaseDetail2);
    }

}