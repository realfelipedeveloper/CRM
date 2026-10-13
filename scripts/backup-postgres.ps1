param(
  [string]$Destino = ".\backups"
)
$ErrorActionPreference = "Stop"
New-Item -ItemType Directory -Force -Path $Destino | Out-Null
$arquivo = Join-Path $Destino ("crm-{0}.dump" -f (Get-Date -Format "yyyyMMdd-HHmmss"))
docker compose exec -T postgres pg_dump -U crm -d crm -Fc > $arquivo
if ((Get-Item $arquivo).Length -eq 0) { throw "O backup foi criado sem conteúdo." }
Write-Output "Backup validado em $arquivo"

