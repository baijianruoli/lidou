package io.github.baijianruoli.lidou.config;


import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
//@Service
@Retention(RetentionPolicy.RUNTIME)
public @interface LidouService {
    String value() default "";
}
