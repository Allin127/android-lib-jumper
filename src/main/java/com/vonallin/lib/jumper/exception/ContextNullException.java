package com.vonallin.lib.jumper.exception;

public class ContextNullException extends RuntimeException {
    public ContextNullException(){

    }
    public ContextNullException(String Message){
        super(Message);
    }
}
