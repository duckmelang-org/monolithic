package umc.duckmelang.domain.report.domain.enums;

public enum ReportType {
    TOTAL(Values.TOTAL),
    CHAT(Values.CHAT),
    PROFILE(Values.PROFILE),
    POST(Values.POST),
    REVIEW(Values.REVIEW);

    private String value;

    ReportType(String value) {
        if(!this.name().equals(value))
            throw new IllegalArgumentException("Incorrect use");
    }

    public static class Values {
        public static final String TOTAL = "TOTAL";
        public static final String CHAT = "CHAT";
        public static final String PROFILE = "PROFILE";
        public static final String POST = "POST";
        public static final String REVIEW = "REVIEW";
    }
}
