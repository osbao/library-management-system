<template>
  <div style="padding: 20px">
    <el-card>
      <div slot="header" class="clearfix">
        <span style="font-size: 18px; font-weight: bold">
          <i class="el-icon-star-on" style="color: #ff6b6b"></i> 
          为您推荐
        </span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="loadRecommendations">
          <i class="el-icon-refresh"></i> 刷新推荐
        </el-button>
      </div>
      
      <div v-loading="loading">
        <el-empty v-if="!loading && recommendations.length === 0" description="暂无推荐图书，快去借阅一些书吧~"></el-empty>
        
        <el-row :gutter="20" v-else>
          <el-col :span="4" v-for="book in recommendations" :key="book.id" style="margin-bottom: 20px">
            <el-card :body-style="{ padding: '0px' }" shadow="hover" class="book-card" @click.native="viewDetail(book.id)">
              <div class="book-cover">
                <div class="book-cover-placeholder">
                  <div class="book-title-text">{{ book.name }}</div>
                </div>
              </div>
              <div style="padding: 10px">
                <div class="book-title">{{ book.name }}</div>
                <div class="book-info">
                  <p><i class="el-icon-user"></i> {{ book.author }}</p>
                  <p><i class="el-icon-date"></i> {{ book.publishDate }}</p>
                </div>
                <div class="book-footer">
                  <el-tag :type="book.nums > 0 ? 'success' : 'danger'" size="mini">
                    {{ book.nums > 0 ? `库存: ${book.nums}` : '已借完' }}
                  </el-tag>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-card>
    
    <!-- 推荐理由说明 -->
    <el-card style="margin-top: 20px">
      <div slot="header">
        <span><i class="el-icon-info"></i> 推荐说明</span>
      </div>
      <div style="color: #666; line-height: 1.8">
        <p>📚 <strong>个性化推荐算法：</strong>基于您的借阅历史，为您精准推荐感兴趣的图书</p>
        <p>✨ <strong>推荐依据：</strong></p>
        <ul style="margin-left: 20px">
          <li>分析您历史借阅的图书分类（借书+还书记录）</li>
          <li>根据最近借阅时间计算兴趣权重</li>
          <li>推荐与您喜好分类匹配的图书</li>
          <li>排除您已经借阅过的图书</li>
          <li>每次刷新都会随机打乱推荐顺序</li>
        </ul>
        <p>💡 <strong>提示：</strong>如果您是新用户，我们会为您推荐当前热门图书</p>
        <p>🔄 <strong>注意：</strong>每次访问推荐页面，看到的图书顺序都会不同哦！</p>
      </div>
    </el-card>
  </div>
</template>

<script>
import request from "@/utils/request";
import Cookies from "js-cookie";

export default {
  name: "BookRecommend",
  data() {
    return {
      loading: false,
      recommendations: []
    };
  },
  created() {
    this.loadRecommendations();
  },
  methods: {
    loadRecommendations() {
      const userInfo = Cookies.get('user');
      if (!userInfo) {
        this.$notify.error('请先登录');
        this.$router.push('/login');
        return;
      }
      
      const user = JSON.parse(userInfo);
      this.loading = true;
      
      // 添加时间戳防止浏览器缓存
      const timestamp = new Date().getTime();
      request.get(`/report/recommend/user/${user.id}?t=${timestamp}`).then(res => {
        this.loading = false;
        if (res.code === '200') {
          this.recommendations = res.data || [];
          // 将score转换为适合el-rate的格式（假设原始score是0-10，需要转为0-5）
          this.recommendations.forEach(book => {
            if (book.score && book.score > 5) {
              book.score = book.score / 2;
            } else if (!book.score) {
              book.score = 0;
            }
          });
          
          if (this.recommendations.length === 0) {
            this.$message.info('暂无推荐图书');
          } else {
            this.$message.success('刷新成功！为您更新了推荐图书');
          }
        } else {
          this.$notify.error(res.msg || '加载推荐失败');
        }
      }).catch(err => {
        this.loading = false;
        this.$notify.error('网络错误，请稍后重试');
        console.error(err);
      });
    },
    borrowBook(book) {
      // 跳转到借书页面，并传递图书信息
      this.$router.push({
        path: '/addBorrow',
        query: {
          bookNo: book.bookNo,
          bookName: book.name
        }
      });
    },
    viewDetail(bookId) {
      // 跳转到图书详情页
      this.$router.push({
        path: '/userHome/bookDetail',
        query: { id: bookId }
      });
    }
  }
};
</script>

<style scoped>
.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}
.clearfix:after {
  clear: both;
}

.book-card {
  transition: all 0.3s;
  height: 100%;
  cursor: pointer;
}

.book-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.book-cover {
  width: 100%;
  height: 280px;
  overflow: hidden;
  background-color: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-cover {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 书籍封面占位符样式 */
.book-cover-placeholder {
  width: 140px;
  height: 200px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  border-radius: 4px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
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
  font-size: 14px;
  font-weight: bold;
  text-align: center;
  padding: 10px;
  word-wrap: break-word;
  max-width: 100%;
  line-height: 1.4;
  z-index: 1;
}

.book-title {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #303133;
}

.book-info {
  font-size: 12px;
  color: #606266;
  margin-bottom: 8px;
}

.book-info p {
  margin: 5px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.book-info i {
  margin-right: 5px;
  color: #909399;
}

.book-footer {
  border-top: 1px solid #ebeef5;
  padding-top: 8px;
  text-align: center;
}
</style>
