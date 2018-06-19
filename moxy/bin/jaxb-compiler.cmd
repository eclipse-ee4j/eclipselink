@REM
@REM Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
@REM
@REM This program and the accompanying materials are made available under the
@REM terms of the Eclipse Public License 2.0 which is available at
@REM http://www.eclipse.org/legal/epl-2.0.
@REM
@REM SPDX-License-Identifier: EPL-2.0
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
set CLASSPATH=%THIS%..\jlib\moxy\javax.json-api_1.1.2.jar
set CLASSPATH=%CLASSPATH%;%THIS%..\jlib\moxy\javax.json_1.1.2.jar
set CLASSPATH=%CLASSPATH%;%THIS%..\jlib\moxy\jaxb-core_2.2.11.v201407311112.jar
set CLASSPATH=%CLASSPATH%;%THIS%..\jlib\moxy\jaxb-core_2.2.11.v201407311112.jar
set CLASSPATH=%CLASSPATH%;%THIS%..\jlib\moxy\jaxb-xjc_2.2.11.v201407311112.jar
set CLASSPATH=%CLASSPATH%;%THIS%..\jlib\moxy\javax.validation.api_2.0.1.Final.jar
set CLASSPATH=%CLASSPATH%;%THIS%..\jlib\eclipselink.jar
set JAVA_ARGS=%*

%JAVA_HOME%\bin\java.exe %JVM_ARGS% -cp %CLASSPATH% -Djava.endorsed.dirs=..\jlib\moxy\api org.eclipse.persistence.jaxb.xjc.MOXyXJC %JAVA_ARGS%

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
