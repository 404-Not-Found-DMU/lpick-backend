package com.notfound.lpickbackend.AUTO_ENTITIES.TOOL;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class IdPrefixUtil {
    private static final Map<String,String> PREFIX_MAP = new HashMap<>();

    // static을 사용했으니, 매번 동작하는 코드 블록이 아님.
    // 즉, id 기입시마다 disk io가 발생하는 불상사는 생기지 않음.
    static {
        YamlPropertiesFactoryBean yamlFactory = new YamlPropertiesFactoryBean();
        yamlFactory.setResources(new ClassPathResource("ID-PREFIX.yml"));
        Properties props = yamlFactory.getObject();
        for (String key : props.stringPropertyNames()) {
            PREFIX_MAP.put(key, props.getProperty(key));
        }
    }

    public static String get(String entityName) {
        return PREFIX_MAP.getOrDefault(entityName, entityName);
    }
}
