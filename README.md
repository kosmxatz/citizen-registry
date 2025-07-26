# Info

This Restful application which allows you to add a registration with the following attributes:

- name
- surname

You can perform the following actions:
- add
- delete
- search

Default branch: develop 

# How to

## Linux

```bash
mvn clean compile exec:java
mvn clean compile
mvn exec:java

curl -X POST "http://localhost:8080/api/citizens" \
     -H "Content-Type: application/json" \
     -d '{"name":"Pit","surname":"smith"}'
```
On another terminal
```bash
curl http://localhost:8080/api/citizens
```

## Windows

```powershell
$body = @{
    name    = "Pit"
    surname = "Smith"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/citizens" `
                  -Method Post `
                  -Body $body `
                  -ContentType "application/json"

curl -Uri "http://localhost:8080/api/citizens"
```