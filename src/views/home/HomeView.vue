<template>
  <div>
    <div style="margin: 5px 0">
      <el-select class="input" v-model="timeRange" placeholder="请选择" @change="load">
        <el-option
          v-for="item in options"
          :key="item.value"
          :label="item.label"
          :value="item.value">
        </el-option>
      </el-select>
    </div>

    <!-- 三个并列的折线图表 - 借书、还书、预约 -->
    <div style="display: flex; gap: 10px; margin-top: 5px;">
      <!-- 借书数量折线图 -->
      <el-card style="flex: 1;">
        <div id="borrowChart" style="width: 100%; height: 260px"></div>
      </el-card>

      <!-- 还书数量折线图 -->
      <el-card style="flex: 1;">
        <div id="returnChart" style="width: 100%; height: 260px"></div>
      </el-card>

      <!-- 预约数量折线图 -->
      <el-card style="flex: 1;">
        <div id="reservationChart" style="width: 100%; height: 260px"></div>
      </el-card>
    </div>

    <!-- 新增两个图表区域 - 调整为占据更多空间 -->
    <div style="display: flex; margin-top: 5px;">
      <!-- 左侧：近期热门区域 - 包含图书排行榜和最热分类 -->
      <el-card style="width: 60%; margin-right: 5px;">
        <div style="margin-bottom: 15px; font-weight: bold; font-size: 16px; text-align: center;">近期热门</div>
        
        <!-- 两个排行榜垂直排列显示 -->
        <div style="display: flex; flex-direction: column; gap: 10px;">
          <!-- 最受欢迎图书排行榜（前10） -->
          <div style="flex: 1;">
            <div style="font-weight: bold; margin-bottom: 10px; color: #409EFF; text-align: center; font-size: 14px;">📚 热门图书 Top 10</div>
            <el-table :data="bookTop10List" border style="width: 100%" size="mini" max-height="320">
              <el-table-column type="index" label="排名" width="50" align="center">
                <template slot-scope="scope">
                  <span :style="getRankStyle(scope.$index)">{{ scope.$index + 1 }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="name" label="图书名称" show-overflow-tooltip></el-table-column>
              <el-table-column prop="value" label="借阅" width="60" align="center"></el-table-column>
            </el-table>
          </div>

          <!-- 最热分类（前10） -->
          <div style="flex: 1;">
            <div style="font-weight: bold; margin-bottom: 10px; color: #67C23A; text-align: center; font-size: 14px;">🏷️ 热门分类 Top 10</div>
            <el-table :data="categoryTop10List" border style="width: 100%" size="mini" max-height="320">
              <el-table-column type="index" label="排名" width="50" align="center">
                <template slot-scope="scope">
                  <span :style="getRankStyle(scope.$index)">{{ scope.$index + 1 }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="name" label="分类名称" show-overflow-tooltip></el-table-column>
              <el-table-column prop="value" label="借阅" width="60" align="center"></el-table-column>
            </el-table>
          </div>
        </div>
      </el-card>

      <!-- 右侧:借阅用户top10饼图 -->
      <el-card style="width: 40%; margin-left: 5px;">
        <div id="userTop10" style="width: 100%; height: 580px"></div>
      </el-card>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request";
import * as echarts from 'echarts'
import Cookies from "js-cookie";

// 借书数量折线图配置
const borrowOption = {
  title: {
    text: '借书数量趋势',
    left: 'center'
  },
  tooltip: {
    trigger: 'axis'
  },
  legend: {
    data: ['借书数量'],
    bottom: 0
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '10%',
    containLabel: true
  },
  toolbox: {
    feature: {
      saveAsImage: {}
    }
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: []
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      name: '借书数量',
      type: 'line',
      smooth: true,
      data: [],
      itemStyle: {
        color: '#409EFF'
      },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [{
            offset: 0, color: 'rgba(64, 158, 255, 0.3)'
          }, {
            offset: 1, color: 'rgba(64, 158, 255, 0.1)'
          }]
        }
      }
    }
  ]
};

// 还书数量折线图配置
const returnOption = {
  title: {
    text: '还书数量趋势',
    left: 'center'
  },
  tooltip: {
    trigger: 'axis'
  },
  legend: {
    data: ['还书数量'],
    bottom: 0
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '10%',
    containLabel: true
  },
  toolbox: {
    feature: {
      saveAsImage: {}
    }
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: []
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      name: '还书数量',
      type: 'line',
      smooth: true,
      data: [],
      itemStyle: {
        color: '#67C23A'
      },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [{
            offset: 0, color: 'rgba(103, 194, 58, 0.3)'
          }, {
            offset: 1, color: 'rgba(103, 194, 58, 0.1)'
          }]
        }
      }
    }
  ]
};

// 预约数量折线图配置
const reservationOption = {
  title: {
    text: '预约数量趋势',
    left: 'center'
  },
  tooltip: {
    trigger: 'axis'
  },
  legend: {
    data: ['预约数量'],
    bottom: 0
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '10%',
    containLabel: true
  },
  toolbox: {
    feature: {
      saveAsImage: {}
    }
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: []
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      name: '预约数量',
      type: 'line',
      smooth: true,
      data: [],
      itemStyle: {
        color: '#E6A23C'
      },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [{
            offset: 0, color: 'rgba(230, 162, 60, 0.3)'
          }, {
            offset: 1, color: 'rgba(230, 162, 60, 0.1)'
          }]
        }
      }
    }
  ]
};

// 图书热借top10柱形图配置 - 修复横坐标问题
const bookTop10Option = {
  title: {
    text: '图书热借Top10',
    left: 'center'
  },
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'shadow'
    }
  },
  grid: {
    left: '5%',
    right: '5%',
    bottom: '10%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    data: [],
    axisTick: {
      alignWithLabel: true
    },
    axisLabel: {
      rotate: 45, // 旋转标签避免重叠
      interval: 0 // 显示所有标签
    }
  },
  yAxis: {
    type: 'value'
  },
  series: [{
    name: '借阅次数',
    type: 'bar',
    barWidth: '60%',
    // 设置不同颜色的数组
    itemStyle: {
      color: function(params) {
        // 根据索引设置不同颜色
        const colors = ['#3398DB', '#F46C2A', '#5AB1EF', '#FFB980', '#749F83',
          '#9FEA8D', '#E6B1B1', '#FAD866', '#8B78F6', '#F5994E'];
        return colors[params.dataIndex % colors.length];
      }
    },
    data: []
  }]
};

// 借阅用户top10饼图配置
const userTop10Option = {
  title: {
    text: '借阅用户Top10',
    left: 'center'
  },
  tooltip: {
    trigger: 'item'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  series: [{
    name: '借阅次数',
    type: 'pie',
    radius: ['40%', '70%'],
    avoidLabelOverlap: false,
    itemStyle: {
      borderRadius: 10,
      borderColor: '#fff',
      borderWidth: 2
    },
    label: {
      show: false,
      position: 'center'
    },
    emphasis: {
      label: {
        show: true,
        fontSize: '18',
        fontWeight: 'bold'
      }
    },
    labelLine: {
      show: false
    },
    data: []
  }]
};

export default {
  name: 'HomeView',
  data() {
    return {
      admin: Cookies.get('admin') ? JSON.parse(Cookies.get('admin')) : {},
      borrowChartBox: null,
      returnChartBox: null,
      reservationChartBox: null,
      userTop10Box: null,
      timeRange: 'week',
      options: [
        { label: '最近一周', value: 'week' },
        { label: '最近一个月', value: 'month' },
        { label: '最近二个月', value: 'month2' },
        { label: '最近三个月', value: 'month3' },
      ],
      // 图书排行榜数据
      bookTop10List: [],
      // 分类排行榜数据
      categoryTop10List: []
    }
  },
  mounted() {
    this.load()
  },
  methods: {
    // 获取排名样式
    getRankStyle(index) {
      if (index === 0) {
        return 'color: #FFD700; font-weight: bold; font-size: 16px;' // 金色 - 第一名
      } else if (index === 1) {
        return 'color: #C0C0C0; font-weight: bold; font-size: 15px;' // 银色 - 第二名
      } else if (index === 2) {
        return 'color: #CD7F32; font-weight: bold; font-size: 14px;' // 铜色 - 第三名
      } else {
        return 'color: #909399; font-size: 13px;' // 普通灰色
      }
    },
    
    load() {
      // 初始化图表容器
      if (!this.borrowChartBox) {
        this.borrowChartBox = echarts.init(document.getElementById('borrowChart'));
      }

      if (!this.returnChartBox) {
        this.returnChartBox = echarts.init(document.getElementById('returnChart'));
      }

      if (!this.reservationChartBox) {
        this.reservationChartBox = echarts.init(document.getElementById('reservationChart'));
      }

      if (!this.userTop10Box) {
        this.userTop10Box = echarts.init(document.getElementById('userTop10'));
      }

      // 加载借书数据
      request.get('/borrow/lineCharts/' + this.timeRange).then(res => {
        console.log('借还统计数据响应:', res)
        console.log('日期数据:', res.data.date)
        console.log('借书数据:', res.data.borrow)
        console.log('还书数据:', res.data.retur)
        borrowOption.xAxis.data = res.data.date
        borrowOption.series[0].data = res.data.borrow
        this.borrowChartBox.setOption(borrowOption)
      }).catch(err => {
        console.error('加载借书数据失败', err)
      })

      // 加载还书数据
      request.get('/borrow/lineCharts/' + this.timeRange).then(res => {
        console.log('还书图表数据响应:', res)
        returnOption.xAxis.data = res.data.date
        returnOption.series[0].data = res.data.retur
        this.returnChartBox.setOption(returnOption)
      }).catch(err => {
        console.error('加载还书数据失败', err)
      })

      // 加载预约数据
      request.get('/borrow/reservationCharts/' + this.timeRange).then(res => {
        reservationOption.xAxis.data = res.data.date
        reservationOption.series[0].data = res.data.reservation
        this.reservationChartBox.setOption(reservationOption)
      })

      // 加载借阅用户top10数据
      request.get('/report/users').then(res => {
        const nameList = res.data.nameList ? res.data.nameList.split(',') : []
        const numberList = res.data.numberList ? res.data.numberList.split(',').map(Number) : []

        // 构造饼图数据
        const pieData = nameList.map((name, index) => ({
          name: name,
          value: numberList[index] || 0
        }))

        userTop10Option.series[0].data = pieData
        this.userTop10Box.setOption(userTop10Option)
      })

      // 加载图书排行榜数据（表格）
      request.get('/report/top10').then(res => {
        const nameList = res.data.nameList ? res.data.nameList.split(',') : []
        const numberList = res.data.numberList ? res.data.numberList.split(',').map(Number) : []

        // 构造表格数据
        this.bookTop10List = nameList.map((name, index) => ({
          name: name,
          value: numberList[index] || 0
        }))
      })

      // 加载热门分类数据
      request.get('/report/categories').then(res => {
        const nameList = res.data.nameList ? res.data.nameList.split(',') : []
        const numberList = res.data.numberList ? res.data.numberList.split(',').map(Number) : []

        // 构造表格数据
        this.categoryTop10List = nameList.map((name, index) => ({
          name: name,
          value: numberList[index] || 0
        }))
      })
      
      // 添加窗口resize事件监听
      window.addEventListener('resize', () => {
        if (this.borrowChartBox) {
          this.borrowChartBox.resize();
        }
        if (this.returnChartBox) {
          this.returnChartBox.resize();
        }
        if (this.reservationChartBox) {
          this.reservationChartBox.resize();
        }
        if (this.userTop10Box) {
          this.userTop10Box.resize();
        }
      });
    }
  }
}
</script>
