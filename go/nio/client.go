package main

import (
	"bufio"
	"fmt"
	"net"
)

func main() {
	serverAddr := "127.0.0.1:8888"
	// 连接到 Java 服务端
	conn, err := net.Dial("tcp", serverAddr)
	if err != nil {
		fmt.Println("无法连接到服务端:", err)
		return
	}
	defer conn.Close()
	fmt.Println("成功连接到服务端:", serverAddr)

	// 创建 Reader 和 Writer
	reader := bufio.NewReader(conn)
	writer := bufio.NewWriter(conn)

	// 准备发送的数据
	message := `{"name":"test", "data":"hello", "hostId":"1234"}`
	fmt.Println("发送消息到服务端:", message)

	// 发送消息并加上换行符
	_, err = writer.WriteString(message + "\n")
	if err != nil {
		fmt.Println("发送消息失败:", err)
		return
	}
	writer.Flush()

	// 读取服务端响应
	response, err := reader.ReadString('\n')
	if err != nil {
		fmt.Println("读取服务端响应失败:", err)
		return
	}
	fmt.Println("服务端回应:", response)
}