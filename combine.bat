@echo off
rem Joins all clips in output\<folder>\ into output\combined\<folder>_full.mp4 (default folder: arrays)
cd /d "%~dp0"
set FOLDER=%1
if "%FOLDER%"=="" set FOLDER=arrays
mvn -q compile exec:java -Dexec.mainClass="com.lecviz.tools.CombineClips" -Dexec.args="%FOLDER%"
pause
