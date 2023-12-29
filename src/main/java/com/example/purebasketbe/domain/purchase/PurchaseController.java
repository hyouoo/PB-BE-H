package com.example.purebasketbe.domain.purchase;

import com.example.purebasketbe.domain.member.entity.Member;
import com.example.purebasketbe.domain.product.PurchaseFacade;
import com.example.purebasketbe.domain.purchase.dto.PurchaseRequestDto;
import com.example.purebasketbe.domain.purchase.dto.PurchaseResponseDto;
import com.example.purebasketbe.global.tool.LoginAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final PurchaseFacade purchaseFacade;

    @PostMapping
    public ResponseEntity<Void> purchaseProducts(@RequestBody @Validated PurchaseRequestDto requestDto,
                                                 @LoginAccount Member member) {
        purchaseFacade.purchaseProducts(requestDto.purchaseList(), member);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @GetMapping
    public ResponseEntity<Page<PurchaseResponseDto>> getPurchases(
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "purchasedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String order, @LoginAccount Member member) {
        Page<PurchaseResponseDto> responseBody = purchaseService.getPurchases(member, page - 1, sortBy, order);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }
}