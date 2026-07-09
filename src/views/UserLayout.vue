<template>
  <div id="app">
    <!-- 顶部标题栏 -->
    <div style="height: 60px;line-height: 60px;background-color: #cdd9d7;margin-bottom: 2px;display:flex">
      <div style="width: 300px">
        <img src="@/assets/logo.png" style="width: 40px;position: relative;top: 10px;left: 10px">
        <span style="margin-left: 25px;font-size: 24px">图书借阅管理系统</span>
      </div>
      <div style="flex: 1;text-align: right;padding-right: 20px">
        <el-dropdown size="medium" trigger="hover">
            <span class="el-dropdown-link" style="cursor: pointer">
              {{ user.name || user.username }}<i class="el-icon-arrow-down el-icon--right"></i>
            </span>
          <el-dropdown-menu slot="dropdown" style="margin-top: -5px">
            <el-dropdown-item @click.native="$router.push('/userHome/userProfile')">个人信息</el-dropdown-item>
            <el-dropdown-item @click.native="logout()">退出</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>

    </div>
    
    <!-- 顶部导航菜单 -->
    <div style="background-color: #ffffff;border-bottom: 1px solid #e8e8e8;padding: 0 20px">
      <el-menu :default-active="$route.path" mode="horizontal" router class="top-menu">
        <el-menu-item index="/userHome">
          <i class="el-icon-s-home"></i>
          <span slot="title">首页</span>
        </el-menu-item>

        <el-menu-item index="/userHome/userBookList">
          <i class="el-icon-reading"></i>
          <span slot="title">书库浏览</span>
        </el-menu-item>

        <el-submenu index="bookshelf">
          <template slot="title">
            <i class="el-icon-notebook-2"></i>
            <span>我的书架</span>
          </template>
          <el-menu-item index="/userHome/userBorrowList">我的借阅</el-menu-item>
          <el-menu-item index="/userHome/myReservation">我的预约</el-menu-item>
          <el-menu-item index="/userHome/userReturnList">我的归还</el-menu-item>
        </el-submenu>

        <el-menu-item index="/userHome/inform">
          <i class="el-icon-message"></i>
          <span slot="title">系统公告</span>
        </el-menu-item>
      </el-menu>
    </div>
    
    <!-- 主体内容区域 -->
    <div style="padding: 20px;background-color: #f5f5f5;min-height:calc(100vh - 120px)">
      <router-view/>
    </div>

  </div>
</template>
<script>
import Cookies from "js-cookie";
export default {
  name: "UserLayout",
  data(){
    return{
      user: Cookies.get('user') ? JSON.parse(Cookies.get('user')) : {}
    }
  },
  methods:{
    logout(){
      Cookies.remove('user')
      this.$router.push('/login')
    }
  },
  created() {
    // 如果是管理员登录，重定向到管理员首页
    const admin = Cookies.get('admin');
    if (admin && !Cookies.get('user')) {
      this.$router.push('/');
    }
  }
}
</script>

<style scoped>
.top-menu {
  border-bottom: none;
}

.top-menu .el-menu-item,
.top-menu .el-submenu__title {
  height: 50px;
  line-height: 50px;
  font-size: 15px;
}

.top-menu .el-menu-item i,
.top-menu .el-submenu__title i {
  margin-right: 5px;
}
</style>
