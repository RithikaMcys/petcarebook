@echo off
title PetCareBook - Automated Pet Vaccination & Deworming Tracker
echo ==========================================================
echo   Starting PetCareBook Application...
echo ==========================================================
echo.
echo Dashboard will be available at: http://localhost:8080/
echo Swagger API Docs available at:  http://localhost:8080/swagger-ui.html
echo.
cd /d "%~dp0"
call mvnw.cmd spring-boot:run
pause
