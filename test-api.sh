#!/bin/bash

# API测试脚本
# 使用方法: ./test-api.sh

BASE_URL="http://localhost:8080"
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "=========================================="
echo "  微服务API测试脚本"
echo "=========================================="
echo ""

# 检查服务是否启动
echo -e "${YELLOW}1. 检查服务状态...${NC}"
if curl -s http://localhost:8080 > /dev/null 2>&1; then
    echo -e "${GREEN}✓ 网关服务已启动${NC}"
else
    echo -e "${RED}✗ 网关服务未启动，请先启动服务${NC}"
    exit 1
fi
echo ""

# 测试登录
echo -e "${YELLOW}2. 测试账号密码登录...${NC}"
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login/username" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "123456"
  }')

echo "$LOGIN_RESPONSE" | python -m json.tool 2>/dev/null || echo "$LOGIN_RESPONSE"

# 提取token
TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo -e "${RED}✗ 登录失败，无法获取Token${NC}"
    exit 1
else
    echo -e "${GREEN}✓ 登录成功，Token已获取${NC}"
    echo "Token: ${TOKEN:0:50}..."
fi
echo ""

# 测试获取用户角色
echo -e "${YELLOW}3. 测试获取用户角色...${NC}"
ROLE_RESPONSE=$(curl -s -X GET "$BASE_URL/api/user/roles?userId=1" \
  -H "Authorization: Bearer $TOKEN")
echo "$ROLE_RESPONSE" | python -m json.tool 2>/dev/null || echo "$ROLE_RESPONSE"
echo ""

# 测试获取用户权限
echo -e "${YELLOW}4. 测试获取用户权限...${NC}"
PERMISSION_RESPONSE=$(curl -s -X GET "$BASE_URL/api/user/permissions?userId=1" \
  -H "Authorization: Bearer $TOKEN")
echo "$PERMISSION_RESPONSE" | python -m json.tool 2>/dev/null || echo "$PERMISSION_RESPONSE"
echo ""

# 测试创建订单
echo -e "${YELLOW}5. 测试创建订单...${NC}"
ORDER_RESPONSE=$(curl -s -X POST "$BASE_URL/api/order/create?userId=1&productId=1&quantity=2&price=99.99" \
  -H "Authorization: Bearer $TOKEN")
echo "$ORDER_RESPONSE" | python -m json.tool 2>/dev/null || echo "$ORDER_RESPONSE"
echo ""

# 测试秒杀初始化
echo -e "${YELLOW}6. 测试秒杀库存初始化...${NC}"
SECKILL_INIT_RESPONSE=$(curl -s -X POST "$BASE_URL/api/seckill/init/1?stock=100" \
  -H "Authorization: Bearer $TOKEN")
echo "$SECKILL_INIT_RESPONSE" | python -m json.tool 2>/dev/null || echo "$SECKILL_INIT_RESPONSE"
echo ""

# 测试秒杀下单
echo -e "${YELLOW}7. 测试秒杀下单...${NC}"
SECKILL_RESPONSE=$(curl -s -X POST "$BASE_URL/api/seckill/1?userId=1" \
  -H "Authorization: Bearer $TOKEN")
echo "$SECKILL_RESPONSE" | python -m json.tool 2>/dev/null || echo "$SECKILL_RESPONSE"
echo ""

echo "=========================================="
echo -e "${GREEN}测试完成！${NC}"
echo "=========================================="

