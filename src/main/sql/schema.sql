create database secondkill;
use secondkill;
create table seckill(
`seckill_id` bigint not null auto_increment comment '商品库存id',
`name` varchar(120) not null comment '商品名称',
`number` int  not null comment '库存数量',
`create_time` timestamp not null default current_timestamp comment '创建时间',/*多个timestamp有默认值的要放在最前面，或者必须所有显示指定默认值，不然报错*/
`start_time` timestamp not null comment '秒杀开始时间',
`end_time` timestamp  not null  comment '秒杀结束时间',
primary key (seckill_id),
key idx_start_time(start_time),
key idx_end_time(end_time),
key idx_create_time(create_time)
)engine=Innodb auto_increment=1000 default  charset='utf8' comment='秒杀库存表';/*指定字符集为utf8,没有“-”*/
-- 初始化数据
insert into seckill(name,number,start_time,end_time)
values
('1000元秒杀iPhoneX',100,'2019-04-01 00:00:00','2019-04-02 00:00:00'),
('800元秒杀iPhone8',200,'2019-04-01 00:00:00','2019-04-02 00:00:00'),
('500元秒杀小米8',300,'2019-04-01 00:00:00','2019-04-02 00:00:00'),
('200元秒杀红米note',400,'2019-04-01 00:00:00','2019-04-02 00:00:00');

-- 秒杀成功明细表
-- 用户登录认证相关的信息
create table success_killed(
  `seckill_id` bigint not null comment '秒杀商品id',
  `user_phone` bigint not null comment '用户手机号',
  `state` tinyint not null default -1 comment '状态标识：-1无效；0成功；1已付款2：已发货',
  `create_time` timestamp not null comment '创建时间',
  primary key (seckill_id,user_phone),/*联合主键,防止用户对同一产品做重复秒杀*/
  key idx_create_time(create_time)
) engine=InnoDB DEFAULT charset=utf8 comment'秒杀成功明细表'

-- 连接数据库的控制台
mysql -uroot -p123456