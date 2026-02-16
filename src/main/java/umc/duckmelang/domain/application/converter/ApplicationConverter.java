package umc.duckmelang.domain.application.converter;

import umc.duckmelang.domain.application.domain.Application;
import umc.duckmelang.domain.application.domain.type.Status;
import umc.duckmelang.domain.application.dto.response.ApplicationResponseDto;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.post.domain.Post;

public class ApplicationConverter {

    public static Application toApplication(Post post, Member member){
        return Application.builder()
                .status(Status.PENDING)
                .post(post)
                .member(member)
                .build();
    }

    public static ApplicationResponseDto.CreateResultDto toApplicationResponseDto(Application application){
        return ApplicationResponseDto.CreateResultDto.builder()
                .applicationId(application.getId())
                .build();
    }

    public static ApplicationResponseDto.UpdateResultDto toUpdateResultDto(Application application){
        return ApplicationResponseDto.UpdateResultDto.builder()
                .applicationId(application.getId())
                .status(application.getStatus().getLabel())
                .build();
    }
}
