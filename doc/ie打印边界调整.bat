@echo off
title IE�������ӡ�߾��������  by:���� QQ:65651828
COLOR 1e
CLS
echo ..............................................................................
echo            "IE�������ӡ�߾�������� by �������ҽ�� Ⱥ��9184571"
echo ..............................................................................
echo ��������ֵ����25.4����4λС�����뵽���棡����ʵ�ʱ߾�Ϊ5��������Ҫ����0.1968
set /p id1=�������ϱ߾ࣨ1=25.4mm����
set /p id2=�������±߾ࣨ1=25.4mm����
set /p id3=��������߾ࣨ1=25.4mm����
set /p id4=�������ұ߾ࣨ1=25.4mm����

echo �����޸�ҳ������... 
reg add "HKEY_CURRENT_USER\Software\Microsoft\Internet Explorer\PageSetup" /v footer /t REG_SZ /d "" /f 
reg add "HKEY_CURRENT_USER\Software\Microsoft\Internet Explorer\PageSetup" /v header /t REG_SZ /d "" /f 
reg add "HKEY_CURRENT_USER\Software\Microsoft\Internet Explorer\PageSetup" /v margin_bottom /t REG_SZ /d %id2% /f 
reg add "HKEY_CURRENT_USER\Software\Microsoft\Internet Explorer\PageSetup" /v margin_left /t REG_SZ /d %id3% /f 
reg add "HKEY_CURRENT_USER\Software\Microsoft\Internet Explorer\PageSetup" /v margin_right /t REG_SZ /d %id4% /f 
reg add "HKEY_CURRENT_USER\Software\Microsoft\Internet Explorer\PageSetup" /v margin_top /t REG_SZ /d %id1% /f 
pause
echo on