drop table if exists `station`;
create table `station` (
                           `id` bigint not null comment 'id',
                           `name` varchar(20) not null comment 'վ��',
                           `name_pinyin` varchar(50) not null comment 'վ��ƴ��',
                           `name_py` varchar(50) not null comment 'վ��ƴ������ĸ',
                           `create_time` datetime(3) comment '����ʱ��',
                           `update_time` datetime(3) comment '�޸�ʱ��',
                           primary key (`id`),
                           unique key `name_unique` (`name`)
) engine=innodb default charset=utf8mb4 comment='��վ';
