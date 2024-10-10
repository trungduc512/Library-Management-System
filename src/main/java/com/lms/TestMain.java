package com.lms;

import java.util.Scanner;

public class TestMain {


  public static void main(String[] args) {
    // Đăng nhập tài khoản Librarian
    Scanner scanner = new Scanner(System.in);
    System.out.print("Nhập họ tên: ");
    String hoTen = scanner.nextLine();
    System.out.print("Nhập tên tài khoản: ");
    String username = scanner.nextLine();
    System.out.print("Nhập mật khẩu: ");
    String password = scanner.nextLine();

    if (Librarian.register(hoTen, username, password)) {
      System.out.println("Đăng ký thành công.");
    } else {
      System.out.println("Đăng ký không thành công.");
    }
  }


}
