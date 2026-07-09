<script>
export default {
  name: "Inform"
}
</script>

<template>
  <div class="inform-container">
    <!-- 顶部提示栏 -->
    <div class="top-banner">
      📢 图书馆公告栏，请各位读者仔细阅读并遵守相关规定 📢
    </div>

    <!-- 标题 -->
    <h2 class="title">图书管理系统使用公告</h2>

    <!-- 公告内容 -->
    <div class="announcement-content-wrapper">
      <div class="intro-text">
        尊敬的读者：<br/>
        为了维护良好的图书借阅秩序，提高图书资源利用率，本系统将于近期实施新的管理规定，请各位读者仔细阅读并遵守以下规则：
      </div>

      <!-- 一、预约取书规则 -->
      <div class="section">
        <h3 class="section-title">一、预约取书规则</h3>
        <ol class="rule-list">
          <li><strong>预约有效期：</strong>成功预约图书后，请在<strong>1个自然日（24小时）内</strong>到馆取书</li>
          <li><strong>取书时间计算：</strong>从预约成功时刻开始计算，超过24小时未取书将自动取消预约</li>
          <li><strong>取书流程：</strong>携带本人有效证件到图书馆服务台办理取书手续</li>
          <li><strong>逾期处理：</strong>超过规定时间未取书，系统将自动释放该图书供其他读者预约</li>
        </ol>
      </div>

      <!-- 二、积分管理制度 -->
      <div class="section">
        <h3 class="section-title">二、积分管理制度</h3>
        
        <div class="subsection">
          <h4 class="subsection-title">积分获取</h4>
          <ul class="rule-list">
            <li>新用户注册赠送初始积分：<strong>100分</strong></li>
            <li>按时归还图书：每次<strong>+1分</strong></li>
          </ul>
        </div>

        <div class="subsection">
          <h4 class="subsection-title">积分扣除规则</h4>
          <ol class="rule-list">
            <li><strong>预约违约：</strong>预约后未在1天内取书，且未取消，每次扣除<strong>5分</strong></li>
            <li><strong>逾期还书：</strong>每逾期1天扣除<strong>5分</strong></li>
            <li><strong>图书损坏：</strong>根据损坏程度扣除<strong>10-20分</strong></li>
            <li><strong>图书丢失：</strong>除按价赔偿外，额外扣除<strong>30分</strong></li>
            <li><strong>恶意占书：</strong>频繁预约不取书行为，经核实后每次扣除<strong>20分</strong></li>
          </ol>
        </div>

        <div class="subsection">
          <h4 class="subsection-title">积分影响</h4>
          <ul class="rule-list highlight-list">
            <li><span class="warning-badge">警告</span> 积分低于<strong>50分</strong>：暂停预约功能7天</li>
            <li><span class="danger-badge">严重</span> 积分低于<strong>20分</strong>：暂停借阅功能15天</li>
            <li><span class="critical-badge">禁止</span> 积分低于<strong>0分</strong>：暂停所有借阅权限30天，需到服务台申请恢复</li>
          </ul>
        </div>
      </div>

      <!-- 三、温馨提示 -->
      <div class="section">
        <h3 class="section-title">三、温馨提示</h3>
        <ol class="rule-list">
          <li>请合理安排预约时间，避免盲目预约造成资源浪费</li>
          <li>如无法按时取书，请及时取消预约</li>
          <li>关注个人积分变化，保持良好的借阅习惯</li>
          <li>如有疑问，请联系图书馆服务台或查看系统帮助文档</li>
        </ol>
      </div>

      <!-- 四、实施时间 -->
      <div class="section implementation-section">
        <h3 class="section-title">四、实施时间</h3>
        <p class="implementation-text">
          以上规则自<strong>2026年4月1日</strong>起正式实施，请各位读者相互转告。
        </p>
        <p class="closing-text">
          感谢您的理解与配合！<br/><br/>
          <strong>图书馆管理中心</strong><br/>
          2026年3月31日
        </p>
      </div>
    </div>
  </div>
</template>


<style scoped>
.inform-container {
  padding: 20px;
  font-family: 'Microsoft YaHei', 'Arial', sans-serif;
  max-width: 1200px;
  margin: 0 auto;
}

.top-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 15px 20px;
  border-radius: 8px;
  margin-bottom: 30px;
  text-align: center;
  font-size: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.title {
  text-align: center;
  font-size: 28px;
  margin-bottom: 30px;
  color: #333;
  font-weight: bold;
  padding-bottom: 15px;
  border-bottom: 3px solid #667eea;
}

.announcement-content-wrapper {
  background-color: #ffffff;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.intro-text {
  font-size: 16px;
  line-height: 1.8;
  color: #555;
  margin-bottom: 30px;
  padding: 15px;
  background-color: #f8f9fa;
  border-left: 4px solid #667eea;
  border-radius: 4px;
}

.section {
  margin-bottom: 35px;
}

.section-title {
  font-size: 22px;
  color: #333;
  margin-bottom: 20px;
  padding-left: 15px;
  border-left: 5px solid #667eea;
  font-weight: bold;
}

.subsection {
  margin-bottom: 20px;
  margin-left: 20px;
}

.subsection-title {
  font-size: 18px;
  color: #555;
  margin-bottom: 15px;
  font-weight: bold;
}

.rule-list {
  padding-left: 25px;
  line-height: 2;
  color: #666;
  font-size: 15px;
}

.rule-list li {
  margin-bottom: 10px;
  padding-left: 5px;
}

.rule-list strong {
  color: #333;
  font-weight: 600;
}

.highlight-list {
  background-color: #fff9e6;
  padding: 15px 15px 15px 40px;
  border-radius: 6px;
  border-left: 4px solid #ffa500;
}

.warning-badge,
.danger-badge,
.critical-badge {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
  margin-right: 8px;
  color: white;
}

.warning-badge {
  background-color: #ffa500;
}

.danger-badge {
  background-color: #ff6b6b;
}

.critical-badge {
  background-color: #dc3545;
}

.implementation-section {
  background-color: #f0f7ff;
  padding: 20px;
  border-radius: 8px;
  border: 2px solid #667eea;
}

.implementation-text {
  font-size: 16px;
  color: #333;
  line-height: 1.8;
  margin-bottom: 20px;
}

.closing-text {
  font-size: 16px;
  color: #555;
  line-height: 2;
  text-align: right;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #ddd;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .inform-container {
    padding: 15px;
  }
  
  .title {
    font-size: 22px;
  }
  
  .announcement-content-wrapper {
    padding: 20px;
  }
  
  .section-title {
    font-size: 18px;
  }
  
  .rule-list {
    font-size: 14px;
  }
}
</style>