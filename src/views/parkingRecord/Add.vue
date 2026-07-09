<template>
  <div style="background-color: white">
    <h1>停车记录新增</h1>
    <el-form :model="form" label-width="120px" ref="formRef" :rules="rules">
      <el-form-item label="车位ID" prop="spotId">
        <el-input-number v-model="form.spotId" :min="1" placeholder="请输入车位ID"></el-input-number>
      </el-form-item>
      <el-form-item label="驾驶员ID" prop="driverId">
        <el-input-number v-model="form.driverId" :min="1" placeholder="请输入驾驶员ID"></el-input-number>
      </el-form-item>
      <el-form-item label="车牌号" prop="licensePlate">
        <el-input v-model="form.licensePlate" placeholder="请输入车牌号"></el-input>
      </el-form-item>
      <el-form-item label="入场时间" prop="entryTime">
        <el-date-picker v-model="form.entryTime" type="datetime" placeholder="选择入场时间"></el-date-picker>
      </el-form-item>
      <el-form-item label="出场时间">
        <el-date-picker v-model="form.exitTime" type="datetime" placeholder="选择出场时间"></el-date-picker>
      </el-form-item>
      <el-form-item label="费用">
        <el-input-number v-model="form.fee" :min="0" :precision="2" placeholder="请输入费用"></el-input-number>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="save">保存</el-button>
        <el-button @click="$router.push('/parkingRecordList')">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
import request from "@/utils/request";
export default {
  name: 'ParkingRecordAdd',
  data() {
    return {
      form: {},
      rules: {
        spotId: [{ required: true, message: '请输入车位ID', trigger: 'blur' }],
        driverId: [{ required: true, message: '请输入驾驶员ID', trigger: 'blur' }],
        licensePlate: [{ required: true, message: '请输入车牌号', trigger: 'blur' }],
        entryTime: [{ required: true, message: '请选择入场时间', trigger: 'change' }]
      }
    }
  },
  methods: {
    save() {
      this.$refs['formRef'].validate(valid => {
        if (valid) {
          request.post('/parking/save', this.form).then(res => {
            if (res.code === '200') {
              this.$notify.success('新增成功！');
              this.$router.push('/parkingRecordList');
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