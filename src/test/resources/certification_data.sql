DELETE FROM certification_code;
INSERT INTO certification_code (email, code, expired_at)
VALUES
    ('test@naver.com', 'ABCDEF', '2050-02-02 00:00:00'),
    ('expired@naver.com', 'EXPIRE', '2020-01-01 00:00:00');