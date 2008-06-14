@echo off
rem ---------------------------------------------------
rem  HIP Startup.
rem ---------------------------------------------------

set HIP_CLASSPATH=hip-0.1.jar;./lib/jetty-6.1.9.jar;./lib/jetty-util-6.1.9.jar;./lib/logbot.jar;./lib/pircbot.jar;./lib/servlet-api-2.5-6.1.9.jar

java -classpath %HIP_CLASSPATH% com.enjoyxstudy.hip.HipServer
