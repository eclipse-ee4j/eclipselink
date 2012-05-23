@REM Use the special ~dp modifier so that the call to env isn't relative
call "%~dp0env.bat"
call %ANT_HOME%\bin\ant reset-database
@REM pause