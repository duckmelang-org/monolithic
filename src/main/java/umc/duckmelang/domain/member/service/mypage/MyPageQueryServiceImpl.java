package umc.duckmelang.domain.member.service.mypage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.duckmelang.domain.member.converter.MemberFilterConverter;
import umc.duckmelang.domain.member.converter.MemberProfileConverter;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.dto.member.MemberFilterDto;
import umc.duckmelang.domain.member.dto.mypage.MyPageResponseDto;
import umc.duckmelang.domain.member.repository.MemberRepository;
import umc.duckmelang.domain.member.domain.MemberProfileImage;
import umc.duckmelang.domain.member.service.profileImage.MemberProfileImageQueryService;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.MemberException;
import umc.duckmelang.global.apipayload.exception.MemberProfileImageException;

@Service
@RequiredArgsConstructor
public class MyPageQueryServiceImpl implements MyPageQueryService{
    private final MemberRepository memberRepository;
    private final MemberProfileImageQueryService memberProfileImageQueryService;

    public MyPageResponseDto.MyPageProfileEditBeforeDto getMemberProfileBeforeEdit(Long memberId){
        Member member = findMemberOrThrow(memberId);
        MemberProfileImage profileImage = getLatestProfileImageOrThrow(memberId);
        return MemberProfileConverter.toMemberProfileEditBeforeDto(member, profileImage);
    }

    public MemberFilterDto.FilterResponseDto getMemberFilter(Long memberId){
        Member member = findMemberOrThrow(memberId);
        return MemberFilterConverter.toFilterResponseDto(member);
    }

    private Member findMemberOrThrow(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));
    }

    private MemberProfileImage getLatestProfileImageOrThrow(Long memberId) {
        return memberProfileImageQueryService.getLatestPublicMemberProfileImage(memberId)
                .orElseThrow(() -> new MemberProfileImageException(ErrorStatus.MEMBER_PROFILE_IMAGE_NOT_FOUND));
    }
}
