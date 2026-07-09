<template>
  <view class="page">
    <!-- 消息分类tab -->
    <view class="tabs">
      <view
        v-for="(t,i) in tabs"
        :key="i"
        class="tab-item"
        :class="{ active: activeTab === i }"
        @click="activeTab = i"
      >
        <text>{{ t.label }}</text>
        <view v-if="t.unread && busy" class="badge" style="position:static;display:inline-flex;margin-left:6rpx;">{{ t.unread }}</view>
      </view>
    </view>

    <view v-if="list.length===0 && !busy" class="empty">
      <text>暂无消息</text>
    </view>

    <view class="msg-list">
      <view
        v-for="m in list"
        :key="m.id"
        class="msg-card card"
        @click="readMsg(m)"
      >
        <view class="flex-between">
          <view class="flex-center flex-1">
            <view class="msg-avatar" :style="{background: m.bg || '#4A90D9'}">
              <text>{{ m.typeIcon }}</text>
            </view>
            <view class="ml-20 flex-1">
              <view class="flex-between">
                <text class="msg-title" :class="{ 'text-muted': m.read }">{{ m.title }}</text>
                <text class="text-sm text-muted">{{ m.time }}</text>
              </view>
              <text class="msg-content">{{ m.content }}</text>
            </view>
          </view>
          <view v-if="!m.read" class="unread-dot"></view>
        </view>
      </view>
    </view>

    <view style="height: 120rpx;"></view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      tabs: [
        { label: '全部', key: 'all', unread: 0 },
        { label: '组队', key: 'team', unread: 0 },
        { label: '系统', key: 'system', unread: 0 },
        { label: '竞赛', key: 'contest', unread: 0 }
      ],
      activeTab: 0,
      list: [],
      busy: false
    };
  },
  onShow() { this.fetchMessages(); },
  watch: {
    activeTab() { this.fetchMessages(); }
  },
  methods: {
    async fetchMessages() {
      this.busy = true;
      try {
        const token = this.$store.state.token || '';
        const key = this.tabs[this.activeTab].key;
        const res = await uni.request({
          url: 'http://localhost:9091/api/message/list',
          method: 'GET', header: { token }, data: { type: key }
        });
        const d = res[1] ? res[1].data : res.data;
        this.list = d.data?.list || [];
      } catch (e) { this.list = []; }
      this.busy = false;
    },
    readMsg(m) {
      // 标记已读
      if (!m.read) {
        m.read = true;
        const unread = this.list.filter(x => !x.read).length;
        if (unread > 0) {
          uni.setTabBarBadge({ index: 3, text: String(unread) });
        } else {
          uni.removeTabBarBadge({ index: 3 });
        }
      }
      uni.showModal({
        title: m.title,
        content: m.content || '暂无详细内容',
        showCancel: false,
        confirmText: '知道了'
      });
    }
  }
};
</script>

<style scoped>
.page { background: #f5f5f5; min-height: 100vh; }
.tabs { display: flex; background: #fff; padding: 16rpx 0; position: sticky; top: 0; z-index: 10; }
.tab-item { flex: 1; text-align: center; font-size: 28rpx; color: #666; padding: 10rpx 0; }
.tab-item.active { color: #4A90D9; font-weight: 600; }
.msg-list { padding: 20rpx; }
.msg-card { margin: 0 0 20rpx 0; }
.msg-avatar { width: 72rpx; height: 72rpx; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 30rpx; flex-shrink: 0; }
.msg-title { font-size: 28rpx; font-weight: 500; }
.msg-content { display: block; font-size: 24rpx; color: #999; margin-top: 6rpx; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.unread-dot { width: 14rpx; height: 14rpx; border-radius: 50%; background: #ff4444; margin-left: 12rpx; flex-shrink: 0; }
</style>