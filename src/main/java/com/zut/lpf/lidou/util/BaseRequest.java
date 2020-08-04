package com.zut.lpf.lidou.util;

import lombok.Data;

@Data
public class BaseRequest {
    private String className;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] parameTypes;
}
