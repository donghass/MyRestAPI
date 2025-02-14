package com.boot3.myrestapi.lectures.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LectureReqDto {
    //공백 허용하지않음
    @NotBlank(message = "Name은 필수 입력항목입니다.")
    private String name;
    //공백 허용
    @NotEmpty
    private String description;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime beginEnrollmentDateTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @NotNull
    private LocalDateTime closeEnrollmentDateTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @NotNull
    private LocalDateTime beginLectureDateTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @NotNull
    private LocalDateTime endLectureDateTime;
    private String location;
    @Min(0)
    private int basePrice;
    @Min(0)
    private int maxPrice;
    @Min(5) @Max(25)
    private int limitOfEnrollment;  //수강 인원수
}
