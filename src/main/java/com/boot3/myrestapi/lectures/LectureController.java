package com.boot3.myrestapi.lectures;

import com.boot3.myrestapi.lectures.dto.LectureReqDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
@RestController
@RequestMapping(value="/api/lectures", produces = MediaTypes.HAL_JSON_VALUE)
// 롬복 생성자 자동 주입 // 생성자를 직접 구현하지 않고 final로 변수를 선언만 한다.
@RequiredArgsConstructor
public class LectureController {

        private final LectureRepository lectureRepository;
        private final ModelMapper modelMapper;

    //    // 생성자 주입 @RequiredArgsConstructor 사용하여 생략
    //    public LectureController(LectureRepository lectureRepository) {
    //        this.lectureRepository = lectureRepository;
    //    }
    @PostMapping
    public ResponseEntity<?> createLecture(@RequestBody LectureReqDto lectureReqDto) {

        // ReqDTO => Entity 매핑
        Lecture lecture = modelMapper.map(lectureReqDto, Lecture.class);
        // 테이블에 Insert
        Lecture addedLecture = this.lectureRepository.save(lecture);
        // Hateoas Link 생성을 담당하는 객체 http://localhost:8080/api/lectures/10   //getId = 위에 setId해준 10으로 requestMapping 의 /api/lectures/10
        WebMvcLinkBuilder selfLinkBuilder = WebMvcLinkBuilder.linkTo(LectureController.class).slash(addedLecture.getId());
        //생성한 링크를 uri 형식으로 생성해준다
        URI createUri = selfLinkBuilder.toUri();
        // ResponseEntitiy = body + header + statusCode(ex. 200, 404 등등)
        // created() : statuscode 를 201로 설정하고, 위에서 생성한 링크를 response loacation 해더로 설정한다
        return ResponseEntity.created(createUri).body(lecture);
    }

}
