# Auto-Pull Watcher for Jenkins-generated tests
# This script watches the GitHub repo and pulls new commits automatically
# 
# Usage: Run this script in PowerShell, it will check every 30 seconds for new commits

param(
    [int]$IntervalSeconds = 30
)

$projectPath = "C:\Users\Stayha\Desktop\test android"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Auto-Pull Watcher Started" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Project: $projectPath"
Write-Host "Checking every $IntervalSeconds seconds for new commits..."
Write-Host "Press Ctrl+C to stop"
Write-Host ""

Set-Location $projectPath

# Get initial commit hash
$lastCommit = git rev-parse HEAD 2>$null
Write-Host "Current commit: $lastCommit" -ForegroundColor Gray

while ($true) {
    try {
        # Fetch from remote (silent)
        git fetch origin main 2>$null | Out-Null
        
        # Check if there are new commits
        $remoteCommit = git rev-parse origin/main 2>$null
        
        if ($remoteCommit -ne $lastCommit) {
            Write-Host ""
            Write-Host "[$(Get-Date -Format 'HH:mm:ss')] New commits detected!" -ForegroundColor Green
            
            # Pull the changes
            $pullResult = git pull origin main 2>&1
            Write-Host $pullResult -ForegroundColor Yellow
            
            # Show what files changed
            $changedFiles = git diff --name-only $lastCommit $remoteCommit 2>$null
            if ($changedFiles) {
                Write-Host ""
                Write-Host "Files updated:" -ForegroundColor Cyan
                $changedFiles | ForEach-Object { Write-Host "  + $_" -ForegroundColor Green }
            }
            
            $lastCommit = $remoteCommit
            Write-Host ""
            Write-Host "[$(Get-Date -Format 'HH:mm:ss')] Pull complete. Watching for more changes..." -ForegroundColor Gray
        }
        else {
            Write-Host "." -NoNewline -ForegroundColor DarkGray
        }
    }
    catch {
        Write-Host ""
        Write-Host "[$(Get-Date -Format 'HH:mm:ss')] Error: $($_.Exception.Message)" -ForegroundColor Red
    }
    
    Start-Sleep -Seconds $IntervalSeconds
}
