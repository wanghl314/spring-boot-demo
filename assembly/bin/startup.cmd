@echo off
rem Copyright 1999-2018 Alibaba Group Holding Ltd.
rem Licensed under the Apache License, Version 2.0 (the "License");
rem you may not use this file except in compliance with the License.
rem You may obtain a copy of the License at
rem
rem      http://www.apache.org/licenses/LICENSE-2.0
rem
rem Unless required by applicable law or agreed to in writing, software
rem distributed under the License is distributed on an "AS IS" BASIS,
rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
rem See the License for the specific language governing permissions and
rem limitations under the License.
if not exist "%JAVA_HOME%\bin\java.exe" echo Please set the JAVA_HOME variable in your environment, We need java(x64)! jdk17 or later is better! & EXIT /B 1
set "JAVA=%JAVA_HOME%\bin\java.exe"

setlocal enabledelayedexpansion

set BASE_DIR=%~dp0
rem added double quotation marks to avoid the issue caused by the folder names containing spaces.
rem removed the last 5 chars(which means \bin\) to get the base DIR.
set BASE_DIR="%BASE_DIR:~0,-5%"

set CUSTOM_SEARCH_LOCATIONS=file:%BASE_DIR%\conf\

set SERVER=spring-boot-demo
set "DEMO_JVM_OPTS=-Xms512m -Xmx512m -Xmn256m"

rem set demo options
set "DEMO_OPTS=%DEMO_OPTS% -Ddemo.home=%BASE_DIR%"
set "DEMO_OPTS=%DEMO_OPTS% -jar %BASE_DIR%\target\%SERVER%.jar"

rem set demo spring config location
set "DEMO_CONFIG_OPTS=--spring.config.additional-location=%CUSTOM_SEARCH_LOCATIONS%"

rem set demo log4j file location
set "DEMO_LOG4J_OPTS=--logging.config=%BASE_DIR%\conf\logback-spring.xml"


set COMMAND="%JAVA%" %DEMO_JVM_OPTS% %DEMO_OPTS% %DEMO_CONFIG_OPTS% %DEMO_LOG4J_OPTS% demo.demo %*

rem start demo command
%COMMAND%
