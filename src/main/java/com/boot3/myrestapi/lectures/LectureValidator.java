package com.boot3.myrestapi.lectures;

import com.boot3.myrestapi.lectures.dto.LectureReqDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

@Component
public class LectureValidator {
    public void validate(LectureReqDto lectureReqDto, Errors errors) {
        //maxPrice 가 값이 있다면 basePrice > maxPrice 오류 -> basePrice가 더 크면 오류
        if(lectureReqDto.getBasePrice() > lectureReqDto.getMaxPrice() &&
                lectureReqDto.getMaxPrice() != 0) {
            //Field Error 발생시킴
            errors.rejectValue("basePrice", "wrongValue", "BasePrice 가 잘못 됐다.");
            errors.rejectValue("maxPrice", "wrongValue", "MaxPrice 가 잘못 됐다.");
            //Global Error
            errors.reject("wrongPrices", "BasePrice가 MaxPrice 보다 더 작은 값이어야 합니다.");
        }

        //강의종료날짜
        LocalDateTime endLectureDateTime = lectureReqDto.getEndLectureDateTime();

        if(endLectureDateTime.isBefore(lectureReqDto.getBeginLectureDateTime()) ||
                endLectureDateTime.isBefore(lectureReqDto.getCloseEnrollmentDateTime()) ||
                endLectureDateTime.isBefore(lectureReqDto.getBeginEnrollmentDateTime()) ) {
            errors.rejectValue("endLectureDateTime", "wrongValue",
                    "endLectureDateTime(강의종료날짜)를 확인하세요!");
        }

        //강의시작날짜
        LocalDateTime beginLectureDateTime = lectureReqDto.getBeginLectureDateTime();

        if(beginLectureDateTime.isBefore(lectureReqDto.getCloseEnrollmentDateTime()) ||
                beginLectureDateTime.isBefore(lectureReqDto.getBeginEnrollmentDateTime()) ||
                beginLectureDateTime.isAfter(lectureReqDto.getEndLectureDateTime())) {
            errors.rejectValue("beginLectureDateTime", "wrongValue",
                    "beginLectureDateTime(강의시작날짜)를 확인하세요!");
        }
    }
}