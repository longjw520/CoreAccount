#! /bin/sh  
  
#启动方法  
start(){  
  		#nohup java -Xms128m -Xmx2048m -jar test1.jar 5 > log.log &  
        nohup java -jar CoreAccount-front.jar  &  
}  
#停止方法  
stop(){  
        ps -ef|grep test|awk '{print $2}'|while read pid  
        do  
           kill -9 $pid  
        done  
}  
  
case "$1" in  
start)  
  start  
  ;;  
stop)  
  stop  
  ;;  
restart)  
  stop  
  start  
  ;;  
*)  
  printf 'Usage: %s {start|stop|restart}\n' "$prog"  
  exit 1  
  ;;  
esac