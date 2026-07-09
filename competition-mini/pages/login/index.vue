<template>
  <view class="login-page">
    <view class="login-header">
      <image class="logo" src="/static/icons/logo.png" mode="aspectFit" />
      <text class="app-name">竞伙伴</text>
      <text class="slogan">大学生竞赛组队，一拍即合</text>
    </view>

    <view class="login-body">
      <view class="title">选择你的竞赛身份</view>

      <view class="user-grid">
        <view
          v-for="user in presetUsers"
          :key="user.nickName"
          class="user-card"
          :class="{ active: selectedNick === user.nickName }"
          @click="selectUser(user)"
        >
          <image class="user-avatar" :src="user.avatar" mode="aspectFill" />
          <text class="user-name">{{ user.nickName }}</text>
          <text class="user-school">{{ user.school }}</text>
          <view class="user-skills">
            <text v-for="s in user.skills.slice(0,3)" :key="s" class="tag tag-sm">{{ s }}</text>
          </view>
        </view>
      </view>

      <view v-if="selectedNick" class="confirm-section">
        <text class="confirm-hint">将以 <text class="text-primary">{{ selectedUser.realName }}</text> 的身份登录</text>
        <button
          class="btn btn-primary btn-block"
          :loading="loading"
          @click="doLogin"
        >
          进入竞伙伴
        </button>
      </view>
    </view>

    <view class="login-footer">
      <text class="text-muted text-sm">登录即表示同意《用户协议》和《隐私政策》</text>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      selectedNick: '',
      loading: false,
      presetUsers: [
        { nickName: '代码诗人', avatar: '/static/icons/default-avatar.png', realName: '张伟', school: '清华大学', skills: ['Python', 'C++', '算法竞赛'] },
        { nickName: '设计师小王', avatar: '/static/icons/default-avatar.png', realName: '王芳', school: '中央美术学院', skills: ['UI设计', '海报设计', '视频剪辑'] },
        { nickName: '数据魔法师', avatar: '/static/icons/default-avatar.png', realName: '李明', school: '北京大学', skills: ['Python', '数据分析', '机器学习'] },
        { nickName: '前端小旋风', avatar: '/static/icons/default-avatar.png', realName: '赵强', school: '浙江大学', skills: ['JavaScript', '前端开发', '后端开发'] },
        { nickName: '路演达人', avatar: '/static/icons/default-avatar.png', realName: '陈雪', school: '复旦大学', skills: ['路演答辩', '文案策划', '市场调研'] },
        { nickName: '摄影小哥', avatar: '/static/icons/default-avatar.png', realName: '刘洋', school: '中国传媒大学', skills: ['摄影摄像', '视频剪辑', '文案策划'] },
        { nickName: '嵌入式达人', avatar: '/static/icons/default-avatar.png', realName: '周杰', school: '哈尔滨工业大学', skills: ['嵌入式开发', 'C++', 'Python'] },
        { nickName: '财务小能手', avatar: '/static/icons/default-avatar.png', realName: '黄丽', school: '上海财经大学', skills: ['财务分析', '市场调研', '项目管理'] }
      ]
    };
  },
  computed: {
    selectedUser() {
      return this.presetUsers.find(u => u.nickName === this.selectedNick) || {};
    }
  },
  methods: {
    selectUser(user) {
      this.selectedNick = user.nickName;
    },
    async doLogin() {
      if (!this.selectedNick) return;
      this.loading = true;
      const result = await this.$store.dispatch('login', {
        nickName: this.selectedNick,
        avatar: this.selectedUser.avatar
      });
      this.loading = false;
      if (result) {
        uni.showToast({ title: '登录成功', icon: 'success' });
        setTimeout(() => {
          uni.switchTab({ url: '/pages/index/index' });
        }, 500);
      } else {
        uni.showToast({ title: '登录失败，请检查网络', icon: 'none' });
      }
    }
  },
  onLoad() {
    if (this.$store.state.isLogin) {
      uni.switchTab({ url: '/pages/index/index' });
    }
  }
};
</script>

<style scoped>
.login-page { min-height: 100vh; background: linear-gradient(180deg, #e8f4fd 0%, #f5f5f5 100%); }
.login-header { display: flex; flex-direction: column; align-items: center; padding: 80rpx 0 40rpx; }
.logo { width: 120rpx; height: 120rpx; border-radius: 30rpx; }
.app-name { font-size: 48rpx; font-weight: bold; color: #333; margin-top: 20rpx; }
.slogan { font-size: 26rpx; color: #4A90D9; margin-top: 10rpx; }

.login-body { padding: 0 30rpx; }
.title { font-size: 30rpx; font-weight: 600; color: #333; margin-bottom: 20rpx; }

.user-grid { display: flex; flex-wrap: wrap; margin: 0 -10rpx; }
.user-card { width: calc(50% - 20rpx); margin: 10rpx; background: #fff; border-radius: 16rpx; padding: 24rpx; text-align: center; border: 2rpx solid transparent; transition: all 0.2s; }
.user-card.active { border-color: #4A90D9; background: #f0f7ff; }
.user-avatar { width: 80rpx; height: 80rpx; border-radius: 50%; margin-bottom: 12rpx; }
.user-name { display: block; font-size: 28rpx; font-weight: 500; color: #333; }
.user-school { display: block; font-size: 22rpx; color: #999; margin: 6rpx 0; }
.user-skills { display: flex; flex-wrap: wrap; justify-content: center; margin-top: 8rpx; }
.tag-sm { font-size: 18rpx; padding: 2rpx 10rpx; margin: 2rpx; }

.confirm-section { margin-top: 40rpx; text-align: center; }
.confirm-hint { display: block; font-size: 26rpx; color: #666; margin-bottom: 20rpx; }

.login-footer { padding: 60rpx 0 40rpx; text-align: center; }
</style>