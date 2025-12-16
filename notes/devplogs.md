### 设置db外部连接

使用管理员身份打开powershell，执行上述语句
（注意：将172.31.123.45改为wsl的虚拟以太网的地址），就可以将docker的3306端口映射到本机的ip:3306上
```angular2html
netsh interface portproxy add v4tov4 listenaddress=0.0.0.0 listenport=3306 connectaddress=172.31.123.45 connectport=3306
```
### 设置cmd环境代理
```
set http_proxy=http://127.0.0.1:7890
```