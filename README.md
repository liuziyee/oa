:ocean:  
`ubuntu安装mongodb参考文档:https://www.mongodb.com/docs/manual/tutorial/install-mongodb-on-ubuntu`  
`problem-spring-web参考文档:https://github.com/zalando/problem-spring-web/tree/main/problem-spring-web`  
`swagger访问地址:http://localhost:30000/oa/swagger-ui.html`  
`knife4j访问地址:http://localhost:30000/oa/doc.html`  
:ocean:  
```
关于如何刷新访问令牌
把生成的访问令牌缓存到Redis(下面叫做缓存令牌),缓存令牌的过期时间设为访问令牌的一倍,下面以访问令牌过期时间5天为例
如果访问令牌过期,缓存令牌没有过期,说明访问令牌过期后的间隔时间还没有超过5天,要生成新的访问令牌(即续期)并缓存到Redis
如果访问令牌过期,缓存令牌也过期了,说明访问令牌过期后的间隔时间超过了5天,要重新登录
```
:ocean:  
`docker load < /usr/local/face.tar.gz`  
`docker run -d -it -p 3000:3000 -v /usr/local/python/demo:/usr/local/demo --name face-recognition face`  
`docker exec -it face-recognition bash`  
`nohup python3 -c "from app import app;" > log.out 2>&1`