/*
Navicat MySQL Result Transfer
Source Server         : localhost
Source Server Version : 50719
Source Host           : localhost:3306
Source Database       : genhub
Target Server Type    : MYSQL
Target Server Version : 50719
File Encoding         : 65001
Date: 2020-01-18 22:17:57
*/

/* SET FOREIGN_KEY_CHECKS=0; */

-- ----------------------------
-- Table structure for mto_channel
-- ----------------------------
DROP TABLE IF EXISTS mto_channel;
CREATE SEQUENCE mto_channel_id_seq;

CREATE TABLE mto_channel (
  id int NOT NULL DEFAULT NEXTVAL ('mto_channel_id_seq'),
  key_ varchar(32) DEFAULT NULL,
  name varchar(32) DEFAULT NULL,
  status int NOT NULL,
  thumbnail varchar(128) DEFAULT NULL,
  weight int NOT NULL,
  PRIMARY KEY (id)
)  ;

ALTER SEQUENCE mto_channel_id_seq RESTART WITH 5;

-- ----------------------------
-- Records of mto_channel
-- ----------------------------
INSERT INTO mto_channel VALUES ('1', 'banner', 'banner', '1', '', '3');
INSERT INTO mto_channel VALUES ('2', 'blog', 'Blog', '0', '', '2');
INSERT INTO mto_channel VALUES ('3', 'jotting', 'Essay', '0', '', '1');
INSERT INTO mto_channel VALUES ('4', 'share', 'share it', '0', '', '0');

-- ----------------------------
-- Table structure for mto_options
-- ----------------------------
DROP TABLE IF EXISTS mto_options;
CREATE SEQUENCE mto_options_id_seq;

CREATE TABLE mto_options (
  id bigint NOT NULL DEFAULT NEXTVAL ('mto_options_id_seq'),
  key_ varchar(32) DEFAULT NULL,
  type int DEFAULT 0,
  value varchar(300) DEFAULT NULL,
  PRIMARY KEY (id)
)  ;

ALTER SEQUENCE mto_options_id_seq RESTART WITH 17;

-- ----------------------------
-- Records of mto_options
-- ----------------------------
INSERT INTO mto_options VALUES ('1', 'site_name', '0', 'GenHub');
INSERT INTO mto_options VALUES ('2', 'site_domain', '0', 'genhub.online');
INSERT INTO mto_options VALUES ('3', 'site_keywords', '0', 'Genhub,Blog, community');
INSERT INTO mto_options VALUES ('4', 'site_description', '0', 'Genhub, Be a meaningful technical community');
INSERT INTO mto_options VALUES ('5', 'site_metas', '0', '');
INSERT INTO mto_options VALUES ('6', 'site_copyright', '0', 'Copyright © Genhub');
INSERT INTO mto_options VALUES ('7', 'site_icp', '0', '');
INSERT INTO mto_options VALUES ('8', 'qq_callback', '0', '');
INSERT INTO mto_options VALUES ('9', 'qq_app_id', '0', '');
INSERT INTO mto_options VALUES ('10', 'qq_app_key', '0', '');
INSERT INTO mto_options VALUES ('11', 'weibo_callback', '0', '');
INSERT INTO mto_options VALUES ('12', 'weibo_client_id', '0', '');
INSERT INTO mto_options VALUES ('13', 'weibo_client_sercret', '0', '');
INSERT INTO mto_options VALUES ('14', 'github_callback', '0', '');
INSERT INTO mto_options VALUES ('15', 'github_client_id', '0', '');
INSERT INTO mto_options VALUES ('16', 'github_secret_key', '0', '');

-- ----------------------------
-- Table structure for mto_user
-- ----------------------------
DROP TABLE IF EXISTS mto_user;
CREATE SEQUENCE mto_user_id_seq;

CREATE TABLE mto_user (
  id bigint NOT NULL DEFAULT NEXTVAL ('mto_user_id_seq'),
  username varchar(32) DEFAULT NULL,
  name varchar(32) DEFAULT NULL,
  avatar varchar(128) DEFAULT 'https://s.gravatar.com/avatar/30a3742efd884b026c73eea0e1afe7f6?s=80',
  bio varchar(250) DEFAULT NULL,
  email varchar(64) DEFAULT NULL,
  password varchar(64) DEFAULT NULL,
  status int NOT NULL,
  created_at timestamp(0) DEFAULT NULL,
  updated_at timestamp(0) DEFAULT NULL,
  last_login timestamp(0) DEFAULT NULL,
  gender int NOT NULL,
  comments int NOT NULL,
  posts int NOT NULL,
  signature varchar(140) DEFAULT NULL,
  PRIMARY KEY (id),
  CONSTRAINT UK_USERNAME UNIQUE (username)
)  ;

ALTER SEQUENCE mto_user_id_seq RESTART WITH 2;

-- ----------------------------
-- Records of mto_user
-- ----------------------------
INSERT INTO mto_user VALUES ('1', 'admin', 'Salah atwa', 'https://s.gravatar.com/avatar/30a3742efd884b026c73eea0e1afe7f6?s=80','CEO', 'satwa@alinma.com', '$2a$10$Ihg6rvc3yxk0uq/.KAKiMu.jdqyQC3ES5R7geQLSdOVeehU9D4CqK', '0', '2017-08-06 17:52:41', '2017-07-26 11:08:36', '2017-10-17 13:24:13', '0', '0', '0', '');

-- ----------------------------
-- Table structure for mto_user_oauth
-- ----------------------------
DROP TABLE IF EXISTS mto_user_oauth;
CREATE SEQUENCE mto_user_oauth_id_seq;

CREATE TABLE mto_user_oauth (
  id bigint NOT NULL DEFAULT NEXTVAL ('mto_user_oauth_id_seq'),
  user_id bigint DEFAULT NULL,
  access_token varchar(128) DEFAULT NULL,
  expire_in varchar(128) DEFAULT NULL,
  oauth_code varchar(128) DEFAULT NULL,
  oauth_type int DEFAULT NULL,
  oauth_user_id varchar(128) DEFAULT NULL,
  refresh_token varchar(128) DEFAULT NULL,
  PRIMARY KEY (id)
) ;

-- ----------------------------
-- Records of mto_user_oauth
-- ----------------------------



-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS role;
CREATE SEQUENCE role_id_seq;

CREATE TABLE role (
  id bigint NOT NULL DEFAULT NEXTVAL ('role_id_seq'),
  description varchar(140) DEFAULT NULL,
  name varchar(32) NOT NULL,
  status int NOT NULL,
  PRIMARY KEY (id)
)  ;

ALTER SEQUENCE role_id_seq RESTART WITH 2;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO role VALUES ('1', null, 'admin', '0');
INSERT INTO role VALUES ('2', null, 'user', '0');



-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS user_role;
CREATE SEQUENCE user_role_id_seq;

CREATE TABLE user_role (
  id bigint NOT NULL DEFAULT NEXTVAL ('user_role_id_seq'),
  role_id bigint DEFAULT NULL,
  user_id bigint DEFAULT NULL,
  PRIMARY KEY (id)
)  ;

ALTER SEQUENCE user_role_id_seq RESTART WITH 2;

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO user_role VALUES ('1', '1', '1');