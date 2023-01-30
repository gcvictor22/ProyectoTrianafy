package com.salesianostriana.dam.trianafy.exception;

import javax.persistence.EntityNotFoundException;

public class EmptyException extends EntityNotFoundException {

    public EmptyException(){
        super("Nothing were found");
    }

}
