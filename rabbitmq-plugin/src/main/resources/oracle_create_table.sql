create table MESSAGE_COMMIT_LOG
(
  id          VARCHAR2(32 CHAR) not null,
  status      VARCHAR2(2 CHAR),
  create_time TIMESTAMP(3),
  update_time TIMESTAMP(3),
  max_retry   NUMBER(10) default 0,
  version     NUMBER(10) default 0,
  retry_count NUMBER(10) default 0,
  body        VARCHAR2(512 CHAR)
)