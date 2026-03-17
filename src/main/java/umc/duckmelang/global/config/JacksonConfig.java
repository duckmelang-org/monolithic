package umc.duckmelang.global.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import umc.duckmelang.global.common.serializer.LocalDateSerializer;
import umc.duckmelang.global.common.serializer.LocalDateTimeSerializer;
import umc.duckmelang.global.common.serializer.ZonedDateTimeSerializer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary // 이 ObjectMapper를 기본으로 설정(Spring Boot에서는 기본적으로 ObjectMapper를 자동으로 구성하기 때문에, 이 설정이 우선 적용되도록 설정)
    public ObjectMapper objectMapper() {
        SimpleModule module = new SimpleModule();
        // 내가 커스텀한 직렬화기를 추가한다.
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        module.addSerializer(LocalDate.class, new LocalDateSerializer());
        module.addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer());

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // JavaTimeModule 등록
        mapper.registerModule(module);

        return mapper;
    }
}