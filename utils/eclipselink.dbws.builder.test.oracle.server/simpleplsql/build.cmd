@echo off
@setlocal
call "%~dp0env.bat"
call %ANT_HOME%\bin\ant -emacs build
@endlocal
@REM pause