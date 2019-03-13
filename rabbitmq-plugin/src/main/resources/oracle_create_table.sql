create table message_commit_log
(
  id                VARCHAR2(32 CHAR) primary key,
  status            VARCHAR2(2 CHAR),
  create_time       TIMESTAMP(3),
  update_time       TIMESTAMP(3)
)