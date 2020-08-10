package com.zut.lpf.lidou.config;


import org.springframework.stereotype.Service;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
//@Service
@Retention(RetentionPolicy.RUNTIME)
public @interface LidouService {
    String value() default "";
}
