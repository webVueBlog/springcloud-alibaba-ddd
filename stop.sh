#!/bin/bash

# SpringCloud Alibaba DDD 微服务停止脚本

echo "=========================================="
echo "停止所有微服务..."
echo "=========================================="

if [ -d "logs" ]; then
    for pid_file in logs/*.pid; do
        if [ -f "$pid_file" ]; then
            PID=$(cat "$pid_file")
            SERVICE_NAME=$(basename "$pid_file" .pid)
            if ps -p $PID > /dev/null 2>&1; then
                echo "停止服务: $SERVICE_NAME (PID: $PID)"
                kill $PID
                sleep 2
                if ps -p $PID > /dev/null 2>&1; then
                    echo "强制停止服务: $SERVICE_NAME"
                    kill -9 $PID
                fi
            else
                echo "服务 $SERVICE_NAME 未运行"
            fi
            rm -f "$pid_file"
        fi
    done
else
    echo "日志目录不存在，尝试查找Java进程..."
    pkill -f "spring-boot:run"
fi

echo ""
echo "所有服务已停止"
echo "=========================================="

