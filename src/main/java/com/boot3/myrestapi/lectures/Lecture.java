package com.boot3.myrestapi.lectures;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of="id")
public class Lecture {
    private String name;
    private String description;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginLectureDateTime;
    private LocalDateTime endLectureDateTime;
    private String location;
    private int basePrice;
    private int maxPrice;
    private boolean offline;
    private boolean free;
    private LectureStatus lectureStatus = LectureStatus.DRAFT;
}
