package com.example.purebasketbe.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    EMAIL_DIFFERENT_FORMAT(HttpStatus.BAD_REQUEST.value(), "이메일 형식이 올바르지 않습니다."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "이메일을 찾을 수 없습니다"),
    PASSWORD_DIFFERENT_FORMAT(HttpStatus.BAD_REQUEST.value(), "비밀번호는 8~15자리로, 알파벳 대소문자, 숫자, 특수문자를 포함해야 합니다."),
    PRODUCT_ALREADY_EXISTS(HttpStatus.BAD_REQUEST.value(), "이미 등록된 물건입니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT.value(), "이미 등록된 이메일입니다."),
    INVALID_EMAIL_PASSWORD(HttpStatus.BAD_REQUEST.value(), "이메일 또는 비밀번호가 정확하지 않습니다."),


    MENU_ALREADY_EXIST(HttpStatus.CONFLICT.value(), "이미 존재하는 메뉴입니다."),
    MENU_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "등록되지 않은 메뉴입니다"),
    NEGATIVE_PRICE_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED.value(), "잘못된 가격표기법 입니다."),
    IMAGE_UPLOAD_FAILED(HttpStatus.BAD_REQUEST.value(), "이미지 업로드 오류"),
    INVALID_IMAGE(HttpStatus.BAD_REQUEST.value(), "잘못된 이미지입니다."),

    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 사용자를 찾을 수 없습니다."),
    INVALID_CART_ITEM(HttpStatus.BAD_REQUEST.value(), "장바구니에 없는 상품입니다."),
    PRODUCT_ALREADY_ADDED(HttpStatus.BAD_REQUEST.value(), "이미 장바구니에 등록된 상품입니다."),

    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "존재하지 않는 상품입니다."),
    NOT_ENOUGH_PRODUCT(HttpStatus.NOT_FOUND.value(), "상품 재고가 부족합니다."),
    RECIPE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "존재하지 않는 레시피입니다."),
    RECIPE_ALREADY_EXISTS(HttpStatus.CONFLICT.value(), "이미 존재하는 레시피입니다."),

    PHONENUMBER_ALREADY_EXISTS(HttpStatus.CONTINUE.value(), "이미 등록된 전화번호입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED.value(), "토큰이 만료되었습니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED.value(), "리프레시 토큰이 만료되었습니다."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED.value(), "토큰이 유효하지 않습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED.value(), "리프레시 토큰이 유효하지 않습니다."),
    INVALID_QUANTITY(HttpStatus.BAD_REQUEST.value(), "유효하지 않은 수량입니다."),
    NOT_FOUND_TOKEN(HttpStatus.UNAUTHORIZED.value(), "엑세스 토큰을 찾을 수 없습니다."),
    NOT_FOUND_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED.value(), "로그인 후 이용해 주세요."),
    OUT_OF_RANGE(HttpStatus.BAD_REQUEST.value(), "요청한 페이지 범위가 적절하지 않습니다."),
    TOKEN_NOT_EXIST(HttpStatus.UNAUTHORIZED.value(), "이 기능을 사용하기 위해서는 로그인이 필요합니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED.value(), "만료된 토큰입니다."),
    TOKEN_UNSUPPORTED(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), "지원하지 않는 토큰형식입니다"),
    ELEMENTS_IS_REQUIRED(HttpStatus.BAD_REQUEST.value(), "필수 입력 필드가 누락되었습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN.value(), "접근 권한이 없습니다."),
    INSUFFICIENT_POINT(HttpStatus.BAD_REQUEST.value(), "포인트가 부족합니다."),
    ORDER_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "처리할 주문이 없습니다."),
    UNSUPPORTED_USER_TYPE(HttpStatus.BAD_REQUEST.value(), "잘못된 사용자 유형입니다."),
    UNEXPECTED_ERROR(443, "예상치 못한 오류가 발생했습니다.");

    private final int httpStatus;
    private final String message;
}