package com.boot3.myrestapi.lectures.runner;

import com.boot3.myrestapi.lectures.Lecture;
import com.boot3.myrestapi.lectures.LectureRepository;
import com.boot3.myrestapi.lectures.LectureStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

@Component
// application-dev.properties 에서만 작동
@Profile("dev")
public class LectureInsertRunner implements ApplicationRunner {
    @Autowired
    LectureRepository lectureRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //IntStream.forEach(IntConsumer) IntConsumer의 추상메서드 void accept(int value)
        //IntStream.range(0, 15).forEach(value -> generateLecture(value));
        IntStream.rangeClosed(1, 15).forEach(this::generateLecture);
    }

    private Lecture generateLecture(int index) {
        Lecture lecture = buildLecture(index);
        return this.lectureRepository.save(lecture);
    }

    private Lecture buildLecture(int index) {
        return Lecture.builder()
                .name(index + " lecture ")
                .description("test lecture")
                .beginEnrollmentDateTime(LocalDateTime.of(2022, 11, 23, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2022, 11, 24, 14, 21))
                .beginLectureDateTime(LocalDateTime.of(2022, 11, 25, 14, 21))
                .endLectureDateTime(LocalDateTime.of(2022, 11, 26, 14, 21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location(index + " 강의장")
                .free(false)
                .offline(true)
                .lectureStatus(LectureStatus.DRAFT)
                .build();
    }
}
