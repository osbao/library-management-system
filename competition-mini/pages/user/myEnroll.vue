<template>
  <view class="page">
    <view v-for="e in list" :key="e.id" class="enroll-card card">
      <view class="flex-between">
        <view class="flex-1">
          <text class="text-lg">{{ e.contestName }}</text>
          <text class="text-sm text-muted">{{ e.role }}</text>
        </view>
        <view class="tag" :class="e.statusColor">{{ e.statusText || '已报名' }}</view>
      </view>
      <view class="flex-between mt-20">
        <text class="text-sm text-muted">报名时间：{{ e.createdAt }}</text>
        <text v-if="e.teamName" class="text-sm text-primary">团队：{{ e.teamName }}</text>
      </view>
    </view>
    <view v-if="list.length===0 && !loading" class="empty">
      <text>还没有报名任何竞赛</text>
      <button class="btn btn-primary mt-30" @click="goContest">去看看竞赛</button>
    </view>
    <view style="height: 80rpx;"></view>
  </view>
</template>

<script>
export default {
  data() { return { list: [], loading: false }; },
  onShow() { this.fetchList(); },
  methods: {
    async fetchList() {
      this.loading = true;
      try {
        const token = this.$store.state.token || '';
        const res = await uni.request({ url: 'http://localhost:9091/api/contest/my-enrollments', method: 'GET', header: { token } });
        const d = res[1] ? res[1].data : res.data;
        this.list = d.data?.list || [];
      } catch (e) { this.list = []; }
      this.loading = false;
    },
    goContest() { uni.switchTab({ url: '/pages/contest/list' }); }
  }
};
</script>

<style scoped>
.page { background: #f5f5f5; min-height: 100vh; padding: 20rpx; }
.enroll-card { margin-bottom: 20rpx; }
</style>