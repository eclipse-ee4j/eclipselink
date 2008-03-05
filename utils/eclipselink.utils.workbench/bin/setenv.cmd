@REM User MUST set DRIVER_CLASSPATH to point to their desired driver jar(s), such as JDBC and J2C.  For example:
@REM DRIVER_CLASSPATH=C:\MySQL\MySQLDriver4.jar
@REM 
@REM Note: DRIVER_CLASSPATH should NOT contain any classes for your persistent business objects - these are 
@REM configured in the Mapping Workbench project.
set DRIVER_CLASSPATH=

@REM User MUST set JAVA_HOME to point a supported JRE
set JAVA_HOME=C:\Program Files\Java\jdk1.5.0_07

@REM Please do not change any of the following lines:

set _FIXPATH=
call :fixpath "%DRIVER_CLASSPATH%"
set DRIVER_CLASSPATH=%_FIXPATH:~1%
set _FIXPATH=
call :fixpath "%JAVA_HOME%"
set JAVA_HOME=%_FIXPATH:~1%
goto :EOF

:fixpath
if not %1.==. (
  for /f "tokens=1* delims=;" %%a in (%1) do (
    call :shortfilename "%%a" & call :fixpath "%%b"
  )
)
goto :EOF
:shortfilename
for %%i in (%1) do set _FIXPATH=%_FIXPATH%;%%~fsi
goto :EOF
