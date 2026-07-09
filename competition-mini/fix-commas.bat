@echo off
chcp 65001 >nul
cd /d "%~dp0"
echo 正在修复 pages.json 中缺失的逗号...

powershell -NoProfile -Command ^
  "$f='pages.json'; $c=Get-Content $f -Raw; ^
   $c=$c -replace '(\""navigationBarTextStyle\"":\s*\""white\"")\s*\n\s*(\""enablePullDownRefresh\"":\s*true)', '$1,`n        $2'; ^
   [System.IO.File]::WriteAllText((Resolve-Path $f).Path, $c, [System.Text.UTF8Encoding]::new($false)); ^
   Write-Host '逗号修复完成!'; ^
   try { $null=$c | ConvertFrom-Json; Write-Host 'JSON验证通过!' -ForegroundColor Green } catch { Write-Host ('JSON错误: '+$_.Exception.Message) -ForegroundColor Red }"

pause
