@REM User MUST set DRIVER_CLASSPATH to point to their desired JDBC driver jar(s).
@REM For example:
@REM DRIVER_CLASSPATH=/some/directory/mysql-connector-java-5.1.6-bin.jar
@REM 
set DRIVER_CLASSPATH=C:\oracle\product\11.1.0\db\jdbc\lib\ojdbc6_g.jar

@REM User MUST set JAVA_HOME to point a JavaSE 6 JDK installation
set JAVA_HOME=C:\jdk1.6-current

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
