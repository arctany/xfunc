@echo off
echo ===================================
echo AI虚拟助手平台 - 编译和运行工具
echo ===================================
echo.

:: 检查Java是否已安装
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未检测到Java。请安装Java 17或更高版本。
    echo 下载地址: https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)

:: 检查Maven是否已安装
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未检测到Maven。正在使用内置的Maven编译。
    set "MAVEN_CMD=.\mvnw"
) else (
    set "MAVEN_CMD=mvn"
)

echo [信息] 开始编译项目...
%MAVEN_CMD% clean package -DskipTests
if %errorlevel% neq 0 (
    echo [错误] 编译失败，请检查代码和依赖项。
    pause
    exit /b 1
)
echo [信息] 编译成功。

:: 检查NVIDIA GPU并配置参数
echo [信息] 检测系统配置...
nvidia-smi >nul 2>&1
if %errorlevel% equ 0 (
    echo [信息] 检测到NVIDIA GPU，启用GPU加速。
    set "JAVA_OPTS=-Dprism.forceGPU=true -Dprism.verbose=true -Djava.library.path=./src/main/resources/natives"
) else (
    echo [信息] 未检测到NVIDIA GPU，使用CPU模式。
    set "JAVA_OPTS="
)

:: 运行应用程序
echo [信息] 启动AI虚拟助手平台...
echo.
java %JAVA_OPTS% -jar target/aivirtualassistant-0.0.1-SNAPSHOT.jar
if %errorlevel% neq 0 (
    echo [错误] 应用程序运行失败。
    pause
    exit /b 1
)

exit /b 0 