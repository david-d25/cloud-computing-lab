# Backend
## Requirements
1. Java 11
2. Tomcat 9
3. PostgreSQL 13

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
## Amazon roles
1. cloud_computing_lab_lambda
   - Permissions:
      - AmazonRDSFullAccess
      - AmazonS3FullAccess
      - AmazonEC2FullAccess
      - AmazonRDSDataFullAccess
      - AWSLambdaBasicExecutionRole
## Amazon EC2 instances
- backend
  - Type: t2.micro
## Amazon functions
1. lambda-model-generator
    - Runtime: Java 11
    - Architecture: x86_64
    - Execution role: cloud_computing_lab_lambda
    - Enable Network: Yes
    - VPC: (same as in EC2 'backend' instance)
    - Subnets (same as in EC2 'backend' instance)
    - Security groups: default
    - Triggers:
      - API Gateway
        - API Type: REST API
        - Security: API key
# Frontend
## Requirements
1. NPM 6.*
2. NodeJS 12.*