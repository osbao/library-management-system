<template>
  <div class="title">
    <div style="width:330px;height: 380px;background-color: rgba(255,255,255,0.43);border-radius: 10px;margin:0 0 0 600px;padding:20px">
      <h2 class="wrap">用户注册</h2>
      <el-form :model="user" :rules="rules" ref="signForm">
        <el-form-item prop="name">
          <el-input class="text" placeholder="请输入姓名" prefix-icon="el-icon-user" size="medium" v-model="user.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input class="text" placeholder="请输入密码" prefix-icon="el-icon-lock" size="medium" v-model="user.password" autocomplete="off" show-password></el-input>
        </el-form-item>
        <el-form-item prop="newPassword">
          <el-input class="text" placeholder="请确认密码" prefix-icon="el-icon-lock" size="medium" v-model="user.newPassword" autocomplete="off" show-password></el-input>
        </el-form-item>
        <el-form-item>
          <el-button style="margin-left: 27px;margin-top: 10px" size="30px" type="success" @click="sign()"><i class="el-icon-edit"> 注册</i></el-button>
          <el-button style="margin-left: 27px;margin-top: 10px" size="30px" type="primary" @click="reset()"><i class="el-icon-eleme"> 重置</i></el-button>
          <el-button style="margin-left: 27px;margin-top: 10px" size="30px" type="info" @click="goToLogin()"><i class="el-icon-back"> 返回登录</i></el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request";

export default {
  name: "UserSign",
  data(){
    const validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'));
      } else if (value !== this.user.password) {
        callback(new Error('两次输入密码不一致!'));
      } else {
        callback();
      }
    };
    
    return{
      user:{},
      rules: {
        name: [
          {required: true, message: '请输入姓名', trigger: 'blur'},
          {min: 2, max: 20, message: '长度在2-20个字符', trigger: 'blur'}
        ],
        password: [
          {required: true, message: '请输入密码', trigger: 'blur'},
          {min: 3, max: 20, message: '长度在3-20个字符', trigger: 'blur'}
        ],
        newPassword: [
          {required: true, validator: validatePass, trigger: 'blur'}
        ]
      }
    }
  },
  methods:{
    sign(){
      this.$refs['signForm'].validate((valid) => {
        if (valid) {
          request.post('/user/sign', this.user).then(res => {
            if(res.code === '200'){
              this.$notify.success(res.data || "注册成功！")
              this.$router.push('/login')
            }else{
              this.reset()
              this.$notify.error(res.msg || "注册失败！");
            }
          })
        }
      })
    },
    reset(){
      this.user = {}
      this.$refs['signForm'].clearValidate()
    },
    goToLogin(){
      this.$router.push('/login')
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
</style>
