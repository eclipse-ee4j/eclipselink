@echo off
setlocal
call "%~dp0../../../bin/setenv.cmd"

@REM User may increase Java memory setting(s) if desired:
set JVM_ARGS=-Xmx256m

REM Please do not change any of the following lines:
set _FIXPATH=
call :fixpath "%~dp0"
set THIS=%_FIXPATH:~1%

set CLASSPATH=%THIS%..\jlib\xercesImpl.jar
set CLASSPATH=%CLASSPATH%;%THIS%..\jlib\connector.jar
set CLASSPATH=%CLASSPATH%;%THIS%..\..\..\jlib\eclipselink.jar
set CLASSPATH=%CLASSPATH%;%THIS%..\jlib\elmwcore.jar
set CLASSPATH=%CLASSPATH%;%THIS%..\jlib\eclipselinkmw.jar
set CLASSPATH=%CLASSPATH%;%THIS%..\..\..\jlib\jpa\javax.persistence_1.0.0.jar
set CLASSPATH=%CLASSPATH%;%THIS%..\config
set CLASSPATH=%CLASSPATH%;%DRIVER_CLASSPATH%

set WORKBENCH_ARGS=-open %*
 
start %JAVA_HOME%\bin\javaw.exe %JVM_ARGS% -cp %CLASSPATH% org.eclipse.persistence.tools.workbench.Main %WORKBENCH_ARGS%

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
