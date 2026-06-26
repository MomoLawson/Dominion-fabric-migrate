package cn.lunadeer.dominion.utils.configuration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Headers {
    String[] value();
}
