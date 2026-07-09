<template>
  <view class="page">
    <view class="card">
      <!-- 头像 -->
      <view class="form-item center">
        <image class="avatar avatar-xl" :src="form.avatar" mode="aspectFill" @click="chooseAvatar" />
        <text class="text-sm text-muted mt-10">点击更换头像</text>
      </view>
      <view class="form-item">
        <text class="form-label">昵称</text>
        <input class="form-input" v-model="form.nickName" placeholder="输入昵称" />
      </view>
      <view class="form-item">
        <text class="form-label">学校</text>
        <input class="form-input" v-model="form.school" placeholder="输入学校名称" />
      </view>
      <view class="form-item">
        <text class="form-label">专业</text>
        <input class="form-input" v-model="form.major" placeholder="输入专业" />
      </view>
      <view class="form-item">
        <text class="form-label">技能 (逗号分隔)</text>
        <input class="form-input" v-model="form.skillsStr" placeholder="Python, 前端, 数据分析" />
      </view>
      <view class="form-item">
        <text class="form-label">个人介绍</text>
        <textarea class="form-textarea" v-model="form.desc" placeholder="介绍一下你自己..." :maxlength="200" />
      </view>
    </view>
    <view class="submit-section">
      <button class="btn btn-primary btn-block" :loading="loading" @click="doSave">保存</button>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return { loading: false, form: { avatar: '/static/logo.png', nickName: '', school: '', major: '', skillsStr: '', desc: '' } };
  },
  onShow() {
    const u = this.$store.state.userInfo;
    if (u) {
      this.form.avatar = u.avatar || '/static/logo.png';
      this.form.nickName = u.nickName || '';
      this.form.school = u.school || '';
      this.form.major = u.major || '';
      this.form.skillsStr = (u.skills || []).join(', ');
      this.form.desc = u.desc || '';
    }
  },
  methods: {
    chooseAvatar() {},
    async doSave() {
      this.loading = true;
      try {
        const token = this.$store.state.token || '';
        const skills = this.form.skillsStr.split(/[,，]/).map(s=>s.trim()).filter(Boolean);
        const res = await uni.request({
          url: 'http://localhost:9091/api/user/profile',
          method: 'PUT', header: { token },
          data: { ...this.form, skills }
        });
        const d = res[1] ? res[1].data : res.data;
        if (d.code === '200') {
          this.$store.commit('setUserInfo', d.data);
          uni.showToast({ title: '保存成功', icon: 'success' });
          setTimeout(() => uni.navigateBack(), 1000);
        } else {
          uni.showToast({ title: d.msg || '保存失败', icon: 'none' });
        }
      } catch (e) { uni.showToast({ title: '网络错误', icon: 'none' }); }
      this.loading = false;
    }
  }
};
</script>

<style scoped>
.page { background: #f5f5f5; min-height: 100vh; padding-bottom: 60rpx; }
.form-item { margin-bottom: 24rpx; }
.form-item.center { display: flex; flex-direction: column; align-items: center; margin-bottom: 30rpx; }
.form-label { font-size: 26rpx; color: #666; display: block; margin-bottom: 8rpx; }
.form-input { background: #f5f5f5; border-radius: 12rpx; padding: 16rpx 20rpx; font-size: 26rpx; }
.form-textarea { background: #f5f5f5; border-radius: 12rpx; padding: 16rpx 20rpx; font-size: 26rpx; min-height: 100rpx; width: 100%; box-sizing: border-box; }
.submit-section { padding: 0 30rpx; }
</style>