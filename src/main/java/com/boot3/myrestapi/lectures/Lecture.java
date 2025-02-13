package com.boot3.myrestapi.lectures;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of="id")
@Entity
@Table(name="lectures")
public class Lecture {
    // pk / 값 자동 상승(mysql 계열 autoincrement) 1씩 자동 상승 // 오라클은 sequence
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    // not null
    @Column(nullable = false)
    private String name;
    private String description;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    private LocalDateTime beginEnrollmentDateTime;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    private LocalDateTime closeEnrollmentDateTime;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    private LocalDateTime beginLectureDateTime;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
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
}
