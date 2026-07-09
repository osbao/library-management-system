<template>
  <div style="padding: 20px">
    <div style="margin-bottom: 30px">新增借书记录</div>
    <el-form :inline="true" :model="form" :rules="rules" ref="ruleForm" style="width: 90%" label-width="100px" >
      <el-form-item label="图书标准码" prop="bookNo">
        <el-select v-model="form.bookNo" clearable filterable placeholder="请选择" @change="selBook">
          <el-option
              v-for="item in books"
              :key="item.id"
              :label="item.bookNo"
              :value="item.bookNo">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="图书名称" prop="bookName">
        <el-input v-model="form.bookName" disabled placeholder="请输入名称"/>
      </el-form-item>
      <el-form-item label="借阅数量" prop="nums">
        <el-input-number v-model="form.nums" :min="1" :max="maxNums" placeholder="请输入借阅数量"/>
        <span style="margin-left: 10px; color: #909399;">(库存: {{ maxNums }})</span>
      </el-form-item>
      <el-form-item label="预计扣除积分" prop="totalScore">
        <el-input :value="totalScore" disabled>
          <template slot="prepend">{{ totalScore }}</template>
        </el-input>
      </el-form-item>
      <br />

      <el-form-item label="会员码" prop="userNo">
        <el-select v-model="form.userNo" clearable filterable placeholder="请选择" @change="selUser" :disabled="!isAdmin">
          <el-option
              v-for="item in users"
              :key="item.id"
              :label="item.username"
              :value="item.username">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="用户名称" prop="userName">
        <el-input v-model="form.userName" disabled/>
      </el-form-item>

      <el-form-item label="用户联系方式" prop="userPhone">
        <el-input v-model="form.userPhone" disabled/>
      </el-form-item>

      <el-form-item label="用户账户积分" prop="account">
        <el-input v-model="form.account" disabled/>
      </el-form-item>
      <el-form-item label="借出天数" prop="days">
        <el-input-number v-model="form.days" :min="1" :max="60" label="借出的天数"/>
      </el-form-item>
<!--      <el-form-item label="封面" prop="cover">-->
<!--        <el-input v-model="form.cover" placeholder="请输入封面"/>-->
<!--      </el-form-item>-->
<!--      <el-form-item label="作者" prop="author">-->
<!--        <el-input v-model="form.author" placeholder="请输入作者"/>-->
<!--      </el-form-item>-->
<!--      <el-form-item label="出版社" prop="publisher">-->
<!--        <el-input v-model="form.publisher" placeholder="请输入出版社"/>-->
<!--      </el-form-item>-->
<!--      <el-form-item label="分类" prop="category">-->
<!--        <el-cascader v-model="form.categories"-->
<!--                     :props="{ value: 'name', label: 'name'}"-->
<!--                     :options="categories"-->
<!--                     placeholder="请选择分类"-->
<!--                     @change="handleChange"/>-->
<!--      </el-form-item>-->
<!--      <el-form-item label="标准码" prop="bookNo">-->
<!--        <el-input v-model="form.bookNo" placeholder="请输入标准码"/>-->
<!--      </el-form-item>-->

    </el-form>

    <div style="text-align: center;margin-top: 30px">
      <el-button type="primary" @click="save"><i class="el-icon-sunset"></i> 提交</el-button>
      <el-button type="danger" @click="reset"><i class="el-icon-refresh"></i> 重置</el-button>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request";
import {options} from "axios";
import Cookies from "js-cookie";
export default {
  name: "AddBorrow",
  data(){
    return{
      books:[],
      users:[],
      categories:[],
      admin:Cookies.get('admin')?JSON.parse(Cookies.get('admin')): {},
      user:Cookies.get('user')?JSON.parse(Cookies.get('user')): {},
      maxNums: 0, // 图书最大可借数量（库存）
      form: { score:10, nums: 1},
      rules:{
        name:[
          {required: true, message: '请输入图书名称', trigger: 'blur'},
          {min: 2,max: 10,message:'长度在3-10个字符', trigger: 'blur'}
        ],
        bookNo:[
          {required: true, message: '请输入图书标准码', trigger: 'blur'},
          {min: 2,max: 100,message:'长度在2-100个字符', trigger: 'blur'}
        ],
        userNo:[
          {required: true, message: '请输入会员码', trigger: 'blur'},
          {min: 2,max: 100,message:'长度在2-100个字符', trigger: 'blur'}
        ],
        nums:[
          {required: true, message: '请输入借阅数量', trigger: 'blur'},
          {type: 'number', min: 1, message:'借阅数量至少为1', trigger: 'blur'}
        ]
      }
    }
  },
  computed: {
    isAdmin() {
      return !!this.admin.id;
    },
    totalScore() {
      // 计算总积分：单本积分 × 天数 × 数量
      const score = this.form.score || 0;
      const days = this.form.days || 0;
      const nums = this.form.nums || 0;
      return score * days * nums;
    }
  },
  created() {
    request.get('/book/list').then(res => {
      this.books = res.data;
    })
    
    // 管理员可以查看所有用户，普通用户只能看到自己
    if (this.isAdmin) {
      request.get('/user/list').then(res => {
        this.users = res.data.filter(v => v.status)
      })
    } else {
      // 普通用户，只设置当前用户信息
      if (this.user.username) {
        this.form.userNo = this.user.username;
        // 直接设置用户信息，不需要从列表中查找
        this.$set(this.form, 'userName', this.user.name || '');
        this.$set(this.form, 'userPhone', this.user.phone || '');
        this.$set(this.form, 'account', this.user.account || 0);
      }
    }
    
    // 检查是否有URL参数传递的图书和用户信息
    const { bookNo, bookName, userNo } = this.$route.query;
    if (bookNo) {
      this.form.bookNo = bookNo;
      this.selBook();
    }
    if (userNo && this.isAdmin) {
      this.form.userNo = userNo;
      this.selUser();
    }
  },
  methods:{
    save(){
      this.$refs['ruleForm'].validate((valid) =>{
        if(valid){
          request.post( '/borrow/save',this.form).then(res => {
            if(res.code === '200'){
              this.$notify.success('新增成功！')
              this.$refs['ruleForm'].resetFields();
              // 根据用户角色跳转到不同页面
              const admin = Cookies.get('admin');
              if (admin) {
                this.$router.push("/borrowList")
              } else {
                this.$router.push("/userHome/userBorrowList")
              }
            }else{
              this.$notify.error(res.data)
            }
          })
        }
      })
    },
    reset(){
      this.$refs['ruleForm'].resetFields();
    },
    selBook(){
      const  book = this.books.find(v => v.bookNo ==  this.form.bookNo)
      // console.log(book)
      request.get( '/book/'+book.id).then(res => {
        //强制设置对象属性
        this.$set(this.form, 'bookName', res.data.name);
        // this.form.bookName = res.data.name;
        this.form.score =  res.data.score;
        this.maxNums = res.data.nums; // 设置最大可借数量为库存
        this.form.nums = 1; // 默认借1本
      })
    },
    selUser(){
      const  user = this.users.find(v => v.username ==  this.form.userNo)
      // console.log(user)
      request.get( '/user/'+user.id).then(res => {
        this.$set(this.form, 'userName', res.data.name);
        // this.form.userName = user.name;
        this.form.userPhone = user.phone;
        this.form.account = user.account;
      })
    }
  }
}
</script>

