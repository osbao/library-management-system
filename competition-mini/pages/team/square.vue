<template>
  <view class="page">
    <view class="search-row">
      <input class="search-input" v-model="keyword" placeholder="搜索竞赛、技能..." @confirm="fetchList" />
      <text class="search-btn" @click="fetchList">搜索</text>
    </view>

    <view class="team-list">
      <view v-for="t in list" :key="t.id" class="team-card card" @click="goDetail(t.id)">
        <view class="flex-between">
          <view class="flex-center">
            <image class="avatar" :src="t.userAvatar" mode="aspectFill" />
            <view class="ml-10">
              <text class="text-lg">{{ t.userName }}</text>
              <text class="text-sm text-muted">发起 · {{ t.contestName }}</text>
            </view>
          </view>
          <view class="tag" :class="t.statusColor">{{ t.statusText }}</view>
        </view>
        <view class="mt-10 flex-wrap">
          <text v-for="s in t.tags" :key="s" class="tag">{{ s }}</text>
        </view>
        <text class="desc mt-10">{{ t.desc }}</text>
        <view class="flex-between mt-20">
          <text class="text-sm text-muted">{{ t.members || 1 }}/{{ t.maxMembers || 5 }} 人</text>
          <text class="text-sm text-primary">{{ t.createdAt }}</text>
        </view>
      </view>
    </view>

    <view v-if="list.length===0 && !loading" class="empty">
      <text>暂无组队信息</text>
      <text class="text-sm text-muted mt-10">成为第一个发起组队的人吧！</text>
    </view>

    <!-- 发布按钮 -->
    <view class="float-btn" @click="goCreate">
      <text class="float-icon">+</text>
    </view>

    <view style="height: 140rpx;"></view>
  </view>
</template>

<script>
export default {
  data() {
    return { keyword: '', list: [], loading: false };
  },
  onShow() { this.fetchList(); },
  onPullDownRefresh() { this.fetchList().finally(() => uni.stopPullDownRefresh()); },
  methods: {
    async fetchList() {
      this.loading = true;
      try {
        const token = this.$store.state.token || '';
        const res = await uni.request({
          url: 'http://localhost:9091/api/team/list',
          method: 'GET',
          header: { token },
          data: { keyword: this.keyword }
        });
        const d = res[1] ? res[1].data : res.data;
        this.list = d.data?.list || [];
      } catch (e) { this.list = []; }
      this.loading = false;
    },
    goDetail(id) { uni.navigateTo({ url: '/pages/team/detail?id=' + id }); },
    goCreate() { uni.navigateTo({ url: '/pages/team/create' }); }
  }
};
</script>

<style scoped>
.page { background: #f5f5f5; min-height: 100vh; }
.search-row { display: flex; padding: 20rpx; align-items: center; }
.search-input { flex: 1; background: #fff; border-radius: 40rpx; padding: 14rpx 24rpx; font-size: 26rpx; }
.search-btn { margin-left: 16rpx; font-size: 26rpx; color: #4A90D9; white-space: nowrap; }
.team-list { padding: 0 20rpx; }
.team-card { margin-bottom: 20rpx; }
.desc { font-size: 24rpx; color: #666; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.float-btn { position: fixed; bottom: 60rpx; right: 40rpx; width: 100rpx; height: 100rpx; border-radius: 50%; background: #4A90D9; box-shadow: 0 4rpx 20rpx rgba(74,144,217,0.4); display: flex; align-items: center; justify-content: center; z-index: 99; }
.float-icon { font-size: 50rpx; color: #fff; line-height: 1; }
</style>