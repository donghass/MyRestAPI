package com.boot3.myrestapi.common;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import org.springframework.hateoas.EntityModel;
import org.springframework.validation.Errors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Getter
public class ErrorsResource extends EntityModel<Errors> {
    private Errors errors;

    public ErrorsResource(Errors content) {
        this.errors = content;
        // IndexController 의 index() 메서드에 설정된 path(/api)를 링크로 만듬
        add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
    }

}