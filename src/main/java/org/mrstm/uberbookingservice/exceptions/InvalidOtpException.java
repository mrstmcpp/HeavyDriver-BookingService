package org.mrstm.uberbookingservice.exceptions;

public class InvalidOtpException extends RuntimeException {
  public InvalidOtpException(String message) {
    super(message);
  }
}
