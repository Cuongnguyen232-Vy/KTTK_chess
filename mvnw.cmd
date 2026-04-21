@REM ----------------------------------------------------------------------------
@REM Maven Start Up Batch script
@REM ----------------------------------------------------------------------------
@IF "%__MVNW_ARG0_NAME__%"=="" (SET "MVN_CMD=mvn") ELSE (SET "MVN_CMD=%__MVNW_ARG0_NAME__%")

@SET MAVEN_PROJECTBASEDIR=%~dp0

@SET MAVEN_WRAPPER_JAVA_HOME=%JAVA_HOME%
@IF NOT "%JAVA_HOME%"=="" GOTO OKJHome
@FOR %%i IN (java.exe) DO (SET "JAVA_HOME=%%~$PATH:i\..")
:OKJHome

@SET WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain
@SET DOWNLOAD_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"

@SET WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
@SET WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

"%JAVA_HOME%\bin\java.exe" "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR:~0,-1%" -classpath %WRAPPER_JAR% %WRAPPER_LAUNCHER% %*
