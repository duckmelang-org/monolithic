package umc.duckmelang.domain.idolcategory.service;

import org.springframework.web.multipart.MultipartFile;
import umc.duckmelang.domain.idolcategory.dto.IdolCategoryRequestDto;
import java.util.List;

public interface IdolCategoryCommandService {
    void deleteIdolCategory(Long idolId);
    void createIdolCategory(List<String> names, List<MultipartFile> files);
    void updateIdolCategory(IdolCategoryRequestDto.IdolCategoryRequestList request, List<MultipartFile> files);
}
