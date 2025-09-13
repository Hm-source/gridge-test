package org.example.gridgestagram.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.example.gridgestagram.repository.log.entity.vo.LogType;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogAction {

    LogType value();

    String targetType() default "";

    String description() default "";

    boolean async() default true;
}
