@echo off
echo Starting WildNest in development mode...
echo Using remote MySQL database (pppnas.top:9009)
echo.
java -jar target/WildNest-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
pause