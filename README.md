# Running the restful server


### Pre-requisites
You must have maven and java (1.8) installed and on your class path.

From the top level directory, run `mvn clean install` to build to project.

Once the project is built, navigate to the /target folder

Here you can run `java -jar points-0.0.1-SNAPSHOT.jar` to start the server

The following endpoints will be exposed at localhost:8080

### GET /users/{userId}/points-balance

Retrieves the current points balance for the given user.

Example return:
```json
{
  "DANNON": 1000,    
  "UNILEVER": 0,    
  "MILLER COORS": 5300
}
```

