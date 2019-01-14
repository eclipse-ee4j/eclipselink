@REM
@REM Copyright (c) 2018, 2019 Oracle and/or its affiliates. All rights reserved.
@REM
@REM This program and the accompanying materials are made available under the
@REM terms of the Eclipse Public License v. 2.0 which is available at
@REM http://www.eclipse.org/legal/epl-2.0,
@REM or the Eclipse Distribution License v. 1.0 which is available at
@REM http://www.eclipse.org/org/documents/edl-v10.php.
@REM
@REM SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
@REM

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
set CLASSPATH=%THIS%..\jlib\moxy\jakarta.json.jar
set CLASSPATH=%CLASSPATH%;%THIS%..\jlib\moxy\jaxb-osgi.jar
set CLASSPATH=%CLASSPATH%;%THIS%..\jlib\moxy\jakarta.activation.jar
set CLASSPATH=%CLASSPATH%;%THIS%..\jlib\moxy\jakarta.validation-api.jar
set CLASSPATH=%CLASSPATH%;%THIS%..\jlib\eclipselink.jar
set JAXB_API=%THIS%..\jlib\moxy\api\jakarta.xml.bind-api.jar
set ENDORSED_DIR=..\jlib\moxy\api
set MAIN_CLASS=org.eclipse.persistence.jaxb.xjc.MOXyXJC
set JAVA_ARGS=%*

rem Set Java Version
for /f "tokens=3" %%i in ('java -version 2^>^&1 ^| %SystemRoot%\system32\find.exe "version"') do (
  set JAVA_VERSION1=%%i
)
for /f "tokens=1,2 delims=." %%j in ('echo %JAVA_VERSION1:~1,-1%') do (
  if "1" EQU "%%j" (
    set JAVA_VERSION2=%%k
  ) else (
    set JAVA_VERSION2=%%j
  )
)

rem Remove -ea
for /f "delims=-" %%i in ('echo %JAVA_VERSION2%') do set JAVA_VERSION=%%i
echo Java major version: %JAVA_VERSION%

if %JAVA_VERSION% GEQ 9 goto JDK9_OR_GREATER
rem Java 8
%JAVA_HOME%\bin\java.exe %JVM_ARGS% -cp %CLASSPATH% -Djava.endorsed.dirs=%ENDORSED_DIR% %MAIN_CLASS% %JAVA_ARGS%
@endlocal
goto :EOF

:JDK9_OR_GREATER
rem Java
%JAVA_HOME%\bin\java.exe %JVM_ARGS% -cp %CLASSPATH%;%JAXB_API% %MAIN_CLASS% %JAVA_ARGS%
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
