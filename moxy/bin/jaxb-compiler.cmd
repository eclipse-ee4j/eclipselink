@echo off
@setlocal
call "%~dp0setenv.cmd"

@REM User may increase Java memory setting(s) if desired:
set JVM_ARGS=-Xmx256m

@REM If going through a proxy, set the proxy host and proxy port below, then uncomment the line
@REM set JVM_ARGS=%JVM_ARGS% -DproxySet=true -Dhttp.proxyHost= -Dhttp.proxyPort=

@REM Please do not change any of the following lines:

set _FIXPATH=
call :fixpath "%~dp0"
set THIS=%_FIXPATH:~1%
set CLASSPATH=%THIS%..\jlib\moxy\javax.xml.stream_1.0.1.v200903100845.jar
set CLASSPATH=%CLASSPATH%;%THIS%..\jlib\moxy\javax.xml.bind_2.1.12.v20090708-1500.jar
set CLASSPATH=%CLASSPATH%;%THIS%..\jlib\moxy\javax.activation_1.1.0.v200806101325.jar
set CLASSPATH=%CLASSPATH%;%THIS%..\jlib\moxy\jaxb-impl.jar
set CLASSPATH=%CLASSPATH%;%THIS%..\jlib\moxy\jaxb-xjc.jar
set CLASSPATH=%CLASSPATH%;%THIS%..\jlib\eclipselink.jar
set JAVA_ARGS=%*

%JAVA_HOME%\bin\java.exe -Djava.endorsed.dirs=\..\jlib\moxy -cp %CLASSPATH% %JVM_ARGS% com.sun.tools.xjc.XJCFacade -eclipselink %JAVA_ARGS%

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
