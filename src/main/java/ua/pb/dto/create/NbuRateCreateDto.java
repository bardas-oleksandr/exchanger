package ua.pb.dto.create;

import lombok.*;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class NbuRateCreateDto {

    private Integer r030;

    @NotEmpty(message = "empty_txt")
    private String txt;

    private Float rate;

    @NotEmpty(message = "empty_cc")
    private String cc;

    @NotEmpty(message = "empty_exchangedate")
    private String exchangedate;

    @AssertTrue(message = "unacceptable_r030")
    public boolean isCorrectR030() {
        return r030 != null && r030 > 0;
    }

    @AssertTrue(message = "unacceptable_rate")
    public boolean isCorrectRate() {
        return rate != null && rate > 0.0;
    }

    public NbuRateCreateDto(Integer r030, @NotEmpty(message = "empty_txt") String txt
            , Float rate, @NotEmpty(message = "empty_cc") String cc
            , @NotEmpty(message = "empty_exchangedate") String exchangedate) {
        this.r030 = r030;
        this.txt = txt;
        this.rate = rate;
        this.cc = cc;
        this.exchangedate = exchangedate;
    }
}
