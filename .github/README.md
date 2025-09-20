# Relay

Useful for connections between environments such as bot - plugin or other, I let your imagination do the rest

## Features

* Lightweight messaging system with minimal dependencies
* Compatible with multiple message providers (Redis, RabbitMQ, etc.)
* Asynchronous message handling
* Timeout management for response messages
* Easy to use :)

## Installation

### Maven

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <version>3.3.0</version>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
                </execution>
            </executions>
            <configuration>
                <relocations>
                    <relocation>
                        <pattern>fr.kotlini.relay</pattern>
                        <!-- Replace 'com.yourpackae' with the package of your project! -->
                        <shadedPattern>com.yourpackage.relay</shadedPattern>
                    </relocation>
                </relocations>
            </configuration>
        </plugin>
    </plugins>
</build>

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>fr.kotlini</groupId>
        <artifactId>relay</artifactId>
        <version>0.0.1</version>
    </dependency>
</dependencies>
```

### Gradle

```groovy
plugins {
    id 'com.gradleup.shadow' version '8.3.0'
}

repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'fr.kotlini:relay:0.0.1'
}

shadowJar {
    // Replace 'com.yourpackage' with the package of your project 
    relocate 'fr.kotlini.relay', 'com.yourpackage.relay'
}
```

## Usage

### Start

Create a RelayMessaging instance using the builder and start it:
```java
RabbitMQCredentials credentials = new RabbitMQCredentials("localhost", 6379, "user", "pass", "queue-name");

RelayMessaging messaging = RelayMessaging.builder()
        .nodeId(() -> "my-service-node") // OR LOOK TIPS :)
        .provider(MessageProviderType.RABBIT_MQ, credentials)
        .build();

        messaging.start();
```

### Message Listeners

Register listeners to handle incoming messages:
```java
IMessageId messageId = () -> "user.login"; // OR LOOK TIPS :)

messaging.registerListener(() -> "messageId", (request) -> {
    System.out.println("Received messageId: " + request.getMessageId());
    return null; // or create your response here
});
```

### Sending Messages

Send messages to specific nodes:
```java
INode targetNode = () -> "lobby";
IMessageId messageId = () -> "user.update";

UserData userData = new UserData("kotlini", 4000);

// Send a simple fire-and-forget message
messaging.publish(targetNode, messageId, userData);

// Send with response
messaging.publishWithResponse(targetNode, messageId, userData).thenAccept(responseBaseMessage -> {
    if (responseBaseMessage instanceof ResponseMessage<?> response) { // or you custom response !
        System.out.println("Received response: " + response.getPayload());
    }
});

// and custom timeout
CompletableFuture<ResponseBaseMessage> timedResponse = messaging.publishWithResponse(targetNode, messageId, userData, 10, TimeUnit.SECONDS);
timedResponse.exceptionally(throwable -> {
    if (throwable instanceof TimeoutException){
        System.out.println("Delay excepted!");
    }
    return null;
});
```

### Stop

Always properly shutdown RelayMessaging to clean up:
```java
try {
    messaging.stop();
} catch (Exception e) {
    LOGGER.error("Error stopping RelayMessaging", e);
}
```

### Tips
```java
public enum MessageType implements IMessageId {
    
    UPDATE_PROFILE, 
    FRIEND_REQUEST,
    ;

    @Override
    public String getId() {
        return name();
    }
}

public enum NodeType implements INode {

    LOBBY,
    HUB,
    SERVER_1,
    ;

    @Override
    public String getId() {
        return name();
    }
}


```

### TODO

### Benchmark

Performance tests demonstrate excellent throughput for message relay operations:
Mode: 
 - RabbitMQ provider
 - Json codec


| Benchmark | Mode | Count | Score | Error | Units |
|-----------|------|-------|-------|-------|-------|
| RelayBenchmark.testPublishWithResponseAtoB | avgt | 25 | 0.002 | ± 0.001 | s/op |

### Running Benchmarks
To run the performance tests yourself:
```yml
gradle jmh
```

### Next Steps
 - Multi module architecture refactor
 - Message structure refactoring
 - Binary codec implementation
 - Redis provider implementation

### License
See the [LICENSE](../LICENSE) file for license rights and limitations (MIT).