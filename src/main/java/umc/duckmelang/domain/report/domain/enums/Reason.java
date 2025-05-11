package umc.duckmelang.domain.report.domain.enums;

public enum Reason {
    INAPPR("INAPPROPRIATE"),
    INSULT("INSULT"),
    SEXUAL("SEXUAL HARRASMENT"),
    ADVERT("ADVERTISEMENT"),
    FRAUD("FRAUD"),
    ETC("ETC");

    private String mean;

    Reason(String mean) {
        this.mean = mean;
    }
}
