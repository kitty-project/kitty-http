# Kitty HTTP Server

## Install
```shell
./mvnw install
```
## Dependency
```xml
<dependency>
    <groupId>com.julianjupiter.kitty</groupId>
    <artifactId>kitty-http</artifactId>
    <version>0.0.1</version>
</dependency>
```
## Sample Codes

```java
import com.julianjupiter.kitty.http.HttpServer;
import com.julianjupiter.kitty.http.HttpStatus;

public class App {
    private static final System.Logger LOGGER = System.getLogger(App.class.getName());

    public static void main(String[] args) {
        var server = HttpServer.createServer(context -> {
            var response = context.response();
            var currentPath = context.request()
                    .requestLine()
                    .target()
                    .getPath();
            return switch (currentPath) {
                case "/" -> response.body("Welcome home!");
                case "/message" -> response
                        .header("Content-Type", "application/json")
                        .body("""
                                {
                                    "message": "Hello, world!"
                                }
                                """);
                default -> response.status(HttpStatus.NOT_FOUND);
            };
        });
        server.start(() -> LOGGER.log(System.Logger.Level.INFO, "App is running on port 8080."));
    }
}
```
