<template>
  <div style="padding: 20px; background-color: #f5f7fa; min-height: 100vh">
    <el-card>
      <!-- 面包屑导航 -->
      <el-breadcrumb separator="/" style="margin-bottom: 20px">
        <el-breadcrumb-item :to="{ path: '/userHome' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>书库浏览</el-breadcrumb-item>
      </el-breadcrumb>

      <div v-loading="loading" style="min-height: 600px">
        <div class="main-container">
          <!-- 分类导航栏 -->
          <div class="category-nav">
          <div class="nav-header">
            <i class="el-icon-menu" style="font-size: 18px; color: #409EFF"></i>
            <span style="font-size: 16px; font-weight: bold; margin-left: 8px">图书分类</span>
          </div>
          
          <!-- 全部分类按钮 -->
          <div class="category-item" 
               :class="{ active: !selectedCategory }"
               @click="selectCategory(null)">
            <i class="el-icon-s-grid"></i>
            <span>全部</span>
          </div>
          
          <!-- 一级分类列表 -->
          <div v-for="category in categories" :key="category.id" class="category-group">
            <div class="category-item" 
                 :class="{ active: selectedCategory === category.name }"
                 @click="selectCategory(category.name)">
              <i class="el-icon-folder"></i>
              <span>{{ category.name }}</span>
            </div>
            
            <!-- 二级分类列表 -->
            <div v-if="category.children && category.children.length > 0" class="sub-categories">
              <div v-for="child in category.children" :key="child.id"
                   class="category-item sub-item"
                   :class="{ active: selectedCategory === child.name }"
                   @click="selectCategory(child.name)">
                <i class="el-icon-document"></i>
                <span>{{ child.name }}</span>
              </div>
            </div>
            </div>
          </div>

          <!-- 图书展示区域 -->
          <div class="books-section">
          <div class="section-header">
            <h3>
              <i class="el-icon-collection" style="color: #409EFF"></i>
              书库浏览
              <span class="book-count">（共 {{ total }} 本）</span>
            </h3>
            
            <!-- 搜索框 -->
            <div class="search-box">
              <el-input
                v-model="searchKeyword"
                placeholder="请输入ISBN号或书名查询"
                prefix-icon="el-icon-search"
                clearable
                @keyup.enter.native="handleSearch"
                @clear="handleClearSearch"
                style="width: 350px">
              </el-input>
            </div>
          </div>
          
          <el-empty v-if="!loading && books.length === 0" description="该分类下暂无图书"></el-empty>
          
          <el-row :gutter="20" v-else>
            <el-col :span="6" v-for="book in books" :key="book.id" style="margin-bottom: 20px">
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
          
          <!-- 分页 -->
          <div class="pagination-container" v-if="total > 0">
            <el-pagination
              background
              :current-page="pageNum"
              :page-size="pageSize"
              @current-change="handlePageChange"
              layout="prev, pager, next, jumper"
              :total="total">
            </el-pagination>
          </div>
        </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import request from "@/utils/request";

export default {
  name: "BookCategoryList",
  data() {
    return {
      loading: false,
      categories: [],
      books: [],
      selectedCategory: null,
      pageNum: 1,
      pageSize: 12,
      total: 0,
      searchKeyword: ''  // 搜索关键字
    };
  },
  created() {
    this.loadCategories();
    this.loadBooks();
  },
  methods: {
    // 加载分类树
    loadCategories() {
      request.get('/category/tree').then(res => {
        if (res.code === '200') {
          this.categories = res.data || [];
        }
      }).catch(err => {
        console.error('加载分类失败', err);
      });
    },
    
    // 加载图书列表
    loadBooks() {
      this.loading = true;
      
      const params = {
        pageNum: this.pageNum,
        pageSize: this.pageSize
      };
      
      if (this.selectedCategory) {
        params.category = this.selectedCategory;
      }
      
      // 添加搜索关键字
      if (this.searchKeyword && this.searchKeyword.trim()) {
        params.keyword = this.searchKeyword.trim();
      }
      
      console.log('请求参数:', params);
      
      request.get('/book/pageByCategory', { params }).then(res => {
        this.loading = false;
        console.log('响应数据:', res);
        if (res.code === '200') {
          this.books = res.data.list || [];
          this.total = res.data.total || 0;
          console.log('图书列表:', this.books);
          console.log('总数:', this.total);
        } else {
          this.$message.error(res.msg || '加载图书失败');
        }
      }).catch(err => {
        this.loading = false;
        this.$message.error('网络错误，请稍后重试');
        console.error(err);
      });
    },
    
    // 搜索
    handleSearch() {
      this.pageNum = 1; // 重置页码
      this.loadBooks();
    },
    
    // 清除搜索
    handleClearSearch() {
      this.searchKeyword = '';
      this.pageNum = 1; // 重置页码
      this.loadBooks();
    },
    
    // 选择分类
    selectCategory(category) {
      this.selectedCategory = category;
      this.pageNum = 1; // 重置页码
      this.loadBooks();
    },
    
    // 翻页
    handlePageChange(page) {
      this.pageNum = page;
      this.loadBooks();
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
/* 主容器布局 */
.main-container {
  display: flex;
  gap: 20px;
  min-height: calc(100vh - 200px);
  align-items: flex-start;
}

/* 分类导航栏样式 - 粘性定位 */
.category-nav {
  width: 220px;
  flex-shrink: 0;
  padding: 15px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 20px;
  max-height: calc(100vh - 160px);
  overflow-y: auto;
}

.nav-header {
  display: flex;
  align-items: center;
  padding-bottom: 15px;
  margin-bottom: 15px;
  border-bottom: 2px solid #409EFF;
}

.category-group {
  margin-bottom: 10px;
}

.category-item {
  padding: 10px 15px;
  margin: 5px 0;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  color: #606266;
}

.category-item:hover {
  background-color: #ecf5ff;
  color: #409EFF;
}

.category-item.active {
  background-color: #409EFF;
  color: #fff;
}

.category-item i {
  margin-right: 8px;
  font-size: 16px;
}

.sub-categories {
  margin-left: 20px;
}

.sub-item {
  padding: 8px 15px;
  font-size: 14px;
}

/* 图书展示区域样式 */
.books-section {
  flex: 1;
  min-width: 0;
}

.section-header {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #409EFF;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-header h3 {
  margin: 0;
  font-size: 18px;
  color: #303133;
  display: flex;
  align-items: center;
}

/* 搜索框样式 */
.search-box {
  flex-shrink: 0;
}

.book-count {
  font-size: 14px;
  color: #909399;
  font-weight: normal;
}

.pagination-container {
  margin-top: 30px;
  text-align: center;
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