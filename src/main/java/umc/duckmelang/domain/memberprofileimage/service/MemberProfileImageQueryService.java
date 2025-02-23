package umc.duckmelang.domain.memberprofileimage.service;

import org.springframework.data.domain.Page;
import umc.duckmelang.domain.memberprofileimage.domain.MemberProfileImage;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MemberProfileImageQueryService {
    Optional<MemberProfileImage> getLatestPublicMemberProfileImage(Long memberId);
    Page<MemberProfileImage> getAllMemberProfileImageByMemberId(Long memberId, Integer page);
    Page<MemberProfileImage> getPublicMemberProfileImageByMemberId(Long memberId, Integer page);
    Map<Long, String> getFirstProfileImageUrlsForMembers(List<Long> memberIds);
}
