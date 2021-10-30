# Backend
## Requirements:
1. Java 11
2. Tomcat 9
3. PostgreSQL 13

## Dev database setup:
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

# Frontend
*tumbleweed sound*