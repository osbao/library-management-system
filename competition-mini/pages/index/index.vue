<template>
  <view class="home-page">
    <!-- 顶部搜索区 -->
    <view class="header-bar">
      <view class="search-box" @click="goSearch">
        <image class="search-icon" src="/static/icons/search.png" mode="aspectFit" />
        <text class="search-placeholder">搜索竞赛、团队...</text>
      </view>
    </view>

    <!-- Banner轮播 -->
    <swiper class="banner" indicator-dots autoplay circular interval="3000">
      <swiper-item v-for="(b,i) in banners" :key="i" @click="goContest(b.id)">
        <view class="banner-inner" :style="{background: b.bg}">
          <text class="banner-title">{{ b.title }}</text>
          <text class="banner-desc">{{ b.desc }}</text>
        </view>
      </swiper-item>
    </swiper>

    <!-- 快捷入口 -->
    <view class="quick-actions">
      <view class="action-item" @click="goTab('/pages/contest/list')">
        <view class="action-icon icon-blue">📋</view>
        <text>全部竞赛</text>
      </view>
      <view class="action-item" @click="goTab('/pages/team/square')">
        <view class="action-icon icon-green">🧑‍🤝‍🧑</view>
        <text>组队广场</text>
      </view>
      <view class="action-item" @click="goMatch">
        <view class="action-icon icon-orange">🎯</view>
        <text>智能匹配</text>
      </view>
      <view class="action-item" @click="goPage('/pages/user/myEnroll')">
        <view class="action-icon icon-purple">📊</view>
        <text>我的报名</text>
      </view>
    </view>

    <!-- 热门竞赛 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">🔥 热门竞赛</text>
        <text class="section-more" @click="goTab('/pages/contest/list')">更多 ></text>
      </view>
      <scroll-view scroll-x class="contest-scroll">
        <view
          v-for="c in hotContests"
          :key="c.id"
          class="contest-card-h"
          @click="goContestDetail(c.id)"
        >
          <view class="contest-cover" :style="{background: c.bg}">
            <text class="cover-tag">{{ c.level }}</text>
          </view>
          <text class="contest-name">{{ c.name }}</text>
          <text class="contest-time">{{ c.deadlineText }}</text>
        </view>
      </scroll-view>
    </view>

    <!-- 最新组队 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">🧑‍🤝‍🧑 最新组队</text>
        <text class="section-more" @click="goTab('/pages/team/square')">更多 ></text>
      </view>
      <view class="team-list">
        <view
          v-for="t in hotTeams"
          :key="t.id"
          class="team-card card"
          @click="goTeamDetail(t.id)"
        >
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
          <text class="team-desc mt-10">{{ t.desc }}</text>
        </view>
      </view>
    </view>

    <!-- 底部安全区 -->
    <view style="height: 120rpx;"></view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      banners: [
        { id: '1', title: '2026 "挑战杯"全国大学生系列科技学术竞赛', desc: '报名截止倒计时 30 天', bg: 'linear-gradient(135deg, #667eea, #764ba2)' },
        { id: '2', title: '第九届中国国际"互联网+"大学生创新创业大赛', desc: '让你的创意改变世界', bg: 'linear-gradient(135deg, #f093fb, #f5576c)' },
        { id: '3', title: '全国大学生数学建模竞赛', desc: '培养数据思维，解决实际问题', bg: 'linear-gradient(135deg, #4facfe, #00f2fe)' }
      ],
      hotContests: [],
      hotTeams: [],
      loading: false
    };
  },
  onShow() {
    this.fetchData();
  },
  methods: {
    async fetchData() {
      try {
        const token = this.$store.state.token || '';
        const [contestRes, teamRes] = await Promise.all([
          uni.request({ url: 'http://localhost:9091/api/contest/list', method: 'GET', header: { token } }),
          uni.request({ url: 'http://localhost:9091/api/team/list', method: 'GET', header: { token } })
        ]);
        // uni-app 某些版本返回 [err, res]
        const cData = contestRes[1] ? contestRes[1].data : contestRes.data;
        const tData = teamRes[1] ? teamRes[1].data : teamRes.data;
        this.hotContests = (cData.data?.list || []).slice(0, 5);
        this.hotTeams = (tData.data?.list || []).slice(0, 5);
      } catch (e) {}
    },
    goSearch() { uni.navigateTo({ url: '/pages/search/index' }); },
    goContest(id) { uni.navigateTo({ url: '/pages/contest/detail?id=' + id }); },
    goContestDetail(id) { uni.navigateTo({ url: '/pages/contest/detail?id=' + id }); },
    goTeamDetail(id) { uni.navigateTo({ url: '/pages/team/detail?id=' + id }); },
    goTab(url) { uni.switchTab({ url }); },
    goPage(url) { uni.navigateTo({ url }); },
    goMatch() { uni.navigateTo({ url: '/pages/match/index' }); }
  }
};
</script>

<style scoped>
.home-page { background: #f5f5f5; padding-bottom: 20rpx; }
.header-bar { padding: 20rpx 30rpx; background: #fff; }
.search-box { display: flex; align-items: center; background: #f5f5f5; border-radius: 40rpx; padding: 14rpx 24rpx; }
.search-icon { width: 32rpx; height: 32rpx; margin-right: 12rpx; opacity: 0.5; }
.search-placeholder { font-size: 26rpx; color: #bbb; }

.banner { height: 300rpx; margin: 20rpx; border-radius: 20rpx; overflow: hidden; }
.banner-inner { width: 100%; height: 100%; display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 40rpx; }
.banner-title { font-size: 30rpx; color: #fff; font-weight: bold; text-align: center; }
.banner-desc { font-size: 24rpx; color: rgba(255,255,255,0.9); margin-top: 12rpx; }

.quick-actions { display: flex; margin: 20rpx; padding: 30rpx 20rpx; background: #fff; border-radius: 16rpx; }
.action-item { flex: 1; text-align: center; }
.action-icon { width: 80rpx; height: 80rpx; line-height: 80rpx; border-radius: 20rpx; font-size: 36rpx; margin: 0 auto 12rpx; }
.icon-blue { background: #e8f4fd; } .icon-green { background: #e8f8e8; } .icon-orange { background: #fff3e0; } .icon-purple { background: #f3e8ff; }

.section { margin-top: 20rpx; }
.section-header { display: flex; justify-content: space-between; align-items: center; padding: 0 30rpx; margin-bottom: 16rpx; }
.section-title { font-size: 30rpx; font-weight: bold; color: #333; }
.section-more { font-size: 24rpx; color: #4A90D9; }

.contest-scroll { white-space: nowrap; padding: 0 20rpx; }
.contest-card-h { display: inline-block; width: 280rpx; margin-right: 20rpx; background: #fff; border-radius: 16rpx; overflow: hidden; }
.contest-cover { height: 160rpx; display: flex; align-items: flex-end; padding: 16rpx; }
.cover-tag { background: rgba(255,255,255,0.9); padding: 4rpx 14rpx; border-radius: 12rpx; font-size: 20rpx; color: #333; }
.contest-name { display: block; padding: 16rpx 16rpx 8rpx; font-size: 26rpx; font-weight: 500; white-space: normal; }
.contest-time { display: block; padding: 0 16rpx 16rpx; font-size: 22rpx; color: #999; }

.team-list { padding: 0 20rpx; }
.team-card { margin: 0 0 20rpx 0; }
.team-desc { font-size: 24rpx; color: #666; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
</style>