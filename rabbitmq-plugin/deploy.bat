@echo on
@echo =============================================================
@echo $                                                           $
@echo $                      rabbitmq-plugin                      $
@echo $                                                           $
@echo =============================================================
@echo.
@echo off

@title rabbitmq-plugin deploy
@color 0a

rem  Please execute command in local directory.

call mvn clean deploy -DskipTests -P release

pause

pause