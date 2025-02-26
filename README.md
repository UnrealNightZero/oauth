React + Spring Boot OAuth2.0 JWT 登入系統
===
簡介
---
本專案使用 React 作為前端，Spring Boot 作為後端，並實作 OAuth 2.0 登入機制。登入後，JWT 令牌會存儲在 HTTP-only Cookie 中，以提高安全性。

技術棧
---
前端: React, React Router,

後端: Spring Boot, Spring Security, OAuth2, JWT ,JPA

資料庫:  MySQL

認證: Google OAuth 2.0

基礎概念
---
#### Google Oauth2.0 工作流程 Code Flow
![](<https://cdn.discordapp.com/attachments/1344320312476696627/1344320605121679421/flow.png?ex=67c07ba0&is=67bf2a20&hm=bed4e7777d18d6b162eec3cf9a06b711b7e9ad1a5321d9d809b50c24c4649ff0&> "工作流程")

環境變數設定
---
[ 建立Google方法id&secret](https://support.google.com/workspacemigrate/answer/9222992?hl=zh-Hant)
請在 ==backend/src/main/resources/application.yml== 設定 OAuth 2.0 憑證資訊：
```
# JDBC
# 填入連接資料庫這裡使用Mysql當範例
spring.datasource.url=jdbc:mysql:
spring.datasource.username=
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
# JWT
#建議使用OpenSSL生成(Git自帶)openssl rand -base64 32
jwt.secret-key=
#JWT過期時間單位s
jwt.expiration-time=
# JPA
#配置為開發用請勿使用在正式環境
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
# Google
google.client.id=
google.client.secret=
google.redirect.uri=postmessage
```
請在 ==fornt-project\fornt== 設定
```
VITE_GOOGLE_CLIENT_ID=
GOOGLE_CLIENT_SECRET=
```
安裝與執行
===
後端 (Spring Boot)
---
#### 進入 backend 目錄
安裝依賴並運行 Spring Boot 伺服器：
==\end-project\back-end==
```
mvn clean install
mvn spring-boot:run
```
前端 (React)
---
#### 進入 frontend 目錄
==\fornt-project\fornt==

安裝依賴並啟動開發伺服器：
```
yarn install
yarn dev
```
API 端點
---
#### 前端
#### 註冊request POST http://localhost:8080/auth/sign-in
```
request
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}
response
200 User registered successfully!
```
#### 登入request POST http://localhost:8080/auth/sign-in
```
request
{
  "email": "john@example.com",
  "password": "password123"
}
response
{
    "message": "Login successful",
    "email": "john@example.com"
}
```
#### oauth登入 POST http://localhost:8080/oauth2/callback/google
```
request
{
  "code": codeResponse.code
}
response
{
    "message": "Login successful",
    "email": "john@example.com"
}
```
#### Cookie
| Name | Value | Domain | Path | Expires | HttpOnly | Secure |
| :--: | :--: | :--: | :--: | :--: | :--: | :--: |
| token |<div style="width: 300pt">eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwiaWF0IjoxNzQwNTc2Mzc5LCJleHAiOjE3NDA2NjI3Nzl9.fu03uGkKI1CRGnAfIM4sh95nDQ9MBZZWgDJVq1_Xmqk|localhost|/|Thu, 27 Feb 2025 13:26:19 GMT|true|true


#### 後端 
#### 取得Token POST https://oauth2.googleapis.com/token
```
request
{
  "client_id":"",
  "client_secret":"",
  "code":"",
  "grant_type":"authorization_code"
  "redirect_uri":""
}
response
{
  "email":"jerry9149@gmail.com",
  "message":"Google login successful"
}
```
#### Cookie
| Name | Value | Domain | Path | Expires | HttpOnly | Secure |
| :--: | :--: | :--: | :--: | :--: | :--: | :--: |
| token |<div style="width: 300pt">eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwiaWF0IjoxNzQwNTc2Mzc5LCJleHAiOjE3NDA2NjI3Nzl9.fu03uGkKI1CRGnAfIM4sh95nDQ9MBZZWgDJVq1_Xmqk|localhost|/|Thu, 27 Feb 2025 13:26:19 GMT|true|true
---
#### JWT 配置 (Spring Security)
JWT 會儲存在 HTTP-only Cookie，後端 SecurityConfig.java 會攔截請求並驗證 JWT。
```
http
    .cors(cors -> cors.configurationSource(corsConfigurationSource()))
    .csrf(csrf -> csrf.disable())
    .authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/**").permitAll()
            .requestMatchers("/oauth2/**").permitAll()
            .requestMatchers("/sign-out").hasRole("User")
            .requestMatchers("/api/public/**").permitAll()
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated()
    )
    .userDetailsService(userDetailsService) // 使用自定義 UserDetailsService
    .httpBasic(withDefaults());
```
結論
===
這是一個完整的 OAuth2.0 JWT 登入系統，適用於前後端分離的應用程式，確保登入資訊安全並支援主流 OAuth2 提供者 Google