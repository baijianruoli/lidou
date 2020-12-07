package io.github.baijianruoli.lidou.util;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BaseRequest {
    private String className;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] parameTypes;

    public BaseRequest(String className) {
        this.className = className;
    }

    public BaseRequest(String className, String methodName, Object[] parameters, Class<?>[] parameTypes) {
        this.className = className;
        this.methodName = methodName;
        this.parameters = parameters;
        this.parameTypes = parameTypes;
    }
}
