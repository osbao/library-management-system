<template>
  <div id="app">
    <div style="height: 60px;line-height: 60px;background-color: #1a1a2e;margin-bottom: 2px;display:flex">
      <div style="width: 500px">
        <img src="@/assets/logo.png" style="width: 40px;position: relative;top: 10px;left: 10px">
        <span style="margin-left: 25px;font-size: 24px;color: #e94560;font-weight: bold">智慧停车场管理系统</span>
      </div>
      <div style="flex: 1;text-align: right;padding-right: 20px">
        <el-dropdown size="medium" trigger="hover">
            <span class="el-dropdown-link" style="cursor: pointer;color: #fff">
              {{ admin.name || admin.username }}<i class="el-icon-arrow-down el-icon--right"></i>
            </span>
          <el-dropdown-menu slot="dropdown" style="margin-top: -5px">
            <el-dropdown-item @click.native="$router.push('/adminProfile')">个人信息</el-dropdown-item>
            <el-dropdown-item @click.native="logout()">退出</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>

    </div>
    <!-- 侧边栏和主体 -->
    <div style="display: flex">
      <div style="width:200px;min-height:calc(100vh - 62px); overflow: hidden;margin-right: 2px;background-color: #16213e">
        <el-menu :default-active="$route.path" :default-openeds="[]" router class="el-menu-item"
                 style="margin-bottom: 10px;background-color: #16213e;border-right: none"
                 text-color="#a0a0b0" active-text-color="#e94560">
          <el-menu-item index="/">
            <i class="el-icon-s-home" style="color: #e94560"></i>
            <span>首页</span>
          </el-menu-item>

          <el-submenu index="parkingRecord">
            <template slot="title">
              <i class="el-icon-truck" style="color: #e94560"></i>
              <span>停车记录</span>
            </template>
            <el-menu-item index="/parkingRecordList">停车记录列表</el-menu-item>
            <el-menu-item index="/addParkingRecord">停车记录添加</el-menu-item>
          </el-submenu>

          <el-menu-item index="/exitRecordList">
            <i class="el-icon-circle-check" style="color: #e94560"></i>
            <span>离场记录</span>
          </el-menu-item>

          <el-submenu index="parkingSpot">
            <template slot="title">
              <i class="el-icon-place" style="color: #e94560"></i>
              <span>车位管理</span>
            </template>
            <el-menu-item index="/parkingSpotList">车位列表</el-menu-item>
            <el-menu-item index="/addParkingSpot">车位添加</el-menu-item>
          </el-submenu>

          <el-submenu index="spotType">
            <template slot="title">
              <i class="el-icon-collection-tag" style="color: #e94560"></i>
              <span>车位类型管理</span>
            </template>
            <el-menu-item index="/spotTypeList">类型列表</el-menu-item>
            <el-menu-item index="/addSpotType">类型添加</el-menu-item>
          </el-submenu>

          <el-submenu index="driver">
            <template slot="title">
              <i class="el-icon-user-solid" style="color: #e94560"></i>
              <span>车主管理</span>
            </template>
            <el-menu-item index="/driverList">车主列表</el-menu-item>
            <el-menu-item index="/addDriver">车主添加</el-menu-item>
          </el-submenu>

          <el-submenu index="admin">
            <template slot="title">
              <i class="el-icon-s-tools" style="color: #e94560"></i>
              <span>管理员管理</span>
            </template>
            <el-menu-item index="/adminList">管理员列表</el-menu-item>
            <el-menu-item index="/addAdmin">管理员添加</el-menu-item>
          </el-submenu>

          <el-submenu index="reservation">
            <template slot="title">
              <i class="el-icon-date" style="color: #e94560"></i>
              <span>预约管理</span>
            </template>
            <el-menu-item index="/spotReservationList">预约列表</el-menu-item>
          </el-submenu>

          <el-menu-item index="/garageReport">
            <i class="el-icon-data-line" style="color: #e94560"></i>
            <span>数据报表</span>
          </el-menu-item>
        </el-menu>
      </div>
      <div style="flex: 1;background-color: #f5f5f5;padding: 10px">
        <router-view/>
      </div>
    </div>

  </div>
</template>
<script>
import Cookies from "js-cookie";
export default {
  name: "Layout",
  data(){
    return{
      admin: Cookies.get('admin') ? JSON.parse(Cookies.get('admin')) : {}
    }
  },
  methods:{
    logout(){
      Cookies.remove('admin')
      this.$router.push('/login')
    }
  },
  created() {
    const user = Cookies.get('user');
    if (user && !Cookies.get('admin')) {
      this.$router.push('/userHome');
    }
  }
}
</script>