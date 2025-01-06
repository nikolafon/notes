## Steps to run the application

### Prerequisites

1. Install [corretto-21](https://docs.aws.amazon.com/corretto/latest/corretto-21-ug/downloads-list.html)
2. Install Maven [maven](https://maven.apache.org/install.html)

### Steps

1. Go to notes-service folder and execute the following commans:
```bash 
    mvn install
    mvn spring-boot:run
```

2. Go to notes folder and execute the following commands:
```bash 
    npm install
    npm start
```
3. Open the browser and go to http://localhost:4200/
login with the following credentials:
```text
    username: admin
    password: admin
    organization: default
```
You can also login using GitHub login but make sure to enter the correct organization name.