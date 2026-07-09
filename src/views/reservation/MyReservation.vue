<template>
  <div style="padding: 20px; background-color: #f5f7fa; min-height: 100vh">
    <el-card>
      <!-- 面包屑导航 -->
      <el-breadcrumb separator="/" style="margin-bottom: 20px">
        <el-breadcrumb-item :to="{ path: '/userHome' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>我的预约</el-breadcrumb-item>
      </el-breadcrumb>

      <h3 style="margin-bottom: 20px">
        <i class="el-icon-date" style="color: #409EFF"></i>
        我的预约
      </h3>

      <!-- 筛选条件 -->
      <el-form :inline="true" style="margin-bottom: 20px">
        <el-form-item label="状态">
          <el-select v-model="filterStatus" placeholder="全部" clearable @change="loadReservations">
            <el-option label="全部" value=""></el-option>
            <el-option label="待取书" value="待取书"></el-option>
            <el-option label="已借出" value="已借出"></el-option>
            <el-option label="已归还" value="已归还"></el-option>
            <el-option label="已取消" value="已取消"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-refresh" @click="loadReservations">刷新</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="reservations" stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60"></el-table-column>
        <el-table-column prop="bookName" label="图书名称" min-width="200"></el-table-column>
        <el-table-column prop="bookNo" label="ISBN" width="150"></el-table-column>
        <el-table-column prop="barcode" label="书号" width="150"></el-table-column>
        <el-table-column prop="reserveTime" label="预约时间" width="180">
          <template v-slot="scope">
            {{ formatDateTime(scope.row.reserveTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="expireTime" label="预约到期时间" width="180">
          <template v-slot="scope">
            {{ formatDateTime(scope.row.expireTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="预约状态" width="100">
          <template v-slot="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template v-slot="scope">
            <el-button 
              type="danger" 
              size="mini"
              :disabled="scope.row.status !== '待取书'"
              @click="cancelReservation(scope.row)">
              取消预约
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <el-empty v-if="!loading && reservations.length === 0" description="暂无预约记录"></el-empty>
    </el-card>
  </div>
</template>

<script>
import { reservationApi } from "@/utils/request";

export default {
  name: "MyReservation",
  data() {
    return {
      loading: false,
      reservations: [],
      filterStatus: ''
    };
  },
  created() {
    this.loadReservations();
  },
  methods: {
    loadReservations() {
      this.loading = true;
      reservationApi.getMyReservations(this.filterStatus).then(res => {
        if (res.code === '200') {
          this.reservations = res.data || [];
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
    cancelReservation(row) {
      this.$confirm('确认取消该预约吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        reservationApi.cancel(row.id).then(res => {
          if (res.code === '200') {
            this.$message.success('取消成功');
            this.loadReservations();
          } else {
            this.$message.error(res.msg || '取消失败');
          }
        }).catch(err => {
          this.$message.error('网络错误');
          console.error(err);
        });
      }).catch(() => {
        // 用户取消
      });
    },
    getStatusType(status) {
      const typeMap = {
        '待取书': 'warning',
        '已借出': 'primary',
        '已归还': 'success',
        '已逾期': 'danger',
        '已取消': 'info'
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
