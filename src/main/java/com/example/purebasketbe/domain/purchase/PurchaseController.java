package com.example.purebasketbe.domain.purchase;

import com.example.purebasketbe.domain.member.entity.Member;
import com.example.purebasketbe.domain.purchase.dto.PurchaseHistoryResponseDto;
import com.example.purebasketbe.domain.purchase.dto.PurchaseRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping
    public ResponseEntity<Void> purchaseProducts(@RequestBody PurchaseRequestDto purchaseRequestDto,
                                                  @AuthenticationPrincipal Member member) {
        purchaseService.purchaseProducts(purchaseRequestDto.getPurchaseList(), member);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/histories")
    public ResponseEntity<Page<PurchaseHistoryResponseDto>> getPurchaseHistory(
        @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue="purchasedAt") String sortBy,
        @RequestParam(defaultValue = "desc") String order, @AuthenticationPrincipal Member member) {
        Page<PurchaseHistoryResponseDto> responseBody = purchaseService.getPurchaseHistory(member, page - 1, sortBy, order);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }
}
