<template>
  <div style="background-color: white">
    <h1>停车位编辑</h1>
    <el-form :model="form" label-width="120px" ref="formRef" :rules="rules">
      <el-form-item label="车位编号" prop="spotNo">
        <el-input v-model="form.spotNo" placeholder="请输入车位编号"></el-input>
      </el-form-item>
      <el-form-item label="类型ID" prop="typeId">
        <el-input-number v-model="form.typeId" :min="1" placeholder="请输入类型ID"></el-input-number>
      </el-form-item>
      <el-form-item label="区域" prop="area">
        <el-input v-model="form.area" placeholder="请输入区域"></el-input>
      </el-form-item>
      <el-form-item label="楼层" prop="floor">
        <el-input-number v-model="form.floor" :min="1" placeholder="请输入楼层"></el-input-number>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="form.status" placeholder="请选择状态">
          <el-option label="空闲" value="free"></el-option>
          <el-option label="占用中" value="occupied"></el-option>
          <el-option label="已预约" value="reserved"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="单价(元/时)" prop="pricePerHour">
        <el-input-number v-model="form.pricePerHour" :min="0" :precision="2" placeholder="请输入单价"></el-input-number>
      </el-form-item>
      <el-form-item label="图片">
        <el-upload
          action="http://localhost:9090/api/spot/file/upload"
          :on-success="handleImgSuccess"
          list-type="picture">
          <el-button size="small" type="primary">点击上传</el-button>
          <div slot="tip" class="el-upload__tip">只能上传jpg/png文件</div>
        </el-upload>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="save">保存</el-button>
        <el-button @click="$router.push('/parkingSpotList')">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
import request from "@/utils/request";
export default {
  name: 'ParkingSpotEdit',
  data() {
    return {
      form: {},
      rules: {
        spotNo: [{ required: true, message: '请输入车位编号', trigger: 'blur' }],
        area: [{ required: true, message: '请输入区域', trigger: 'blur' }],
        floor: [{ required: true, message: '请输入楼层', trigger: 'blur' }]
      }
    }
  },
  created() {
    const id = this.$route.query.id;
    request.get('/spot/' + id).then(res => {
      if (res.code === '200') {
        this.form = res.data;
      }
    });
  },
  methods: {
    handleImgSuccess(res) {
      this.form.img = res.data;
    },
    save() {
      this.$refs['formRef'].validate(valid => {
        if (valid) {
          request.put('/spot/update', this.form).then(res => {
            if (res.code === '200') {
              this.$notify.success('编辑成功！');
              this.$router.push('/parkingSpotList');
            } else {
              this.$notify.error(res.msg);
            }
          });
        }
      });
    }
  }
}
</script>