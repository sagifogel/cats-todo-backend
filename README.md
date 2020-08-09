# cats-todo-backend

Todo-Backend implementation using cats, http4s and circe

A sample project of a microservice using [cats](https://typelevel.org/cats/), [http4s](https://http4s.org/), [doobie](http://tpolecat.github.io/doobie/) and [circe](https://circe.github.io/circe/).

The microservice allows CRUD of todo items with a description and enables the option to mark todo as done.


## Database

[h2](http://www.h2database.com/) is used as a database. This is an in memory database, so when stopping the application, the state of the
microservice is lost.

### The following are prerequisites to running the application:

1. Scala 2.13
2. Java 8
3. sbt

### Running:

- Via sbt: `sbt "project core" run`
- Via IDEA: 
    - Load the project
    - Refresh sbt dependencies
    - Go to "Edit Configuration"
    - Select the `+` sign to add a new app (or just CTRL + SHIFT + F9 on the `Main` file of `modules.core`):
        - Main class: `com.github.sagifogel.todo.Main`
        - Program arguments: should be the path to your data generation executable
        - Use classpath on module: `core.cats-todo-backend-core`
        - JRE: should be 1.8 (Java 8)
        

### Tests:

You can test the microservice with `sbt "project tests" test` in the terminal
    
### Querying the data API

After running the service, the process starts an HTTP service which by default binds 
to "0.0.0.0:8080". If you'd like to change the IP or port, this can be configured via 
the **application.conf**, and a restart to the service.

#### Endpoints exposed:

1. List all todo items:
    - URL: [IP:Port]/v1/todos/
    - Verb: GET
    
2. Add todo:
    - URL: [IP:Port]/v1/todos/
    - Verb: POST
    - Payload:
    
    ```json
    {
      "$schema": "http://json-schema.org/draft-04/schema#",
      "type": "object",
      "properties": {
        "description": {
          "type": "string"
        },
        "completed": {
          "type": "boolean"
        }
      },
      "required": [
        "description",
        "completed"
      ]
    }
    ```
   
3. Update todo:
    - URL: [IP:Port]/v1/todos/{uuid}
    - Verb: PUT
    - Payload:
    
        ```json
        {
          "$schema": "http://json-schema.org/draft-04/schema#",
          "type": "object",
          "properties": {
            "description": {
              "type": "string"
            },
            "completed": {
              "type": "boolean"
            }
          },
          "required": [
            "description",
            "completed"
          ]
        }
        ```
      
 4. Mark todo as done:
     - URL: [IP:Port]/v1/todos/{uuid}/done
     - Verb: PUT
     
 5. Delete specific todo item:
     - URL: [IP:Port]/v1/todos/{uuid}
     - Verb: DELETE
     
5. Delete all todo items:
  - URL: [IP:Port]/v1/todos/
  - Verb: DELETE    
  
  
  
  