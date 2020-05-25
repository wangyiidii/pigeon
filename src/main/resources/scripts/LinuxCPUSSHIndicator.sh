cpu_util=$(top -b -n 1|grep Cpu|awk '{print $2}'|cut -f 1 -d ".")
echo "CPU Util=${cpu_util}"
printf "statusDesc=CPU: %s" $cpu_util