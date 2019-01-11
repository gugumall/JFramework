set classpath=d:\lib\servlet-api.jar; d:\lib\jshop.jar; d:\lib\jpay.jar; d:\lib\j.jar;
set path=%path%; C:\Program Files\Java\jdk1.7.0_25\bin

::use rmi-iiop
::start orbd -ORBInitialHost service.vselected.com -ORBInitialPort 8999

::use rmi
start rmiregistry 1099