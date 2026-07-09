<template>
  <div style="padding: 20px; background-color: #f5f7fa; min-height: 100vh">
    <el-card>
      <!-- 面包屑导航 -->
      <el-breadcrumb separator="/" style="margin-bottom: 20px">
        <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>预约管理</el-breadcrumb-item>
      </el-breadcrumb>

      <h3 style="margin-bottom: 20px">
        <i class="el-icon-s-order" style="color: #409EFF"></i>
        预约管理
      </h3>

      <!-- 搜索筛选区 -->
      <el-form :inline="true" :model="searchForm" style="margin-bottom: 20px">
        <el-form-item label="用户姓名">
          <el-input v-model="searchForm.userName" placeholder="请输入用户姓名" clearable></el-input>
        </el-form-item>
        <el-form-item label="会员码">
          <el-input v-model="searchForm.userNo" placeholder="请输入会员码" clearable></el-input>
        </el-form-item>
        <el-form-item label="图书名称">
          <el-input v-model="searchForm.bookName" placeholder="请输入图书名称" clearable></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待取书" value="待取书"></el-option>
            <el-option label="已取书" value="已取书"></el-option>
            <el-option label="已取消" value="已取消"></el-option>
            <el-option label="已超时" value="已超时"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <el-button type="primary" icon="el-icon-refresh" @click="loadReservationList" style="margin-bottom: 20px">
        刷新
      </el-button>

      <!-- 预约列表 -->
      <el-table :data="reservationList" stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60"></el-table-column>
        <el-table-column prop="userName" label="用户姓名" width="90"></el-table-column>
        <el-table-column prop="userNo" label="会员码" width="110"></el-table-column>
        <el-table-column prop="bookName" label="图书名称" min-width="120"></el-table-column>
        <el-table-column label="ISBN/书号" min-width="180">
          <template v-slot="scope">
            <div style="line-height: 1.6;">
              <div v-if="scope.row.bookNo" style="color: #409EFF; white-space: nowrap;">
                <i class="el-icon-document"></i> {{ scope.row.bookNo }}
              </div>
              <div v-if="scope.row.barcode" style="color: #67C23A; margin-top: 3px; white-space: nowrap;">
                <i class="el-icon-tickets"></i> {{ scope.row.barcode }}
              </div>
              <div v-if="!scope.row.bookNo && !scope.row.barcode" style="color: #909399;">
                暂无数据
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="reserveTime" label="预约时间" width="155">
          <template v-slot="scope">
            {{ formatDateTime(scope.row.reserveTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="expireTime" label="有效期" width="155">
          <template v-slot="scope">
            {{ formatDateTime(scope.row.expireTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template v-slot="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" fixed="right">
          <template v-slot="scope">
            <div style="display: flex; gap: 8px;">
              <el-button 
                v-if="scope.row.status === '待取书'"
                type="success" 
                size="mini"
                @click="confirmPickup(scope.row)">
                办理借书
              </el-button>
              <el-button 
                v-if="scope.row.status === '待取书'"
                type="warning" 
                size="mini"
                @click="cancelReservation(scope.row)">
                取消预约
              </el-button>
              <el-button 
                type="info" 
                size="mini"
                @click="viewDetail(scope.row)">
                查看详情
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.pageNum"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pagination.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        style="margin-top: 20px; text-align: right">
      </el-pagination>

      <!-- 空状态 -->
      <el-empty v-if="!loading && reservationList.length === 0" description="暂无预约记录"></el-empty>
    </el-card>

    <!-- 确认取书对话框 -->
    <el-dialog title="办理借书" :visible.sync="confirmDialogVisible" width="500px">
      <el-form :model="confirmForm" label-width="100px">
        <el-form-item label="图书名称">
          <span>{{ currentRow.bookName }}</span>
        </el-form-item>
        <el-form-item label="用户姓名">
          <span>{{ currentRow.userName }}</span>
        </el-form-item>
        <el-form-item label="书号">
          <span>{{ currentRow.barcode }}</span>
        </el-form-item>
        <el-form-item label="借阅天数" prop="days">
          <el-input-number 
            v-model="confirmForm.days" 
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
      </el-form>
      <div slot="footer">
        <el-button @click="confirmDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirm">确认</el-button>
      </div>
    </el-dialog>

    <!-- 取消预约对话框 -->
    <el-dialog title="取消预约" :visible.sync="cancelDialogVisible" width="400px">
      <p>确定要取消该预约吗？</p>
      <el-form :model="currentRow" label-width="100px">
        <el-form-item label="图书名称">
          <span>{{ currentRow.bookName }}</span>
        </el-form-item>
        <el-form-item label="用户姓名">
          <span>{{ currentRow.userName }}</span>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="cancelDialogVisible = false">取消</el-button>
        <el-button type="warning" @click="handleCancel">确认取消</el-button>
      </div>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog title="预约详情" :visible.sync="detailDialogVisible" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="预约编号">{{ detailData.id }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(detailData.status)">{{ detailData.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="用户姓名">{{ detailData.userName }}</el-descriptions-item>
        <el-descriptions-item label="会员码">{{ detailData.userNo }}</el-descriptions-item>
        <el-descriptions-item label="图书名称" :span="2">{{ detailData.bookName }}</el-descriptions-item>
        <el-descriptions-item label="ISBN/书号" :span="2">
          <div style="line-height: 1.5;">
            <div v-if="detailData.bookNo" style="color: #409EFF;">
              <i class="el-icon-document"></i> ISBN: {{ detailData.bookNo }}
            </div>
            <div v-if="detailData.barcode" style="color: #67C23A; margin-top: 4px;">
              <i class="el-icon-tickets"></i> 书号: {{ detailData.barcode }}
            </div>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="预约时间">{{ formatDateTime(detailData.reserveTime) }}</el-descriptions-item>
        <el-descriptions-item label="有效期">{{ formatDateTime(detailData.expireTime) }}</el-descriptions-item>
        <el-descriptions-item label="取书时间" v-if="detailData.pickupTime">
          {{ formatDateTime(detailData.pickupTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="取消时间" v-if="detailData.cancelTime">
          {{ formatDateTime(detailData.cancelTime) }}
        </el-descriptions-item>
      </el-descriptions>
      <div slot="footer">
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from "@/utils/request";

export default {
  name: "WaitingPickup",
  data() {
    return {
      loading: false,
      reservationList: [],
      searchForm: {
        userName: '',
        userNo: '',
        bookName: '',
        status: ''
      },
      pagination: {
        pageNum: 1,
        pageSize: 10,
        total: 0
      },
      confirmDialogVisible: false,
      cancelDialogVisible: false,
      detailDialogVisible: false,
      currentRow: {},
      detailData: {},
      confirmForm: {
        days: 30
      }
    };
  },
  created() {
    this.loadReservationList();
  },
  computed: {
    calculatedReturnDate() {
      if (!this.confirmForm.days || this.confirmForm.days <= 0) return '';
      const today = new Date();
      const returnDate = new Date(today);
      returnDate.setDate(today.getDate() + this.confirmForm.days);
      return returnDate.toISOString().split('T')[0]; // 格式化为 yyyy-MM-dd
    }
  },
  methods: {
    loadReservationList() {
      this.loading = true;
      const params = {
        pageNum: this.pagination.pageNum,
        pageSize: this.pagination.pageSize,
        ...this.searchForm
      };
      
      request.get('/reservation/admin/list', { params }).then(res => {
        if (res.code === '200') {
          this.reservationList = res.data.list || [];
          this.pagination.total = res.data.total || 0;
        } else {
          this.$message.error(res.msg || '加载失败');
        }
      }).catch(err => {
        this.$message.error('网络错误');
        console.error(err);
      }).finally(() => {
        this.loading = false;
      });
    },
    handleSearch() {
      this.pagination.pageNum = 1;
      this.loadReservationList();
    },
    handleReset() {
      this.searchForm = {
        userName: '',
        userNo: '',
        bookName: '',
        status: ''
      };
      this.pagination.pageNum = 1;
      this.loadReservationList();
    },
    handleSizeChange(val) {
      this.pagination.pageSize = val;
      this.pagination.pageNum = 1;
      this.loadReservationList();
    },
    handleCurrentChange(val) {
      this.pagination.pageNum = val;
      this.loadReservationList();
    },
    confirmPickup(row) {
      this.currentRow = row;
      this.confirmForm.days = row.days || 30;
      this.confirmDialogVisible = true;
    },
    handleConfirm() {
      // 验证借阅天数
      if (!this.confirmForm.days || this.confirmForm.days < 1 || this.confirmForm.days > 365) {
        this.$message.error('借阅天数必须在1-365天之间');
        return;
      }
      
      // 调用确认取书接口，传递借阅天数
      request.post(`/reservation/confirm/${this.currentRow.id}`, { days: this.confirmForm.days }).then(res => {
        if (res.code === '200') {
          this.$message.success('办理借书成功');
          this.confirmDialogVisible = false;
          this.loadReservationList();
        } else {
          this.$message.error(res.msg || '办理失败');
        }
      }).catch(err => {
        this.$message.error('网络错误');
        console.error(err);
      });
    },
    cancelReservation(row) {
      this.currentRow = row;
      this.cancelDialogVisible = true;
    },
    handleCancel() {
      // 调用管理员取消预约接口
      request.post(`/reservation/admin/cancel/${this.currentRow.id}`).then(res => {
        if (res.code === '200') {
          this.$message.success('取消预约成功');
          this.cancelDialogVisible = false;
          this.loadReservationList();
        } else {
          this.$message.error(res.msg || '取消失败');
        }
      }).catch(err => {
        this.$message.error('网络错误');
        console.error(err);
      });
    },
    viewDetail(row) {
      this.detailData = row;
      this.detailDialogVisible = true;
    },
    getStatusType(status) {
      const typeMap = {
        '待取书': 'warning',
        '已取书': 'success',
        '已取消': 'info',
        '已超时': 'danger'
      };
      return typeMap[status] || '';
    },
    formatDateTime(dateTime) {
      if (!dateTime) return '-';
      return dateTime.replace('T', ' ').substring(0, 19);
    }
  }
};
</script>

<style scoped>
</style>
