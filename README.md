# User storage application
This is a simple user storage application for managing users accounts.

You can run the application by executing the following command from the directory where this file resides. Please ensure you have installed a Java 17. You may first need to execute `chmod +x mvnw`.

```
./mvnw clean package wildfly:dev
```

Once the runtime starts, you can access the project at http://localhost:8080/user-storage.

You can also run the project via Docker. To build the Docker image, execute the following commands from the directory where this file resides. Please ensure you have installed a Java 17 and docker. You may first need to execute `chmod +x mvnw`.

```
./mvnw clean package
docker build -t user-storage:v1 .
```

You can then run the Docker image by executing:

```
docker run -it --rm -p 8080:8080 user-storage:v1
```

Once the runtime starts, you can access the project at http://localhost:8080/user-storage.
