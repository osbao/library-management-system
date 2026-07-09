@echo off
chcp 65001 >nul
title 图书管理系统 - 启动中...

echo.
echo ╔══════════════════════════════════════════════╗
echo ║       图书管理系统 - 一键启动脚本              ║
echo ╚══════════════════════════════════════════════╝
echo.

:: ============ 1. 检查 Node.js 环境 ============
echo [1/4] 检查 Node.js 环境...
node --version >nul 2>&1
if %errorlevel% neq 0 (
    echo   [错误] 未找到 Node.js，请安装 Node.js！
    echo   下载地址：https://nodejs.org/
    pause
    exit /b 1
)
for /f "tokens=*" %%i in ('node --version') do set NODE_VER=%%i
echo   [成功] Node.js 已就绪 (版本: %NODE_VER%)

:: ============ 2. 安装前端依赖（如需要）============
echo [2/4] 检查前端依赖...
if not exist "node_modules\" (
    echo   正在安装前端依赖（首次运行，可能需要几分钟）...
    call npm install
    if %errorlevel% neq 0 (
        echo   [错误] 前端依赖安装失败！
        pause
        exit /b 1
    )
    echo   [成功] 前端依赖安装完成
) else (
    echo   [成功] 前端依赖已存在
)

:: ============ 3. 安装模拟后端依赖（如需要）============
echo [3/4] 检查后端依赖...
if not exist "mock-server\node_modules\" (
    echo   正在安装后端依赖（首次运行）...
    cd /d "%~dp0mock-server"
    call npm install
    cd /d "%~dp0"
    if %errorlevel% neq 0 (
        echo   [错误] 后端依赖安装失败！
        pause
        exit /b 1
    )
    echo   [成功] 后端依赖安装完成
) else (
    echo   [成功] 后端依赖已存在
)

:: ============ 4. 启动服务 ============
echo [4/4] 启动服务...

:: ---- 启动模拟后端 (Node.js, 端口 9090) ----
echo   正在启动后端服务 (端口 9090，Node.js 模拟后端)...
cd /d "%~dp0mock-server"
start "图书管理系统-后端" cmd /c "node server.js"
cd /d "%~dp0"

:: 等待后端启动
echo   等待后端服务启动中...
set /a COUNT=0
:wait_backend
timeout /t 2 >nul
set /a COUNT+=1
curl -s http://localhost:9090 >nul 2>&1
if %errorlevel% equ 0 goto backend_ready
if %COUNT% geq 15 (
    echo   [提示] 后端可能需要更多时间，继续启动前端...
    goto start_frontend
)
goto wait_backend

:backend_ready
echo   [成功] 后端服务已启动 (http://localhost:9090)

:start_frontend
:: ---- 启动前端 (Vue, 端口 8080) ----
echo   正在启动前端服务 (端口 8080)...
start "图书管理系统-前端" cmd /c "npm run serve"

:: 等待前端启动
echo   等待前端服务启动中...
set /a COUNT=0
:wait_frontend
timeout /t 2 >nul
set /a COUNT+=1
curl -s http://localhost:8080 >nul 2>&1
if %errorlevel% equ 0 goto frontend_ready
if %COUNT% geq 30 (
    echo   [提示] 前端启动超时，正在打开浏览器...
    goto open_browser
)
goto wait_frontend

:frontend_ready
echo   [成功] 前端服务已启动 (http://localhost:8080)

:open_browser
:: ---- 打开浏览器 ----
echo   正在打开浏览器...
start http://localhost:8080

echo.
echo ╔══════════════════════════════════════════════╗
echo ║           系统启动完成！                       ║
echo ║                                               ║
echo ║   前端地址：http://localhost:8080              ║
echo ║   后端地址：http://localhost:9090              ║
echo ║                                               ║
echo ║   【演示账号】                                 ║
echo ║   管理员：admin / 123456                       ║
echo ║   用  户：user1 / 123456                        ║
echo ║                                               ║
echo ║   双击 stop.bat 可一键停止所有服务             ║
echo ╚══════════════════════════════════════════════╝
echo.

pause