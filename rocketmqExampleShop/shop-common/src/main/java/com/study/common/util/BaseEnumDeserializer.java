package com.study.common.util;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.study.common.constant.ShopCode;
import org.springframework.beans.BeanUtils;

import java.io.IOException;

//public class BaseEnumDeserializer extends JsonDeserializer<ShopCode> {
//    @Override
//    @SuppressWarnings("unchecked")
//    public ShopCode deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
//        JsonNode node = jp.getCodec().readTree(jp);
//        String currentName = jp.currentName();
//        Object currentValue = jp.getCurrentValue();
//        @SuppressWarnings("rawtypes")
//        Class findPropertyType = BeanUtils.findPropertyType(currentName, currentValue.getClass());
//        JsonFormat annotation = (JsonFormat) findPropertyType.getAnnotation(JsonFormat.class);
//        ShopCode valueOf;
//        if(annotation == null || annotation.shape() != JsonFormat.Shape.OBJECT) {
//            valueOf = ShopCode.valueOf(node.asText(), findPropertyType);
//        }else {
//            valueOf =ShopCode.valueOf(node.get("code").asText(),findPropertyType);
//        }
//        return valueOf;
//    }
//}
