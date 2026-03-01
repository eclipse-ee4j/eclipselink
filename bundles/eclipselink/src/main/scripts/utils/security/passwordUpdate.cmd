@REM
@REM Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
@REM
@REM This program and the accompanying materials are made available under the
@REM terms of the Eclipse Public License v. 2.0 which is available at
@REM http://www.eclipse.org/legal/epl-2.0,
@REM or the Eclipse Distribution License v. 1.0 which is available at
@REM http://www.eclipse.org/org/documents/edl-v10.php.
@REM
@REM SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
@REM

@REM Usage is `passwordUpdate.sh|.cmd -ip <old encrypted password>`
@REM This application internally decrypt old encrypted password used by some previous version EclipseLink and encrypt it by latest algorithm.

@echo off
setlocal
call "%~dp0../../bin/setenv.cmd"

@REM User may increase Java memory setting(s) if desired:
set JVM_ARGS=-Xmx256m

REM Please do not change any of the following lines:
set _FIXPATH=
call :fixpath "%~dp0"
set THIS=%_FIXPATH:~1%

set CLASSPATH=%CLASSPATH%;%THIS%..\..\jlib\eclipselink.jar

set PASSWORD_UPDATE_ARGS=%*
 
%JAVA_HOME%\bin\java.exe %JVM_ARGS% -cp %CLASSPATH% org.eclipse.persistence.tools.security.JCEEncryptorCmd %PASSWORD_UPDATE_ARGS%

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
