package com.tscp.mvne.refund;

import com.tscp.mvne.exception.MVNEException;

public class OldRefundException extends MVNEException {
  private static final long serialVersionUID = 4018439343134758689L;

  public OldRefundException() {
    super();
  }

  public OldRefundException(String methodName, String message, Throwable cause) {
    super(methodName, message, cause);
  }

  public OldRefundException(String methodName, String message) {
    super(methodName, message);
  }

  public OldRefundException(String message, Throwable cause) {
    super(message, cause);
  }

  public OldRefundException(String message) {
    super(message);
  }

  public OldRefundException(Throwable cause) {
    super(cause);
  }

}
