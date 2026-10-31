param(
    [Parameter(Mandatory = $true)]
    [ValidateSet('baseline', 'after', 'exploration')]
    [string] $Stage,

    [Parameter(Mandatory = $true)]
    [ValidateNotNullOrEmpty()]
    [string] $Scenario,

    [Parameter(Mandatory = $true)]
    [ValidateNotNullOrEmpty()]
    [string] $ProfileReference,

    [string] $Hotspot = 'untriaged',
    [string] $Environment = '',
    [string] $Notes = '',
    [string] $IndexPath = ''
)

$ErrorActionPreference = 'Stop'
$repositoryRoot = Split-Path -Parent $PSScriptRoot
$profileIndex = if ([string]::IsNullOrWhiteSpace($IndexPath)) {
    Join-Path $repositoryRoot 'profiles\spark\index.csv'
} elseif ([IO.Path]::IsPathRooted($IndexPath)) {
    $IndexPath
} else {
    Join-Path $repositoryRoot $IndexPath
}
$referenceUri = $null

if ($ProfileReference -match '^[A-Za-z][A-Za-z0-9+.-]*://') {
    if ($ProfileReference -notmatch '^https?://') {
        throw 'ProfileReference URLs must use HTTP or HTTPS.'
    }
} else {
    $resolvedProfile = Resolve-Path -LiteralPath $ProfileReference -ErrorAction Stop
    if (-not (Test-Path -LiteralPath $resolvedProfile.Path -PathType Leaf)) {
        throw 'Local ProfileReference must point to a file.'
    }
    $ProfileReference = $resolvedProfile.Path
}

$indexDirectory = Split-Path -Parent $profileIndex
New-Item -ItemType Directory -Path $indexDirectory -Force | Out-Null

$entry = [pscustomobject] @{
    capturedAt = (Get-Date).ToUniversalTime().ToString('yyyy-MM-ddTHH:mm:ssZ')
    stage = $Stage
    scenario = $Scenario
    hotspot = $Hotspot
    profileReference = $ProfileReference
    environment = $Environment
    notes = $Notes
}

$entry | Export-Csv -LiteralPath $profileIndex -NoTypeInformation -Append -Encoding UTF8
Write-Host "Spark profile reference saved to $profileIndex"
