package com.salesianostriana.dam.trianafy.error.model;

import com.salesianostriana.dam.trianafy.error.model.impl.ApiErrorImpl;
import com.salesianostriana.dam.trianafy.error.model.impl.ApiValidationSubError;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface ApiError {

    HttpStatus getStatus();
    int getStatusCode();
    String getMessage();
    String getPath();
    LocalDateTime getDate();
    List<ApiSubError> getSubErrors();

    static ApiError fromErrorAtributtes(Map<String, Object> defErrorAtr){
        int statusCode = -1;
        HttpStatus status = null;

        if (defErrorAtr.containsKey("status")){
            if (defErrorAtr.get("status") instanceof Integer){
                statusCode = (Integer) defErrorAtr.get("status");
                status = HttpStatus.valueOf(statusCode);
            } else if(defErrorAtr.get("status") instanceof String){
                statusCode = status.value();
                status = HttpStatus.valueOf((String) defErrorAtr.get("status"));
            }
        }

        ApiErrorImpl result =
                ApiErrorImpl.builder()
                        .status(status)
                        .statusCode(statusCode)
                        .message((String) defErrorAtr.getOrDefault("message", "No message avialable"))
                        .path((String) defErrorAtr.getOrDefault("path", "No path available"))
                        .build();

        if (defErrorAtr.containsKey("errors")){
            List<ObjectError> errors = (List<ObjectError>) defErrorAtr.get("errors");

            List<ApiSubError> subErrors = errors.stream()
                    .map(ApiValidationSubError::fromObjectError)
                    .collect(Collectors.toList());

            result.setSubErrors(subErrors);
        }

        return result;
    }

}
