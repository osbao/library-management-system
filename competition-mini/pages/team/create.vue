<template>
  <view class="page">
    <view class="card">
      <text class="section-label">发布组队信息</text>
      <view class="form-item">
        <text class="form-label">竞赛名称</text>
        <input class="form-input" v-model="form.contestName" placeholder="如：挑战杯" />
      </view>
      <view class="form-item">
        <text class="form-label">团队简介</text>
        <textarea class="form-textarea" v-model="form.desc" placeholder="描述你的团队目标和需求..." :maxlength="200" />
      </view>
      <view class="form-item">
        <text class="form-label">所需技能 (逗号分隔)</text>
        <input class="form-input" v-model="form.skillsStr" placeholder="Python, 前端, 路演" />
      </view>
      <view class="form-item">
        <text class="form-label">需要人数 (不含自己)</text>
        <input class="form-input" type="number" v-model="form.maxMembers" placeholder="3" />
      </view>
      <view class="form-item">
        <text class="form-label">你的角色</text>
        <input class="form-input" v-model="form.role" placeholder="如：技术开发" />
      </view>
    </view>
    <view class="submit-section">
      <button class="btn btn-primary btn-block" :loading="loading" @click="doSubmit">发布组队</button>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return { loading: false, form: { contestName: '', desc: '', skillsStr: '', maxMembers: '3', role: '' } };
  },
  methods: {
    async doSubmit() {
      if (!this.form.contestName) return uni.showToast({ title: '请填写竞赛名称', icon: 'none' });
      if (!this.form.desc) return uni.showToast({ title: '请填写团队简介', icon: 'none' });
      this.loading = true;
      try {
        const token = this.$store.state.token || '';
        const skills = this.form.skillsStr.split(/[,，]/).map(s=>s.trim()).filter(Boolean);
        const res = await uni.request({
          url: 'http://localhost:9091/api/team/create',
          method: 'POST', header: { token },
          data: { contestName: this.form.contestName, desc: this.form.desc, tags: skills, maxMembers: parseInt(this.form.maxMembers)||3, role: this.form.role }
        });
        const d = res[1] ? res[1].data : res.data;
        if (d.code === '200') {
          uni.showToast({ title: '发布成功！', icon: 'success' });
          setTimeout(() => uni.navigateBack(), 1000);
        } else {
          uni.showToast({ title: d.msg || '发布失败', icon: 'none' });
        }
      } catch (e) { uni.showToast({ title: '网络错误', icon: 'none' }); }
      this.loading = false;
    }
  }
};
</script>

<style scoped>
.page { background: #f5f5f5; min-height: 100vh; padding-bottom: 60rpx; }
.section-label { font-size: 28rpx; font-weight: 600; margin-bottom: 20rpx; display: block; }
.form-item { margin-bottom: 24rpx; }
.form-label { font-size: 26rpx; color: #666; display: block; margin-bottom: 8rpx; }
.form-input { background: #f5f5f5; border-radius: 12rpx; padding: 16rpx 20rpx; font-size: 26rpx; }
.form-textarea { background: #f5f5f5; border-radius: 12rpx; padding: 16rpx 20rpx; font-size: 26rpx; min-height: 120rpx; width: 100%; box-sizing: border-box; }
.submit-section { padding: 30rpx; }
</style>