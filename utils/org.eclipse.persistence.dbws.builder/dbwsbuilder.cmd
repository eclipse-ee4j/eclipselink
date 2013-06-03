@echo off
setlocal
call "%~dp0../../bin/setenv.cmd"

@REM User may increase Java memory setting(s) if desired:
set JVM_ARGS=-Xmx256m

REM Please do not change any of the following lines:
set _FIXPATH=
call :fixpath "%~dp0"
set THIS=%_FIXPATH:~1%

set CLASSPATH=%THIS%\javax.servlet_2.4.0.v200806031604.jar
set CLASSPATH=%CLASSPATH%;%THIS%\javax.wsdl_1.6.2.v201012040545.jar
set CLASSPATH=%CLASSPATH%;%THIS%\org.eclipse.persistence.oracleddlparser_1.0.0.v20130531.jar
set CLASSPATH=%CLASSPATH%;%THIS%..\..\jlib\eclipselink.jar
set CLASSPATH=%CLASSPATH%;%THIS%\eclipselink-dbwsutils.jar
set CLASSPATH=%CLASSPATH%;%DRIVER_CLASSPATH%
set CLASSPATH=%CLASSPATH%;%JAVA_HOME%\lib\tools.jar

set DBWSBUILDER_ARGS=%*
 
%JAVA_HOME%\bin\java.exe %JVM_ARGS% -cp %CLASSPATH% org.eclipse.persistence.tools.dbws.DBWSBuilder %DBWSBUILDER_ARGS%

endlocal
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
