# Requirements
1. Java 11
2. Tomcat 9
3. PostgreSQL 13
4. NPM 6.*
5. NodeJS 12.*

# How to build
1. Follow the [requirements](#requirements)
2. Run ```mvn package``` for the root module
3. The output WAR file is **bundle/target/ROOT.war**

# How to run
1. [Build](#how-to-build) the WAR file
2. [Prepare the database](#dev-database-setup)
3. [Prepare the environment variables](#dev-environment-variables)
4. Place the WAR file info **webapps** folder and run Tomcat

# Backend dev configuration
## Dev database setup
```sql
create database cloud_computing_dev;
create user cloud_computing_dev with password 'cloud_computing_dev';
grant all privileges on database cloud_computing_dev to cloud_computing_dev;
```

## Dev environment variables
```shell
DB_URL=jdbc:postgresql://localhost:5432/cloud_computing_dev
DB_USERNAME=cloud_computing_dev
DB_PASSWORD=cloud_computing_dev
```
# How to run backend for development
1. ```mvn clean package```
2. [Prepare dev database](#dev-database-setup)
3. [Configure environment variables for dev database connection](#dev-environment-variables)
4. Use 'backend' module artifact as webapp for Tomcat/IDEA

# How to run frontend for development
1. Have a backend running on ```/api``` context and port ```8080```
2. ```npm run dev```

# Cloud deploying info
## Amazon roles
1. cloud_computing_lab_lambda
   - Permissions:
      - AmazonRDSFullAccess
      - AmazonS3FullAccess
      - AmazonEC2FullAccess
      - AmazonRDSDataFullAccess
      - AWSLambdaBasicExecutionRole
2. aws-lambda-user
    - Permissions:
      - AWSLambda_FullAccess

## Amazon EC2 instances
- backend
  - Type: t2.micro
## Amazon RDS
- Method: easy create
- Engine: PostgreSQL 13
- Template: Dev/Test
- DB instance identifier: cloud-computing-dev
- Master username: postgres
- Master password: postgres
## Amazon functions
1. lambda-model-generator
    - Runtime: Java 11
    - Region: us-east-2
    - Architecture: x86_64
    - Execution role: cloud_computing_lab_lambda
    - Enable Network: Yes
    - VPC: (same as in EC2 'backend' instance)
    - Subnets (same as in EC2 'backend' instance)
    - Security groups: default
    - Runtime settings
      - Handler: space.davids_digital.cloud_computing_lab.lambda.handler.Handler
    - Triggers:
      - API Gateway
        - API Type: HTTP API
        - Security: Open