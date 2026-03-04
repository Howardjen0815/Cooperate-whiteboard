Collaborative Whiteboard System (Java RMI)
這是一個基於 Java RMI (Remote Method Invocation) 技術開發的多人實時協作白板系統 。系統採用中心化 P2P 架構，由管理員（Manager）啟動 RMI 服務並兼任伺服器，負責同步所有用戶的繪圖與通訊數據 。
+4

🌟 核心功能
1. 實時協作繪圖

多功能工具箱：支持自由書寫（Freedraw）、直線、矩形、圓形、三角形及文字工具 。
+1


畫布同步：繪圖動作封裝為 DrawAction 物件，透過伺服器廣播至所有客戶端，實現毫秒級同步 。


繪圖者標示：當有人正在操作時，用戶列表會以綠色背景標示該繪圖者名稱 。

2. 即時通訊系統

聊天室：用戶可進行實時文字交流 。
+1


自動同步歷史：新用戶加入時，系統會自動加載過往的聊天紀錄，確保資訊不中斷 。
+1

3. 管理員專屬權限 (Manager)

准入控制：新用戶加入需經過管理員批准（彈出確認視窗） 。
+1


用戶管理：可強制剔除（Kick out）特定用戶 。
+2


文件操作：具備新建、開啟、儲存及另存新檔（JSON 格式）白板的功能 。
+1

🛠️ 技術架構

通訊協議：Java RMI，利用動態代理機制簡化遠端調用 。
+1


併發處理：利用 RMI 內建線程池，並針對關鍵資源（如聊天訊息、加入請求）實施線程同步保護 。
+2


數據儲存：使用 GSON 將畫布數據序列化為 JSON 檔案，具備跨平台可讀性 。
+1


GUI 框架：Java Swing，並確保所有 UI 更新皆在 Event Dispatch Thread (EDT) 執行以保證穩定性 。
+1

🚀 快速上手
1. 環境需求
Java JDK 8 或以上版本。

依賴庫：lib/gson-2.13.1.jar 。

2. 運行指令
管理員端 (Manager)
啟動 RMI 註冊表並建立白板：

Bash

java -Dsun.java2d.metal=false -jar CreateWhiteBoard.jar <IP> <Port> <Username>
# 範例
java -Dsun.java2d.metal=false -jar CreateWhiteBoard.jar 127.0.0.1 1025 Howard

注意：在 macOS 上建議加上 -Dsun.java2d.metal=false 以避免字體渲染問題 。

客戶端 (Client)
加入現有的白板：

Bash

java -jar MemberJoin.jar <IP> <Port> <Username>
# 範例
java -jar MemberJoin.jar 127.0.0.1 1025 Jack
