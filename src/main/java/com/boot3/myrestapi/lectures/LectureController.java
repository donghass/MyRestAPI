package com.boot3.myrestapi.lectures;

import com.boot3.myrestapi.common.ErrorsResource;
import com.boot3.myrestapi.common.exception.BusinessException;
import com.boot3.myrestapi.lectures.dto.LectureReqDto;
import com.boot3.myrestapi.lectures.dto.LectureResDto;
import com.boot3.myrestapi.lectures.dto.LectureResource;
import com.boot3.myrestapi.security.userInfo.CurrentUser;
import com.boot3.myrestapi.security.userInfo.UserInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/api/lectures", produces = MediaTypes.HAL_JSON_VALUE)
// 롬복 생성자 자동 주입 // 생성자를 직접 구현하지 않고 final로 변수를 선언만 한다.
@RequiredArgsConstructor
public class LectureController {

    private final LectureRepository lectureRepository;
    private final ModelMapper modelMapper;
    private final LectureValidator lectureValidator;

    //    // 생성자 주입 @RequiredArgsConstructor 사용하여 생략
    //    public LectureController(LectureRepository lectureRepository) {
    //        this.lectureRepository = lectureRepository;
    //    }
    /*
     @Valid 어노테이션 = Data Binding 하고 Check 역할 / 매핑이 잘 됐는지 검증
     Errors - 입력 항목 에러 정보를 저장하거나 조회해주는 역할
     */
    @PostMapping
    public ResponseEntity<?> createLecture(@RequestBody @Valid LectureReqDto lectureReqDto, Errors errors, @CurrentUser UserInfo currentUser) {
        // 입력항목 검증시 에러 발생 확인 에러 나면 종료   // 입력 항목 값 에러 검증(어노테이션 확인)
        if (errors.hasErrors()) {
            return getErrors(errors);
        }

        //비지니스로직 입력항목 검증 - lectureValidator 호출 / 입력 항목 계산하여 에러 검증
        lectureValidator.validate(lectureReqDto, errors);
        if (errors.hasErrors()) {
            return getErrors(errors);
        }

        // ReqDTO => Entity 매핑
        Lecture lecture = modelMapper.map(lectureReqDto, Lecture.class);

        // free, offline 값 업데이트
        lecture.update();

        // lecture 를 등록한 유저 , Lecture 와 UserInfo 의 관계 저장
        lecture.setUserInfo(currentUser);

        // 테이블에 Insert
        Lecture addedLecture = this.lectureRepository.save(lecture);

        // Entity 를 ResDTO로 변환
        LectureResDto lectureResDto = modelMapper.map(addedLecture, LectureResDto.class);

        // Hateoas Link 생성을 담당하는 객체 http://localhost:8080/api/lectures/10   //getId = 위에 setId해준 10으로 requestMapping 의 /api/lectures/10
        WebMvcLinkBuilder selfLinkBuilder = linkTo(LectureController.class).slash(lectureResDto.getId());

        //생성한 링크를 uri 형식으로 생성해준다
        URI createUri = selfLinkBuilder.toUri();


        LectureResource lectureResource = new LectureResource(lectureResDto);
        lectureResource.add(linkTo(LectureController.class).withRel("query-lectures")); // query-lectures 라는 이름을 갖는 링크 생성
        // self Link 생성
//        lectureResource.add(selfLinkBuilder.withSelfRel());
        lectureResource.add(selfLinkBuilder.withRel("update-lecture"));


        // ResponseEntitiy = body + header + statusCode(ex. 200, 404 등등)
        // created() : statuscode 를 201로 설정하고, 위에서 생성한 링크를 response loacation 해더로 설정한다
        return ResponseEntity.created(createUri).body(lectureResource);
    }

    private static ResponseEntity<ErrorsResource> getErrors(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
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

    /*
        HATEOAS PagedResourcesAssembler 는 (page 는 paging data 를) -> (pagedModel 는 paging data + Link) 로 변환
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')") // ADMIN Role 를 가진 사용자만 접근 권한이 있음
    public ResponseEntity queryLectures(Pageable pageable, PagedResourcesAssembler<LectureResDto> assembler, @CurrentUser UserInfo currentUser) {
        Page<Lecture> lecturePage = this.lectureRepository.findAll(pageable);

        //page<Lecture> -> page<LectureResDto> 로 변환
        Page<LectureResDto> lectureResDtoPage = lecturePage.map(lecture -> modelMapper.map(lecture, LectureResDto.class));

        // page<LectureResDto> -> HATEOAS PagedModel 로 변환
//        PagedModel<EntityModel<LectureResDto>> pagedResources = assembler.toModel(lectureResDtoPage);
        // RepresentationModelAssembler 의 D toModel(T entity)
//      assembler.toModel(lectureResDtoPage,resDto ->new LectureResource(resDto));
        PagedModel<LectureResource> pagedModel = assembler.toModel(lectureResDtoPage, LectureResource::new);

        // 사용자 정보가 null 이 아니면 링크 추가
        if (currentUser != null) {
            pagedModel.add(linkTo(LectureController.class).withRel("create-Lecture"));
        }
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")      // USER Role 를 가진 사용자만 조회 가능
    public ResponseEntity getLecture(@PathVariable Integer id) {
        Lecture lecture = getLectureExistOrElseThrow(id);
//        if(optionalLecture.isEmpty()) {
////            return ResponseEntity.notFound().build();
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("강의가 없습니다.");
//        }
//        Lecture lecture = optionalLecture.get();



        LectureResDto lectureResDto = modelMapper.map(lecture, LectureResDto.class);
        LectureResource lectureResource = new LectureResource(lectureResDto);
        return ResponseEntity.ok(lectureResource);
    }

    private Lecture getLectureExistOrElseThrow(Integer id) {
        Optional<Lecture> optionalLecture = this.lectureRepository.findById(id);    // Optional 은 반환받은 데이터의 null 체크를 수동으로 하지 않아도됨

        String errMsg = String.format("Id = %d 강의가 없습니다.", id);
        // 아래 네줄 생략 - Optional 의 저장된 객체가 null 이면 오류 반환 null 이 아니면 객체 반환
        Lecture lecture = optionalLecture.orElseThrow(() -> new BusinessException(errMsg, HttpStatus.NOT_FOUND));
        return lecture;
    }


    @PutMapping("/{id}")
    public ResponseEntity updateLecture(@PathVariable Integer id, @RequestBody @Valid LectureReqDto lectureReqDto, Errors errors) {
        Lecture existingLecture  = getLectureExistOrElseThrow(id); //아래 세줄 생략
//        Optional<Lecture> optionalLecture = lectureRepository.findById(id);
//        String errMsg = String.format("Id = %d Lecture Not Found", id);
//        optionalLecture.orElseThrow(() -> new BusinessException(errMsg, HttpStatus.NOT_FOUND));
        if (errors.hasErrors()) {
            return getErrors(errors);
        }
        this.lectureValidator.validate(lectureReqDto, errors);
        if (errors.hasErrors()) {
            return getErrors(errors);
        }
//        Lecture existingLecture = optionalLecture.get();
        this.modelMapper.map(lectureReqDto, existingLecture);
        existingLecture.update();
        Lecture savedLecture = this.lectureRepository.save(existingLecture);

        //Entity => ResDto 매핑
        LectureResDto lectureResDto = modelMapper.map(savedLecture, LectureResDto.class);
        return ResponseEntity.ok(new LectureResource(lectureResDto));
    }
}
