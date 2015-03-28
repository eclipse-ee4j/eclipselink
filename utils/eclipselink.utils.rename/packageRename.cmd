@echo off
@setlocal
call "%~dp0../../bin/setenv.cmd"

@REM User may increase Java memory setting(s) if desired:
set JVM_ARGS=-Xmx256M

@REM Please do not change any of the following lines:
set _FIXPATH=
call :fixpath "%~dp0"
set THIS=%_FIXPATH:~1%
set _FIXPATH=
call :fixpath "%1"
set SRC_DIR=%_FIXPATH:~1%
set _FIXPATH=
call :fixpath "%2"
set DEST_DIR=%_FIXPATH:~1%
set _FIXPATH=
call :fixpath "%3"
set LOG_DIR=%_FIXPATH:~1%

set CLASSPATH=%THIS%package-rename.jar
set JAVA_ARGS=%SRC_DIR% %DEST_DIR% %THIS%package-rename.properties

@echo on
%JAVA_HOME%\bin\java %JVM_ARGS% -classpath %CLASSPATH% org.eclipse.persistence.utils.rename.MigrateTopLinkToEclipseLink %JAVA_ARGS%
pause
@echo off
@endlocal
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