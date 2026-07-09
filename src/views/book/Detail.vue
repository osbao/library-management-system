<template>
  <div style="padding: 20px; background-color: #f5f7fa; min-height: 100vh">
    <el-card v-loading="loading">
      <!-- 面包屑导航 -->
      <el-breadcrumb separator="/" style="margin-bottom: 20px">
        <el-breadcrumb-item :to="{ path: '/userHome' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/userBookList' }">书库</el-breadcrumb-item>
        <el-breadcrumb-item>图书详情</el-breadcrumb-item>
      </el-breadcrumb>

      <div v-if="book">
        <!-- 图书基本信息 -->
        <div class="book-header">
          <div class="book-header-content">
            <!-- 左侧大图封面 -->
            <div class="book-cover-large">
              <div class="book-cover-placeholder-large">
                <div class="book-title-text-large">{{ book.name }}</div>
              </div>
            </div>
            <!-- 右侧基本信息 -->
            <div class="book-info-right">
              <h2 class="book-main-title">
                <i class="el-icon-collection" style="color: #409EFF"></i>
                【图书】{{ book.name }}
              </h2>
              <div class="quick-info">
                <p><i class="el-icon-user"></i> 作者：{{ book.author }}</p>
                <p><i class="el-icon-office-building"></i> 出版社：{{ book.publisher }}</p>
                <p><i class="el-icon-date"></i> 出版日期：{{ book.publishDate }}</p>
                <p><i class="el-icon-collection-tag"></i> 分类：{{ book.category }}</p>
                <p><i class="el-icon-goods"></i> 库存：{{ book.nums }} 本</p>
                <p><i class="el-icon-star-on"></i> 评分：
                  <el-rate v-model="book.score" disabled show-score text-color="#ff9900"></el-rate>
                </p>
              </div>
            </div>
          </div>
        </div>

        <!-- 馆藏信息和统计图表 -->
        <div class="info-and-charts" style="margin-top: 20px; display: flex; gap: 20px;">
          <!-- 左侧：馆藏信息 -->
          <div class="collection-info" style="flex: 1; min-width: 0;">
            <h4>纸本馆藏</h4>
            <el-table :data="bookItems" stripe style="margin-top: 15px" size="small" max-height="300">
              <el-table-column type="index" label="序号" width="50"></el-table-column>
              <el-table-column prop="barcode" label="书号" width="120"></el-table-column>
              <el-table-column prop="isbn" label="ISBN" width="130" show-overflow-tooltip>
                <template v-slot="scope">
                  <span style="white-space: nowrap;">{{ scope.row.isbn }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="storage_time" label="入藏时间" width="100">
                <template v-slot="scope">
                  {{ formatDate(scope.row.storage_time) }}
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="80">
                <template v-slot="scope">
                  <el-tag :type="getStatusType(scope.row.status)" size="mini">
                    {{ scope.row.status }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="location" label="馆藏地" min-width="120" show-overflow-tooltip></el-table-column>
              <el-table-column label="操作" width="70" fixed="right">
                <template v-slot="scope">
                  <el-button 
                    type="primary" 
                    size="mini"
                    :disabled="scope.row.status !== '在架'"
                    @click="reserveBook(scope.row)">
                    预约
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <!-- 右侧：统计图表 -->
          <div class="charts-container" style="flex: 1; min-width: 0; display: flex; flex-direction: column; gap: 15px;">
            <!-- 借阅统计图表 -->
            <div class="chart-wrapper">
              <h4><i class="el-icon-data-line"></i> 借阅趋势（最近12个月）</h4>
              <div id="borrowChart" style="width: 100%; height: 220px;"></div>
            </div>
            
            <!-- 预约统计图表 -->
            <div class="chart-wrapper">
              <h4><i class="el-icon-bell"></i> 预约趋势（最近12个月）</h4>
              <div id="reservationChart" style="width: 100%; height: 220px;"></div>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import request, { reservationApi } from "@/utils/request";
import * as echarts from 'echarts';

export default {
  name: "BookDetail",
  data() {
    return {
      loading: false,
      book: null,
      bookItems: [],
      reserveDialogVisible: false,
      reserveDays: 30, // 默认借阅天数
      borrowChart: null, // ECharts实例
      reservationChart: null // 预约图表ECharts实例
    };
  },
  created() {
    this.loadBookDetail();
  },
  methods: {
    loadBookDetail() {
      const bookId = this.$route.query.id;
      if (!bookId) {
        this.$message.error('图书ID不存在');
        this.$router.back();
        return;
      }

      this.loading = true;

      // 加载图书基本信息
      request.get(`/book/${bookId}`).then(res => {
        if (res.code === '200') {
          this.book = res.data;
          // 将score转换为适合el-rate的格式
          if (this.book.score && this.book.score > 5) {
            this.book.score = this.book.score / 2;
          }
          
          // 图书信息加载完成后，再加载统计数据
          this.$nextTick(() => {
            this.loadBorrowStats();
            this.loadReservationStats();
          });
        } else {
          this.$message.error(res.msg || '加载图书信息失败');
        }
      }).catch(err => {
        this.$message.error('网络错误');
        console.error(err);
      });

      // 加载馆藏信息（book_item表数据）
      request.get(`/book/items/${bookId}`).then(res => {
        console.log('完整响应:', res);
        if (res.code === '200') {
          this.bookItems = res.data || [];
          console.log('馆藏信息数据:', this.bookItems);
          if (this.bookItems.length > 0) {
            console.log('第一条馆藏数据:', JSON.stringify(this.bookItems[0]));
            console.log('所有字段名:', Object.keys(this.bookItems[0]));
            console.log('storage_time:', this.bookItems[0].storage_time);
            console.log('storageTime:', this.bookItems[0].storageTime);
          }
        }
      }).catch(err => {
        console.error('加载馆藏信息失败', err);
      }).finally(() => {
        this.loading = false;
      });
    },
    formatDate(date) {
      if (!date) return '-';
      // 如果已经是字符串格式（yyyy-MM-dd），直接返回
      if (typeof date === 'string') {
        // 验证是否是有效的日期字符串
        if (date.match(/^\d{4}-\d{2}-\d{2}$/)) {
          return date;
        }
        // 尝试解析其他格式的字符串
        const d = new Date(date);
        if (!isNaN(d.getTime())) {
          const year = d.getFullYear();
          const month = String(d.getMonth() + 1).padStart(2, '0');
          const day = String(d.getDate()).padStart(2, '0');
          return `${year}-${month}-${day}`;
        }
        return '-';
      }
      // 如果是Date对象或时间戳，格式化为 yyyy-MM-dd
      const d = new Date(date);
      if (isNaN(d.getTime())) return '-';
      const year = d.getFullYear();
      const month = String(d.getMonth() + 1).padStart(2, '0');
      const day = String(d.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    },
    getStatusType(status) {
      const typeMap = {
        '在架': 'success',
        '已预约': 'warning',
        '已借出': 'danger',
        '维修中': 'info',
        '遗失': 'info'
      };
      return typeMap[status] || '';
    },
    reserveBook(item) {
      this.$confirm('确认预约该图书吗？预约后请在3天内到馆取书。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        reservationApi.reserve({
          bookNo: this.book.bookNo,
          days: this.reserveDays
        }).then(res => {
          if (res.code === '200') {
            this.$message.success('预约成功！请在3天内到馆取书。');
            // 刷新馆藏信息
            this.loadBookDetail();
          } else {
            this.$message.error(res.msg || '预约失败');
          }
        }).catch(err => {
          this.$message.error('网络错误');
          console.error(err);
        });
      }).catch(() => {
        // 用户取消
      });
    },
    loadBorrowStats() {
      if (!this.book || !this.book.bookNo) {
        console.warn('图书信息或bookNo不存在，无法加载借阅统计');
        return;
      }

      console.log('开始加载借阅统计，bookNo:', this.book.bookNo);
      request.get(`/borrow/stats/${this.book.bookNo}`).then(res => {
        console.log('借阅统计响应:', res);
        if (res.code === '200' && res.data) {
          console.log('借阅统计数据:', res.data);
          this.renderBorrowChart(res.data);
        } else {
          console.warn('借阅统计返回数据异常:', res);
        }
      }).catch(err => {
        console.error('加载借阅统计失败', err);
      });
    },
    renderBorrowChart(data) {
      console.log('渲染借阅图表，数据:', data);
      // 准备数据
      const dates = data.map(item => item.date);
      const counts = data.map(item => item.count);
      
      console.log('借阅图表 - 日期:', dates);
      console.log('借阅图表 - 数量:', counts);

      // 确保DOM元素存在
      this.$nextTick(() => {
        const chartDom = document.getElementById('borrowChart');
        if (!chartDom) {
          console.error('借阅图表DOM元素不存在');
          return;
        }

        // 初始化或获取ECharts实例
        if (!this.borrowChart) {
          this.borrowChart = echarts.init(chartDom);
        }

        // 配置选项 - 折线图
        const option = {
          title: {
            text: '月度借阅次数',
            left: 'center',
            textStyle: {
              fontSize: 14,
              fontWeight: 'normal'
            }
          },
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'cross'
            }
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            top: '15%',
            containLabel: true
          },
          xAxis: {
            type: 'category',
            data: dates,
            boundaryGap: false,
            axisLabel: {
              rotate: 45,
              interval: 0,
              fontSize: 10
            }
          },
          yAxis: {
            type: 'value',
            name: '借阅次数',
            minInterval: 1,
            nameTextStyle: {
              fontSize: 11
            },
            axisLabel: {
              fontSize: 10
            }
          },
          series: [
            {
              name: '借阅次数',
              type: 'line',
              data: counts,
              smooth: true,
              symbol: 'circle',
              symbolSize: 6,
              lineStyle: {
                color: '#409EFF',
                width: 2
              },
              itemStyle: {
                color: '#409EFF'
              },
              areaStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
                  { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
                ])
              }
            }
          ]
        };

        console.log('设置借阅图表配置');
        // 设置配置项
        this.borrowChart.setOption(option);

        // 响应式调整
        window.addEventListener('resize', () => {
          if (this.borrowChart) {
            this.borrowChart.resize();
          }
        });
      });
    },
    loadReservationStats() {
      if (!this.book || !this.book.bookNo) {
        console.warn('图书信息或bookNo不存在，无法加载预约统计');
        return;
      }

      console.log('开始加载预约统计，bookNo:', this.book.bookNo);
      request.get(`/borrow/reservation-stats/${this.book.bookNo}`).then(res => {
        console.log('预约统计响应:', res);
        if (res.code === '200' && res.data) {
          console.log('预约统计数据:', res.data);
          this.renderReservationChart(res.data);
        } else {
          console.warn('预约统计返回数据异常:', res);
        }
      }).catch(err => {
        console.error('加载预约统计失败', err);
      });
    },
    renderReservationChart(data) {
      console.log('渲染预约图表，数据:', data);
      // 准备数据
      const dates = data.map(item => item.date);
      const counts = data.map(item => item.count);
      
      console.log('预约图表 - 日期:', dates);
      console.log('预约图表 - 数量:', counts);

      // 确保DOM元素存在
      this.$nextTick(() => {
        const chartDom = document.getElementById('reservationChart');
        if (!chartDom) {
          console.error('预约图表DOM元素不存在');
          return;
        }

        // 初始化或获取ECharts实例
        if (!this.reservationChart) {
          this.reservationChart = echarts.init(chartDom);
        }

        // 配置选项 - 折线图
        const option = {
          title: {
            text: '月度预约次数',
            left: 'center',
            textStyle: {
              fontSize: 14,
              fontWeight: 'normal'
            }
          },
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'cross'
            }
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            top: '15%',
            containLabel: true
          },
          xAxis: {
            type: 'category',
            data: dates,
            boundaryGap: false,
            axisLabel: {
              rotate: 45,
              interval: 0,
              fontSize: 10
            }
          },
          yAxis: {
            type: 'value',
            name: '预约次数',
            minInterval: 1,
            nameTextStyle: {
              fontSize: 11
            },
            axisLabel: {
              fontSize: 10
            }
          },
          series: [
            {
              name: '预约次数',
              type: 'line',
              data: counts,
              smooth: true,
              symbol: 'circle',
              symbolSize: 6,
              lineStyle: {
                color: '#67C23A',
                width: 2
              },
              itemStyle: {
                color: '#67C23A'
              },
              areaStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
                  { offset: 1, color: 'rgba(103, 194, 58, 0.05)' }
                ])
              }
            }
          ]
        };

        console.log('设置预约图表配置');
        // 设置配置项
        this.reservationChart.setOption(option);

        // 响应式调整
        window.addEventListener('resize', () => {
          if (this.reservationChart) {
            this.reservationChart.resize();
          }
        });
      });
    }
  }
};
</script>

<style scoped>
.book-header {
  padding-bottom: 20px;
  border-bottom: 2px solid #409EFF;
  margin-bottom: 20px;
}

.book-header-content {
  display: flex;
  gap: 30px;
  align-items: flex-start;
}

.book-cover-large {
  flex-shrink: 0;
}

.book-cover-placeholder-large {
  width: 200px;
  height: 280px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
}

.book-cover-placeholder-large::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(45deg, transparent 30%, rgba(255, 255, 255, 0.1) 50%, transparent 70%);
}

.book-title-text-large {
  color: white;
  font-size: 18px;
  font-weight: bold;
  text-align: center;
  padding: 20px;
  word-wrap: break-word;
  max-width: 100%;
  line-height: 1.5;
  z-index: 1;
}

.book-info-right {
  flex: 1;
}

.book-main-title {
  font-size: 24px;
  color: #303133;
  margin: 0 0 20px 0;
}

.quick-info {
  font-size: 15px;
  color: #606266;
  line-height: 2;
}

.quick-info p {
  margin: 8px 0;
}

.quick-info i {
  margin-right: 8px;
  color: #909399;
}

.collection-info {
  padding: 20px;
}

.collection-info h4 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 16px;
}

.charts-container {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.chart-wrapper {
  padding: 15px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.08);
}

.chart-wrapper h4 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 14px;
}

.chart-wrapper h4 i {
  margin-right: 6px;
  color: #409EFF;
}

/* 响应式设计：小屏幕时改为上下布局 */
@media (max-width: 1200px) {
  .info-and-charts {
    flex-direction: column !important;
  }
}
</style>
