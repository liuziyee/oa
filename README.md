`problem-spring-web参考文档:https://github.com/zalando/problem-spring-web/tree/main/problem-spring-web`  
`swagger:http://localhost:8080/oa/swagger-ui.html`  
`knife4j:http://localhost:8080/oa/doc.html`
> ##### 关于如何刷新访问令牌
###### 把生成的访问令牌缓存到Redis(下面叫做缓存令牌), 缓存令牌的过期时间设为访问令牌的一倍, 下面以访问令牌过期时间5天为例
###### 如果访问令牌过期, 缓存令牌没有过期, 说明访问令牌过期后的间隔时间还没有超过5天, 要生成新的访问令牌(即续期)并缓存到Redis
###### 如果访问令牌过期, 缓存令牌也过期了, 说明访问令牌过期后的间隔时间超过了5天, 要重新登录
> ##### 关于MySQL和Java日期数据类型的映射
###### MySQL的datetime映射为Java的Date(下同)
###### date(time)映射为String, 不用Java的Date是因为Date同时包含日期和时间, 而MySQL的date(time)只包含日期(时间), 所以这里用日期(时间)字符串来替代Date
###### JDK8+: datetime, timestamp映射为LocalDateTime, date映射为LocalDate, time映射为LocalTime
> ##### 零星
###### MySQL的时区要设置为Asia/Shanghai
###### 签到接口会接收签到照片和位置信息(JSON), 用@RequestParam接收文件, 不要用@RequestBody接收JSON
###### Stream的map, peek等中间操作为惰性操作, 要跟上结束操作, Optional的map不是惰性操作
###### 接口用到了@RequestBody, 会执行XssHttpServletRequestWrapper的getInputStream()
###### JSON响应忽略空值, 可以用@JsonInclude(JsonInclude.Include.NON_NULL)或配置spring.jackson.default-property-inclusion=non_null
###### 用到的mysql函数: if, ifnull, json_contains, date_format, current_date, case when then, cast, concat, timestampdiff, group_concat
###### A.isBeforeOrEquals(B)等价于A.compareTo(B)<=0
###### mongo message: 消息集合 message_push_record: 消息推送记录集合
###### spring.data.mongodb.password要加上单引号
```shell
keytool -genkeypair -alias [keypair] -keyalg RSA -keystore [keypair.keystore] -keypass [dorohedoro] -storepass [dorohedoro]

apt-get update
apt-get install -y docker.io
systemctl status docker

docker pull mysql:8.0.23
docker run -it -d --name mysql --net=host -m 500m -v /usr/local/mysql/data:/var/lib/mysql \
-v /usr/local/mysql/config:/etc/mysql/conf.d \
-e MYSQL_ROOT_PASSWORD=12345 -e TZ=Asia/Shanghai mysql:8.0.23

docker pull redis:6.0.10
mkdir -p /usr/local/redis/conf
vim /usr/local/redis/conf/redis.conf
bind 0.0.0.0
protected-mode yes
port 6379
tcp-backlog 511
timeout 0
tcp-keepalive 0
loglevel notice
logfile ""
databases 4
save 900 1
save 300 10
save 60 10000
stop-writes-on-bgsave-error yes
rdbcompression yes
rdbchecksum yes
dbfilename dump.rdb
dir ./
requirepass 12345
docker run -it -d --name redis --net=host -m 300m -v /usr/local/redis/conf:/usr/local/etc/redis \
redis:6.0.10 redis-server /usr/local/etc/redis/redis.conf

docker pull rabbitmq:3.11.3-management
docker run -it -d --name rabbit --net=host -m 300m rabbitmq:3.11.3-management

ubuntu安装mongodb参考文档:https://www.mongodb.com/docs/manual/tutorial/install-mongodb-on-ubuntu
vim /etc/mongod.conf
bindIp 0.0.0.0

docker pull openjdk:11.0.11-jdk-oraclelinux7
docker run -it -d --name oa --net=host -m 1G -v /usr/local/oa:/usr/local/oa -v /etc/localtime:/etc/localtime \
-e TZ=Asia/Shanghai openjdk:11.0.11-jdk-oraclelinux7
docker exec -it oa bash
nohup java -jar /usr/local/oa/oa-1.0-SNAPSHOT.jar >> /dev/null 2>&1 &
```