package com.vonallin.lib.jumper.exception;

public class OptionsSingletoneTagEmptyException extends RuntimeException {
    public OptionsSingletoneTagEmptyException(){

    }
    public OptionsSingletoneTagEmptyException(String Message){
        super(Message);
    }
}
