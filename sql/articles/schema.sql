CREATE TABLE articles
(
    article_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT,
    content       VARCHAR(255),
    event         VARCHAR(255),
    thought       VARCHAR(255),
    emotion       VARCHAR(255),
    result        VARCHAR(255),
    satisfaction  FLOAT,
    reason        VARCHAR(255),
    improvements  VARCHAR(255),
    ai_comment    VARCHAR(255),
    amount        INT,
    register_type VARCHAR(255),
    created_date    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    edited_date    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
