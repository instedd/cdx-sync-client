CRCCheck On

!define MUI_PRODUCT "CDX Client"
!define APP_NAME    "cdx"
!define EXE_NAME    "${APP_NAME}-client.exe"
!define PROPS_FILE  "cdxsync.properties"

;--------------------------------
;Plugins dir
!addplugindir nsis
;--------------------------------
;Includes dir
!addincludedir nsis

;--------------------------------
;Include Modern UI
!include "MUI2.nsh"
!include "UAC.nsh"
!include "FileFunc.nsh"

;---------------------------------
;General

Name "${MUI_PRODUCT}"
BrandingText "${MUI_PRODUCT} v${VERSION}"

OutFile "${APP_NAME}-${PLATFORM}-${VERSION}.exe"

RequestExecutionLevel user

ShowInstDetails "nevershow"
ShowUninstDetails "nevershow"

!define APP_REGKEY "Software\${APP_NAME}\Client"
!define INSTALL_DIR_REGNAME "InstallDir"
!define IS_INSTALLED_REGNAME "IsInstalled"

!ifdef ON64BITS
  InstallDir "$PROGRAMFILES64\${APP_NAME}\Client"
!else
  InstallDir "$PROGRAMFILES\${APP_NAME}\Client"
!endif

!define START_MENU_GROUP "${MUI_PRODUCT}"

;Get installation folder from registry if available
InstallDirRegKey HKLM "${APP_REGKEY}" "${INSTALL_DIR_REGNAME}"

!define MUI_FINISHPAGE_RUN
!define MUI_FINISHPAGE_RUN_TEXT "Launch ${MUI_PRODUCT}"
!define MUI_FINISHPAGE_RUN_FUNCTION StartApp

;--------------------------------
;Exe details

VIProductVersion                 "${VIVERSION}"
VIAddVersionKey ProductName      "CDX Client"
VIAddVersionKey Comments         "Connected Diagnostics Platform"
VIAddVersionKey CompanyName      "CDX"
VIAddVersionKey LegalCopyright   "CDX"
VIAddVersionKey FileDescription  "Connected Diagnostics Platform Client"
VIAddVersionKey FileVersion      "${VERSION}"
VIAddVersionKey ProductVersion   "${VERSION}"
VIAddVersionKey InternalName     "CDXClient"
VIAddVersionKey LegalTrademarks  "CDX"

;--------------------------------
;Interface Settings

!define MUI_ABORTWARNING

;--------------------------------
;Pages

!insertmacro MUI_PAGE_LICENSE "license.txt"
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH

SpaceTexts none
!define MUI_COMPONENTSPAGE_NODESC
!insertmacro MUI_UNPAGE_COMPONENTS
!insertmacro MUI_UNPAGE_INSTFILES

;--------------------------------
;Languages

!insertmacro MUI_LANGUAGE "English"

;--------------------------------
;Macros

!macro UacInit thing
uac_tryagain:
!insertmacro UAC_RunElevated
${Switch} $0
${Case} 0
	${IfThen} $1 = 1 ${|} Quit ${|} ;we are the outer process, the inner process has done its work, we are done
	${IfThen} $3 <> 0 ${|} ${Break} ${|} ;we are admin, let the show go on
	${If} $1 = 3 ;RunAs completed successfully, but with a non-admin user
		MessageBox mb_YesNo|mb_IconExclamation|mb_TopMost|mb_SetForeground "This ${thing} requires admin privileges, try again" /SD IDNO IDYES uac_tryagain IDNO 0
	${EndIf}
	;fall-through and die
${Case} 1223
	MessageBox mb_IconStop|mb_TopMost|mb_SetForeground "This ${thing} requires admin privileges, aborting!"
	Quit
${Case} 1062
	MessageBox mb_IconStop|mb_TopMost|mb_SetForeground "Logon service not running, aborting!"
	Quit
${Default}
	MessageBox mb_IconStop|mb_TopMost|mb_SetForeground "Unable to elevate, error $0"
	Quit
${EndSwitch}

SetShellVarContext all
!macroend

;--------------------------------
;Functions

Function StartApp
  !insertmacro UAC_AsUser_ExecShell "" "$INSTDIR\bin\${EXE_NAME}" "" "" ""
FunctionEnd

Function .onInit
  !insertmacro UacInit "installer"
FunctionEnd

Function un.onInit
  !insertmacro UacInit "uninstaller"
FunctionEnd

Function RelaunchAppIfNeeded
  ${GetOptions} $CMDLINE "/launch" $0
  StrCmp $0 "App" reopen no_reopen
reopen:
  Call StartApp
no_reopen:
FunctionEnd

Function .onInstSuccess
  Call RelaunchAppIfNeeded
FunctionEnd

Function .onInstFailed
  Call RelaunchAppIfNeeded
FunctionEnd

;--------------------------------
;Installer Sections

Section "Install" Install
  SetOutPath "$INSTDIR"
  File "license.txt"
  File "${PROPS_FILE}"

  ;Add files
  SetOutPath "$INSTDIR\bin"
  File "bin\RCEDIT.exe"
  File /oname=${EXE_NAME} "bin\WinRun4J.exe"
  File "bin\${APP_NAME}-client.ini"
  SetOutPath "$INSTDIR\lib"
  File "lib\*.jar"

  SetOutPath "$INSTDIR\jre"
  File /r "jre1.8.0_31\*.*"

; TODO: extract to a $INSTDIR\rsync subdir
  SetOutPath "$INSTDIR"
  File /r "cwRsync\*.*"

  WriteUninstaller "$INSTDIR\Uninstall.exe"
SectionEnd

Section "Build Launcher"
  DetailPrint "Building launcher..."
  SetOutPath "$INSTDIR\bin"
  ExecDos::exec 'RCEDIT.exe /N ${EXE_NAME} ${APP_NAME}-client.ini' "" "$TEMP/${APP_NAME}-installer.log"
  Delete "${APP_NAME}-client.ini"
  Delete "rcedit.exe"
SectionEnd

Section "Create Shortcuts"
  SetOutPath "$INSTDIR"

  ;create desktop shortcut
  CreateShortCut "$DESKTOP\${MUI_PRODUCT}.lnk" "$INSTDIR\bin\${EXE_NAME}" "${PROPS_FILE}"

  ;create start-menu items
  CreateDirectory "$SMPROGRAMS\${START_MENU_GROUP}"
  CreateShortCut "$SMPROGRAMS\${START_MENU_GROUP}\Uninstall.lnk" "$INSTDIR\Uninstall.exe" "" "$INSTDIR\Uninstall.exe" 0
  CreateShortCut "$SMPROGRAMS\${START_MENU_GROUP}\${MUI_PRODUCT}.lnk" "$INSTDIR\bin\${EXE_NAME}" "${PROPS_FILE}" "$INSTDIR\bin\${EXE_NAME}" 0

;write uninstall information to the registry
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${MUI_PRODUCT}" "DisplayName" "${MUI_PRODUCT}"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${MUI_PRODUCT}" "UninstallString" "$INSTDIR\Uninstall.exe"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${MUI_PRODUCT}" "Publisher" "CDX"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${MUI_PRODUCT}" "DisplayVersion" "${VERSION}"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${MUI_PRODUCT}" "DisplayIcon" "$INSTDIR\Uninstall.exe"

  ;Store a flag when installed
  WriteRegDWORD HKLM "${APP_REGKEY}" "${IS_INSTALLED_REGNAME}" 0x1

  ;Store installation folder
  WriteRegStr HKLM "${APP_REGKEY}" "${INSTALL_DIR_REGNAME}" $INSTDIR

SectionEnd

;--------------------------------
;Uninstaller Sections

Section "un.Remove CDX Client"
  SectionIn RO

;Delete Files
  RMDir /r "$INSTDIR\bin\*.*"
  RMDir /r "$INSTDIR\lib\*.*"
  RMDir /r "$INSTDIR\jre\*.*"
  Delete "$INSTDIR\license.txt"

;Delete Start Menu Shortcuts
  Delete "$DESKTOP\${MUI_PRODUCT}.lnk"
  Delete "$SMPROGRAMS\${START_MENU_GROUP}\*.*"
  RmDir  "$SMPROGRAMS\${START_MENU_GROUP}"

;Delete Uninstaller And Unistall Registry Entries
  DeleteRegKey HKEY_LOCAL_MACHINE "SOFTWARE\${MUI_PRODUCT}"
  DeleteRegKey HKEY_LOCAL_MACHINE "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\${MUI_PRODUCT}"

  Delete "$INSTDIR\Uninstall.exe"

  DeleteRegValue HKLM "${APP_REGKEY}" "${IS_INSTALLED_REGNAME}"

  DeleteRegKey /ifempty HKLM "${APP_REGKEY}"

  RMDir /r "$INSTDIR"
SectionEnd

Section "Run client at start"
  WriteRegStr HKEY_CURRENT_USER "Software\Microsoft\Windows\CurrentVersion\Run" "${APP_NAME}-client" '"$INSTDIR\bin\${EXE_NAME}" ${PROPS_FILE}'
SectionEnd
