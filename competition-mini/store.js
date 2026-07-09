export default {
  state: {
    token: uni.getStorageSync('token') || '',
    userInfo: uni.getStorageSync('userInfo') ? JSON.parse(uni.getStorageSync('userInfo')) : null,
    isLogin: !!uni.getStorageSync('token')
  },
  mutations: {
    setToken(state, token) {
      state.token = token;
      state.isLogin = !!token;
      if (token) {
        uni.setStorageSync('token', token);
      } else {
        uni.removeStorageSync('token');
      }
    },
    setUserInfo(state, info) {
      state.userInfo = info;
      state.isLogin = !!info;
      if (info) {
        uni.setStorageSync('userInfo', JSON.stringify(info));
      } else {
        uni.removeStorageSync('userInfo');
      }
    },
    logout(state) {
      state.token = '';
      state.userInfo = null;
      state.isLogin = false;
      uni.removeStorageSync('token');
      uni.removeStorageSync('userInfo');
    }
  },
  actions: {
    async login({ commit }, { nickName, avatar }) {
      try {
        const res = await uni.request({
          url: 'http://localhost:9091/api/user/login',
          method: 'POST',
          data: { nickName, avatar }
        });
        const d = res[1] ? res[1].data : res.data;
        if (d.code === '200' && d.data) {
          commit('setToken', d.data.token);
          commit('setUserInfo', d.data.user);
          return d.data;
        }
        return null;
      } catch (e) {
        return null;
      }
    },
    async syncUser({ state }) {
      if (!state.userInfo) return;
      try {
        const res = await uni.request({
          url: 'http://localhost:9091/api/user/sync',
          method: 'POST',
          header: { token: state.token },
          data: { nickName: state.userInfo.nickName, avatar: state.userInfo.avatar }
        });
        const d = res[1] ? res[1].data : res.data;
        if (d.code === '200' && d.data) {
          this.commit('setToken', d.data.token);
          this.commit('setUserInfo', d.data.user);
        }
      } catch (e) {}
    }
  }
};