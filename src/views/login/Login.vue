<template>
  <div class="title">
<!--    <el-card class="cover" v-if="loginAdmin.id">-->
<!--      <slide-verify :l="42"-->
<!--                    :r="10"-->
<!--                    :w="310"-->
<!--                    :h="155"-->
<!--                    :accuracy="6"-->
<!--                    slider-text="向右滑动"-->
<!--                    @success="onSuccess"-->
<!--                    @fail="onFail"-->
<!--                    @refresh="onRefresh"-->
<!--      ></slide-verify>-->
<!--    </el-card>-->
    <div>{{msg}}</div>
    <div style="width:300px;height: 380px;background-color: rgba(255,255,255,0.43);border-radius: 10px;margin:0 0 0 600px;padding:20px">
      <h2 class="wrap">Login</h2>
      <el-form :model="admin" :rules="rules" ref="loginForm" class="form">
        <el-form-item  prop="role">
          <el-radio-group v-model="role" style="margin-bottom: 10px;">
            <el-radio label="admin">管理员</el-radio>
            <el-radio label="user">用户</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item  prop="username">
          <el-input class="text" placeholder="username" prefix-icon="el-icon-user" size="medium" v-model="admin.username" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item  prop="password">
          <el-input class="text" placeholder="password" prefix-icon="el-icon-lock" size="medium" v-model="admin.password" autocomplete="off" show-password></el-input>
        </el-form-item>
        <el-form-item >
          <el-button style="margin-left: 27px;margin-top: 10px" size="30px" type="success" @click="login()"><i class="el-icon-edit"> 登录</i></el-button>
          <el-button v-if="role === 'admin'" style="margin-left: 27px;margin-top: 10px" size="30px" type="primary" @click="goToSign()"><i class="el-icon-eleme"> 注册</i></el-button>
          <el-button v-if="role === 'user'" style="margin-left: 27px;margin-top: 10px" size="30px" type="primary" @click="goToUserSign()"><i class="el-icon-eleme"> 注册</i></el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request";
import Cookies from "js-cookie"
export default {
  name: "Login",
  data(){
    return{
      role: 'admin', // 默认管理员登录
      loginAdmin:{},
      msg: '',
      admin:{},
      rules: {
        username: [
          {required: true, message: '请输入用户名', trigger: 'blur'},
          {min:3, max:10, message: "长度在3-10个字符",trigger: 'blur'}
        ],
        password:[
          {required: true, message: '请输入密码', trigger: 'blur'},
          {min: 3, max: 10, message: "长度在3-10个字符",trigger: 'blur'}
        ]
      }
    }
  },
  methods:{
    // onSuccess(){
    //   this.$notify.success("登录成功！")
    //   Cookies.set('admin',JSON.stringify(this.loginAdmin))
    //   this.$router.push('/')
    // },
    // onFail(){
    //   this.msg = ''
    // },
    // onRefresh(){
    //   this.msg = ''
    // },
    login(){
      this.$refs['loginForm'].validate((valid) =>{
        if(valid) {
          // 根据角色选择不同的登录接口
          const loginUrl = this.role === 'admin' ? '/admin/login' : '/user/login';
          const cookieKey = this.role === 'admin' ? 'admin' : 'user';
          
          request.post(loginUrl, this.admin).then(res => {
            if (res.code === '200') {
              this.loginAdmin=res.data
              this.$notify.success("登录成功！")
              // 保存用户信息和角色
              const userInfo = {...this.loginAdmin, role: this.role}
              Cookies.set(cookieKey, JSON.stringify(userInfo))
              // 根据角色跳转到不同的首页
              if (this.role === 'admin') {
                this.$router.push('/')
              } else {
                this.$router.push('/userHome')
              }
            } else {
              console.log(res)
              this.$notify.error(res.data || res.msg)
            }
          })
        }
      })
    },
    goToSign() {
      console.log('Attempting navigation to /sign')
      this.$router.push('/sign')
    },
    goToUserSign() {
      console.log('Attempting navigation to /userSign')
      this.$router.push('/userSign')
    }
  }
}
</script>

<style scoped>
.title {
  position: absolute;
  box-sizing: border-box;
  width: 100%;
  height: 100%;
  padding-top: 18%;
  background-image: url('@/assets/02.png');
  background-size: 100%;
}
.wrap{
  text-align: center;
  font-size: 30px;
  margin-bottom: 30px;
  color: #fff;
  text-shadow: 0 0 10px #ff9dff80;
}
.text{
  border: 5px solid #ffffff
}
.cover{
  width:fit-content;
  background-color: white;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%,-50%);
  z-index: 1000;
}
</style>