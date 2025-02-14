package com.boot3.myrestapi.lectures;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "lectures")
public class Lecture {
    // pk / 값 자동 상승(mysql 계열 autoincrement) 1씩 자동 상승 // 오라클은 sequence
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    // not null
    @Column(nullable = false)
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginLectureDateTime;
    private LocalDateTime endLectureDateTime;
    private String location;
    private int basePrice;
    private int maxPrice;
    private int limitOfEnrollment;
    private boolean offline;
    private boolean free;
    // 숫자or문자열 설정 // string는 문자열 설정
    @Enumerated(EnumType.STRING)
    private LectureStatus lectureStatus = LectureStatus.DRAFT;

    public void update() {
// Update free test
//        if (this.basePrice == 0 && this.maxPrice == 0) {
//            this.free = true;
//        } else {
//            this.free = this.basePrice == 0 && this.maxPrice == 0 ;
//        }
        this.free = this.basePrice == 0 && this.maxPrice == 0 ;
// Update offline
//        if (this.location == null || this.location.isBlank()) {
//            this.offline = false;
//        } else {
//            this.offline = true;
//        }
        this.offline = this.location != null && !this.location.isBlank();
    }
}
