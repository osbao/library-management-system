<template>
  <view class="page">
    <view class="search-header">
      <view class="search-input-row">
        <input class="search-input" v-model="keyword" placeholder="搜索竞赛、团队、技能..." focus @confirm="doSearch" />
        <text class="search-btn" @click="doSearch">搜索</text>
      </view>
    </view>

    <view class="history-section" v-if="!searched && history.length > 0">
      <view class="flex-between mb-20">
        <text class="section-title">搜索历史</text>
        <text class="text-sm text-muted" @click="clearHistory">清空</text>
      </view>
      <view class="flex-wrap">
        <text v-for="(h,i) in history" :key="i" class="history-tag" @click="searchHistory(h)">{{ h }}</text>
      </view>
    </view>

    <view class="hot-section" v-if="!searched">
      <text class="section-title mb-20">🔥 热门搜索</text>
      <view class="flex-wrap">
        <text v-for="h in hotKeywords" :key="h" class="history-tag hot" @click="searchHistory(h)">{{ h }}</text>
      </view>
    </view>

    <view v-if="searched" class="results-section">
      <view class="result-tabs">
        <view v-for="(t,i) in resultTabs" :key="i" class="result-tab" :class="{active:activeResultTab===i}" @click="activeResultTab=i">
          <text>{{ t.label }} ({{ t.count }})</text>
        </view>
      </view>

      <view v-if="activeResultTab===0">
        <view v-for="c in contestResults" :key="c.id" class="card result-card" @click="goContestDetail(c.id)">
          <view class="flex-between">
            <view class="flex-1"><text class="result-name">{{ c.name }}</text><text class="text-sm text-muted">{{ c.org }} | {{ c.level }}</text></view>
            <view class="tag tag-primary">{{ c.statusText || '报名中' }}</view>
          </view>
        </view>
        <view v-if="contestResults.length===0 && !loading" class="empty"><text>未找到相关竞赛</text></view>
      </view>

      <view v-if="activeResultTab===1">
        <view v-for="t in teamResults" :key="t.id" class="card result-card" @click="goTeamDetail(t.id)">
          <view class="flex-between">
            <view class="flex-center">
              <image class="avatar avatar-sm" :src="t.userAvatar" mode="aspectFill" />
              <view class="ml-10"><text class="result-name">{{ t.contestName }}</text><text class="text-sm text-muted">{{ t.userName }} 发起</text></view>
            </view>
            <view class="tag" :class="t.statusColor">{{ t.statusText }}</view>
          </view>
        </view>
        <view v-if="teamResults.length===0 && !loading" class="empty"><text>未找到相关团队</text></view>
      </view>
    </view>

    <view v-if="loading" class="loading-more"><text>搜索中...</text></view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      keyword: '', searched: false, loading: false, history: [],
      hotKeywords: ['挑战杯','互联网+','数学建模','Python','前端开发','数据分析','ACM','创新创业'],
      activeResultTab: 0,
      resultTabs: [{label:'竞赛',count:0},{label:'团队',count:0}],
      contestResults: [], teamResults: []
    };
  },
  onLoad() { this.loadHistory(); },
  methods: {
    loadHistory() {
      try { const h=uni.getStorageSync('search_history'); this.history=h?JSON.parse(h):[]; } catch(e) { this.history=[]; }
    },
    saveHistory(kw) {
      if(!kw.trim()) return;
      let h=[...this.history]; const idx=h.indexOf(kw);
      if(idx>-1) h.splice(idx,1);
      h.unshift(kw); if(h.length>10) h.pop();
      this.history=h; uni.setStorageSync('search_history',JSON.stringify(h));
    },
    clearHistory() { this.history=[]; uni.removeStorageSync('search_history'); },
    searchHistory(kw) { this.keyword=kw; this.doSearch(); },
    async doSearch() {
      const kw=this.keyword.trim(); if(!kw) return;
      this.searched=true; this.loading=true; this.saveHistory(kw);
      this.contestResults=[]; this.teamResults=[];
      try {
        const token=this.$store.state.token||'';
        const [cRes,tRes]=await Promise.all([
          uni.request({url:'http://localhost:9091/api/contest/list',method:'GET',header:{token},data:{keyword:kw}}),
          uni.request({url:'http://localhost:9091/api/team/list',method:'GET',header:{token},data:{keyword:kw}})
        ]);
        this.contestResults=(cRes[1]?cRes[1].data:cRes.data).data?.list||[];
        this.teamResults=(tRes[1]?tRes[1].data:tRes.data).data?.list||[];
        this.resultTabs[0].count=this.contestResults.length;
        this.resultTabs[1].count=this.teamResults.length;
      } catch(e) { this.contestResults=[]; this.teamResults=[]; }
      this.loading=false;
    },
    goContestDetail(id) { uni.navigateTo({url:'/pages/contest/detail?id='+id}); },
    goTeamDetail(id) { uni.navigateTo({url:'/pages/team/detail?id='+id}); }
  }
};
</script>

<style scoped>
.page { background:#f5f5f5; min-height:100vh; }
.search-header { background:#fff; padding:16rpx 20rpx; }
.search-input-row { display:flex; align-items:center; background:#f5f5f5; border-radius:40rpx; padding:10rpx 20rpx; }
.search-input { flex:1; font-size:28rpx; padding:8rpx 0; }
.search-btn { font-size:26rpx; color:#4A90D9; margin-left:16rpx; white-space:nowrap; padding:8rpx 16rpx; }
.history-section,.hot-section { padding:30rpx; }
.section-title { font-size:28rpx; font-weight:600; color:#333; display:block; }
.history-tag { display:inline-block; padding:10rpx 24rpx; background:#f0f0f0; border-radius:30rpx; font-size:24rpx; color:#666; margin:0 12rpx 16rpx 0; }
.history-tag.hot { background:#fff7e6; color:#e65100; }
.result-tabs { display:flex; background:#fff; padding:0 20rpx; border-bottom:1rpx solid #f0f0f0; }
.result-tab { flex:1; text-align:center; padding:24rpx 0; font-size:28rpx; color:#666; position:relative; }
.result-tab.active { color:#4A90D9; font-weight:600; }
.result-tab.active::after { content:''; position:absolute; bottom:0; left:50%; transform:translateX(-50%); width:60rpx; height:4rpx; background:#4A90D9; border-radius:2rpx; }
.result-card { margin:10rpx 20rpx; }
.result-name { font-size:28rpx; font-weight:500; display:block; margin-bottom:6rpx; }
.loading-more { text-align:center; padding:40rpx; color:#999; font-size:24rpx; }
</style>