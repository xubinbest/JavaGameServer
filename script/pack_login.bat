@echo off
REM 获取脚本所在目录
set SCRIPT_DIR=%~dp0

REM 切换到项目根目录（xbgame目录）
cd /d "%SCRIPT_DIR%..\"

mvn clean dependency:copy-dependencies package -pl login -am
pause