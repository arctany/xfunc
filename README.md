# AI虚拟助手平台

AI虚拟助手平台是一个基于Java和现代AI技术的桌面应用程序，允许用户创建和定制自己的AI虚拟助手。

## 主要功能

- **多模态对话**: 使用大型语言模型提供智能对话能力
- **虚拟形象**: 支持2D和3D虚拟形象
- **摄像头交互**: 通过摄像头实现人机交互
- **语音交互**: 语音识别和合成功能
- **形象定制**: 自定义AI助手的外观和声音
- **形象生成**: 通过文本描述或图片生成形象

## 系统要求

- **操作系统**: Windows 10/11 (支持RTX 3090显卡)
- **Java**: Java 11或更高版本
- **显卡**: 支持NVIDIA RTX系列显卡（用于3D渲染和AI计算）
- **硬件**: 至少8GB内存，50GB存储空间
- **外设**: 摄像头和麦克风（用于交互功能）

## 安装步骤

1. **安装Java 11或更高版本**:
   - 访问 [Oracle Java下载页面](https://www.oracle.com/java/technologies/javase-downloads.html) 或使用OpenJDK
   - 安装Java并设置JAVA_HOME环境变量

2. **安装Maven**:
   - 访问 [Maven下载页面](https://maven.apache.org/download.cgi)
   - 解压并设置M2_HOME环境变量

3. **克隆项目**:
   ```
   git clone https://github.com/your-username/ai-virtual-assistant.git
   cd ai-virtual-assistant
   ```

4. **编译项目**:
   ```
   mvn clean package
   ```

5. **运行应用程序**:
   ```
   java -jar target/ai-virtual-assistant-1.0-SNAPSHOT.jar
   ```

## 配置

编辑 `src/main/resources/application.properties` 文件以配置:

- API密钥 (例如 OpenAI API密钥)
- AI模型参数
- 其他定制选项

## 使用方法

1. **启动应用程序**:
   - 运行JAR文件或通过IDE启动应用程序

2. **与AI助手交互**:
   - 在文本框中输入消息或使用麦克风说话
   - AI助手将通过其虚拟形象和语音回应

3. **定制AI助手**:
   - 点击"自定义AI助手"按钮
   - 选择2D或3D形象
   - 通过文本描述生成形象
   - 从图片创建形象
   - 自定义语音

## 开发者指南

### 项目结构

- `src/main/java/com/arctany/aivirtualassistant/`: 源代码
  - `core/`: 核心功能
  - `model/`: 数据模型
  - `view/`: 用户界面
  - `controller/`: 控制器类
  - `service/`: 服务接口和实现
  - `config/`: 配置类
  - `util/`: 工具类

### 扩展功能

1. **添加新的AI模型**:
   - 在`service/impl/AIServiceImpl.java`中扩展AI服务

2. **添加新的虚拟形象类型**:
   - 在`service/impl/AvatarServiceImpl.java`中扩展虚拟形象服务

3. **添加新的交互方式**:
   - 在相应的服务类中添加新的交互功能

## API集成

### 图像生成API

平台使用外部API生成虚拟形象，可以集成:
- Stable Diffusion API
- Midjourney API
- DALL-E API

### 语言模型API

平台使用外部API处理语言交互，支持:
- OpenAI API (GPT模型)
- Azure OpenAI
- 本地LLM部署

## 故障排除

- **摄像头不工作**: 确保摄像头驱动已安装并授予应用程序访问权限
- **语音识别问题**: 检查麦克风设置和驱动
- **3D渲染问题**: 确保安装了最新的显卡驱动程序
- **Java版本错误**: 确保使用正确的Java版本

## 许可

本项目遵循MIT许可证。详见LICENSE文件。

## 联系方式

如有问题或建议，请联系[您的联系方式]。 