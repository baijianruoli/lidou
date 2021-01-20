package io.github.baijianruoli.lidou.annotation;


import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LidouService {
    int weight() default 1;
}
