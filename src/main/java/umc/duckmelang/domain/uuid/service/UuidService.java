package umc.duckmelang.domain.uuid.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import umc.duckmelang.domain.uuid.domain.Uuid;
import umc.duckmelang.domain.uuid.repository.UuidRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UuidService {

    private final UuidRepository uuidRepository;

    /**
     * 중복되지 않는 고유한 UUID를 생성하고 저장합니다.
     */
    public Uuid generateUniqueUuid() {
        String uuid;
        do {
            uuid = UUID.randomUUID().toString();
        } while (uuidRepository.existsByUuid(uuid));

        return uuidRepository.save(Uuid.builder()
                .uuid(uuid)
                .build());
    }

    /**
     * UUID 문자열만 필요한 경우
     */
    public String generateUniqueUuidString() {
        return generateUniqueUuid().getUuid();
    }

    public void delete(String uuid) {
        uuidRepository.deleteByUuid(uuid);

        // todo : deleteByUuid가 db에 반영되게 할 다른 방법 추후 찾기
        uuidRepository.flush();
    }
}