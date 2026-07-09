@echo off
chcp 65001 >nul
cd /d "%~dp0"
echo 正在修复 pages.json ...

powershell -Command ^
  "$content = Get-Content 'pages.json' -Raw; ^
   $content = $content -replace '(\""navigationStyle\"":\s*\""custom\""\s*\n\s*\}\s*\n)\s*\}\s*\n(\s*\},\s*\n\s*\{)', '$1    },`n    {'; ^
   Set-Content 'pages.json' -Value $content -NoNewline; ^
   Write-Host '修复完成!'"

echo.
echo 验证 JSON 格式...
powershell -Command ^
  "try { $null = Get-Content 'pages.json' -Raw | ConvertFrom-Json; Write-Host 'JSON 格式验证通过!' -ForegroundColor Green } catch { Write-Host ('错误: ' + $_.Exception.Message) -ForegroundColor Red }"

pause
