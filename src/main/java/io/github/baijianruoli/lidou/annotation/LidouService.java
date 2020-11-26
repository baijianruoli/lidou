package io.github.baijianruoli.lidou.annotation;


import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
//@Service
@Retention(RetentionPolicy.RUNTIME)
public @interface LidouService {
    String value() default "";
}
