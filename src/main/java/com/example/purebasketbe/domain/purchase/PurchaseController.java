package com.example.purebasketbe.domain.purchase;

import com.example.purebasketbe.domain.purchase.dto.PurchaseHistoryDto;
import com.example.purebasketbe.domain.purchase.dto.PurchaseRequestDto;
import com.example.purebasketbe.domain.user.entity.User;
import com.example.purebasketbe.global.tool.LoginAccount;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping
    public ResponseEntity<Void> purchaseProducts(@RequestBody PurchaseRequestDto purchaseRequestDto,
                                                  @LoginAccount User user) {
        purchaseService.purchaseProducts(purchaseRequestDto.getPurchaseList(), user);
        return ResponseEntity.status(HttpStatus.CREATED).location(URI.create("")).build();
    }

    @GetMapping("/histories")
    public ResponseEntity<List<PurchaseHistoryDto>> getPurchaseHistory(@LoginAccount User user) {

        List<PurchaseHistoryDto> responseBody = purchaseService.getPurchaseHistory(user);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }
}
