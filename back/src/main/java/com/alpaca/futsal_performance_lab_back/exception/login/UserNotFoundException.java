package com.alpaca.futsal_performance_lab_back.exception.login;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(String message) {
    super(message);
  }
}

