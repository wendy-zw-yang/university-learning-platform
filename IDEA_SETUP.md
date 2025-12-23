# IntelliJ IDEA 配置指南

## 问题：SDK未配置或已损坏

### 快速解决方案

#### 方法1：自动配置（最简单）
1. 点击错误提示框中的 **"自动修复配置..."** 链接
2. IDEA会自动下载 Microsoft OpenJDK 17.0.17 (178.1 MB)
3. 等待下载和配置完成
4. 完成后重启IDEA

#### 方法2：手动配置JDK

##### 步骤1：下载JDK 17
访问以下任一网站下载JDK 17：
- **Oracle OpenJDK**: https://jdk.java.net/17/
- **Microsoft OpenJDK**: https://learn.microsoft.com/zh-cn/java/openjdk/download
- **Adoptium (推荐)**: https://adoptium.net/zh-CN/temurin/releases/?version=17

##### 步骤2：在IDEA中配置JDK

1. **打开项目结构设置**
   - 方式1：`File` → `Project Structure` (Ctrl+Alt+Shift+S)
   - 方式2：点击错误提示，选择"配置SDK"

2. **配置Project SDK**
   - 在左侧选择 `Project`
   - 点击 `SDK` 下拉框
   - 选择 `Add SDK` → `Download JDK...`
   
3. **下载JDK**
   - Version: 17
   - Vendor: 选择以下任一：
     - Oracle OpenJDK
     - Microsoft OpenJDK (推荐，错误提示中建议的)
     - Eclipse Temurin
   - 点击 `Download`

4. **应用配置**
   - 点击 `Apply`
   - 点击 `OK`

##### 步骤3：配置语言级别
在 `Project Structure` → `Project` 中：
- **Project SDK**: 选择刚配置的 JDK 17
- **Project language level**: 选择 `17 - Sealed types, always-strict floating-point semantics`

##### 步骤4：配置模块SDK
1. 在 `Project Structure` 左侧选择 `Modules`
2. 选择 `university-learning-platform` 模块
3. 在 `Dependencies` 标签页中
4. 确认 `Module SDK` 设置为 `<Project SDK>` 或 `17`

##### 步骤5：配置Maven
1. 打开 `File` → `Settings` (Ctrl+Alt+S)
2. 导航到 `Build, Execution, Deployment` → `Build Tools` → `Maven` → `Runner`
3. 在 `JRE` 中选择配置好的 JDK 17

### 配置Tomcat服务器

配置好JDK后，还需要配置Tomcat：

1. **下载Tomcat 10**
   - 访问: https://tomcat.apache.org/download-10.cgi
   - 下载 Windows 版本的 zip 文件
   - 解压到本地目录（例如：`D:\apache-tomcat-10.x.x`）

2. **在IDEA中配置Tomcat**
   - 点击右上角的 `Add Configuration...`
   - 点击 `+` → `Tomcat Server` → `Local`
   - 配置Tomcat目录
   - 在 `Deployment` 标签页添加 `Artifact`
   - 选择 `university-learning-platform:war exploded`
   - 设置 Application context 为 `/` 或 `/ulp`
   - 点击 `Apply` 和 `OK`

### 验证配置

1. **检查JDK版本**
   - 打开IDEA终端
   - 运行: `java -version`
   - 应该显示: `openjdk version "17.x.x"`

2. **Maven编译测试**
   - 打开右侧的 Maven 面板
   - 展开 `Lifecycle`
   - 双击 `clean`，然后双击 `compile`
   - 如果成功，说明配置正确

3. **运行项目**
   - 点击右上角的运行按钮
   - 浏览器访问: `http://localhost:8080/admin/courses`

## 常见问题

### 问题1：Maven依赖下载失败
**解决方案**：
- 配置国内Maven镜像（阿里云）
- 在 `Settings` → `Maven` → `User settings file`
- 添加阿里云镜像配置

### 问题2：Tomcat启动失败
**原因**：可能是端口冲突
**解决方案**：
- 检查8080端口是否被占用
- 在Tomcat配置中修改端口

### 问题3：无法识别Servlet注解
**原因**：可能是依赖版本问题
**解决方案**：
- 确认pom.xml中的servlet-api版本
- 确认Tomcat版本（需要Tomcat 10+支持Servlet 6.x）

### 问题4：数据库连接失败
**解决方案**：
- 检查MySQL服务是否启动
- 检查DBHelper.java中的数据库配置
- 确认数据库名称、用户名、密码正确

## 项目运行步骤

### 1. 确保环境就绪
- ✅ JDK 17 已安装并配置
- ✅ Maven 已配置
- ✅ Tomcat 10 已配置
- ✅ MySQL 数据库已启动
- ✅ 数据库表已创建（运行dbDisign.md中的SQL）

### 2. 编译项目
```bash
mvn clean compile
```

### 3. 启动Tomcat
- 点击IDEA右上角的运行按钮
- 或使用快捷键 Shift+F10

### 4. 访问应用
- 课程管理: http://localhost:8080/admin/courses
- 教师管理: http://localhost:8080/admin/teachers

## 技术支持

如果以上步骤无法解决问题，请检查：
1. IDEA版本（建议使用2023.3或更高版本）
2. 防火墙设置
3. IDEA日志（Help → Show Log in Explorer）

---

**配置完成后记得重启IDEA！**
