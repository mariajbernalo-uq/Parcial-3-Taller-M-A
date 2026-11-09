@echo off
setlocal
chcp 65001 >nul
cd /d "%~dp0"
where javac >nul 2>&1
if errorlevel 1 (
    echo No se encontro javac. Instala un JDK y configura PATH o ejecuta desde IntelliJ.
    pause
    exit /b 1
)
if not exist out mkdir out
javac -encoding UTF-8 -d out @fuentes.txt
if errorlevel 1 (
    echo La compilacion no termino correctamente.
    pause
    exit /b 1
)
java -Dfile.encoding=UTF-8 -cp out uniquindio.Main
if errorlevel 1 (
    pause
    exit /b 1
)
pause
