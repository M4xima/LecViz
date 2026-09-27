@echo off
echo ===========================
echo  LecViz - PDS Video Generator
echo  Prof. Rupesh Nasre, IIT Madras
echo ===========================
echo.

if "%1"=="" (
    echo Available scenes:
    echo   intro       - Introduction to PDS
    echo   complexity  - Complexity Analysis
    echo   arrays      - Arrays, Searching ^& Sorting
    echo   linkedlist  - Linked Lists
    echo   stack       - Stacks ^& Queues
    echo   tree        - Trees ^& BST
    echo   dictionary  - Hash Tables
    echo   priorityq   - Priority Queues / Heaps
    echo   graph       - Graphs (BFS, DFS)
    echo   all         - Render ALL scenes
    echo.
    set /p SCENE="Enter scene name (or 'all'): "
) else (
    set SCENE=%1
)

if /i "%SCENE%"=="all" (
    echo Rendering ALL scenes...
    mvn -q compile exec:java -Dexec.mainClass="com.lecviz.LecVizApp"
) else (
    echo Rendering scene: %SCENE%
    mvn -q compile exec:java -Dexec.mainClass="com.lecviz.LecVizApp" -Dexec.args="%SCENE%"
)

echo.
echo Done! Check the 'output' folder for MP4 files.
pause
