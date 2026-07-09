<template>
  <view class="page" v-if="detail">
    <!-- 头部信息 -->
    <view class="detail-header" :style="{background: detail.bg || '#667eea'}">
      <text class="header-level">{{ detail.level }}</text>
      <text class="header-name">{{ detail.name }}</text>
      <text class="header-org">{{ detail.org }}</text>
      <view class="header-meta">
        <text>报名截止：{{ detail.deadlineText }}</text>
        <text class="ml-20">比赛时间：{{ detail.contestTime }}</text>
      </view>
      <view class="header-actions">
        <view class="tag tag-primary">{{ detail.statusText || '报名中' }}</view>
        <view class="tag ml-10" v-if="detail.enrolled">已报名 {{ detail.enrolled }} 人</view>
      </view>
    </view>

    <!-- 标签 -->
    <view class="card">
      <text class="section-label">竞赛标签</text>
      <view class="flex-wrap mt-10">
        <text v-for="t in detail.tags" :key="t" class="tag">{{ t }}</text>
      </view>
    </view>

    <!-- 竞赛介绍 -->
    <view class="card">
      <text class="section-label">竞赛介绍</text>
      <text class="desc-text mt-10">{{ detail.desc }}</text>
    </view>

    <!-- 参赛要求 -->
    <view class="card">
      <text class="section-label">参赛要求</text>
      <view class="requirement-list mt-10">
        <view v-for="(r,i) in detail.requirements" :key="i" class="req-item">
          <text class="req-dot">•</text>
          <text>{{ r }}</text>
        </view>
      </view>
    </view>

    <!-- 底部按钮 -->
    <view class="bottom-bar">
      <button class="btn btn-primary btn-block" @click="goEnroll">
        {{ detail.enrolled ? '立即报名' : '立即报名' }}
      </button>
    </view>
  </view>
  <view v-else class="empty">
    <text>加载中...</text>
  </view>
</template>

<script>
export default {
  data() {
    return { id: '', detail: null };
  },
  onLoad(opt) {
    this.id = opt.id;
    this.fetchDetail();
  },
  methods: {
    async fetchDetail() {
      try {
        const token = this.$store.state.token || '';
        const res = await uni.request({
          url: 'http://localhost:9091/api/contest/detail?id=' + this.id,
          method: 'GET',
          header: { token }
        });
        const d = res[1] ? res[1].data : res.data;
        this.detail = d.data || null;
      } catch (e) {}
    },
    goEnroll() {
      uni.navigateTo({ url: '/pages/contest/enroll?id=' + this.id });
    }
  }
};
</script>

<style scoped>
.page { background: #f5f5f5; min-height: 100vh; padding-bottom: 140rpx; }
.detail-header { padding: 50rpx 30rpx 40rpx; color: #fff; }
.header-level { font-size: 22rpx; background: rgba(255,255,255,0.25); padding: 4rpx 16rpx; border-radius: 20rpx; }
.header-name { display: block; font-size: 36rpx; font-weight: bold; margin-top: 16rpx; }
.header-org { display: block; font-size: 24rpx; opacity: 0.85; margin-top: 8rpx; }
.header-meta { margin-top: 20rpx; font-size: 22rpx; opacity: 0.8; }
.header-actions { margin-top: 16rpx; display: flex; }

.section-label { font-size: 28rpx; font-weight: 600; color: #333; }
.desc-text { font-size: 26rpx; color: #666; line-height: 1.6; white-space: pre-wrap; }
.requirement-list { }
.req-item { display: flex; font-size: 26rpx; color: #666; margin-bottom: 10rpx; line-height: 1.5; }
.req-dot { margin-right: 10rpx; }

.bottom-bar { position: fixed; bottom: 0; left: 0; right: 0; padding: 20rpx 30rpx; padding-bottom: calc(20rpx + env(safe-area-inset-bottom)); background: #fff; box-shadow: 0 -2rpx 10rpx rgba(0,0,0,0.05); }
</style>