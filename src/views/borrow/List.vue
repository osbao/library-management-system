<template>
  <div style="padding: 20px; background-color: #f5f7fa; min-height: 100vh">
    <el-card v-loading="loading">
      <!-- 面包屑导航 -->
      <el-breadcrumb separator="/" style="margin-bottom: 20px">
        <el-breadcrumb-item :to="{ path: '/userHome' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>我的借阅</el-breadcrumb-item>
      </el-breadcrumb>

      <h3 style="margin-bottom: 20px">
        <i class="el-icon-notebook-2" style="color: #409EFF"></i>
        我的借阅
      </h3>

      <!-- 筛选条件 -->
      <el-form :inline="true" style="margin-bottom: 20px">
        <!-- 管理员可以看到的筛选条件 -->
        <template v-if="admin.id">
          <el-form-item label="图书名称">
            <el-input v-model="params.bookName" placeholder="请输入图书名称" clearable></el-input>
          </el-form-item>
          <el-form-item label="图书标准码">
            <el-input v-model="params.bookNo" placeholder="请输入图书标准码" clearable></el-input>
          </el-form-item>
          <el-form-item label="用户名称">
            <el-input v-model="params.userName" placeholder="请输入用户名称" clearable></el-input>
          </el-form-item>
        </template>
        
        <!-- 状态筛选（所有用户都可以看到） -->
        <el-form-item label="状态">
          <el-select v-model="params.status" placeholder="全部" clearable @change="load">
            <el-option label="全部" value=""></el-option>
            <el-option label="已取书" value="已借出"></el-option>
            <el-option label="已归还" value="已归还"></el-option>
            <el-option label="已逾期" value="已逾期"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="load">搜索</el-button>
          <el-button type="warning" icon="el-icon-refresh" @click="reset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 借阅列表 -->
      <el-table :data="tableData" stripe>
        <el-table-column type="index" label="序号" width="60"></el-table-column>
        <el-table-column prop="bookName" label="图书名称" min-width="200"></el-table-column>
        <el-table-column label="ISBN/书号" width="280">
          <template v-slot="scope">
            <div style="line-height: 1.5;">
              <div v-if="scope.row.bookNo" style="color: #409EFF;">
                <i class="el-icon-document"></i> ISBN: {{ scope.row.bookNo }}
              </div>
              <div v-if="scope.row.barcode" style="color: #67C23A; margin-top: 4px;">
                <i class="el-icon-tickets"></i> 书号: {{ scope.row.barcode }}
              </div>
              <div v-if="!scope.row.bookNo && !scope.row.barcode" style="color: #909399;">
                暂无数据
              </div>
            </div>
          </template>
        </el-table-column>
        <!-- 只有管理员才显示用户信息 -->
        <el-table-column prop="userNo" label="会员码" width="150" v-if="admin.id"></el-table-column>
        <el-table-column prop="userName" label="用户名称" width="120" v-if="admin.id"></el-table-column>
        <el-table-column prop="createtime" label="借出日期" width="180"></el-table-column>
        <el-table-column prop="days" label="借出时长" width="100">
          <template v-slot="scope">
            {{ scope.row.days }} 天
          </template>
        </el-table-column>
        <el-table-column prop="returnDate" label="到期时间" width="180"></el-table-column>
        <el-table-column prop="status" label="借阅状态" width="100">
          <template v-slot="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="note" label="过期提醒" width="100">
          <template v-slot="scope">
            <el-tag :type="getNoteType(scope.row.note)" size="small">{{scope.row.note}}</el-tag>
          </template>
        </el-table-column>
        <!-- 只有管理员才显示还书和删除按钮 -->
        <el-table-column label="还书" width="100" v-if="admin.id">
          <template v-slot="scope">
            <el-button type="primary" size="mini" @click="returnBooks(scope.row)" v-if="scope.row.status === '已借出' || scope.row.status === '已取书'">还书</el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" v-if="admin.id">
          <template v-slot="scope">
            <el-popconfirm title="您确定删除这行数据吗?" @confirm="del(scope.row.id)">
              <el-button type="danger" size="mini" slot="reference">删除</el-button>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <el-empty v-if="!loading && tableData.length === 0" description="暂无借阅记录"></el-empty>

      <!-- 分页（只有管理员显示） -->
      <div style="margin-top: 20px; text-align: center" v-if="admin.id">
        <el-pagination
          background
          :current-page="params.pageNum"
          :page-size="params.pageSize"
          @current-change="handleCurrentChange"
          layout="prev, pager, next"
          :total="total">
        </el-pagination>
      </div>
    </el-card>
  </div>
</template>
<script>
// @ is an alias to /src
import request from "@/utils/request";
import Cookies from "js-cookie";
export default {
  name: 'borrowList',
  data() {
    return{
      loading: false,
      admin:Cookies.get('admin')?JSON.parse(Cookies.get('admin')): {},
      user:Cookies.get('user')?JSON.parse(Cookies.get('user')): {},
      tableData:[],
      total:0,
      params: {
        pageNum:1,
        pageSize:10,
        bookName: '',
        bookNo:'',
        userName:'',
        status: '',
      }
    }
  },
  created() {
    this.load();
  },
  methods:{
    load(){
      this.loading = true;
      // 如果是用户，只查询当前用户的借阅记录
      // 如果是管理员，不设置userName，可以查看所有用户的记录
      if (this.user.id && !this.admin.id) {
        this.params.userName = this.user.name;
      }
      
      request.get('/borrow/page',{
        params: this.params
      }).then(res => {
        if(res.code === '200'){
          this.tableData= res.data.list
          this.total=res.data.total
        }
      }).catch(err => {
        this.$message.error('网络错误');
        console.error(err);
      }).finally(() => {
        this.loading = false;
      });
    },
    reset(){
      this.params ={
        pageNum: 1,
        pageSize: 10,
        bookName: '',
        bookNo:'',
        userName:'',
        status: '',
      }
      this.load()
    },
    handleCurrentChange(pageNum){
      this.params.pageNum=pageNum
      this.load()
    },
    del(id){
      request.delete("/borrow/delete/"+id).then(res => {
        if(res.code === '200'){
          this.$notify.success("删除成功！")
          this.load()
        }else{
          this.$notify.error(res.data)
        }
      })
    },
    returnBooks(row){
      request.post("/borrow/saveRetur/", row).then(res => {
        if(res.code === '200'){
          this.$notify.success("还书成功!")
          this.load()
        }else{
          this.$notify.error(res.data)
        }
      })
    },
    getStatusType(status) {
      const typeMap = {
        '已借出': 'primary',
        '已取书': 'primary',
        '已归还': 'success',
        '已逾期': 'danger'
      };
      return typeMap[status] || '';
    },
    getStatusText(status) {
      // 将数据库中的"已借出"显示为"已取书"
      if (status === '已借出') {
        return '已取书';
      }
      return status;
    },
    getNoteType(note) {
      const typeMap = {
        '正常': 'success',
        '即将到期': 'warning',
        '已到期': 'warning',
        '已过期': 'danger'
      };
      return typeMap[note] || '';
    }
  }
}
</script>


<style scoped>

</style>