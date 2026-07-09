<template>
  <div style="background-color: white">
    <div>
      <el-input style="width: 200px" placeholder="请输入车位编号" v-model="params.spotNo"></el-input>
      <el-input style="width: 200px;margin-left: 5px" placeholder="请输入区域" v-model="params.area"></el-input>
      <el-button style="margin-left: 3px" type="primary" @click="load"><i class="el-icon-search"></i> 搜索</el-button>
      <el-button style="margin-left: 3px" type="warning" @click="reset"><i class="el-icon-refresh"></i> 重置</el-button>
    </div>
    <el-divider content-position="left"></el-divider>

    <el-table :data="tableData" style="margin-top: 3px; width: 100%;">
      <el-table-column prop="id" label="编号" width="80px"></el-table-column>
      <el-table-column prop="spotNo" label="车位编号" width="120px"></el-table-column>
      <el-table-column prop="typeId" label="类型ID" width="100px"></el-table-column>
      <el-table-column prop="area" label="区域" width="120px"></el-table-column>
      <el-table-column prop="floor" label="楼层" width="80px"></el-table-column>
      <el-table-column label="状态" width="120px">
        <template v-slot="scope">
          <el-tag v-if="scope.row.status === 'free'" type="success">空闲</el-tag>
          <el-tag v-else-if="scope.row.status === 'occupied'" type="danger">占用中</el-tag>
          <el-tag v-else-if="scope.row.status === 'reserved'" type="warning">已预约</el-tag>
          <el-tag v-else type="info">{{ scope.row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="pricePerHour" label="单价(元/时)" width="120px"></el-table-column>
      <el-table-column label="图片" width="120px">
        <template v-slot="scope">
          <el-image v-if="scope.row.img" :src="scope.row.img" style="width: 60px;height: 60px" fit="cover"></el-image>
        </template>
      </el-table-column>
      <el-table-column prop="createtime" label="创建时间" width="160px"></el-table-column>
      <el-table-column prop="updatetime" label="修改时间" width="160px"></el-table-column>
      <el-table-column label="操作" width="200px">
        <template v-slot="scope">
          <el-button type="primary" @click="$router.push('/editParkingSpot?id='+scope.row.id)">编辑</el-button>
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
  name: 'ParkingSpotList',
  data() {
    return {
      tableData: [],
      total: 0,
      params: {
        pageNum: 1,
        pageSize: 10,
        spotNo: '',
        area: ''
      }
    }
  },
  created() {
    this.load();
  },
  methods: {
    load() {
      request.get('/spot/page', { params: this.params }).then(res => {
        if (res.code === '200') {
          this.tableData = res.data.list;
          this.total = res.data.total;
        }
      });
    },
    reset() {
      this.params = { pageNum: 1, pageSize: 10, spotNo: '', area: '' };
      this.load();
    },
    handleCurrentChange(pageNum) {
      this.params.pageNum = pageNum;
      this.load();
    },
    del(id) {
      request.delete('/spot/delete/' + id).then(res => {
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