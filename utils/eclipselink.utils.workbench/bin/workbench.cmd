@echo off
setlocal
call "%~dp0setenv.cmd"

@REM User may increase Java memory setting(s) if desired:
set JVM_ARGS=-Xmx256m
set JVM_ARGS=%JVM_ARGS% -Dice.pilots.html4.ignoreNonGenericFonts=true

REM Please do not change any of the following lines:
set _FIXPATH=
call :fixpath "%~dp0"
set THIS=%_FIXPATH:~1%

set CLASSPATH=%THIS%..\..\eclipselink.utils.workbench.lib\compile\xercesImpl.jar
set CLASSPATH=%CLASSPATH%;%THIS%..\..\eclipselink.utils.workbench.lib\run\connector.jar
set CLASSPATH=%CLASSPATH%;%THIS%..\..\eclipselink.utils.workbench.lib\compile\eclipselink.jar
set CLASSPATH=%CLASSPATH%;%THIS%..\..\eclipselink.utils.workbench.lib\mw\elmwcore.jar
set CLASSPATH=%CLASSPATH%;%THIS%..\..\eclipselink.utils.workbench.lib\mw\eclipselinkmw.jar
set CLASSPATH=%CLASSPATH%;%THIS%..\config
set CLASSPATH=%CLASSPATH%;%THIS%..\lib\java\internal
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
