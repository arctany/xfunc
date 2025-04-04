@echo off
REM AI虚拟助手平台启动脚本

echo ================================================
echo             AI虚拟助手平台启动工具
echo ================================================
echo.

REM 检查Java是否已安装
java -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo 错误: 未检测到Java环境!
    echo 请安装Java 11或更高版本后再尝试运行。
    echo 可以从这里下载: https://adoptium.net/
    echo.
    pause
    exit /b 1
)

REM 检查是否存在Maven
call mvn -v >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo 未检测到Maven，将尝试使用已编译的JAR文件...
    goto :run_jar
) else (
    echo 检测到Maven，是否需要重新编译项目?
    choice /c YN /m "选择 Y 重新编译，选择 N 直接运行"
    if %ERRORLEVEL% EQU 1 goto :compile
    if %ERRORLEVEL% EQU 2 goto :run_jar
)

:compile
echo.
echo 正在编译项目...
call mvn clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo 编译失败! 请检查错误信息。
    pause
    exit /b 1
)
echo 编译成功!
echo.

:run_jar
echo.
echo 正在启动AI虚拟助手平台...

REM 检查是否存在JAR文件
if not exist "target\ai-virtual-assistant-1.0-SNAPSHOT.jar" (
    echo 错误: 未找到可执行JAR文件!
    echo 请确保项目已正确编译，或者尝试重新编译。
    pause
    exit /b 1
)

REM 检测显卡驱动
nvidia-smi >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo 检测到NVIDIA显卡，将启用GPU加速功能
    set JAVA_OPTS=-Dorg.nd4j.linalg.cpu.nativepath= -Dorg.nd4j.cuda.use=true
) else (
    echo 未检测到NVIDIA显卡，将使用CPU模式
    set JAVA_OPTS=
)

echo.
echo 启动应用程序...
java %JAVA_OPTS% -jar target\ai-virtual-assistant-1.0-SNAPSHOT.jar
if %ERRORLEVEL% NEQ 0 (
    echo 应用程序异常退出! 错误代码: %ERRORLEVEL%
)

echo.
pause 