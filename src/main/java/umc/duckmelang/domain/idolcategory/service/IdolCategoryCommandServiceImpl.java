package umc.duckmelang.domain.idolcategory.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import umc.duckmelang.domain.idolcategory.domain.IdolCategory;
import umc.duckmelang.domain.idolcategory.dto.IdolCategoryRequestDto;
import umc.duckmelang.domain.idolcategory.repository.IdolCategoryRepository;
import umc.duckmelang.domain.uuid.service.UuidService;
import umc.duckmelang.global.aws.AmazonS3Manager;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional
public class IdolCategoryCommandServiceImpl implements  IdolCategoryCommandService{
    private final IdolCategoryRepository idolCategoryRepository;
    private final UuidService uuidService;
    private final AmazonS3Manager s3Manager;

    @Value("${spring.custom.default.profile-image}")
    private String defaultImage;

    @Override
    public void deleteIdolCategory(Long idolId) {
        IdolCategory idolCategory = idolCategoryRepository.findById(idolId).orElseThrow();
        s3Manager.deleteFile(s3Manager.generateIdolCategoryKeyName(idolCategory.getUuid()));
        uuidService.delete(idolCategory.getUuid());
        idolCategoryRepository.removeIdolCategoryById(idolId);
    }

    @Override
    public void createIdolCategory(List<String> names, List<MultipartFile> files) {
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            String uuid = uuidService.generateUniqueUuidString();

            String imageUrl;
            if (files.get(i).isEmpty())
                imageUrl = defaultImage;
            else imageUrl = s3Manager.uploadFile(s3Manager.generateIdolCategoryKeyName(uuid), files.get(i));

            IdolCategory idolCategory = IdolCategory.builder()
                    .name(name)
                    .profileImage(imageUrl)
                    .uuid(uuid)
                    .build();
            idolCategoryRepository.save(idolCategory);
        }
    }

    @Override
    public void updateIdolCategory(IdolCategoryRequestDto.IdolCategoryRequestList request, List<MultipartFile> files) {
        // 검증
        if (files != null && files.size() != request.getDto().size()) {
            throw new IllegalArgumentException("DTO와 파일 개수가 일치하지 않습니다.");
        }

        List<Long> idolIds = request.getDto().stream()
                .map(IdolCategoryRequestDto.IdolCategoryDto::getIdolId)
                .collect(Collectors.toUnmodifiableList());

        Map<Long, IdolCategory> idolCategoryMap = idolCategoryRepository.findAllById(idolIds).stream()
                .collect(Collectors.toMap(IdolCategory::getId, Function.identity()));

        IntStream.range(0, request.getDto().size())
                .forEach(i -> {
                    IdolCategoryRequestDto.IdolCategoryDto dto = request.getDto().get(i);
                    MultipartFile file = files.get(i);
                    IdolCategory category = idolCategoryMap.get(dto.getIdolId());

                    updateCategory(category, dto, file);
                });
    }

    private void updateCategory(IdolCategory category, IdolCategoryRequestDto.IdolCategoryDto dto, MultipartFile file){
        String profileImage = category.getProfileImage();
        if (!file.isEmpty())
            s3Manager.uploadFile(s3Manager.generateIdolCategoryKeyName(category.getUuid()), file);
        else profileImage = defaultImage;

        category.updateForAdmin(dto.getName(), profileImage);
    }

}
