package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class DemoApplication {

  // @Value("${NAME:World}")
  // String name;

  // @RestController
  // class HelloworldController {
  //   @GetMapping("/")
  //   // String hello() {
  //   //   return "Hello " + name + "!";
  //   // }
  //   void hello() {
  //     System.out.println("Hello Kartik");
  //   }
  // }

  public  void hello(){

  System.out.println("Hello Kartik");

  }

  public static void main(String[] args) {
   DemoApplication demoApplication = new DemoApplication();
   demoApplication.hello();
    SpringApplication.run(DemoApplication.class, args);
  }

}
