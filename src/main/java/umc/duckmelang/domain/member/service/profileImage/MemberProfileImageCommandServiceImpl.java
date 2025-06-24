package umc.duckmelang.domain.member.service.profileImage;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.repository.MemberRepository;
import umc.duckmelang.domain.member.converter.MemberProfileImageConverter;
import umc.duckmelang.domain.member.domain.MemberProfileImage;
import umc.duckmelang.domain.member.dto.profileImage.MemberProfileImageRequestDto;
import umc.duckmelang.domain.member.repository.MemberProfileImageRepository;
import umc.duckmelang.domain.uuid.domain.Uuid;
import umc.duckmelang.domain.uuid.repository.UuidRepository;
import umc.duckmelang.domain.uuid.service.UuidService;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.MemberException;
import umc.duckmelang.global.apipayload.exception.MemberProfileImageException;
import umc.duckmelang.global.aws.AmazonS3Manager;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberProfileImageCommandServiceImpl implements MemberProfileImageCommandService {
    private final MemberProfileImageRepository memberProfileImageRepository;
    private final MemberRepository memberRepository;
    private final UuidService uuidService;
    private final AmazonS3Manager s3Manager;

    @Value("${spring.custom.default.profile-image}")
    private String defaultProfileImage;

    @Override
    @Transactional
    public void deleteProfileImage(Long memberId, Long imageId) {
        MemberProfileImage profileImage = memberProfileImageRepository.findById(imageId)
                .orElseThrow(() -> new MemberProfileImageException(ErrorStatus.MEMBER_PROFILE_IMAGE_NOT_FOUND));
        // 삭제하려는 이미지가 사용자의 프로필 이미지인지 확인
        if(!profileImage.getMember().getId().equals(memberId)){
            throw new MemberException(ErrorStatus.UNAUTHORIZED_MEMBER);
        }
        // 기본 프로필 이미지인지 확인하여 삭제 불가 처리
        if(profileImage.getMemberImage().equals(defaultProfileImage)){
            throw new MemberProfileImageException(ErrorStatus.CANNOT_UPDATE_DEFAULT_PROFILE_IMAGE);
        }
        memberProfileImageRepository.delete(profileImage);
    }

    @Override
    @Transactional
    public MemberProfileImage updateProfileImageStatus(Long memberId, Long imageId, MemberProfileImageRequestDto.UpdateProfileImageStatusDto request) {
        MemberProfileImage profileImage = memberProfileImageRepository.findById(imageId)
                .orElseThrow(() -> new MemberProfileImageException(ErrorStatus.MEMBER_PROFILE_IMAGE_NOT_FOUND));
        // 업데이트하려는 이미지가 사용자의 프로필 이미지인지 확인
        if(!profileImage.getMember().getId().equals(memberId)){
            throw new MemberException(ErrorStatus.UNAUTHORIZED_MEMBER);
        }
        // 기본 프로필 이미지인지 확인하여 업데이트 불가 처리
        if(profileImage.getMemberImage().equals(defaultProfileImage)){
            throw new MemberProfileImageException(ErrorStatus.CANNOT_DELETE_DEFAULT_PROFILE_IMAGE);
        }
        MemberProfileImage updatedProfileImage = MemberProfileImageConverter.toMemberProfileImageWithChangedStatus(profileImage, request.isPublicStatus());
        return memberProfileImageRepository.save(updatedProfileImage);
    }

    @Override
    public MemberProfileImage createProfileImage(Long memberId, MultipartFile profileImage) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));
        String uuid = uuidService.generateUniqueUuidString();

        // 프로필 사진을 선택하지 않은 경우, 기본 프로필 사진으로 설정
        String profileImageUrl;
        if (profileImage == null || profileImage.isEmpty())
            profileImageUrl = defaultProfileImage;
        else profileImageUrl = s3Manager.uploadFile(s3Manager.generateMemberProfileImageKeyName(uuid), profileImage);

        return memberProfileImageRepository.save(
                MemberProfileImage.builder()
                        .memberImage(profileImageUrl)
                        .member(member)
                        .isPublic(true)
                        .uuid(uuid)
                        .build()
        );
    }
}
