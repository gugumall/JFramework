alter table j_log add column STAFF_ID2 varchar(128) after STAFF_ID_OF_SHOP;
alter table j_log add column SELLER_ID2 varchar(128) after STAFF_ID2;
alter table j_log add column STAFF_ID_OF_SHOP2 varchar(128) after SELLER_ID2;