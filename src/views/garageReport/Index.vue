<template>
  <div style="background-color: white; padding: 20px;">
    <h2 style="text-align: center; margin-bottom: 20px;">停车场数据报表</h2>
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card style="margin-bottom: 20px;">
          <div slot="header">
            <span style="font-weight: bold; font-size: 16px;">停车场收入 Top10 车位</span>
          </div>
          <div id="revenueChart" style="width: 100%; height: 400px;"></div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <div slot="header">
            <span style="font-weight: bold; font-size: 16px;">高频车主 Top10</span>
          </div>
          <div id="driverChart" style="width: 100%; height: 400px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <div slot="header">
            <span style="font-weight: bold; font-size: 16px;">热门车位类型 Top10</span>
          </div>
          <div id="spotTypeChart" style="width: 100%; height: 400px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>
<script>
import request from "@/utils/request";
import * as echarts from 'echarts';
export default {
  name: 'GarageReport',
  data() {
    return {
      revenueChart: null,
      driverChart: null,
      spotTypeChart: null
    }
  },
  mounted() {
    this.loadAllReports();
  },
  beforeDestroy() {
    if (this.revenueChart) this.revenueChart.dispose();
    if (this.driverChart) this.driverChart.dispose();
    if (this.spotTypeChart) this.spotTypeChart.dispose();
  },
  methods: {
    loadAllReports() {
      this.loadRevenueTop10();
      this.loadDriverTop10();
      this.loadSpotTypeTop10();
    },
    loadRevenueTop10() {
      request.get('/report/top10').then(res => {
        if (res.code === '200' && res.data) {
          this.initRevenueChart(res.data);
        }
      });
    },
    loadDriverTop10() {
      request.get('/report/drivers').then(res => {
        if (res.code === '200' && res.data) {
          this.initDriverChart(res.data);
        }
      });
    },
    loadSpotTypeTop10() {
      request.get('/report/spotTypes').then(res => {
        if (res.code === '200' && res.data) {
          this.initSpotTypeChart(res.data);
        }
      });
    },
    initRevenueChart(data) {
      const chartDom = document.getElementById('revenueChart');
      if (!chartDom) return;
      if (this.revenueChart) this.revenueChart.dispose();
      this.revenueChart = echarts.init(chartDom);
      const option = {
        tooltip: { trigger: 'axis' },
        xAxis: {
          type: 'category',
          data: data.map(item => item.spotNo || '车位' + item.spotId),
          axisLabel: { rotate: 30 }
        },
        yAxis: { type: 'value', name: '收入(元)' },
        series: [{
          name: '收入',
          type: 'bar',
          data: data.map(item => item.totalRevenue || 0),
          itemStyle: { color: '#409EFF' },
          label: { show: true, position: 'top' }
        }]
      };
      this.revenueChart.setOption(option);
      window.addEventListener('resize', () => this.revenueChart.resize());
    },
    initDriverChart(data) {
      const chartDom = document.getElementById('driverChart');
      if (!chartDom) return;
      if (this.driverChart) this.driverChart.dispose();
      this.driverChart = echarts.init(chartDom);
      const option = {
        tooltip: { trigger: 'item' },
        series: [{
          name: '访问次数',
          type: 'pie',
          radius: ['30%', '70%'],
          data: data.map(item => ({
            name: item.driverName || '用户' + item.driverId,
            value: item.visitCount || 0
          })),
          label: { formatter: '{b}: {c}次' }
        }]
      };
      this.driverChart.setOption(option);
      window.addEventListener('resize', () => this.driverChart.resize());
    },
    initSpotTypeChart(data) {
      const chartDom = document.getElementById('spotTypeChart');
      if (!chartDom) return;
      if (this.spotTypeChart) this.spotTypeChart.dispose();
      this.spotTypeChart = echarts.init(chartDom);
      const option = {
        tooltip: { trigger: 'axis' },
        xAxis: {
          type: 'category',
          data: data.map(item => item.typeName || '类型' + item.typeId),
          axisLabel: { rotate: 30 }
        },
        yAxis: { type: 'value', name: '使用次数' },
        series: [{
          name: '使用次数',
          type: 'bar',
          data: data.map(item => item.usageCount || 0),
          itemStyle: { color: '#67C23A' },
          label: { show: true, position: 'top' }
        }]
      };
      this.spotTypeChart.setOption(option);
      window.addEventListener('resize', () => this.spotTypeChart.resize());
    }
  }
}
</script>