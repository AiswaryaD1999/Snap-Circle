package com.example.snapcircle.exception;

public class PhotoUploadException extends Exception{

    public PhotoUploadException(String message){
        super(message);
    }

    public PhotoUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
