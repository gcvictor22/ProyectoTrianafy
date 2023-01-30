package com.salesianostriana.dam.trianafy.error.model.erroratrib;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesianostriana.dam.trianafy.error.model.ApiError;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@Order(value = Ordered.HIGHEST_PRECEDENCE)
@Component
@RequiredArgsConstructor
public class ApiCustomAtributtes extends DefaultErrorAttributes {

    private final ObjectMapper mapper;

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {

        Map<String, Object> defErrorAtributtes = super.getErrorAttributes(webRequest, options);
        ApiError apiError = ApiError.fromErrorAtributtes(defErrorAtributtes);

        return mapper.convertValue(apiError, Map.class);
    }
}
