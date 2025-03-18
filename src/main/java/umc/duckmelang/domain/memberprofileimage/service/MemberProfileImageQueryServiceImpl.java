package umc.duckmelang.domain.memberprofileimage.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import umc.duckmelang.global.validation.annotation.ExistsMember;
import umc.duckmelang.domain.memberprofileimage.domain.MemberProfileImage;
import umc.duckmelang.domain.memberprofileimage.repository.MemberProfileImageRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberProfileImageQueryServiceImpl implements MemberProfileImageQueryService {
    private final MemberProfileImageRepository memberProfileImageRepository;

    @Value("${spring.custom.default.profile-image}")
    private String defaultProfileImage;
    /*
    대표 프로필 사진 불러오는 서비스(해당하는 사용자의 isPublic 설정이 true인 것들 중 createdAt이 가장 최신인 entity 1개)
    회원의 프로필사진이 노출되는 다양한 API에 사용 가능하도록 별도의 Service로 작성
     */
    @Override
    @Transactional
    public Optional<MemberProfileImage> getLatestPublicMemberProfileImage(@ExistsMember Long memberId) {
        return memberProfileImageRepository.findFirstByMemberIdAndIsPublicTrueOrderByCreatedAtDesc(memberId);
    }

    @Override
    @Transactional
    public Page<MemberProfileImage> getAllMemberProfileImageByMemberId(@ExistsMember Long memberId, Integer page) {
        return memberProfileImageRepository.findAllByMemberIdAndMemberImageNotOrderByCreatedAtDesc(memberId, defaultProfileImage, PageRequest.of(page,10));
    }

    @Override
    public Page<MemberProfileImage> getPublicMemberProfileImageByMemberId(@ExistsMember Long memberId, Integer page) {
        return memberProfileImageRepository.findAllByIsPublicIsTrueAndMemberIdAndMemberImageNotOrderByCreatedAtDesc(memberId, defaultProfileImage, PageRequest.of(page,10));
    }

    @Override
    public Map<Long, String> getFirstProfileImageUrlsForMembers(List<Long> memberIds) {
        if (memberIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // 조회된 이미지들을 Map으로 변환
        Map<Long, String> imageMap = memberProfileImageRepository.findFirstProfileImagesForMembers(memberIds)
                .stream()
                .collect(Collectors.toMap(
                        image -> image.getMember().getId(),
                        MemberProfileImage::getMemberImage,
                        (a,b) -> a
                ));

        // 최종 결과 생성
        return memberIds.stream()
                .distinct()
                .collect(Collectors.toMap(
                        Function.identity(),
                        memberId -> imageMap.getOrDefault(memberId, defaultProfileImage)
                ));
    }

}
