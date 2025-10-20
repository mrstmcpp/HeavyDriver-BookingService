package org.mrstm.uberbookingservice.exceptions;

public class OtpNotFoundException extends RuntimeException{
    public OtpNotFoundException(String message){
        super(message);
    }
}
