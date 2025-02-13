package com.boot3.myrestapi.common.serialize;

import java.io.IOException;

import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.Errors;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

@JsonComponent
public class ErrorsSerializer extends JsonSerializer<Errors>{
    @Override
    public void serialize(Errors errors, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartArray(); // 직렬화 시작
        errors.getFieldErrors().forEach(a -> {      // FieldErrors 객체 정보 직렬화
            try {
                gen.writeStartObject();
                gen.writeStringField("field", a.getField());                            //필드명
                gen.writeStringField("objectName", a.getObjectName());                  //필드가 포함된 DTO 객체명
                gen.writeStringField("code", a.getCode());                              //검증규칙(어노테이션)
                gen.writeStringField("defaultMessage", a.getDefaultMessage());          //에러메세지
                Object rejectedValue = a.getRejectedValue();
                if (rejectedValue != null) {
                    gen.writeStringField("rejectedValue", rejectedValue.toString());    //잘못 입력된 값
                }
                gen.writeEndObject();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        errors.getGlobalErrors().forEach(e -> {     // (ObjectError) 두개 이상의 필드에 걸쳐 에러를 확인해야할때 사용
            try {
                gen.writeStartObject();
                gen.writeStringField("objectName", e.getObjectName());
                gen.writeStringField("code", e.getCode());
                gen.writeStringField("defaultMessage", e.getDefaultMessage());
                gen.writeEndObject();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        gen.writeEndArray(); // 직렬화 끝

    }
}