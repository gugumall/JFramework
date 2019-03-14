use jframeworkx;

drop table j_user;
drop table j_user_certification;
drop table j_thirdparty_user;
drop table j_thirdparty_for_login;

create table j_thirdparty_user
(
   UUID                 varchar(64) not null,
   USER_ID              varchar(64),
   THIRDPARTY_CODE      varchar(64),
   THIRDPARTY_USER_ID   varchar(128),
   THIRDPARTY_NICKNAME  varchar(128),
   THIRDPARTY_HEADER    varchar(256),
   primary key (UUID)
) engine = InnoDB;

/*==============================================================*/
/* Table: j_user_login                                          */
/*==============================================================*/
create table j_user_login
(
   UUID                 varchar(64) not null,
   USER_ID              varchar(64),
   USER_AGENT_SN        varchar(128),
   USER_IP              varchar(128),
   THIRDPARTY_CODE      varchar(64),
   THIRDPARTY_USER_ID   varchar(128),
   LOGIN_TIME_TRY       bigint,
   LOGIN_TIME_OK        bigint,
   LOGIN_TIME_AUTO      bigint,
   LOGIN_STATUS         char(3) comment '000 登录失败
            001 登录成功',
   LOGIN_METHOD         char(3) comment '000 用户名+密码
            001 手机号+密码
            002 邮箱+密码
            
            100 手机号+短信验证码
            101 邮箱+邮件验证码
            
            200 第三方账号授权
            
            300 WEB cookie自动登录
            301 APP token自动登录
            
            浏览器与单次登录服务器之间的session关联全局会话实现单次登录。
            token与设备唯一编号对应，实现同一app内跨应用单次登录。',
   LOGIN_FAILED_TIMES   smallint,
   APPID_LOGIN_FROM     varchar(64),
   SESSION_ID_LOGIN_FROM varchar(64) comment 'app登录时，指第一次通过http发起登录请求时产生的session_id',
   SESSION_ID_GLOBAL    varchar(64),
   primary key (UUID)
) engine = InnoDB;