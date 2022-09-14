INSERT INTO member(id, password, nickname, email, image, role, provider, created_date, password_updated_date) VALUES ("test1234", "$2a$10$B8jdYtd2hVsiJBXj7WO2geSDHtnFfzGCFdw.xa9aZ/XXjT9N100/a", "심심한 심심이", "tester@test.com", "image", "USER", "LOCAL", '2022-01-01', '2022-01-01');

INSERT INTO board_category(seq, name) VALUES (1, "feed");
INSERT INTO board_category(seq, name) VALUES (2, "wiki");
INSERT INTO board_category(seq, name) VALUES (3, "qna");


INSERT INTO board(seq, title, content, member_seq, board_category_seq) values (1, "title입니다.1", "content입니다.", 1, 1);
INSERT INTO board(seq, title, content, member_seq, board_category_seq) values (2, "title입니다.2", "content입니다.", 1, 2);
INSERT INTO board(seq, title, content, member_seq, board_category_seq) values (3, "title입니다.3", "content입니다.", 1, 1);
INSERT INTO board(seq, title, content, member_seq, board_category_seq) values (4, "title입니다.4", "content입니다.", 1, 3);
INSERT INTO board(seq, title, content, member_seq, board_category_seq) values (5, "title입니다.5", "content입니다.", 1, 1);
INSERT INTO board(seq, title, content, member_seq, board_category_seq) values (6, "title입니다.6", "content입니다.", 1, 3);
INSERT INTO board(seq, title, content, member_seq, board_category_seq) values (7, "title입니다.7", "content입니다.", 1, 1);
INSERT INTO board(seq, title, content, member_seq, board_category_seq) values (8, "title입니다.8", "content입니다.", 1, 2);
INSERT INTO board(seq, title, content, member_seq, board_category_seq) values (9, "title입니다.9", "content입니다.", 1, 1);
INSERT INTO board(seq, title, content, member_seq, board_category_seq) values (10, "title입니다.10", "content입니다.", 1, 1);

INSERT INTO nickname(seq, adjective, noun) values (1, "심심한", "심심이");
INSERT INTO nickname(seq, adjective, noun) values (2, "배고픈", "철수");
INSERT INTO nickname(seq, adjective, noun) values (3, "졸린", "영수");
INSERT INTO nickname(seq, adjective, noun) values (4, "궁금한", "영희");
INSERT INTO nickname(seq, adjective, noun) values (5, "재밌는", "퉁퉁이");
INSERT INTO nickname(seq, adjective, noun) values (6, "재미없는", "돼지");



INSERT INTO reply (seq, content, is_root, reply_parent, created_date, board_seq, member_seq) values (1, "댓글 솰라", True, 1, "2022-01-01", 1, 1);
INSERT INTO reply (seq, content, is_root, reply_parent, created_date, board_seq, member_seq) values (2, "댓글 솰라", True, 2, "2022-01-02", 1, 1);
INSERT INTO reply (seq, content, is_root, reply_parent, created_date, board_seq, member_seq) values (3, "댓글 솰라", True, 3, "2022-01-03", 1, 1);
INSERT INTO reply (seq, content, is_root, reply_parent, created_date, board_seq, member_seq) values (4, "댓글 솰라", False, 1, "2022-01-04", 1, 1);
INSERT INTO reply (seq, content, is_root, reply_parent, created_date, board_seq, member_seq) values (5, "댓글 솰라", False, 2, "2022-01-05", 1, 1);
INSERT INTO reply (seq, content, is_root, reply_parent, created_date, board_seq, member_seq) values (6, "댓글 솰라", False, 2, "2022-01-06", 1, 1);

