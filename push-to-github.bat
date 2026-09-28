@echo off
setlocal
echo ==========================================================
echo  Pushing PetCareBook to Your GitHub Account (RithikaMcys)
echo ==========================================================
echo.
set "PATH=%LOCALAPPDATA%\Programs\Git\cmd;%PATH%"

set DEFAULT_URL=https://github.com/RithikaMcys/PetCareBook.git
echo Default Remote: %DEFAULT_URL%
set /p REPO_URL="Press Enter to use default or enter URL: "
if "%REPO_URL%"=="" set REPO_URL=%DEFAULT_URL%

echo.
echo [1/3] Setting remote origin to %REPO_URL%...
git remote remove origin >nul 2>&1
git remote add origin %REPO_URL%

echo [2/3] Setting branch to main...
git branch -M main

echo [3/3] Pushing code to GitHub...
git push -u origin main

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ==========================================================
    echo  SUCCESS! PetCareBook has been pushed to your GitHub!
    echo ==========================================================
) else (
    echo.
    echo [NOTICE] Ensure you have created the empty repo at:
    echo https://github.com/new (Name: PetCareBook)
)

pause
