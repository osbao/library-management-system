<script>
export default {
  onLaunch() {
    const token = uni.getStorageSync('token');
    const userInfo = uni.getStorageSync('userInfo');
    if (token) {
      this.$store.commit('setToken', token);
    }
    if (userInfo) {
      try {
        const user = JSON.parse(userInfo);
        this.$store.commit('setUserInfo', user);
      } catch(e) {
        console.error('解析用户信息失败', e);
        uni.removeStorageSync('userInfo');
      }
    }
  }
};
</script>
<style>
@import './common.css';
</style>