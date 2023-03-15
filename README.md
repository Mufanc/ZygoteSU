# ZygoteSU

* 别再关闭 SELinux 了

* [https://www.xda-developers.com/permissive-selinux-dangers-exploits/](https://www.xda-developers.com/permissive-selinux-dangers-exploits/)

## 使用

* Android 10+

* Zygote 启动前关闭 SELinux

```shell
$ sh $(dirname $(pm path xyz.mufanc.zsu | cut -d : -f 2))/lib/*/shell.sh
```
