# Nukkit Panel
Nukkit Panel旨在打造一款简易高效的服务器管理插件，尽量使用最简便的交互及部署方式。内置HTTP/FTP/WebSocket服务器，你可以用它监控服务器状态、控制台，管理插件、文件、地图等游戏内容。降低小白服主开服门槛。

# 兼容性
Nukkit Panel兼容IE 11+ 和现代主流浏览器(Chrome、Firefox、Edge等)​
对于服务端，经测试支持NukkitX、PowerNukkit、NukkitPetteriM1Edition核心.

# 进入面板
用浏览器访问http://地址:端口/admin，如 http://127.0.0.1:19132/admin

# 注意事项
请在防火墙放行相应的TCP端口(http、websocket、ftp)

# 配置文件及其默认值
plugins/NukkitPanel/config.properties
```
#面板登录的用户名
username=nukkit

#面板登录的密码
password=123456

#保持连接的时间，超时需重新登录
#单位为 分 min
keep-connected=120

#编码格式 支持的选项 auto, utf8, gbk, gb2312等
#auto会根据Java启动参数和操作系统自动选择合适的编码
charset=auto

#是否使用本地的MDUI资源
#建议在内网环境时开启
local-mdui=off

#WebSocket使用的端口
#auto会自动选择10000-20000的随机端口
websocket-port=auto

#设置语言, 支持的选项 auto, chs, cht
#auto会根据Nukkit的语言自动调整
language=auto

#是否启用FTP服务器
#登录账号同面板账户
ftp-server=on

#FTP服务器端口
ftp-port=2121

#是否启用快速建站
#网站存放路径 plugins/NukkitPanel/wwwroot
#仅支持静态页面(html/css/js/png等),其他文件会跳转到下载
website=off
```