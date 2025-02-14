package com.boot3.myrestapi.lectures;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// 테이블 , pk데이터타입
public interface LectureRepository extends JpaRepository<Lecture, Integer> {
// find, save 등의 메서드는 상속받는 인터페이스에 따라 용도가 다르다  (ListCrudRepository, ListPagingAndSortingRepository)
    // select * from LECTURE where name like '%name%'; Containing %로 감싸기
    List<Lecture> findByNameContaining(String name);
    // select * from LECTURE where basePrice >= ?basePrice; GreaterThanEqual 크거나 같은것 찾기
    List<Lecture> findByBasePriceGreaterThanEqual(int basePrice);
}
