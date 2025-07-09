package umc.duckmelang.domain.admin.converter;

import org.springframework.stereotype.Component;
import umc.duckmelang.domain.admin.dto.AdminResponseDto;
import umc.duckmelang.domain.bookmark.domain.Bookmark;
import umc.duckmelang.domain.bookmark.dto.BookmarkResponseDto;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.post.converter.PostConverter;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AdminConverter {
    public static AdminResponseDto.AdminManagerDto adminManagerDto(Member member) {
        return AdminResponseDto.AdminManagerDto.builder()
                .memberId(member.getId())
                .loginId(member.getLoginId())
                .role(member.getRole())
                .build();
    }

    public static List<AdminResponseDto.AdminManagerDto> toAdminManagerList(List<Member> adminList) {
        return adminList.stream()
                .map(AdminConverter::adminManagerDto)
                .collect(Collectors.toList());
    }

}
