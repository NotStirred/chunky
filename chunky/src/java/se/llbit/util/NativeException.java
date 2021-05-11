package se.llbit.util;

/**
 * Used to throw an exception by native libs
 */
public class NativeException extends RuntimeException {
  public NativeException(String message) {
    super(message);
  }
}
