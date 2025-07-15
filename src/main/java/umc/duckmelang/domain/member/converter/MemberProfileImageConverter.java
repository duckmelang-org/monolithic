package umc.duckmelang.domain.member.converter;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import umc.duckmelang.domain.member.domain.MemberProfileImage;
import umc.duckmelang.domain.member.dto.profileImage.MemberProfileImageResponseDto;

import java.util.stream.Collectors;
import java.util.List;

@Component
public class MemberProfileImageConverter {

    public static MemberProfileImageResponseDto.MemberProfileImageListDto toMemberProfileImageListDto(Page<MemberProfileImage> page) {

        List<MemberProfileImageResponseDto.MemberProfileImageDto> list = page.stream()
                .map(MemberProfileImageConverter::toMemberProfileImageDto)
                .collect(Collectors.toList());

        return MemberProfileImageResponseDto.MemberProfileImageListDto.builder()
                .profileImageList(list)
                .totalPage(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .listSize(list.size())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .currentPage(page.getNumber())
                .build();
    }

    public static MemberProfileImageResponseDto.MemberProfileImageDto toMemberProfileImageDto(MemberProfileImage memberProfileImage){
        return MemberProfileImageResponseDto.MemberProfileImageDto.builder()
                .memberProfileImageId(memberProfileImage.getId())
                .memberProfileImageUrl(memberProfileImage.getMemberImage())
                .isPublic(memberProfileImage.isPublic())
                .createdAt(memberProfileImage.getCreatedAt())
                .build();
    }

    public static MemberProfileImageResponseDto.UpdateProfileImageStatusResultDto toUpdateProfileImageStatusResultDto(MemberProfileImage updatedMemberProfileImage) {
        String changedStatus;

        if (updatedMemberProfileImage.isPublic()) {
            changedStatus = "PUBLIC";
        } else {
            changedStatus ="PRIVATE";
        }
        return MemberProfileImageResponseDto.UpdateProfileImageStatusResultDto.builder()
                .memberProfileImageId(updatedMemberProfileImage.getId())
                .changedStatus(changedStatus)
                .build();
    }
}
