package com.logni.account.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

@Slf4j
public class AccountUtil {
    private AccountUtil(){}
    public static final ObjectMapper OBJECT_MAPPER;
    public static final ModelMapper MODEL_MAPPER;

    static  {
        OBJECT_MAPPER = new ObjectMapper();
        MODEL_MAPPER = new ModelMapper();
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MODEL_MAPPER.getConfiguration().setAmbiguityIgnored(true);
    }

    public static final <T> Optional<String> toJson(T req) {
        String response = null;
        try {
            response = OBJECT_MAPPER.writeValueAsString(req);
        } catch (Exception ex) {
        }
        return Optional.ofNullable(response);
    }

    public static final <T> Optional<String> toJson(T... req) {
        String response = null;
        try {
            response = OBJECT_MAPPER.writeValueAsString(req);
        } catch (Exception ex) {
        }
        return Optional.ofNullable(response);
    }

    public static final <T> Optional<T> fromJson(String json, Class<T> tClass) {
        T response = null;
        try {
            response = OBJECT_MAPPER.readValue(json, tClass);
        } catch (Exception ex) {
        }
        return Optional.ofNullable(response);
    }

    public static <T> Optional<List<T>> listFromJson(String json, Class<T> tClass){
        List<T> response = null;
        try{
            response =  OBJECT_MAPPER.readValue(
                    json,
                    OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, tClass)
            );
        }catch (Exception ex){
        }
        return Optional.ofNullable(response);
    }

    public static <T, E> Optional<E> map(T obj, Class<E> eClass) {
        E e = null;
        try {
            e = MODEL_MAPPER.map(obj, eClass);
        } catch (Exception ex) {
            log.error("Exception:",ex);
        }
        return Optional.ofNullable(e);
    }

    public static Optional<JsonNode> tree(String json) {
        JsonNode resp = null;
        try {
            resp = OBJECT_MAPPER.readTree(json);
        } catch (Exception ex) {
        }
        return Optional.ofNullable(resp);
    }

    public static <T> void writeValue(PrintWriter writer, T obj) throws IOException {
        OBJECT_MAPPER.writeValue(writer, obj);
    }

    /*
    public static String encrypt(String salt, String value) {
        AES256TextEncryptor aesEncryptor = new AES256TextEncryptor();
        aesEncryptor.setPassword(salt);
        return aesEncryptor.encrypt(value);
    }

    public static String decrypt(String salt, String value) {
        AES256TextEncryptor aesEncryptor = new AES256TextEncryptor();
        aesEncryptor.setPassword(salt);
        return aesEncryptor.decrypt(value);
    }


    public static String encrypt(String value) {
        return encrypt(SALT, value);
    }

    public static String decrypt(String value) {
        return decrypt(SALT, value);
    }

    public static String uniqueKey() {
        return Generators.randomBasedGenerator().generate().toString().replaceAll("-", "");
    }

    public static String merge(String... value) {
        return Arrays.stream(value).filter(StringUtils::isNotBlank).filter(str->!"null".equalsIgnoreCase(str)).collect(Collectors.joining(","));
    }
    */
}
