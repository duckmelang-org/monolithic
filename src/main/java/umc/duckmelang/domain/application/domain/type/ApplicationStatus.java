package umc.duckmelang.domain.application.domain.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ApplicationStatus {
    PENDING("대기"),
    ACCEPTED("수락"),
    REJECTED("거절");

    private final String label;

    public String getLabel() {
        return label;
    }
}
