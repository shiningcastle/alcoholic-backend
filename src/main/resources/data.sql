INSERT INTO member(id, password, nickname, email, image, role, provider, created_date, password_updated_date) VALUES ("tester", "pw", "심심한 심심이", "tester@test.com", "image", "USER", "LOCAL", '2022-01-01', '2022-01-01');

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



