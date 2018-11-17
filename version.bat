@echo on
@echo =============================================================
@echo $                                                           $
@echo $                      rabbitmq-plugin                      $
@echo $                                                           $
@echo =============================================================
@echo.
@echo off

@title rabbitmq-plugin version update
@color 0a

rem  Please execute command in local directory.

call mvn -N versions:update-child-modules
call mvn versions:set -DnewVersion=1.0.0
call mvn versions:commit

pause