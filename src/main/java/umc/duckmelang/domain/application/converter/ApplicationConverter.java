package umc.duckmelang.domain.application.converter;

import org.springframework.data.domain.Page;
import umc.duckmelang.domain.application.domain.Application;
import umc.duckmelang.domain.application.dto.ApplicationResponseDto;
import umc.duckmelang.domain.application.dto.ShowApplicationDto;
import umc.duckmelang.domain.application.domain.MateRelationship;
import java.util.*;
import java.util.stream.Collectors;

import static umc.duckmelang.domain.member.util.NicknameUtil.getDisplayName;

public class ApplicationConverter {
    public static ApplicationResponseDto.CommonApplicationResponseDto toApplicationStatusChangeResponseDto(Application application) {
        return ApplicationResponseDto.CommonApplicationResponseDto.builder()
                .newStatus(application.getStatus())
                .modifiedAt(application.getUpdatedAt())
                .build();
    }

    public static ApplicationResponseDto.MateRelationshipCreateResponseDto toMateRelationshipCreateResponseDto(MateRelationship mateRelationship) {
        return umc.duckmelang.domain.application.dto.ApplicationResponseDto.MateRelationshipCreateResponseDto.builder()
                .mateRelationshipId(mateRelationship.getId())
                .createdAt(mateRelationship.getCreatedAt())
                .build();
    }

    public static ApplicationResponseDto.ShowApplicationListDto toShowApplicationListDto(Page<ShowApplicationDto> applications) {
        List<ShowApplicationDto> receivedApplicationList = applications.stream().collect(Collectors.toList());
        return umc.duckmelang.domain.application.dto.ApplicationResponseDto.ShowApplicationListDto.builder()
                .applicationList(receivedApplicationList)
                .listSize(receivedApplicationList.size())
                .totalPage(applications.getTotalPages())
                .totalElements(applications.getTotalElements())
                .isFirst(applications.isFirst())
                .isLast(applications.isLast())
                .currentPage(applications.getNumber())
                .build();
    }

    public static ShowApplicationDto toShowApplicationDto(Application application) {
        return ShowApplicationDto.builder()
                .postId(application.getPost().getId())
                .postTitle(application.getPost().getTitle())
                .oppositeNickname(getDisplayName(application.getMember()))
                .applicationCreatedAt(application.getCreatedAt())
                .applicationStatus(application.getStatus())
                .build();
    }
}
