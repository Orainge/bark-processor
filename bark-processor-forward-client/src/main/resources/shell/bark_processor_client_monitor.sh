#! /bin/bash

# ######################################################
#
#    项目运行状态监控 & 自动重启脚本
#
# ######################################################

# 添加系统级定时任务
#
# crontab -e
# ------------------------------------------------------
# 0,5,10,15,20,25,30,35,40,45,50,55 * * * * /path/to/bark-processor-client/monitor/bark_processor_client_monitor.sh >/dev/null 2>&1
# ------------------------------------------------------

# 监控脚本存放根目录
monitor_folder=/path/to/bark-processor-client/monitor

# 日志文件名
log_file_name=bark_processor_client_monitor.log

# 进程检测关键字
process_check_key=bark-processor-forward-client-1.0.jar

# PID 全局变量
timer_pid=""

# 获取 PID 函数
get_pid(){
  timer_pid=`ps -ef | grep "${process_check_key}" | grep -v "grep" | awk '{print $2}'`
}

# 检测函数
check_timer(){
  get_pid
  if [ `echo ${timer_pid} | wc -w` -eq 0 ];then
    /usr/bin/java -jar /path/to/bark-processor-client/bark-processor-forward-client-1.0.jar >> /path/to/bark-processor-client/out.log 2>&1 &
    get_pid
    echo "[`date +'%Y-%m-%d %H:%M:%S'`] 进程已停止，重启 ${process_check_key} [`echo ${timer_pid}`]" >> ${monitor_folder}/${log_file_name}
  fi
}

# 运行检测函数
check_timer