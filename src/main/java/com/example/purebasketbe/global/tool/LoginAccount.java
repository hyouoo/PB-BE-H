package com.example.purebasketbe.global.tool;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

/**
 * Custom Annotation that is used to resolve {@link com.example.purebasketbe.global.security.impl.UserDetailsImpl#getUser()} to a method
 * argument by implementing {@link AuthenticationPrincipal#expression()}
 *
 * @author Sanghyu Lee
 *
 * See: <a href=
 * "{@docRoot}/org/springframework/security/core/annotation/AuthenticationPrincipal.html" >
 * "@AuthenticationPrincipal" </a>
 */

@SuppressWarnings("ALL")
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@AuthenticationPrincipal(expression = "user")
public @interface LoginAccount {
}
