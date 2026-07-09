<template>
  <div style="padding: 20px; max-width: 800px; margin: 0 auto">
    <!-- 管理员信息概览卡片 -->
    <el-card shadow="hover" style="margin-bottom: 20px">
      <div slot="header" class="card-header">
        <i class="el-icon-user-solid" style="font-size: 20px; color: #409EFF; margin-right: 8px"></i>
        <span style="font-size: 18px; font-weight: bold">管理员信息概览</span>
      </div>
      <div class="info-overview">
        <div class="info-item">
          <span class="info-label">姓名：</span>
          <span class="info-value">{{ admin.name || '未设置' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">用户名：</span>
          <span class="info-value">{{ admin.username }}</span>
        </div>
      </div>
    </el-card>

    <!-- 编辑基本信息卡片 -->
    <el-card shadow="hover" style="margin-bottom: 20px">
      <div slot="header" class="card-header">
        <i class="el-icon-edit-outline" style="font-size: 20px; color: #67C23A; margin-right: 8px"></i>
        <span style="font-size: 18px; font-weight: bold">编辑基本信息</span>
      </div>
      <div class="edit-form">
        <!-- 姓名字段 -->
        <div class="form-item">
          <label class="field-label">姓名</label>
          <div class="field-row">
            <div class="field-content">
              <el-input 
                v-if="editingField === 'name'" 
                v-model="tempValues.name" 
                placeholder="请输入姓名"
                @blur="handleBlur('name')"
                @keyup.enter="saveEdit('name')"
                ref="nameInput"
              ></el-input>
              <span v-else class="field-text">{{ admin.name || '未设置' }}</span>
            </div>
            <div class="field-action">
              <i 
                v-if="editingField !== 'name'" 
                class="el-icon-edit action-icon" 
                @click="startEdit('name')"
                title="编辑"
              ></i>
              <i 
                v-else 
                class="el-icon-close cancel-icon" 
                @click="cancelEdit('name')" 
                title="取消"
              ></i>
            </div>
          </div>
        </div>

        <!-- 手机号字段 -->
        <div class="form-item">
          <label class="field-label">手机号</label>
          <div class="field-row">
            <div class="field-content">
              <el-input 
                v-if="editingField === 'phone'" 
                v-model="tempValues.phone" 
                placeholder="请输入手机号"
                @blur="handleBlur('phone')"
                @keyup.enter="saveEdit('phone')"
                ref="phoneInput"
              ></el-input>
              <span v-else class="field-text">{{ admin.phone || '未设置' }}</span>
            </div>
            <div class="field-action">
              <i 
                v-if="editingField !== 'phone'" 
                class="el-icon-edit action-icon" 
                @click="startEdit('phone')"
                title="编辑"
              ></i>
              <i 
                v-else 
                class="el-icon-close cancel-icon" 
                @click="cancelEdit('phone')" 
                title="取消"
              ></i>
            </div>
          </div>
        </div>

        <!-- 邮箱字段 -->
        <div class="form-item">
          <label class="field-label">邮箱</label>
          <div class="field-row">
            <div class="field-content">
              <el-input 
                v-if="editingField === 'email'" 
                v-model="tempValues.email" 
                placeholder="请输入邮箱"
                @blur="handleBlur('email')"
                @keyup.enter="saveEdit('email')"
                ref="emailInput"
              ></el-input>
              <span v-else class="field-text">{{ admin.email || '未设置' }}</span>
            </div>
            <div class="field-action">
              <i 
                v-if="editingField !== 'email'" 
                class="el-icon-edit action-icon" 
                @click="startEdit('email')"
                title="编辑"
              ></i>
              <i 
                v-else 
                class="el-icon-close cancel-icon" 
                @click="cancelEdit('email')" 
                title="取消"
              ></i>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 修改密码卡片 -->
    <el-card shadow="hover">
      <div slot="header" class="card-header">
        <i class="el-icon-lock" style="font-size: 20px; color: #E6A23C; margin-right: 8px"></i>
        <span style="font-size: 18px; font-weight: bold">修改密码</span>
      </div>
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordForm" label-width="100px">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input 
            v-model="passwordForm.oldPassword" 
            type="password" 
            placeholder="请输入旧密码" 
            show-password
          ></el-input>
        </el-form-item>
        
        <el-form-item label="新密码" prop="newPassword">
          <el-input 
            v-model="passwordForm.newPassword" 
            type="password" 
            placeholder="请输入新密码" 
            show-password
          ></el-input>
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input 
            v-model="passwordForm.confirmPassword" 
            type="password" 
            placeholder="请再次输入新密码" 
            show-password
          ></el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="changePassword" icon="el-icon-key">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import request from "@/utils/request";
import Cookies from "js-cookie";

export default {
  name: "AdminProfile",
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入的密码不一致'));
      } else {
        callback();
      }
    };
    
    return {
      admin: {},
      editingField: null, // 当前正在编辑的字段
      tempValues: { // 临时存储编辑中的值
        name: '',
        phone: '',
        email: ''
      },
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      passwordRules: {
        oldPassword: [
          { required: true, message: '请输入旧密码', trigger: 'blur' },
          { min: 3, max: 10, message: '长度在3-10个字符', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 3, max: 10, message: '长度在3-10个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入新密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
      }
    };
  },
  created() {
    this.loadAdminInfo();
  },
  methods: {
    loadAdminInfo() {
      const adminInfo = Cookies.get('admin');
      if (adminInfo) {
        const admin = JSON.parse(adminInfo);
        request.get(`/admin/${admin.id}`).then(res => {
          if (res.code === '200') {
            this.admin = res.data;
          }
        });
      }
    },
    
    // 开始编辑字段
    startEdit(field) {
      this.editingField = field;
      // 保存当前值到临时变量
      this.tempValues[field] = this.admin[field];
      
      // 自动聚焦到输入框
      this.$nextTick(() => {
        const inputRef = this.$refs[field + 'Input'];
        if (inputRef) {
          if (inputRef.focus) {
            inputRef.focus();
          } else if (inputRef.$el && inputRef.$el.querySelector) {
            const input = inputRef.$el.querySelector('input');
            if (input) input.focus();
          }
        }
      });
    },
    
    // 保存编辑
    saveEdit(field) {
      // 验证数据
      if (field === 'phone' && this.tempValues.phone) {
        const phoneReg = /^1[3-9]\d{9}$/;
        if (!phoneReg.test(this.tempValues.phone)) {
          this.$notify.error('请输入正确的手机号');
          return;
        }
      }
      
      if (field === 'email' && this.tempValues.email) {
        const emailReg = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
        if (!emailReg.test(this.tempValues.email)) {
          this.$notify.error('请输入正确的邮箱地址');
          return;
        }
      }
      
      // 更新管理员数据
      this.admin[field] = this.tempValues[field];
      
      // 调用后端接口保存
      request.put('/admin/update', this.admin).then(res => {
        if (res.code === '200') {
          this.$notify.success('修改成功');
          // 更新Cookie中的管理员信息
          Cookies.set('admin', JSON.stringify(this.admin));
          // 退出编辑模式
          this.editingField = null;
        } else {
          this.$notify.error(res.msg || '修改失败');
          // 恢复原值
          this.tempValues[field] = this.admin[field];
        }
      }).catch(() => {
        this.$notify.error('网络错误，请稍后重试');
        this.tempValues[field] = this.admin[field];
      });
    },
    
    // 取消编辑
    cancelEdit(field) {
      this.editingField = null;
      // 恢复原值
      this.tempValues[field] = this.admin[field];
    },
    
    // 失去焦点处理
    handleBlur(field) {
      // 延迟执行，让按钮点击事件先触发
      setTimeout(() => {
        if (this.editingField === field) {
          this.cancelEdit(field);
        }
      }, 200);
    },
    
    // 修改密码
    changePassword() {
      this.$refs['passwordForm'].validate((valid) => {
        if (valid) {
          request.put('/admin/password', {
            username: this.admin.username,
            password: this.passwordForm.oldPassword,
            newPass: this.passwordForm.newPassword
          }).then(res => {
            if (res.code === '200') {
              this.$notify.success('密码修改成功，请重新登录');
              // 清空表单
              this.passwordForm = {
                oldPassword: '',
                newPassword: '',
                confirmPassword: ''
              };
              // 退出登录
              setTimeout(() => {
                Cookies.remove('admin');
                this.$router.push('/login');
              }, 1500);
            } else {
              this.$notify.error(res.msg || '密码修改失败');
            }
          });
        }
      });
    }
  }
};
</script>

<style scoped>
/* 卡片头部样式 */
.card-header {
  display: flex;
  align-items: center;
}

/* 用户信息概览 */
.info-overview {
  display: flex;
  justify-content: space-around;
  padding: 20px 0;
}

.info-item {
  text-align: center;
}

.info-label {
  font-size: 14px;
  color: #909399;
  margin-right: 8px;
}

.info-value {
  font-size: 16px;
  color: #303133;
  font-weight: 500;
}

/* 编辑表单样式 */
.edit-form {
  padding: 10px 0;
}

.form-item {
  margin-bottom: 25px;
}

.field-label {
  display: block;
  font-size: 14px;
  color: #606266;
  font-weight: 500;
  margin-bottom: 8px;
}

.field-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.field-content {
  flex: 1;
  min-height: 40px;
  display: flex;
  align-items: center;
}

.field-text {
  font-size: 15px;
  color: #303133;
  padding: 8px 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
  min-width: 150px;
  display: inline-block;
}

.field-action {
  display: flex;
  align-items: center;
  height: 32px;
  flex-shrink: 0;
}

.action-icon {
  font-size: 18px;
  color: #409EFF;
  cursor: pointer;
  transition: all 0.3s;
}

.action-icon:hover {
  color: #66b1ff;
  transform: scale(1.1);
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.save-icon {
  font-size: 20px;
  color: #67C23A;
  cursor: pointer;
  transition: all 0.3s;
}

.save-icon:hover {
  color: #85ce61;
  transform: scale(1.2);
}

.cancel-icon {
  font-size: 20px;
  color: #F56C6C;
  cursor: pointer;
  transition: all 0.3s;
}

.cancel-icon:hover {
  color: #f78989;
  transform: scale(1.2);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .info-overview {
    flex-direction: column;
    gap: 15px;
  }
}
</style>
