-- 모든 제약 조건 비활성화
SET REFERENTIAL_INTEGRITY FALSE;
truncate table user_tb;
truncate table rate;
truncate table category;
truncate table board;
truncate table event;
truncate table tag;
truncate table participant;
truncate table payment;
truncate table wishlist;
truncate table my_category;
truncate table my_location;
truncate table blacklist;
truncate table report;
truncate table review;
truncate table my_account;
truncate table payment_info;
SET REFERENTIAL_INTEGRITY TRUE;
-- 모든 제약 조건 활성화