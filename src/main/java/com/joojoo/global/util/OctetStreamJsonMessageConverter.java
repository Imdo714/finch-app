//package com.joojoo.global.util;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
//import org.springframework.stereotype.Component;
//
//import java.lang.reflect.Type;
//
///**
// * application/octet-stream Content-Type으로 전송된 JSON 데이터를
// * 역직렬화하기 위한 커스텀 HTTP 메시지 컨버터
// */
//@Component
//public class OctetStreamJsonMessageConverter extends AbstractJackson2HttpMessageConverter {
//    @Autowired
//    public OctetStreamJsonMessageConverter(ObjectMapper objectMapper) {
//        super(objectMapper, MediaType.APPLICATION_OCTET_STREAM);
//    }
//
//    @Override
//    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
//        return false;
//    }
//
//    @Override
//    public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {
//        return false;
//    }
//
//    @Override
//    protected boolean canWrite(MediaType mediaType) {
//        return false;
//    }
//}