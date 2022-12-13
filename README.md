# Debugging Revisited

## Learning Goals

- Review Debugging.
- Step through debugging a Spring application.

## Introduction

We learned a few modules back how to use a tool called a debugger in IntelliJ when
working with our Java programs. Lucky for us, we can still make use of the
debugger tool when writing Spring Boot applications! Let's review how to use the
tool and debug an application.

## Code Along: Debugging a Spring Boot Application

In this lesson, we'll look at a small Spring Web MVC that only contains a
default main class and a controller class. We will also continue to use Postman
to send the request URLs to our application.

To view the project we will be debugging in this lesson, pull down this lesson
[here](https://github.com/learn-co-curriculum/spring-mod-1-debugging) and open
it in an IDE.

Notice the project structure:

```text
├── HELP.md
├── README.md
├── mvnw
├── mvnw.cmd
├── pom.xml
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── example
    │   │           └── springdebuggingdemo
    │   │               ├── SpringDebuggingDemoApplication.java
    │   │               ├──controllers
    │   │               └──   └──  DebugController.java
    │   └── resources
    │       ├── application.properties
    │       ├── static
    │       └── templates
    └── test
        └── java
            └── org
                └── example
                    └── springdebuggingdemo
                        └── SpringDebuggingDemoApplicationTests.java
```

Go ahead and run the `SpringDebuggingDemoApplication` in debug mode. Notice that
everything seems to start up okay:

![debug-console](https://curriculum-content.s3.amazonaws.com/spring-mod-1/debugging/debug-spring-console.png)

Before we move on, open up the `DebugController` class in the
`com.example.springdebuggingdemo.controllers` package:

```java
package com.example.springdebuggingdemo.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
public class DebugController {

    @GetMapping("/greet/{name}")
    public String greet(String name) {
        return String.format("Hello %s!", name);
    }
}
```

We might be able to see an issue here already. But let's just see what happens.

It looks like there is only one URL path we can request from our application.
Open up Postman and enter in "http://localhost:8080/greet/Ted" in the request
URL. Don't forget to select "GET" to send the GET request.

![postman-null-name](https://curriculum-content.s3.amazonaws.com/spring-mod-1/debugging/postman-name-null.png)

Uh-oh. It looks like we have an issue. We're getting "Hello null!" back instead
of "Hello Ted!" We might also see that there is no stack trace error in the
console log either.

Let's look back at the controller class. Go ahead and set a breakpoint on line
10 with `return String.format("Hello %s!, name);` to see if it is even entering
the method. As a review, in order to set a breakpoint, click anywhere in the open
space to the right of the line number in the editor window - a red dot will
appear to indicate that you successfully set a breakpoint on that line.

If we send the GET request again, we'll see we are entering the method:

![hit-breakpoint](https://curriculum-content.s3.amazonaws.com/spring-mod-1/debugging/debug-hit-breakpoint.png)

Notice that when we hit the breakpoint and look at the `name` variable, it is
assigned to `null` instead of `Ted`, which is what we expected! Well, why might
that be?

Look closer at the `greet()` method:

```java
    @GetMapping("/greet/{name}")
    public String greet(String name) {
        return String.format("Hello %s!", name);
    }
```

The path variable `name` is not consumed! We need to add the `@PathVariable`
annotation to our method like this:

```java
    @GetMapping("/greet/{name}")
    public String greet(@PathVariable String name) {
        return String.format("Hello %s!", name);
    }
```

Let's re-run our application again in debug.

If we send the same GET request to our application, we'll hit our breakpoint
again:

![hit-breakpoint-correct-name](https://curriculum-content.s3.amazonaws.com/spring-mod-1/debugging/debug-name-ted.png)

We'll now see that the `name` variable is accurately set to `Ted` now. If we
continue running the application, we'll see that it returns `Hello Ted!` now.

## Other Debugging Tips

In the example above, we saw how to debug a bug that might not have been very
obvious.

Let's look at another example of how to use the debugger. This time, we'll refer
back to the football team example we have been using.

Maybe we want to look at the request body that was passed into the application.
We can set a breakpoint to do so!

![football-controller-breakpoint](https://curriculum-content.s3.amazonaws.com/spring-mod-1/debugging/debug-football-breakpoint.png)

When we hit the breakpoint, we can look in the debug console window to see
exactly what was sent and if it matches what we expected:

![football-controller-console](https://curriculum-content.s3.amazonaws.com/spring-mod-1/debugging/debug-football-console.png)

Other common issues include:

- The path variable names are misspelled or different from each other.

![misspelled-path-variable](https://curriculum-content.s3.amazonaws.com/spring-mod-1/debugging/debug-misspelled-path-variable.png)

This could result in a warning:

```text
WARN 25306 --- [nio-8080-exec-4] .w.s.m.s.DefaultHandlerExceptionResolver : Resolved [org.springframework.web.bind.MissingPathVariableException: Required URI template variable 'names' for method parameter type String is not present]
```

- Using the wrong annotations to perform a request mapping.

![wrong-annotation](https://curriculum-content.s3.amazonaws.com/spring-mod-1/debugging/debug-wrong-annotation.png)

This could result in a warning:

```text
WARN 28814 --- [nio-8080-exec-2] .w.s.m.s.DefaultHandlerExceptionResolver : Resolved [org.springframework.web.HttpRequestMethodNotSupportedException: Request method 'GET' not supported]
```

- Using a `@RequestParam` instead of a `@PathVariable` and vice versa.

![requestparam-vs-pathvariable](https://curriculum-content.s3.amazonaws.com/spring-mod-1/debugging/debug-requestparam-instead-pathvariable.png)

This could result in a warning:

```text
WARN 29881 --- [nio-8080-exec-1] .w.s.m.s.DefaultHandlerExceptionResolver : Resolved [org.springframework.web.bind.MissingServletRequestParameterException: Required request parameter 'name' for method parameter type String is not present]
```

## Conclusion

Seeing what is sent to the application and what it is getting back from the
data source is incredibly helpful when trying to figure out the unexpected. The
debugger tool in IntelliJ is not to be overlooked and can serve as an
efficient and quick way to figure out what may be wrong. Using this in
conjunction with Postman and the Java Visualizer tool will help in debugging
and further understanding exactly what is happening in the Spring Boot
application. Even though the Java Visualizer was not used in this lesson, it
can still be used to help debug Java code, as seen in previous lessons.
