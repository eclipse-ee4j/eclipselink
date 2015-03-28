@REM User MUST set DRIVER_CLASSPATH to point to their desired driver jar(s), such as JDBC and J2C.  For example:
@REM set DRIVER_CLASSPATH=c:\some_dir\jdbc\ojdbc14.jar;c:\some_other_dir\j2c\aqapi.jar
@REM 
@REM Note: DRIVER_CLASSPATH should NOT contain any classes for your persistent business objects - these are 
@REM configured in the Mapping Workbench project.

@REM User MUST set JAVA_HOME to point a supported JRE. If none
@REM is provided for INSTALL_JAVA_HOME then the system JAVA_HOME
@REM value will be used
set INSTALL_JAVA_HOME=%s_jreDirectory%
if "%JAVA_HOME%"=="" (
    set JAVA_HOME=%INSTALL_JAVA_HOME%
) 

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
