package io.github.baijianruoli.lidou.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.FIELD})
public @interface Reference {
    String loadBalance() default "random";

    String fallback() default "";

    int tokenLimit() default 1000;

}