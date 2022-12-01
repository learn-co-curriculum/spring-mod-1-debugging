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
    │   │           ├──controllers
    │   │           │   └──  DebugController.java
    │   │           └── springdebuggingdemo
    │   │               └──  SpringDebuggingDemoApplication.java
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

We might see the issue already just by looking at the project structure, but
let us go through the process of debugging the application to see what happens.

Go ahead and run the `SpringDebuggingDemoApplication` in debug mode. Notice that
everything seems to start up okay:

![debug-console](https://curriculum-content.s3.amazonaws.com/spring-mod-1/debugging/debug-spring-console.png)

Before we move on, open up the `DebugController` class in the
`com.example.controllers` package:

```java
package com.example.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DebugController {

    @GetMapping("/")
    public String index() {
        return "Welcome to Spring Boot!";
    }
}
```

It looks like there is only one URL path we can request from our application.
Open up Postman and enter in "http://localhost:8080/" in the request URL. Don't
forget to select "GET" to send the GET request.

![postman-404](https://curriculum-content.s3.amazonaws.com/spring-mod-1/debugging/postman-404.png)

Uh-oh. It looks like we have an issue. We're getting a 404 error that it cannot
find the path. We might also see that there is no stack trace error in the
console log either.

Let's look back at the controller class. Go ahead and set a breakpoint on line
11 with `return "Welcome to Spring Boot";` to see if it is even entering the
method. As a review, in order to set a breakpoint, click anywhere in the open
space to the right of the line number in the editor window - a red dot will
appear to indicate that you successfully set a breakpoint on that line.

If we send the GET request again, we'll see we are still getting the same 404
error. This tells us that it isn't even getting entering the method.

Let's now take a look at the Java Visualizer to help us. In the "Debug" window,
open up the "Actuator|Java Visualizer":

![open-java-visualizer](https://curriculum-content.s3.amazonaws.com/spring-mod-1/debugging/open-java-visualizer.png)

When we open the window, we can actually take a glimpse into the the application
context of our Spring Boot project! Let's look at the beans we have defined in
our context. Click on "application" under the "Beans" tab:

![java-visualizer-beans](https://curriculum-content.s3.amazonaws.com/spring-mod-1/debugging/java-visualizer-beans.png)

In this window, we can see all the beans defined in the application context
listed in alphabetical order!

Notice something missing?

The `DebugController` isn't listed in the application context! Spring doesn't
know that the `DebugController` class even exists!

So what happened? Notice that our `SpringDebuggingDemoApplication` is in the
package `com.example.springdebuggingdemo`. If we remember, the
`@SpringBootApplication` is comprised of three different annotations:

- `@EnableAutoConfiguration`
- `@Configuration`
- `@ComponentScan`

Remember when we were defining the `@ComponentScan` annotation that if we do
not specify the base package, then the default base package is the package where
the `@ComponentScan` annotation is defined in then? In the main application
class, we do not define a base package; therefore, it will only scan for
components in the `com.example.springdebuggingdemo` package. And since our
controller class is defined _outside_ that package, it will not be picked up to
be scanned into the application context.

To fix this, we need to either move the `controllers` package into the
`springdebuggingdemo` package (which is preferred) or specify the base package
like this:

```java
package com.example.springdebuggingdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackages = "com.example.controllers")
public class SpringDebuggingDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringDebuggingDemoApplication.class, args);
    }

}
```

Preferably, we'd just fix the project structure to be like this:

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

Now if we re-run our application in debug mode again, we should see us hitting
the breakpoint when we send the GET request through Postman:

![hit-breakpoint](https://curriculum-content.s3.amazonaws.com/spring-mod-1/debugging/debug-hit-breakpoint.png)

If we click the green play button off to the left-side menu of the debugger
console to resume the program, we'll see that Postman no longer receives a 404
error and that we get a 200 status back with the message "Welcome to Spring
Boot!" in the response body.

![postman-200-status](https://curriculum-content.s3.amazonaws.com/spring-mod-1/debugging/postman-welcome-to-spring-boot.png)

## Other Debugging Tips

In the example above, we saw how to debug a bug that might not have been very
obvious. We did not fully use the debugger to its full extent.

Let's look at another example of how to use the debugger. This time, we'll refer
back to the football team example we have been using.

Maybe we want to look at the request body that was passed into the application.
We can set a breakpoint to do so!

![football-controller-breakpoint](https://curriculum-content.s3.amazonaws.com/spring-mod-1/debugging/debug-football-breakpoint.png)

When we hit the breakpoint, we can look in the debug console window to see
exactly what was sent and if it matches what we expected:

![football-controller-console](https://curriculum-content.s3.amazonaws.com/spring-mod-1/debugging/debug-football-console.png)

## Conclusion

Seeing what is sent to the application and what it is getting back from the
data source is incredibly helpful when trying to figure out the unexpected. The
debugger tool in IntelliJ is not to be overlooked and can serve as an
efficient and quick way to figure out what may be wrong. Using this in
conjunction with Postman and the Java Visualizer tool will help in debugging
and further understanding exactly what is happening in the Spring Boot
application.
