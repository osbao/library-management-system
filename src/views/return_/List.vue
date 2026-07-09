<template>
  <div style="background-color: white">
    <div>
      <el-input style="width: 200px" placeholder="请输入图书名称" v-model="params.bookName"></el-input>
      <el-input style="width: 200px;margin-left: 5px" placeholder="请输入图书标准码" v-model="params.bookNo"></el-input>
      <el-input style="width: 200px;margin-left: 5px" placeholder="请输入用户名称" v-model="params.userName"></el-input>
      <el-button style="margin-left: 3px" type="primary" @click="load"><i class="el-icon-search"></i> 搜索</el-button>
      <el-button style="margin-left: 3px" type="warning" @click="reset"><i class="el-icon-refresh"></i> 重置</el-button>
      <el-divider content-position="left"></el-divider>
    </div>

    <el-table :data="tableData" style="margin-top: 3px; width: 100%" >
      <el-table-column prop="id" label="编号" width="55px"></el-table-column>
      <el-table-column prop="bookName" label="图书名称"></el-table-column>
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
      <el-table-column prop="userNo" label="会员码"></el-table-column>
      <el-table-column prop="userName" label="用户名称"></el-table-column>
      <el-table-column prop="userPhone" label="用户联系方式"></el-table-column>
      <el-table-column prop="score" label="所用积分" width="100"></el-table-column>
      <el-table-column prop="nums" label="借阅数量" width="100">
        <template v-slot="scope">
          <el-tag size="small">{{ scope.row.nums || 1 }} 本</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createtime" label="借出日期"></el-table-column>
      <el-table-column prop="status" label="借出状态"></el-table-column>
      <el-table-column prop="days" label="借出天数"></el-table-column>
      <el-table-column prop="returnDate" label="归还日期"></el-table-column>
      <el-table-column prop="realDate" label="实际归还日期"></el-table-column>

      <el-table-column label="操作" width="100px">
        <template v-slot="scope">
          <el-popconfirm title="您确定删除这行数据吗?" @confirm="del(scope.row.id)" style="margin-left: 5px">
            <el-button type="danger" slot="reference">删除</el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <div >
      <el-pagination
          background
          :current-page="params.pageNum"
          :page-size="params.pageSize"
          @current-change="handleCurrentChange"
          layout="prev, pager, next"
          :total="total">
      </el-pagination>
    </div>

  </div>
</template>
<script>
// @ is an alias to /src
import request from "@/utils/request";
import Cookies from "js-cookie";
export default {
  name: 'ReturnList',
  data() {
    return{
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
      }
    }
  },
  created() {
    this.load();
  },
  methods:{
    load(){
      // 如果是用户，只查询当前用户的还书记录
      // 如果是管理员，不设置userName，可以查看所有用户的记录
      if (this.user.id && !this.admin.id) {
        this.params.userName = this.user.name;
      }
      
      request.get('/borrow/pageRetur',{
        params: this.params
      }).then(res => {
        if(res.code === '200'){
          this.tableData= res.data.list
          this.total=res.data.total
        }
      })
    },
    reset(){
      this.params ={
        pageNum: 1,
        pageSize: 10,
        bookName: '',
        bookNo:'',
        userName:'',
      }
      this.load()
    },
    handleCurrentChange(pageNum){
      this.params.pageNum=pageNum
      this.load()
    },
    del(id){
      request.delete("/borrow/deleteRetur/"+id).then(res => {
        if(res.code === '200'){
          this.$notify.success("删除成功！")
          this.load()
        }else{
          this.$notify.error(res.data)
        }
      })
    },
  }
}
</script>

<style scoped>

</style>
