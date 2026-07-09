<template>
  <view class="page">
    <view v-for="t in list" :key="t.id" class="team-card card">
      <view class="flex-between">
        <view class="flex-1">
          <text class="text-lg">{{ t.contestName }}</text>
          <text class="text-sm text-muted">{{ t.role || '成员' }}</text>
        </view>
        <view class="tag" :class="t.statusColor">{{ t.statusText || '组队中' }}</view>
      </view>
      <view class="mt-10 flex-wrap">
        <text v-for="s in t.tags" :key="s" class="tag">{{ s }}</text>
      </view>
      <view class="flex-between mt-20">
        <text class="text-sm text-muted">{{ t.memberCount || 1 }}/{{ t.maxMembers || 5 }} 人</text>
        <text class="text-sm text-muted">{{ t.createdAt }}</text>
      </view>
      <!-- 成员预览 -->
      <view class="member-avatars mt-10" v-if="t.members && t.members.length > 0">
        <image
          v-for="(m,i) in t.members.slice(0, 5)"
          :key="i"
          class="avatar avatar-xs"
          :style="{ marginLeft: i > 0 ? '-10rpx' : '0' }"
          :src="m.avatar"
          mode="aspectFill"
        />
        <text v-if="t.members.length > 5" class="text-sm text-muted ml-10">+{{ t.members.length - 5 }}</text>
      </view>
    </view>
    <view v-if="list.length===0 && !loading" class="empty">
      <text>还没有加入任何团队</text>
      <button class="btn btn-primary mt-30" @click="goTeam">去组队广场</button>
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
        const res = await uni.request({ url: 'http://localhost:9091/api/user/teams', method: 'GET', header: { token } });
        const d = res[1] ? res[1].data : res.data;
        this.list = d.data?.list || [];
      } catch (e) { this.list = []; }
      this.loading = false;
    },
    goTeam() { uni.switchTab({ url: '/pages/team/square' }); }
  }
};
</script>

<style scoped>
.page { background: #f5f5f5; min-height: 100vh; padding: 20rpx; }
.team-card { margin-bottom: 20rpx; }
.member-avatars { display: flex; align-items: center; }
.avatar-xs { width: 50rpx; height: 50rpx; }
</style>