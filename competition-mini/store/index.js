const storeConfig = {
  state: {
    token: '',
    userInfo: null
  },
  getters: {
    isLogin: (state) => !!state.token && !!state.userInfo
  },
  mutations: {
    setToken(state, token) {
      state.token = token;
      uni.setStorageSync('token', token);
    },
    setUserInfo(state, userInfo) {
      state.userInfo = userInfo;
      uni.setStorageSync('userInfo', JSON.stringify(userInfo));
    },
    clearUser(state) {
      state.token = '';
      state.userInfo = null;
      uni.removeStorageSync('token');
      uni.removeStorageSync('userInfo');
    }
  },
  actions: {
    // 模拟登录 - 根据昵称查找并返回用户
    async login({ commit }, { nickName, avatar }) {
      try {
        // 从后端获取用户列表，查找匹配的用户
        const res = await uni.request({
          url: 'http://localhost:9091/api/user/list',
          method: 'GET'
        });

        const d = res[1] ? res[1].data : res.data;
        const users = d.data?.list || [];
        let user = users.find(u => u.nickName === nickName);

        if (!user) {
          // 如果后端没有该用户，创建模拟用户数据
          user = {
            id: Date.now(),
            nickName: nickName,
            avatar: avatar || '/static/icons/default-avatar.png',
            token: 'mock_token_' + nickName + '_' + Date.now(),
            school: '',
            skills: [],
            enrollCount: 0,
            teamCount: 0,
            inviteCount: 0
          };
        }

        // 生成或使用已有的token
        const token = user.token || ('token_' + nickName + '_' + Date.now());
        commit('setToken', token);
        commit('setUserInfo', user);

        return user;
      } catch (e) {
        // 网络错误时使用模拟数据
        const mockUser = {
          id: Date.now(),
          nickName: nickName,
          avatar: avatar || '/static/icons/default-avatar.png',
          token: 'mock_token_' + nickName + '_' + Date.now(),
          school: '',
          skills: [],
          enrollCount: 0,
          teamCount: 0,
          inviteCount: 0
        };

        const token = mockUser.token;
        commit('setToken', token);
        commit('setUserInfo', mockUser);

        return mockUser;
      }
    },

    // 更新用户资料
    async updateProfile({ commit }, profileData) {
      try {
        commit('setUserInfo', profileData);
        return profileData;
      } catch (e) {
        throw e;
      }
    },

    // 退出登录
    logout({ commit }) {
      commit('clearUser');
    }
  }
};

export default storeConfig;
