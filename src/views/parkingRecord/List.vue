<template>
  <div style="background-color: white">
    <div>
      <el-input style="width: 200px" placeholder="请输入车牌号" v-model="params.licensePlate"></el-input>
      <el-button style="margin-left: 3px" type="primary" @click="load"><i class="el-icon-search"></i> 搜索</el-button>
      <el-button style="margin-left: 3px" type="warning" @click="reset"><i class="el-icon-refresh"></i> 重置</el-button>
    </div>
    <el-divider content-position="left"></el-divider>

    <el-table :data="tableData" style="margin-top: 3px; width: 100%;">
      <el-table-column prop="id" label="编号" width="80px"></el-table-column>
      <el-table-column prop="spotId" label="车位ID" width="100px"></el-table-column>
      <el-table-column prop="driverId" label="驾驶员ID" width="100px"></el-table-column>
      <el-table-column prop="licensePlate" label="车牌号" width="140px"></el-table-column>
      <el-table-column prop="entryTime" label="入场时间" width="180px"></el-table-column>
      <el-table-column prop="exitTime" label="出场时间" width="180px"></el-table-column>
      <el-table-column prop="fee" label="费用" width="100px"></el-table-column>
      <el-table-column prop="createtime" label="创建时间" width="160px"></el-table-column>
      <el-table-column prop="updatetime" label="修改时间" width="160px"></el-table-column>
      <el-table-column label="操作" width="200px">
        <template v-slot="scope">
          <el-button type="primary" @click="$router.push('/editParkingRecord?id='+scope.row.id)">编辑</el-button>
          <el-popconfirm title="您确定删除这行数据吗?" @confirm="del(scope.row.id)" style="margin-left: 5px">
            <el-button type="danger" slot="reference">删除</el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <div>
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
import request from "@/utils/request";
export default {
  name: 'ParkingRecordList',
  data() {
    return {
      tableData: [],
      total: 0,
      params: {
        pageNum: 1,
        pageSize: 10,
        licensePlate: ''
      }
    }
  },
  created() {
    this.load();
  },
  methods: {
    load() {
      request.get('/parking/page', { params: this.params }).then(res => {
        if (res.code === '200') {
          this.tableData = res.data.list;
          this.total = res.data.total;
        }
      });
    },
    reset() {
      this.params = { pageNum: 1, pageSize: 10, licensePlate: '' };
      this.load();
    },
    handleCurrentChange(pageNum) {
      this.params.pageNum = pageNum;
      this.load();
    },
    del(id) {
      request.delete('/parking/delete/' + id).then(res => {
        if (res.code === '200') {
          this.$notify.success('删除成功！');
          this.load();
        } else {
          this.$notify.error(res.msg);
        }
      });
    }
  }
}
</script>