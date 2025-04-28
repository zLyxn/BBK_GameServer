@echo off
chcp 65001
cls
REM Skript zum Starten einer JAR-Datei mit "gameserver" im Namen

set JAR_FILE=

for %%f in (*.jar) do (
    echo Prüfe %%f
    echo %%f | findstr /i "gameserver" >nul
    if not errorlevel 1 if not defined JAR_FILE (
        set "JAR_FILE=%%f"
        echo Gefundene Datei: %%f
	goto :found
    )
)

:found
if not defined JAR_FILE (
    echo Keine JAR-Datei mit "gameserver" im Namen gefunden.
    pause
    exit /b 1
)

echo Starte %JAR_FILE%
java -jar "%JAR_FILE%"
pause
