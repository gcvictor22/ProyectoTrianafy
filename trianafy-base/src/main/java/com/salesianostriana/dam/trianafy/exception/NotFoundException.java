package com.salesianostriana.dam.trianafy.exception;

import javax.persistence.EntityNotFoundException;

public class NotFoundException extends EntityNotFoundException {

    public NotFoundException(Long id){
        super(String.format("Nothing were found with id: %d", id));
    }

}
