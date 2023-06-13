insert into user_tb(username, password, email, profile, rate_id, role, provider, provider_id, status_code, created_at) values('ssar@naver.com', '$2a$10$.qQ7kvdAo1qtk75HkCUG4O/5SKE0Y6Acf9.ZaXvTCxMZipSEC9lTW', 'ssar@naver.com', null, 1,  'ROLE_USER', '', '', 100, now());
insert into user_tb(username, password, email, profile, rate_id, role, provider, provider_id, status_code, created_at) values('naver_1', '$2a$10$.qQ7kvdAo1qtk75HkCUG4O/5SKE0Y6Acf9.ZaXvTCxMZipSEC9lTW', 'zzxc@naver.com', null, 2, 'ROLE_USER', 'naver', '1', 100, now());
insert into user_tb(username, password, email, profile, rate_id, role, provider, provider_id, status_code, created_at) values('admin@naver.com', '$2a$10$.qQ7kvdAo1qtk75HkCUG4O/5SKE0Y6Acf9.ZaXvTCxMZipSEC9lTW', 'admin@naver.com', null, 2, 'ROLE_ADMIN', '', '', 100, now());

insert into rate(rate_name, created_at) values('글레이즈드', now());
insert into rate(rate_name, created_at) values('딸기도넛', now());
insert into rate(rate_name, created_at) values('초코도넛', now());
insert into rate(rate_name, created_at) values('별사탕도넛', now());

insert into my_location(user_id, state, city, town, created_at) values(1, '부산광역시', '부산진구', '부전동', now());

insert into board(category_id, title, organizer_id, content, img, event_id, status_code, views, recommend, state, city, town, created_at) values(3, '삼각김밥 1+1 같이 사실분?', 1, '서면 1번출구 편의점에서 삼각김밥 같이 사실분 구합니다. 직거래 우선이요', 'https://groupbuying.s3.ap-northeast-2.amazonaws.com/%ED%8E%B8%EC%9D%98%EC%A0%90%EB%94%94%ED%8F%B4%ED%8A%B8.jpg', 1, 200, 50, 0, '부산광역시', '부산진구', '부전동', now());
insert into board(category_id, title, organizer_id, content, img, event_id, status_code, views, recommend, state, city, town, created_at) values(3, '편의점 할인 행사 같이 사실분', 1, '서면 1번출구 편의점에서 같이 사실분 구합니다. 직거래 우선이요', 'https://groupbuying.s3.ap-northeast-2.amazonaws.com/%ED%8E%B8%EC%9D%98%EC%A0%90%EB%94%94%ED%8F%B4%ED%8A%B8.jpg', 2, 200, 50, 0, '부산광역시', '부산진구', '부전동', now());

insert into event(latitude, longtitude, qty, payment_type, start_at, end_at, price, created_at) values(35.156834, 129.058987, 1, '직거래', now(), now(), 900, now());
insert into event(latitude, longtitude, qty, payment_type, start_at, end_at, price, created_at) values(35.156834, 129.058987, 1, '직거래', now(), now(), 900, now());

insert into tag(board_id, comment, created_at) values(1, '편의점', now());
insert into tag(board_id, comment, created_at) values(1, '1+1', now());

insert into participant(event_id, user_id, qty, created_at, status_code) values(1, 2, 1, now(), 300);
insert into participant(event_id, user_id, qty, created_at, status_code) values(1, 3, 1, now(), 300);

insert into payment(event_id, user_id, payment_info_id, payment_type, status_code, confirmed, created_at) values(1, 1, 1, '직거래', 401, 0, now());

insert into wishlist(user_id, board_id, created_at) values(1, 1, now());

insert into my_category(user_id, category_id, created_at) values(1, 1, now());
insert into my_category(user_id, category_id, created_at) values(1, 2, now());
insert into my_category(user_id, category_id, created_at) values(1, 3, now());
insert into my_category(user_id, category_id, created_at) values(1, 4, now());

insert into report(reporter_id, reported_id, board_id, title, content, report_type, status_code, created_at) values(2, 1, 1, '노쇼했어요', '삼각김밥 1+1 하자는데 어디에있는지 연락도 안되고 노쇼 했어요', '뇨쇼', 600, now());

insert into blacklist(user_id, blocked_user_id, created_at) values(1, 2, now());

insert into chatroom(event_id, status_code, created_at) values(1, 500, now());

insert into review(reviewer_id, reviewed_id, score, comment, created_at) values(2, 1, 5, '빠른 거래 좋아요', now());

insert into category(name, created_at) values('생활가전', now());
insert into category(name, created_at) values('스포츠', now());
insert into category(name, created_at) values('잡화', now());
insert into category(name, created_at) values('유아물품', now());
insert into category(name, created_at) values('의류', now());
insert into category(name, created_at) values('식품', now());
insert into category(name, created_at) values('미용', now());
insert into category(name, created_at) values('반려동물 물품', now());
insert into category(name, created_at) values('식물', now());
insert into category(name, created_at) values('문구', now());
insert into category(name, created_at) values('티켓 및 교환권', now());
insert into category(name, created_at) values('편의점', now());
insert into category(name, created_at) values('홈쇼핑', now());
insert into category(name, created_at) values('도매상', now());

commit;