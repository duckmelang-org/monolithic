package umc.duckmelang.domain.member.service.profileImage;

import org.springframework.web.multipart.MultipartFile;
import umc.duckmelang.domain.member.domain.MemberProfileImage;
import umc.duckmelang.domain.member.dto.profileImage.MemberProfileImageRequestDto;

public interface MemberProfileImageCommandService {
    void deleteProfileImage(Long memberId, Long imageId);
    MemberProfileImage updateProfileImageStatus(Long memberId, Long imageId, MemberProfileImageRequestDto.UpdateProfileImageStatusDto request);
    MemberProfileImage createProfileImage(Long memberId, MultipartFile profileImage);
    void deleteMember(Long memberId);
}
