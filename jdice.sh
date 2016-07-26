@echo off

set CP=%CLASSPATH%
set CP=%CP%;./target/scriptbuilder/lib/jdom-1.0.jar
set CP=%CP%;./target/scriptbuilder/lib/log4j-1.2.8.jar
set CP=%CP%;./target/jdice-0.5.jar

java -cp %CP% org.culpan.jdice.JDicePanel