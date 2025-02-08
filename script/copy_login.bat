@echo off
REM 获取脚本所在目录
set SCRIPT_DIR=%~dp0

REM 拼接目标文件的绝对路径
set TARGET_FILE=%SCRIPT_DIR%..\login\target\login-0.0.1-SNAPSHOT.jar

REM 执行 SCP 命令
scp -i C:\Users\xubin\.ssh\scp_key "%TARGET_FILE%" root@192.168.101.80:/data/java-game/login

pause