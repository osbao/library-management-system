<template>
  <view class="page">
    <!-- 用户信息头部 -->
    <view class="user-header">
      <image class="avatar avatar-xl" :src="user.avatar" mode="aspectFill" @click="goEdit" />
      <text class="user-name">{{ user.nickName || '未设置昵称' }}</text>
      <text class="user-school">{{ user.school || '未设置学校' }}</text>
      <view class="flex-wrap mt-10" v-if="user.skills && user.skills.length > 0">
        <text v-for="s in user.skills" :key="s" class="skill-tag">{{ s }}</text>
      </view>
      <view class="edit-btn" @click="goEdit">编辑资料</view>
    </view>

    <!-- 数据统计 -->
    <view class="card">
      <view class="stats-row">
        <view class="stat-item">
          <text class="stat-num">{{ user.enrollCount || 0 }}</text>
          <text class="stat-label">已报名</text>
        </view>
        <view class="stat-item">
          <text class="stat-num">{{ user.teamCount || 0 }}</text>
          <text class="stat-label">团队</text>
        </view>
        <view class="stat-item">
          <text class="stat-num">{{ user.inviteCount || 0 }}</text>
          <text class="stat-label">邀请</text>
        </view>
      </view>
    </view>

    <!-- 菜单 -->
    <view class="card">
      <view class="menu-item" @click="go('/pages/user/myEnroll')">
        <text class="menu-label">📋 我的报名</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @click="go('/pages/user/myTeam')">
        <text class="menu-label">👥 我的团队</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @click="go('/pages/message/list')">
        <text class="menu-label">🔔 我的消息</text>
        <view class="flex-center">
          <view v-if="unreadCount > 0" class="badge">{{ unreadCount }}</view>
          <text class="menu-arrow">›</text>
        </view>
      </view>
      <view class="menu-item" @click="go('/pages/index/index')">
        <text class="menu-label">🏠 返回首页</text>
        <text class="menu-arrow">›</text>
      </view>
    </view>

    <!-- 退出登录 -->
    <view class="logout-section">
      <button class="btn btn-block btn-logout" @click="doLogout">退出登录</button>
    </view>

    <view style="height: 100rpx;"></view>
  </view>
</template>

<script>
export default {
  data() {
    return { user: {}, unreadCount: 0 };
  },
  onShow() {
    this.user = this.$store.state.userInfo || {};
    this.fetchUnread();
  },
  methods: {
    go(url) { uni.navigateTo({ url }); },
    goEdit() { uni.navigateTo({ url: '/pages/user/edit' }); },
    async fetchUnread() {
      try {
        const token = this.$store.state.token || '';
        const res = await uni.request({
          url: 'http://localhost:9091/api/message/list',
          method: 'GET', header: { token }
        });
        const d = res[1] ? res[1].data : res.data;
        this.unreadCount = d.data?.unreadCount || 0;
      } catch (e) {}
    },
    doLogout() {
      uni.showModal({
        title: '提示',
        content: '确定要退出登录吗？',
        success: (r) => {
          if (r.confirm) {
            this.$store.commit('setToken', '');
            this.$store.commit('setUserInfo', null);
            uni.reLaunch({ url: '/pages/login/index' });
          }
        }
      });
    }
  }
};
</script>

<style scoped>
.page { background: #f5f5f5; min-height: 100vh; }
.user-header { background: linear-gradient(135deg, #4A90D9, #357ABD); padding: 60rpx 30rpx 40rpx; color: #fff; text-align: center; display: flex; flex-direction: column; align-items: center; }
.user-name { font-size: 36rpx; font-weight: bold; margin-top: 16rpx; }
.user-school { font-size: 24rpx; opacity: 0.8; margin-top: 6rpx; }
.skill-tag { background: rgba(255,255,255,0.2); padding: 4rpx 16rpx; border-radius: 20rpx; font-size: 22rpx; margin: 4rpx; }
.edit-btn { margin-top: 20rpx; padding: 10rpx 40rpx; border: 2rpx solid rgba(255,255,255,0.6); border-radius: 40rpx; font-size: 24rpx; }
.stats-row { display: flex; justify-content: space-around; }
.stat-item { text-align: center; }
.stat-num { display: block; font-size: 40rpx; font-weight: bold; color: #4A90D9; }
.stat-label { font-size: 22rpx; color: #999; margin-top: 4rpx; }
.menu-item { display: flex; justify-content: space-between; align-items: center; padding: 28rpx 0; border-bottom: 1rpx solid #f0f0f0; }
.menu-item:last-child { border-bottom: none; }
.menu-label { font-size: 28rpx; }
.menu-arrow { font-size: 32rpx; color: #ccc; }
.logout-section { padding: 40rpx 30rpx; }
.btn-logout { background: #fff; color: #ff4444; border: 1rpx solid #ff4444; }
</style>