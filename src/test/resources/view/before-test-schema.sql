CREATE VIEW `users_mbti_view` AS
    select `users`.`user_id` AS `user_id`,
           substr(`users`.`mbti`,1,1) AS `ei`,
           substr(`users`.`mbti`,2,1) AS `ns`,
           substr(`users`.`mbti`,3,1) AS `tf`,
           substr(`users`.`mbti`,4,1) AS `pj`
    from `users`
    where (`users`.`mbti` <> 'none');