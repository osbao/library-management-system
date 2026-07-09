<template>
  <view class="page">
    <!-- 匹配头部 -->
    <view class="match-header">
      <text class="match-title">🎯 智能团队匹配</text>
      <text class="match-desc">根据你的技能和需求，自动推荐最合适的队友</text>
    </view>

    <!-- 筛选项 -->
    <view class="card">
      <text class="section-label">匹配条件</text>
      <view class="form-item">
        <text class="form-label">目标竞赛</text>
        <picker :range="contestNames" range-key="name" @change="onContestChange">
          <view class="form-picker">{{ contestNames[contestIndex]?.name || '请选择竞赛' }}</view>
        </picker>
      </view>
      <view class="form-item">
        <text class="form-label">你需要的角色</text>
        <view class="flex-wrap">
          <view
            v-for="r in roleOptions"
            :key="r"
            class="skill-chip"
            :class="{ active: selectedRoles.includes(r) }"
            @click="toggleRole(r)"
          >{{ r }}</view>
        </view>
      </view>
    </view>

    <!-- 匹配按钮 -->
    <view class="match-btn-section">
      <button class="btn btn-primary btn-block" :loading="loading" @click="doMatch">
        开始匹配
      </button>
    </view>

    <!-- 匹配结果 -->
    <view v-if="results.length > 0" class="results-section">
      <text class="section-label ml-20">为你找到 {{ results.length }} 个合适的人选</text>
      <view v-for="u in results" :key="u.id" class="user-card card">
        <view class="flex-between">
          <view class="flex-center">
            <image class="avatar avatar-lg" :src="u.avatar" mode="aspectFill" />
            <view class="ml-20">
              <text class="text-lg">{{ u.nickName }}</text>
              <text class="text-sm text-muted">{{ u.school }}</text>
            </view>
          </view>
          <view class="tag tag-warning">匹配度 {{ u.matchScore }}%</view>
        </view>
        <view class="flex-wrap mt-10">
          <text v-for="s in u.skills" :key="s" class="tag">{{ s }}</text>
        </view>
        <view class="flex-center mt-10">
          <text class="text-sm" v-if="u.remark">{{ u.remark }}</text>
        </view>
        <view class="flex-between mt-20">
          <text class="text-sm text-muted">在找：{{ u.lookingFor }}</text>
          <view class="btn btn-sm btn-outline" @click="inviteUser(u)">邀请组队</view>
        </view>
      </view>
    </view>

    <view v-if="matched && results.length===0" class="empty">
      <text>暂未找到合适人选</text>
      <text class="text-sm text-muted mt-10">试试调整匹配条件</text>
    </view>

    <view style="height: 80rpx;"></view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      contestNames: [
        { id: '1', name: '挑战杯' },
        { id: '2', name: '互联网+' },
        { id: '3', name: '数学建模' },
        { id: '4', name: '电子设计大赛' },
        { id: '5', name: 'ACM程序设计' },
        { id: '6', name: '创业计划大赛' }
      ],
      contestIndex: 0,
      roleOptions: ['技术开发', 'UI/设计', '文案/策划', '路演/答辩', '数据分析', '市场调研', '算法', '项目管理'],
      selectedRoles: [],
      results: [],
      loading: false,
      matched: false
    };
  },
  methods: {
    onContestChange(e) { this.contestIndex = e.detail.value; },
    toggleRole(r) {
      const i = this.selectedRoles.indexOf(r);
      if (i >= 0) { this.selectedRoles.splice(i, 1); }
      else { this.selectedRoles.push(r); }
    },
    async doMatch() {
      this.loading = true;
      this.matched = true;
      try {
        const token = this.$store.state.token || '';
        const res = await uni.request({
          url: 'http://localhost:9091/api/match/teammate',
          method: 'GET', header: { token }
        });
        const d = res[1] ? res[1].data : res.data;
        this.results = d.data?.list || [];
      } catch (e) { this.results = []; }
      this.loading = false;
    },
    inviteUser(u) {
      uni.showToast({ title: '邀请已发送给 ' + u.nickName, icon: 'success' });
    }
  }
};
</script>

<style scoped>
.page { background: #f5f5f5; min-height: 100vh; }
.match-header { padding: 40rpx 30rpx 20rpx; background: linear-gradient(135deg, #4A90D9, #357ABD); color: #fff; }
.match-title { font-size: 40rpx; font-weight: bold; display: block; }
.match-desc { font-size: 24rpx; opacity: 0.8; margin-top: 8rpx; display: block; }

.section-label { font-size: 28rpx; font-weight: 600; }
.form-item { margin-bottom: 24rpx; }
.form-label { font-size: 26rpx; color: #666; display: block; margin-bottom: 8rpx; }
.form-picker { background: #f5f5f5; border-radius: 12rpx; padding: 16rpx 20rpx; font-size: 26rpx; color: #333; }

.skill-chip { display: inline-block; padding: 10rpx 24rpx; border-radius: 30rpx; font-size: 24rpx; background: #f0f0f0; color: #666; margin: 6rpx; }
.skill-chip.active { background: #4A90D9; color: #fff; }

.match-btn-section { padding: 0 30rpx; margin-bottom: 20rpx; }

.results-section {  }
.user-card { margin-bottom: 20rpx; }
</style>