@REM Maven Wrapper script for Windows
@echo off
set MAVEN_WRAPPER_JAR=.mvn\wrapper\maven-wrapper.jar
if not exist "%MAVEN_WRAPPER_JAR%" (
    echo Downloading Maven wrapper...
    mkdir .mvn\wrapper 2>nul
    curl -s https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar -o "%MAVEN_WRAPPER_JAR%"
)
"%JAVA_HOME%\bin\java.exe" -jar "%MAVEN_WRAPPER_JAR%" %*
