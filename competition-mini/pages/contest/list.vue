<template>
  <view class="page">
    <!-- 筛选栏 -->
    <view class="filter-bar">
      <view
        v-for="(f,i) in filters"
        :key="i"
        class="filter-tab"
        :class="{ active: activeFilter === i }"
        @click="switchFilter(i)"
      >
        <text>{{ f.label }}</text>
        <text v-if="f.icon" class="filter-arrow">▼</text>
      </view>
    </view>

    <!-- 搜索框 -->
    <view class="search-row">
      <input class="search-input" v-model="keyword" placeholder="搜索竞赛名称..." @confirm="doSearch" />
      <text class="search-btn" @click="doSearch">搜索</text>
    </view>

    <!-- 竞赛列表 -->
    <view class="list">
      <view
        v-for="c in list"
        :key="c.id"
        class="contest-card card"
        @click="goDetail(c.id)"
      >
        <view class="flex-between">
          <view class="flex-center flex-1">
            <view class="cover-img" :style="{background: c.bg || '#667eea'}">
              <text class="cover-level">{{ c.level }}</text>
            </view>
            <view class="ml-20 flex-1">
              <text class="contest-name">{{ c.name }}</text>
              <text class="text-sm text-muted">{{ c.org }}</text>
            </view>
          </view>
          <view class="tag tag-primary">{{ c.statusText || '报名中' }}</view>
        </view>
        <view class="flex-between mt-20">
          <view class="flex-wrap">
            <text v-for="t in c.tags" :key="t" class="tag">{{ t }}</text>
          </view>
          <text class="text-sm text-muted">截止 {{ c.deadlineText }}</text>
        </view>
      </view>
    </view>

    <view v-if="list.length===0 && !loading" class="empty">
      <text>暂无竞赛数据</text>
    </view>

    <view style="height: 120rpx;"></view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      filters: [
        { label: '全部', icon: false },
        { label: '国家级', icon: false },
        { label: '省部级', icon: false },
        { label: '校级', icon: false },
        { label: '报名中', icon: false }
      ],
      activeFilter: 0,
      keyword: '',
      list: [],
      loading: false
    };
  },
  onShow() { this.fetchList(); },
  onPullDownRefresh() { this.fetchList().finally(() => uni.stopPullDownRefresh()); },
  methods: {
    switchFilter(i) { this.activeFilter = i; this.fetchList(); },
    doSearch() { this.fetchList(); },
    async fetchList() {
      this.loading = true;
      try {
        const token = this.$store.state.token || '';
        const levelMap = ['', '国家级', '省部级', '校级', ''];
        const statusMap = ['', '', '', '', '报名中'];
        const res = await uni.request({
          url: 'http://localhost:9091/api/contest/list',
          method: 'GET',
          header: { token },
          data: { keyword: this.keyword, level: levelMap[this.activeFilter] || '', status: statusMap[this.activeFilter] || '' }
        });
        const d = res[1] ? res[1].data : res.data;
        this.list = d.data?.list || [];
      } catch (e) { this.list = []; }
      this.loading = false;
    },
    goDetail(id) { uni.navigateTo({ url: '/pages/contest/detail?id=' + id }); }
  }
};
</script>

<style scoped>
.page { background: #f5f5f5; min-height: 100vh; }
.filter-bar { display: flex; background: #fff; padding: 16rpx 20rpx; white-space: nowrap; }
.filter-tab { flex: 1; text-align: center; font-size: 26rpx; color: #666; padding: 8rpx 0; }
.filter-tab.active { color: #4A90D9; font-weight: 600; }
.filter-arrow { font-size: 18rpx; margin-left: 4rpx; }

.search-row { display: flex; padding: 20rpx; align-items: center; }
.search-input { flex: 1; background: #fff; border-radius: 40rpx; padding: 14rpx 24rpx; font-size: 26rpx; }
.search-btn { margin-left: 16rpx; font-size: 26rpx; color: #4A90D9; white-space: nowrap; }

.list { padding: 0 20rpx; }
.contest-card { margin-bottom: 20rpx; }
.cover-img { width: 100rpx; height: 100rpx; border-radius: 12rpx; display: flex; align-items: flex-end; padding: 10rpx; }
.cover-level { background: rgba(255,255,255,0.85); padding: 2rpx 10rpx; border-radius: 8rpx; font-size: 18rpx; }
.contest-name { display: block; font-size: 28rpx; font-weight: 500; color: #333; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
</style>