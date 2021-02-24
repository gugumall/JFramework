alter table j_log add column UPD_EVENT_TIME datetime;
alter table j_log add column UPD_EVENT_CODE varchar(32);
alter table j_log add column UPD_EVENT_DATA text;
alter table j_log add column UPD_EVENT_INFLUENCE text;
alter table j_log add column UPD_EVENT_STAT varchar(8);
alter table j_log add column UPD_STAFF_ID varchar(128);
alter table j_log add column UPD_SELLER_ID varchar(128);
alter table j_log add column UPD_STAFF_ID_OF_SHOP varchar(128);