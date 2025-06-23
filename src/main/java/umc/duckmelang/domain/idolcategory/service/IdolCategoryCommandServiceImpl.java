package umc.duckmelang.domain.idolcategory.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import umc.duckmelang.domain.idolcategory.domain.IdolCategory;
import umc.duckmelang.domain.idolcategory.dto.IdolCategoryRequestDto;
import umc.duckmelang.domain.idolcategory.repository.IdolCategoryRepository;
import umc.duckmelang.domain.uuid.domain.Uuid;
import umc.duckmelang.domain.uuid.repository.UuidRepository;
import umc.duckmelang.global.aws.AmazonS3Manager;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IdolCategoryCommandServiceImpl implements  IdolCategoryCommandService{
    private final IdolCategoryRepository idolCategoryRepository;
    private final UuidRepository uuidRepository;
    private final AmazonS3Manager s3Manager;

    @Value("${spring.custom.default.profile-image}")
    private String defaultImage;

    @Override
    public void deleteIdolCategory(Long idolId) {
        idolCategoryRepository.removeIdolCategoryById(idolId);
    }

    @Override
    public void createIdolCategory(List<String> names, List<MultipartFile> files) {
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            String uuid = UUID.randomUUID().toString();
            Uuid savedUuid = uuidRepository.save(Uuid.builder()
                    .uuid(uuid)
                    .build());

            String imageUrl;
            if (files.get(i) == null)
                imageUrl = defaultImage;
            else imageUrl = s3Manager.uploadFile(s3Manager.generateIdolCategoryKeyName(savedUuid), files.get(i));

            IdolCategory idolCategory = IdolCategory.builder()
                    .name(name)
                    .profileImage(imageUrl)
                    .build();
            idolCategoryRepository.save(idolCategory);
        }
    }

    @Override
    public void updateIdolCategory(IdolCategoryRequestDto.idolCategoryRequestList request, List<MultipartFile> files) {

    }



}
