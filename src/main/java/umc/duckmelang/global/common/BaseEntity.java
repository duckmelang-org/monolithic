package umc.duckmelang.global.common;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import umc.duckmelang.global.common.serializer.LocalDateTimeSerializer;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@SuperBuilder
@NoArgsConstructor
public abstract class BaseEntity {

    @CreatedDate
    @JsonSerialize(using = LocalDateTimeSerializer.class)  // 데이터베이스에는 소수점 아래 초 단위까지 다 들어가고, JSON 직렬화할 때에만 소수점 아래를 버린다.
    private LocalDateTime createdAt;

    @LastModifiedDate
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updatedAt;
}
