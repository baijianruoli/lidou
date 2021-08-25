package io.github.baijianruoli.lidou.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.FIELD})
public @interface Reference {
    String loadBalance() default "random";
    int rateLimit() default 1000;
}