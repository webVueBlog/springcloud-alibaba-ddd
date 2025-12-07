#!/bin/bash

# SpringCloud Alibaba DDD 微服务启动脚本

echo "=========================================="
echo "SpringCloud Alibaba DDD 微服务启动脚本"
echo "=========================================="

# 检查Java环境
if ! command -v java &> /dev/null; then
    echo "错误: 未找到Java环境，请先安装JDK 1.8+"
    exit 1
fi

# 检查Maven环境
if ! command -v mvn &> /dev/null; then
    echo "错误: 未找到Maven环境，请先安装Maven 3.6+"
    exit 1
fi

echo "1. 编译项目..."
mvn clean install -DskipTests

if [ $? -ne 0 ]; then
    echo "编译失败，请检查错误信息"
    exit 1
fi

echo ""
echo "2. 启动服务..."
echo "注意: 请确保已启动以下服务:"
echo "  - Nacos (localhost:8848)"
echo "  - Redis (localhost:6379)"
echo "  - MySQL (localhost:3306)"
echo ""

# 启动网关
echo "启动网关服务 (端口: 8080)..."
cd gateway
nohup mvn spring-boot:run > ../logs/gateway.log 2>&1 &
GATEWAY_PID=$!
echo "网关服务已启动 (PID: $GATEWAY_PID)"
cd ..

sleep 5

# 启动认证服务
echo "启动认证服务 (端口: 8081)..."
cd auth-service
nohup mvn spring-boot:run > ../logs/auth-service.log 2>&1 &
AUTH_PID=$!
echo "认证服务已启动 (PID: $AUTH_PID)"
cd ..

sleep 5

# 启动用户服务
echo "启动用户服务 (端口: 8082)..."
cd user-service
nohup mvn spring-boot:run > ../logs/user-service.log 2>&1 &
USER_PID=$!
echo "用户服务已启动 (PID: $USER_PID)"
cd ..

sleep 5

# 启动订单服务
echo "启动订单服务 (端口: 8083)..."
cd order-service
nohup mvn spring-boot:run > ../logs/order-service.log 2>&1 &
ORDER_PID=$!
echo "订单服务已启动 (PID: $ORDER_PID)"
cd ..

sleep 5

# 启动秒杀服务
echo "启动秒杀服务 (端口: 8084)..."
cd seckill-service
nohup mvn spring-boot:run > ../logs/seckill-service.log 2>&1 &
SECKILL_PID=$!
echo "秒杀服务已启动 (PID: $SECKILL_PID)"
cd ..

echo ""
echo "=========================================="
echo "所有服务启动完成！"
echo "=========================================="
echo "服务列表:"
echo "  网关服务:     http://localhost:8080 (PID: $GATEWAY_PID)"
echo "  认证服务:     http://localhost:8081 (PID: $AUTH_PID)"
echo "  用户服务:     http://localhost:8082 (PID: $USER_PID)"
echo "  订单服务:     http://localhost:8083 (PID: $ORDER_PID)"
echo "  秒杀服务:     http://localhost:8084 (PID: $SECKILL_PID)"
echo ""
echo "日志目录: ./logs/"
echo ""
echo "停止服务: ./stop.sh"
echo "=========================================="

# 保存PID到文件
echo "$GATEWAY_PID" > logs/gateway.pid
echo "$AUTH_PID" > logs/auth-service.pid
echo "$USER_PID" > logs/user-service.pid
echo "$ORDER_PID" > logs/order-service.pid
echo "$SECKILL_PID" > logs/seckill-service.pid

