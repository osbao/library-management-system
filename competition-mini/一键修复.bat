@echo off
chcp 65001 >nul
cd /d "%~dp0"
echo ========================================
echo   竞伙伴小程序 - 一键修复&amp;初始化
echo ========================================
echo.

echo [1/3] 修复 pages.json 逗号问题...
powershell -NoProfile -Command ^
  "$f='pages.json'; $c=[IO.File]::ReadAllText($f); ^
   $c=$c -replace '(\""navigationBarTextStyle\"":\s*\""white\"")\s*\r?\n(\s*\""enablePullDownRefresh\"":\s*true)', '$1,`r`n$2'; ^
   $c=$c -replace '(\""navigationStyle\"":\s*\""custom\""[\s\S]*?\}\s*\n)\s*\}[^,]\s*\n(\s*\},)', '$1    },'; ^
   [IO.File]::WriteAllText($f,$c); ^
   try { $null=$c|ConvertFrom-Json; Write-Host '  [OK] JSON验证通过' -ForegroundColor Green } catch { Write-Host '  [警告] 请手动替换pages-fixed.json' -ForegroundColor Yellow }"
echo.

echo [2/3] 生成占位图标...
if exist "node_modules" (
    node scripts\generate-icons.js
) else (
    echo   跳过（需先 npm install）
)
echo.

echo [3/3] 安装依赖...
if exist "package.json" (
    if not exist "node_modules" (
        echo   正在安装 npm 依赖...
        call npm install
    ) else (
        echo   依赖已安装
    )
)
echo.
echo ========================================
echo   修复完成！
echo   用 HBuilderX 打开项目即可运行
echo ========================================
pause
