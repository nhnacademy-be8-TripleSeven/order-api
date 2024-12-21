package com.tripleseven.orderapi.exception.notfound;

public class MemberNotfoundException extends RuntimeException {
  public MemberNotfoundException(String message) {
    super(message);
  }
}
