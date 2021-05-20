# Running the restful server


### Pre-requisites
You must have maven and java (1.8) installed and on your class path.

From the top level directory, run `mvn clean install` to build to project.

Once the project is built, navigate to the /target folder

Here you can run `java -jar points-0.0.1-SNAPSHOT.jar` to start the server


## Endpoints

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

### POST /users/{userId}/points-transactions

Adds a point transaction to the given users account.

Request body example:
```json
{ 
  "payer": "DANNON", 
  "points": 1000, 
  "timestamp": "2020-11-02T14:00:00Z"
}
```

Returns nothing on a successful add.

### POST /users/{userId}/points

Spends the given amount of points from the user's account.

Returns 400 when the user doesn't have enough points.

Request body example:
```json
{
  "points": 5000
}
```

Response example:
```json
{
  "DANNON": -200,
  "UNILEVER": -100,
  "MILLER COORS": -4700
}
```