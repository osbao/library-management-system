@echo off
chcp 65001 >nul
title 图书管理系统 - 停止服务

echo.
echo ╔══════════════════════════════════════════════╗
echo ║       图书管理系统 - 停止所有服务              ║
echo ╚══════════════════════════════════════════════╝
echo.

echo 正在停止服务...

:: 关闭占用 9090 端口的进程（后端）
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":9090" ^| findstr "LISTENING"') do (
    echo   停止后端服务 (PID: %%a)...
    taskkill /F /PID %%a >nul 2>&1
)

:: 关闭占用 8080 端口的进程（前端）
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8080" ^| findstr "LISTENING"') do (
    echo   停止前端服务 (PID: %%a)...
    taskkill /F /PID %%a >nul 2>&1
)

:: 关闭 npm/node 相关进程
echo   清理残留的 Node.js 进程...
taskkill /F /IM "node.exe" >nul 2>&1

:: 关闭 Java 相关进程
echo   清理残留的 Java 进程...
taskkill /F /IM "java.exe" >nul 2>&1

:: 关闭标题匹配的 cmd 窗口
echo   关闭后端窗口...
taskkill /FI "WINDOWTITLE eq 图书管理系统-后端" /F >nul 2>&1

echo   关闭前端窗口...
taskkill /FI "WINDOWTITLE eq 图书管理系统-前端*" /F >nul 2>&1

echo.
echo ╔══════════════════════════════════════════════╗
echo ║           所有服务已停止！                     ║
echo ╚══════════════════════════════════════════════╝
echo.

pause