<template>
  <view class="page" v-if="detail">
    <view class="card">
      <text class="enroll-title">{{ detail.name }}</text>
      <text class="text-sm text-muted">{{ detail.org }} | {{ detail.level }}</text>
    </view>

    <view class="card">
      <text class="section-label">报名信息</text>
      <view class="form-item">
        <text class="form-label">你的昵称</text>
        <input class="form-input" v-model="form.nickName" placeholder="请输入显示名称" />
      </view>
      <view class="form-item">
        <text class="form-label">联系方式</text>
        <input class="form-input" v-model="form.phone" placeholder="请输入手机号" />
      </view>
      <view class="form-item">
        <text class="form-label">参赛角色</text>
        <picker :range="roleOptions" @change="onRoleChange">
          <view class="form-picker">{{ form.role || '请选择角色' }}</view>
        </picker>
      </view>
      <view class="form-item">
        <text class="form-label">是否已有团队</text>
        <view class="radio-group">
          <view class="radio-item" :class="{checked:form.hasTeam==='0'}" @click="form.hasTeam='0'">没有</view>
          <view class="radio-item" :class="{checked:form.hasTeam==='1'}" @click="form.hasTeam='1'">已有</view>
        </view>
      </view>
      <view class="form-item" v-if="form.hasTeam==='0'">
        <text class="form-label">是否允许组队</text>
        <view class="radio-group">
          <view class="radio-item" :class="{checked:form.openTeam==='1'}" @click="form.openTeam='1'">允许</view>
          <view class="radio-item" :class="{checked:form.openTeam==='0'}" @click="form.openTeam='0'">不允许</view>
        </view>
      </view>
      <view class="form-item">
        <text class="form-label">个人简介 / 参赛宣言</text>
        <textarea class="form-textarea" v-model="form.desc" placeholder="展示你的优势..." :maxlength="200" />
      </view>
    </view>

    <view class="submit-section">
      <button class="btn btn-primary btn-block" :loading="loading" @click="doEnroll">确认报名</button>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      id: '',
      detail: null,
      loading: false,
      roleOptions: ['队长/负责人', '技术开发', 'UI/设计', '文案/策划', '路演/答辩', '数据分析', '市场调研'],
      form: { nickName: '', phone: '', role: '', hasTeam: '0', openTeam: '1', desc: '' }
    };
  },
  onLoad(opt) {
    this.id = opt.id;
    this.fetchDetail();
    const u = this.$store.state.userInfo;
    if (u) this.form.nickName = u.nickName || '';
  },
  methods: {
    async fetchDetail() {
      try {
        const token = this.$store.state.token || '';
        const res = await uni.request({ url: 'http://localhost:9091/api/contest/' + this.id, method: 'GET', header: { token } });
        const d = res[1] ? res[1].data : res.data;
        this.detail = d.data || null;
      } catch (e) {}
    },
    onRoleChange(e) { this.form.role = this.roleOptions[e.detail.value]; },
    async doEnroll() {
      if (!this.form.phone) return uni.showToast({ title: '请输入联系方式', icon: 'none' });
      if (!this.form.role) return uni.showToast({ title: '请选择参赛角色', icon: 'none' });
      this.loading = true;
      try {
        const token = this.$store.state.token || '';
        const res = await uni.request({
          url: 'http://localhost:9091/api/contest/register',
          method: 'POST',
          header: { token },
          data: { contestId: this.id, ...this.form }
        });
        const d = res[1] ? res[1].data : res.data;
        if (d.code === '200') {
          uni.showToast({ title: '报名成功！', icon: 'success' });
          setTimeout(() => uni.navigateBack(), 1000);
        } else {
          uni.showToast({ title: d.msg || '报名失败', icon: 'none' });
        }
      } catch (e) { uni.showToast({ title: '网络错误', icon: 'none' }); }
      this.loading = false;
    }
  }
};
</script>

<style scoped>
.page { background: #f5f5f5; min-height: 100vh; padding-bottom: 40rpx; }
.enroll-title { font-size: 32rpx; font-weight: bold; }
.section-label { font-size: 28rpx; font-weight: 600; margin-bottom: 20rpx; display: block; }
.form-item { margin-bottom: 24rpx; }
.form-label { font-size: 26rpx; color: #666; display: block; margin-bottom: 8rpx; }
.form-input { background: #f5f5f5; border-radius: 12rpx; padding: 16rpx 20rpx; font-size: 26rpx; }
.form-picker { background: #f5f5f5; border-radius: 12rpx; padding: 16rpx 20rpx; font-size: 26rpx; color: #333; }
.form-textarea { background: #f5f5f5; border-radius: 12rpx; padding: 16rpx 20rpx; font-size: 26rpx; min-height: 120rpx; width: 100%; box-sizing: border-box; }
.radio-group { display: flex; gap: 20rpx; }
.radio-item { padding: 12rpx 30rpx; border-radius: 30rpx; font-size: 26rpx; background: #f5f5f5; color: #666; }
.radio-item.checked { background: #4A90D9; color: #fff; }
.submit-section { padding: 30rpx 30rpx 60rpx; }
</style>