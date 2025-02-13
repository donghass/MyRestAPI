package com.boot3.myrestapi.lectures;

import com.boot3.myrestapi.lectures.dto.LectureReqDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

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
    /*
     @Valid 어노테이션 = Data Binding 하고 Check 역할 / 매핑이 잘 됐는지 검증
     Errors - 입력 항목 에러 정보를 저장하거나 조회해주는 역할
     */
    @PostMapping
    public ResponseEntity<?> createLecture(@RequestBody @Valid LectureReqDto lectureReqDto, Errors errors) {
        // 에러 발생 확인 에러 나면 종료
        if(errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLecture(@PathVariable int id) {
        // 해당 데이터가 존재하는지 확인
        Optional<Lecture> lectureOptional = this.lectureRepository.findById(id);

        // 없으면 오류
        if (lectureOptional.isEmpty()) {
            return ResponseEntity.notFound().build();  // 404 반환
        }

        // 존재하면 삭제
        lectureRepository.deleteById(id);

        // 응답: 삭제 성공
        return ResponseEntity.ok("삭제를 성공했습니다.");
    }

}
