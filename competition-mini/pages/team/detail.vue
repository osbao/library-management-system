<template>
  <view class="page" v-if="detail">
    <!-- 团队头部 -->
    <view class="team-header" :style="{background: detail.bg || '#4A90D9'}">
      <view class="flex-center">
        <image class="avatar avatar-lg" :src="detail.userAvatar" mode="aspectFill" />
        <view class="ml-20">
          <text class="header-name">{{ detail.userName }}</text>
          <text class="text-sm" style="opacity:0.8;">{{ detail.contestName }}</text>
        </view>
      </view>
      <view class="tag mt-20" :class="detail.statusColor">{{ detail.statusText }}</view>
    </view>

    <!-- 团队描述 -->
    <view class="card">
      <text class="section-label">团队介绍</text>
      <text class="desc-text">{{ detail.desc }}</text>
    </view>

    <!-- 标签 -->
    <view class="card">
      <text class="section-label">技能需求</text>
      <view class="flex-wrap mt-10">
        <text v-for="t in detail.tags" :key="t" class="tag">{{ t }}</text>
      </view>
    </view>

    <!-- 成员列表 -->
    <view class="card">
      <view class="flex-between">
        <text class="section-label">团队成员 ({{ detail.memberList?.length || 1 }}/{{ detail.maxMembers || 5 }})</text>
      </view>
      <view v-for="m in detail.memberList" :key="m.id" class="member-item">
        <image class="avatar avatar-sm" :src="m.avatar" mode="aspectFill" />
        <view class="ml-10 flex-1">
          <text>{{ m.nickName }}</text>
          <text class="text-sm text-muted">{{ m.role }}</text>
        </view>
        <view class="tag" v-if="m.skills"> {{ m.skills[0] }} </view>
      </view>
    </view>

    <!-- 底部 -->
    <view class="bottom-bar">
      <button
        class="btn btn-primary btn-block"
        :disabled="detail.statusText==='已满员'"
        @click="doJoin"
      >
        {{ detail.statusText==='已满员' ? '已满员' : '申请加入' }}
      </button>
    </view>
  </view>
</template>

<script>
export default {
  data() { return { id: '', detail: null }; },
  onLoad(opt) { this.id = opt.id; this.fetchDetail(); },
  methods: {
    async fetchDetail() {
      try {
        const token = this.$store.state.token || '';
        const res = await uni.request({
          url: 'http://localhost:9091/api/team/detail?id=' + this.id,
          method: 'GET', header: { token }
        });
        const d = res[1] ? res[1].data : res.data;
        this.detail = d.data || null;
      } catch (e) {}
    },
    async doJoin() {
      try {
        const token = this.$store.state.token || '';
        const res = await uni.request({
          url: 'http://localhost:9091/api/team/join',
          method: 'POST', header: { token },
          data: { teamId: this.id }
        });
        const d = res[1] ? res[1].data : res.data;
        if (d.code === '200') {
          uni.showToast({ title: '申请已发送！', icon: 'success' });
        } else {
          uni.showToast({ title: d.msg || '操作失败', icon: 'none' });
        }
      } catch (e) { uni.showToast({ title: '网络错误', icon: 'none' }); }
    }
  }
};
</script>

<style scoped>
.page { background: #f5f5f5; min-height: 100vh; padding-bottom: 140rpx; }
.team-header { padding: 50rpx 30rpx 40rpx; color: #fff; }
.header-name { font-size: 34rpx; font-weight: bold; }
.section-label { font-size: 28rpx; font-weight: 600; }
.desc-text { font-size: 26rpx; color: #666; line-height: 1.6; white-space: pre-wrap; display: block; margin-top: 10rpx; }
.member-item { display: flex; align-items: center; padding: 16rpx 0; border-bottom: 1rpx solid #f0f0f0; }
.member-item:last-child { border-bottom: none; }
.bottom-bar { position: fixed; bottom: 0; left: 0; right: 0; padding: 20rpx 30rpx; padding-bottom: calc(20rpx + env(safe-area-inset-bottom)); background: #fff; box-shadow: 0 -2rpx 10rpx rgba(0,0,0,0.05); }
</style>