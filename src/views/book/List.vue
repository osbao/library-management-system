<template>
  <div style="background-color: white">
    <div>
      <el-input style="width: 200px" placeholder="请输入图书名称" v-model="params.name"></el-input>
      <el-input style="width: 200px;margin-left: 5px" placeholder="请输入图书标准码" v-model="params.bookNo"></el-input>
      <el-button style="margin-left: 3px" type="primary" @click="load"><i class="el-icon-search"></i> 搜索</el-button>
      <el-button style="margin-left: 3px" type="warning" @click="reset"><i class="el-icon-refresh"></i> 重置</el-button>
      <el-divider content-position="left"></el-divider>
    </div>

    <el-table :data="tableData" style="margin-top: 3px; width: 100%" >
      <el-table-column prop="id" label="编号" width="55px"></el-table-column>
      <el-table-column prop="name" label="图书名称" width="130px"></el-table-column>
      <el-table-column prop="description" label="描述"></el-table-column>
      <el-table-column prop="publishDate" label="出版日期" width="120px"></el-table-column>
      <el-table-column prop="author" label="作者" width="80px"></el-table-column>
      <el-table-column prop="publisher" label="出版社"></el-table-column>
      <el-table-column prop="category" label="分类" ></el-table-column>
      <el-table-column prop="bookNo" label="标准码" ></el-table-column>
      <el-table-column prop="score" label="借书积分" width="80px"></el-table-column>
      <el-table-column prop="nums" label="数量" width="80px"></el-table-column>
      <el-table-column prop="cover" label="封面" width="150px">
        <template v-slot="scope">
          <div class="book-cover-placeholder">
            <div class="book-title-text">{{ scope.row.name }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="createtime" label="创建时间" width="120px"></el-table-column>
      <el-table-column prop="updatetime" label="修改时间" width="120px"></el-table-column>
      <el-table-column label="操作" width="200px">
        <template v-slot="scope">
          <!-- 管理员显示编辑和删除按钮 -->
          <template v-if="isAdmin">
            <el-button
              type="primary"
              size="medium"
              @click="$router.push('/editBook?id=' + scope.row.id)"
              style="margin-right: 5px; margin-bottom: 0;"
            >
              编辑
            </el-button>
            <el-popconfirm
              title="您确定删除这行数据吗?"
              @confirm="del(scope.row.id)"
              style="margin-bottom: 0;"
            >
              <el-button
                type="danger"
                size="medium"
                slot="reference"
                style="margin-bottom: 0;"
              >
                删除
              </el-button>
            </el-popconfirm>
          </template>
          <!-- 用户显示借书按钮 -->
          <template v-else>
            <el-button
              type="primary"
              size="medium"
              :disabled="scope.row.nums <= 0"
              @click="borrowBook(scope.row)"
              style="width: 100%"
            >
              {{ scope.row.nums > 0 ? '立即借阅' : '暂时无货' }}
            </el-button>
          </template>
        </template>
      </el-table-column>
    </el-table>
    <!-- 借书对话框 -->
    <el-dialog
      title="借阅图书"
      :visible.sync="borrowDialogVisible"
      width="500px"
      @close="resetBorrowForm"
    >
      <el-form :model="borrowForm" :rules="borrowRules" ref="borrowForm" label-width="100px">
        <el-form-item label="图书名称">
          <el-input v-model="borrowForm.bookName" disabled></el-input>
        </el-form-item>
        <el-form-item label="图书标准码">
          <el-input v-model="borrowForm.bookNo" disabled></el-input>
        </el-form-item>
        <el-form-item label="所需积分">
          <el-input v-model="borrowForm.score" disabled></el-input>
        </el-form-item>
        <el-form-item label="借阅数量" prop="nums">
          <el-input-number 
            v-model="borrowForm.nums" 
            :min="1" 
            :max="borrowForm.maxNums"
            label="借阅数量">
          </el-input-number>
          <span style="margin-left: 10px; color: #909399;">(库存: {{ borrowForm.maxNums }})</span>
        </el-form-item>
        <el-form-item label="借阅天数" prop="days">
          <el-input-number 
            v-model="borrowForm.days" 
            :min="1" 
            :max="365"
            label="借阅天数">
          </el-input-number>
          <span style="margin-left: 10px; color: #909399;">(最长365天)</span>
        </el-form-item>
        <el-form-item label="预计归还日期">
          <el-date-picker
            v-model="calculatedReturnDate"
            type="date"
            placeholder="预计归还日期"
            disabled
            value-format="yyyy-MM-dd"
            style="width: 100%;">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="预计扣除积分">
          <el-input v-model="estimatedScore" disabled>
            <template slot="prepend">{{ borrowForm.nums * borrowForm.score * borrowDays }}</template>
          </el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="borrowDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitBorrow" :loading="borrowLoading">确 定</el-button>
      </div>
    </el-dialog>

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
  name: 'BookList',
  data() {
    return{
      admin:Cookies.get('admin')?JSON.parse(Cookies.get('admin')): {},
      user:Cookies.get('user')?JSON.parse(Cookies.get('user')): {},
      tableData:[],
      total:0,
      params: {
        pageNum:1,
        pageSize:10,
        name: '',
        bookNo:''
      },
      borrowDialogVisible: false,
      borrowLoading: false,
      borrowForm: {
        bookName: '',
        bookNo: '',
        score: 0,
        nums: 1,
        maxNums: 0,
        days: 30 // 默认借阅30天
      },
      borrowRules: {
        nums: [
          { required: true, message: '请输入借阅数量', trigger: 'blur' }
        ],
        days: [
          { required: true, message: '请输入借阅天数', trigger: 'blur' },
          { type: 'number', min: 1, max: 365, message: '借阅天数必须在1-365天之间', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    isAdmin() {
      return !!this.admin.id;
    },
    calculatedReturnDate() {
      if (!this.borrowForm.days || this.borrowForm.days <= 0) return '';
      const today = new Date();
      const returnDate = new Date(today);
      returnDate.setDate(today.getDate() + this.borrowForm.days);
      return returnDate.toISOString().split('T')[0]; // 格式化为 yyyy-MM-dd
    },
    borrowDays() {
      return this.borrowForm.days || 0;
    },
    estimatedScore() {
      return this.borrowForm.nums * this.borrowForm.score * this.borrowDays;
    }
  },
  created() {
    this.load();
  },
  methods:{
    load(){
      request.get('/book/page',{
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
        name:'',
        bookNo:''
      }
      this.load()
    },
    handleCurrentChange(pageNum){
      this.params.pageNum=pageNum
      this.load()
    },
    del(id){
      request.delete("/book/delete/"+id).then(res => {
        if(res.code === '200'){
          this.$notify.success("删除成功！")
          this.load()
        }else{
          this.$notify.error(res.data)
        }
      })
    },
    borrowBook(book) {
      // 检查用户是否登录
      if (!this.user.id) {
        this.$notify.error('请先登录');
        this.$router.push('/login');
        return;
      }
      
      // 打开借书对话框
      this.borrowForm.bookName = book.name;
      this.borrowForm.bookNo = book.bookNo;
      this.borrowForm.score = book.score;
      this.borrowForm.nums = 1;
      this.borrowForm.maxNums = book.nums;
      this.borrowForm.days = 30; // 默认借阅30天
      this.borrowDialogVisible = true;
    },
    resetBorrowForm() {
      this.$refs['borrowForm'] && this.$refs['borrowForm'].resetFields();
      this.borrowForm = {
        bookName: '',
        bookNo: '',
        score: 0,
        nums: 1,
        maxNums: 0,
        days: 30 // 默认借阅30天
      };
    },
    submitBorrow() {
      this.$refs['borrowForm'].validate((valid) => {
        if (valid) {
          // 检查积分是否足够
          const requiredScore = this.borrowForm.nums * this.borrowForm.score * this.borrowForm.days;
          if (requiredScore > this.user.account) {
            this.$notify.error(`积分不足！需要 ${requiredScore} 积分，当前只有 ${this.user.account} 积分`);
            return;
          }
          
          // 设置加载状态
          this.borrowLoading = true;
          
          // 提交借阅请求（一次性提交，后端会处理多本图书）
          const borrowData = {
            bookNo: this.borrowForm.bookNo,
            bookName: this.borrowForm.bookName,
            userNo: this.user.username,
            userName: this.user.name,
            userPhone: this.user.phone,
            days: this.borrowForm.days, // 使用用户选择的借阅天数
            score: this.borrowForm.score, // 单本积分
            nums: this.borrowForm.nums // 借阅数量
          };
          
          request.post('/borrow/save', borrowData).then(res => {
            if (res.code === '200') {
              this.$notify.success(`成功借阅 ${this.borrowForm.nums} 本图书！`);
              this.borrowDialogVisible = false;
              this.load(); // 刷新列表更新库存
              // 更新用户信息中的积分 - 保留原有token
              this.user.account -= requiredScore;
              const oldUserInfo = JSON.parse(Cookies.get('user') || '{}');
              const newUserInfo = { ...this.user, token: oldUserInfo.token };
              Cookies.set('user', JSON.stringify(newUserInfo));
            } else {
              this.$notify.error(res.msg || '借阅失败');
            }
          }).catch(err => {
            this.$notify.error('借阅失败：' + (err.message || '未知错误'));
          }).finally(() => {
            this.borrowLoading = false;
          });
        }
      });
    }
  }
}
</script>

<style scoped>
/* 添加自定义样式 */
.el-table .cell {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 确保操作列的按钮在同一行 */
.el-table .el-table__body td:last-child {
  padding-right: 0;
}

/* 调整按钮的内边距 */
.el-button--small {
  padding: 6px 10px;
}

/* 书籍封面占位符样式 */
.book-cover-placeholder {
  width: 80px;
  height: 100px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: relative;
  overflow: hidden;
}

.book-cover-placeholder::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(45deg, transparent 30%, rgba(255, 255, 255, 0.1) 50%, transparent 70%);
}

.book-title-text {
  color: white;
  font-size: 12px;
  font-weight: bold;
  text-align: center;
  padding: 5px;
  word-wrap: break-word;
  max-width: 100%;
  line-height: 1.3;
  z-index: 1;
}
</style>
