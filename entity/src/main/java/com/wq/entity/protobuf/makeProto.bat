@echo off
FOR /D %%s IN (.,*) DO (
  IF EXIST %%s\ (
    IF NOT %%~ns==proto (
      IF NOT %%~ns==protobuf (
        RD /S/Q %%s
      )
    )
  )
)
FOR /R %~dp0 %%i IN (*.proto) DO (
    %~dp0protoc --proto_path=%~dp0 --java_out=../../../../ %%i
)
pause
