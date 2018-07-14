package main.java.files.zip;

public class ZipBreakException extends RuntimeException {
  public ZipBreakException(String msg) {
    super(msg);
  }

  public ZipBreakException(Exception e) {
    super(e);
  }

  public ZipBreakException() {
    super();
  }
}
