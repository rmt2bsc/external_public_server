rem echo off
rem
rem In order to execute this file either include in OS Path or navigate to the directory 
rem where it resides and type at the command line prompt, xml_bind <return key>.
rem 

del /Q C:\ProjectServer\ServiceDispatch\gen\com\xml\schema\bindings\*.*
del /Q C:\ProjectServer\ServiceDispatch\src\java\com\xml\schema\bindings\*.*
xjc -d C:\ProjectServer\ServiceDispatch\gen -p com.bindings.xml.jaxb C:\ProjectServer\ServiceDispatch\resources\xml\schemas\
copy C:\ProjectServer\ServiceDispatch\gen\com\bindings\xml\jaxb\*.* C:\ProjectServer\ServiceDispatch\resources\xml\schemas\bindings