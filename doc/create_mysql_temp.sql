/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2019-03-14 17:05:56                          */
/*==============================================================*/


drop table if exists j_action_log;

drop table if exists j_app;

drop table if exists j_app_mirror;

drop table if exists j_appserver;

drop table if exists j_blacklist;

drop table if exists j_city;

drop table if exists j_continent;

drop table if exists j_country;

drop table if exists j_county;

drop table if exists j_database;

drop table if exists j_db;

drop table if exists j_db_mirror;

drop table if exists j_db_syn_task;

drop table if exists j_fs_task;

drop table if exists j_ip;

drop table if exists j_log;

drop table if exists j_province;

drop table if exists j_server;

drop table if exists j_service;

drop table if exists j_service_mirror;

drop table if exists j_service_router;

drop table if exists j_test;

drop table if exists j_thirdparty_user;

drop table if exists j_user_login;

drop table if exists j_webserver;

drop table if exists j_webserver_to_appserver;

drop table if exists j_workflow;

drop table if exists j_zone;

/*==============================================================*/
/* Table: j_action_log                                          */
/*==============================================================*/
create table j_action_log
(
   EVENT_ID             varchar(64) not null,
   A_SVR_ID             varchar(32),
   A_SYS_ID             varchar(32),
   A_DOMAIN             varchar(64),
   A_URL                varchar(128),
   A_U_IP               varchar(128),
   A_U_ID               varchar(128),
   ACTION_HANDLER       varchar(128),
   ACTION_ID            varchar(64),
   ACTION_PARAMETERS    text,
   ACTION_RESULT        text,
   EVENT_STAT           varchar(8) comment 'TRACE
            DEBUG
            INFO
            WARNING
            ERROR
            FATAL',
   EVENT_TIME           datetime null,
   DEL_BY_SYS           char(1) comment 'N  正常/未删除
            R 回收站
            D 彻底删除',
   primary key (EVENT_ID)
) engine = InnoDB;

/*==============================================================*/
/* Table: j_app                                                 */
/*==============================================================*/
create table j_app
(
   APP_CODE             varchar(64) not null,
   APP_NAME             varchar(90),
   REMARKS              text,
   primary key (APP_CODE)
) engine = InnoDB;

/*==============================================================*/
/* Table: j_app_mirror                                          */
/*==============================================================*/
create table j_app_mirror
(
   APP_MIRROR_CODE      varchar(64) not null,
   APP_CODE             varchar(64),
   APPSERVER_CODE       varchar(64),
   APP_MIRROR_NAME      varchar(90),
   COMM_INTERFACE       varchar(128),
   COMM_KEY             varchar(128),
   APP_MIRROR_STAT      char(3) comment '000 运行
            
            100 关闭
            
            200 维护',
   REMARKS              text,
   primary key (APP_MIRROR_CODE)
) engine = InnoDB;

/*==============================================================*/
/* Table: j_appserver                                           */
/*==============================================================*/
create table j_appserver
(
   APPSERVER_CODE       varchar(64) not null,
   SERVER_CODE          varchar(64),
   APPSERVER_NAME       varchar(90),
   APPSERVER_STAT       char(3) comment '000 运行
            
            100 关闭
            
            200 维护',
   REMARKS              text,
   primary key (APPSERVER_CODE)
) engine = InnoDB;

/*==============================================================*/
/* Table: j_blacklist                                           */
/*==============================================================*/
create table j_blacklist
(
   BLACK_ID             varchar(64) not null,
   U_IP                 varchar(64),
   U_ADDR               varchar(300),
   BLACK_TYPE           char(2) comment '11，禁止使用（跳转到诸如 noservice.html）
            12，禁止访问（显示网页无法打开）',
   START_TIME           datetime null,
   END_TIME             datetime null,
   BLACK_REMARK         varchar(90),
   primary key (BLACK_ID)
) engine = InnoDB;

/*==============================================================*/
/* Table: j_city                                                */
/*==============================================================*/
create table j_city
(
   CITY_ID              varchar(16) not null,
   PROVINCE_ID          varchar(16) comment '由字母、数字、下划线组成，不超过32位',
   COUNTRY_ID           varchar(16) comment '由字母、数字、下划线组成，不超过32位',
   CONTINENT_ID         varchar(16) not null comment '由字母、数字、下划线组成，不超过32位',
   CITY_NAME            varchar(60),
   CITY_NAME_TW         varchar(60),
   CITY_NAME_EN         varchar(150),
   AREA_CODE            varchar(8),
   TIME_ZONE            double(3,1),
   POSTAL_CODE          varchar(16),
   IS_AVAIL             char(1),
   primary key (CITY_ID)
) engine = InnoDB;

/*==============================================================*/
/* Table: j_continent                                           */
/*==============================================================*/
create table j_continent
(
   CONTINENT_ID         varchar(16) not null comment '由字母、数字、下划线组成，不超过32位',
   CONTINENT_CODE       varchar(32) comment '由字母、数字、下划线组成，不超过32位',
   CONTINENT_NAME       varchar(90),
   CONTINENT_NAME_TW    varchar(90),
   CONTINENT_NAME_EN    varchar(150),
   IS_AVAIL             char(1),
   primary key (CONTINENT_ID)
) engine = InnoDB;

/*==============================================================*/
/* Table: j_country                                             */
/*==============================================================*/
create table j_country
(
   COUNTRY_ID           varchar(16) not null comment '由字母、数字、下划线组成，不超过32位',
   CONTINENT_ID         varchar(16) comment '由字母、数字、下划线组成，不超过32位',
   COUNTRY_CODE         varchar(32) comment '由字母、数字、下划线组成，不超过32位',
   COUNTRY_NAME         varchar(90),
   COUNTRY_NAME_TW      varchar(90),
   COUNTRY_NAME_EN      varchar(150),
   AREA_CODE            varchar(8),
   TIME_ZONE            double(3,1),
   IS_AVAIL             char(1),
   primary key (COUNTRY_ID)
) engine = InnoDB;

/*==============================================================*/
/* Table: j_county                                              */
/*==============================================================*/
create table j_county
(
   COUNTY_ID            varchar(16) not null,
   CITY_ID              varchar(16),
   PROVINCE_ID          varchar(16) comment '由字母、数字、下划线组成，不超过32位',
   COUNTRY_ID           varchar(16) comment '由字母、数字、下划线组成，不超过32位',
   CONTINENT_ID         varchar(16) not null comment '由字母、数字、下划线组成，不超过32位',
   COUNTY_NAME          varchar(60),
   COUNTY_NAME_TW       varchar(60),
   COUNTY_NAME_EN       varchar(150),
   AREA_CODE            varchar(8),
   TIME_ZONE            double(3,1),
   POSTAL_CODE          varchar(16),
   IS_AVAIL             char(1),
   primary key (COUNTY_ID)
) engine = InnoDB;

/*==============================================================*/
/* Table: j_database                                            */
/*==============================================================*/
create table j_database
(
   DBSYS_CODE           varchar(64) not null,
   SERVER_CODE          varchar(64),
   DBSYS_NAME           varchar(90),
   DBSYS_TYPE           varchar(32),
   DBSYS_CONNS          int,
   DBSYS_STAT           char(3) comment '000 运行
            
            100 关闭
            
            200 维护',
   REMARKS              text,
   primary key (DBSYS_CODE)
) engine = InnoDB;

/*==============================================================*/
/* Table: j_db                                                  */
/*==============================================================*/
create table j_db
(
   DB_CODE              varchar(64) not null,
   DB_NAME              varchar(90),
   REMARKS              text,
   primary key (DB_CODE)
) engine = InnoDB;

/*==============================================================*/
/* Table: j_db_mirror                                           */
/*==============================================================*/
create table j_db_mirror
(
   DB_MIRROR_CODE       varchar(64) not null,
   DB_CODE              varchar(64),
   DBSYS_CODE           varchar(64),
   SERVER_CODE          varchar(64),
   DB_MIRROR_NAME       varchar(90),
   BE_READ              char(1),
   BE_UPDATED           char(1),
   BE_INSERTED          char(1),
   COMM_INTERFACE       varchar(128),
   COMM_KEY             varchar(128),
   DB_MIRROR_STAT       char(3) comment '000 运行
            
            100 关闭
            
            200 维护',
   REMARKS              text,
   primary key (DB_MIRROR_CODE)
) engine = InnoDB;

/*==============================================================*/
/* Table: j_db_syn_task                                         */
/*==============================================================*/
create table j_db_syn_task
(
   UUID                 varchar(64) not null,
   TASK_TIME            datetime null,
   FROM_UUID            varchar(64),
   TO_UUID              varchar(64),
   TASK_OPERATION       varchar(64),
   TASK_DATA            text,
   SYN_TIMES            int,
   SYN_TIME             datetime null,
   primary key (UUID)
) engine = InnoDB;

/*==============================================================*/
/* Table: j_fs_task                                             */
/*==============================================================*/
create table j_fs_task
(
   UUID                 varchar(64) not null,
   TASK_TIME            datetime null,
   FROM_UUID            varchar(64),
   TO_UUID              varchar(64),
   FILE_PATH            varchar(256),
   TASK_OPERATION       varchar(64),
   TASK_DATA            text,
   SYN_TIMES            int,
   SYN_TIME             datetime null,
   primary key (UUID)
) engine = InnoDB;

/*==============================================================*/
/* Table: j_ip                                                  */
/*==============================================================*/
create table j_ip
(
   IP_ID                bigint not null,
   IP_START             bigint not null,
   IP_END               bigint not null,
   IP_ADDR              varchar(300) not null,
   primary key (IP_ID)
) engine = InnoDB;

/*==============================================================*/
/* Table: j_log                                                 */
/*==============================================================*/
create table j_log
(
   EVENT_ID             varchar(64) not null,
   A_SVR_ID             varchar(32),
   A_SYS_ID             varchar(32),
   A_DOMAIN             varchar(64),
   A_URL                varchar(128),
   A_U_IP               varchar(128),
   A_U_ID               varchar(128),
   BIZ_CODE             varchar(32),
   BIZ_ID               varchar(64),
   BIZ_NAME             varchar(150),
   BIZ_LINK             varchar(128),
   BIZ_ICON             varchar(128),
   BIZ_DATA             text comment '商户自己的业务数据，可以是任何格式，由商户应用决定',
   EVENT_TIME           datetime null,
   EVENT_CODE           varchar(32),
   EVENT_DATA           text,
   EVENT_STAT           varchar(8) comment 'TRACE
            DEBUG
            INFO
            WARNING
            ERROR
            FATAL',
   DEL_BY_SYS           char(1) comment 'N  正常/未删除
            R 回收站
            D 彻底删除',
   primary key (EVENT_ID)
) engine = InnoDB;

/*==============================================================*/
/* Table: j_province                                            */
/*==============================================================*/
create table j_province
(
   PROVINCE_ID          varchar(16) not null comment '由字母、数字、下划线组成，不超过32位',
   COUNTRY_ID           varchar(16) comment '由字母、数字、下划线组成，不超过32位',
   CONTINENT_ID         varchar(16) not null comment '由字母、数字、下划线组成，不超过32位',
   PROVINCE_NAME        varchar(60),
   PROVINCE_NAME_SHORT  varchar(30),
   PROVINCE_NAME_TW     varchar(60),
   PROVINCE_NAME_EN     varchar(150),
   AREA_CODE            varchar(8),
   TIME_ZONE            double(3,1),
   POSTAL_CODE          varchar(16),
   IS_AVAIL             char(1),
   primary key (PROVINCE_ID)
) engine = InnoDB;

/*==============================================================*/
/* Table: j_server                                              */
/*==============================================================*/
create table j_server
(
   SERVER_CODE          varchar(64) not null,
   SERVER_NAME          varchar(90),
   IDC_NAME             varchar(90),
   ZONE_ID              varchar(16) not null,
   COUNTY_ID            varchar(16),
   CITY_ID              varchar(16),
   PROVINCE_ID          varchar(16) comment '由字母、数字、下划线组成，不超过32位',
   COUNTRY_ID           varchar(16) comment '由字母、数字、下划线组成，不超过32位',
   CONTINENT_ID         varchar(16) not null comment '由字母、数字、下划线组成，不超过32位',
   ADDR                 varchar(150) not null comment '由字母、数字、下划线组成，不超过32位',
   STARTUP_TIME         datetime null,
   SHUTDOWN_TIME        datetime null,
   WAN_IP               varchar(128),
   LAN_IP               varchar(128),
   CPU                  varchar(90),
   DISK                 int,
   RAM                  int,
   OS                   varchar(90),
   IN_USE               char(1),
   SERVER_STAT          char(3) comment '000 运行
            
            100 关机',
   REMARKS              text,
   primary key (SERVER_CODE)
) engine = InnoDB;

/*==============================================================*/
/* Table: j_service                                             */
/*==============================================================*/
create table j_service
(
   SERVICE_CODE         varchar(64) not null,
   SERVICE_NAME         varchar(90),
   REMARKS              text,
   primary key (SERVICE_CODE)
) engine = InnoDB;

/*==============================================================*/
/* Table: j_service_mirror                                      */
/*==============================================================*/
create table j_service_mirror
(
   SERVICE_MIRROR_CODE  varchar(64) not null,
   SERVICE_CODE         varchar(64),
   APP_MIRROR_CODE      varchar(64),
   SERVICE_MIRROR_NAME  varchar(90),
   COMM_INTERFACE       varchar(128),
   COMM_KEY             varchar(128),
   SERVICE_MIRROR_STAT  char(3) comment '000 运行
            
            100 关闭
            
            200 维护',
   REMARKS              text,
   primary key (SERVICE_MIRROR_CODE)
) engine = InnoDB;

/*==============================================================*/
/* Table: j_service_router                                      */
/*==============================================================*/
create table j_service_router
(
   ROUTER_CODE          varchar(64) not null,
   APP_MIRROR_CODE      varchar(64),
   ROUTER_NAME          varchar(90),
   COMM_INTERFACE       varchar(128),
   COMM_KEY             varchar(128),
   ROUTER_STAT          char(3) comment '000 运行
            
            100 关闭
            
            200 维护',
   REMARKS              text,
   primary key (ROUTER_CODE)
) engine = InnoDB;

/*==============================================================*/
/* Table: j_test                                                */
/*==============================================================*/
create table j_test
(
   AA                   bigint not null,
   BB                   char(1),
   CC                   date,
   DD                   decimal,
   EE                   double,
   FF                   double precision,
   GG                   float,
   HH                   int,
   II                   integer,
   JJ                   longblob,
   KK                   longtext,
   LL                   numeric,
   MM                   real,
   NN                   blob,
   OO                   smallint,
   PP                   text,
   QQ                   time,
   RR                   timestamp,
   SS                   tinyint,
   TT                   varchar(32),
   primary key (AA)
) engine = InnoDB;

/*==============================================================*/
/* Table: j_thirdparty_user                                     */
/*==============================================================*/
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

/*==============================================================*/
/* Table: j_webserver                                           */
/*==============================================================*/
create table j_webserver
(
   WEBSERVER_CODE       varchar(64) not null,
   SERVER_CODE          varchar(64),
   WEBSERVER_NAME       varchar(90),
   WEBSERVER_STAT       char(3) comment '000 运行
            
            100 关闭
            
            200 维护',
   REMARKS              text,
   primary key (WEBSERVER_CODE)
) engine = InnoDB;

/*==============================================================*/
/* Table: j_webserver_to_appserver                              */
/*==============================================================*/
create table j_webserver_to_appserver
(
   UUID                 varchar(64) not null,
   APPSERVER_CODE       varchar(64),
   WEBSERVER_CODE       varchar(64),
   REMARKS              text,
   primary key (UUID)
) engine = InnoDB;

/*==============================================================*/
/* Table: j_workflow                                            */
/*==============================================================*/
create table j_workflow
(
   WORKFLOW_ID          varchar(64) not null,
   WORKFLOW_CODE        varchar(64),
   START_TIME           timestamp,
   UPD_TIME             timestamp,
   WORKFLOW_STATUS      char(3) comment '000 未开始
            100 进行中
            101 异常导致无法继续
            200 已经完成
            201 终止
            ',
   NODE_ID              varchar(64),
   NODE_STATUS          char(3) comment '000 未开始
            100 进行中
            101 异常导致无法继续
            200 已经完成
            201 终止
            ',
   NODE_RESULT          varchar(16),
   NODE_DATA            text,
   primary key (WORKFLOW_ID)
) engine = InnoDB;

/*==============================================================*/
/* Table: j_zone                                                */
/*==============================================================*/
create table j_zone
(
   ZONE_ID              varchar(16) not null,
   COUNTY_ID            varchar(16),
   CITY_ID              varchar(16),
   PROVINCE_ID          varchar(16) comment '由字母、数字、下划线组成，不超过32位',
   COUNTRY_ID           varchar(16) comment '由字母、数字、下划线组成，不超过32位',
   CONTINENT_ID         varchar(16) not null comment '由字母、数字、下划线组成，不超过32位',
   ZONE_NAME            varchar(60),
   ZONE_NAME_TW         varchar(60),
   ZONE_NAME_EN         varchar(150),
   AREA_CODE            varchar(8),
   TIME_ZONE            double(3,1),
   POSTAL_CODE          varchar(16),
   IS_AVAIL             char(1),
   primary key (ZONE_ID)
) engine = InnoDB;

