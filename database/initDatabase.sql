drop database if exists user_info;

create DATABASE user_info;

use user_info;

CREATE TABLE user_info (
                           id varchar(128) NOT NULL UNIQUE PRIMARY KEY,
                           username VARCHAR(32) UNIQUE NOT NULL,
                           name VARCHAR(32) NOT NULL,
                           surname VARCHAR(32) NOT NULL,
                           email VARCHAR(32) UNIQUE NOT NULL
);