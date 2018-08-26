@echo off
title IE浏览器打印边距调整程序  by:永恒 QQ:65651828
COLOR 1e
CLS
echo ..............................................................................
echo            "IE浏览器打印边距调整程序 by 永恒电脑医生 群号9184571"
echo ..............................................................................
echo 请用输入值除以25.4后保留4位小数输入到下面！例：实际边距为5，在下面要输入0.1968
set /p id1=请输入上边距（1=25.4mm）：
set /p id2=请输入下边距（1=25.4mm）：
set /p id3=请输入左边距（1=25.4mm）：
set /p id4=请输入右边距（1=25.4mm）：

echo 正在修改页面设置... 
reg add "HKEY_CURRENT_USER\Software\Microsoft\Internet Explorer\PageSetup" /v footer /t REG_SZ /d "" /f 
reg add "HKEY_CURRENT_USER\Software\Microsoft\Internet Explorer\PageSetup" /v header /t REG_SZ /d "" /f 
reg add "HKEY_CURRENT_USER\Software\Microsoft\Internet Explorer\PageSetup" /v margin_bottom /t REG_SZ /d %id2% /f 
reg add "HKEY_CURRENT_USER\Software\Microsoft\Internet Explorer\PageSetup" /v margin_left /t REG_SZ /d %id3% /f 
reg add "HKEY_CURRENT_USER\Software\Microsoft\Internet Explorer\PageSetup" /v margin_right /t REG_SZ /d %id4% /f 
reg add "HKEY_CURRENT_USER\Software\Microsoft\Internet Explorer\PageSetup" /v margin_top /t REG_SZ /d %id1% /f 
pause
echo on