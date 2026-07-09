<template>
  <div style="padding: 20px; max-width: 800px; margin: 0 auto">
    <!-- 用户信息概览卡片 -->
    <el-card shadow="hover" style="margin-bottom: 20px">
      <div slot="header" class="card-header">
        <i class="el-icon-user-solid" style="font-size: 20px; color: #409EFF; margin-right: 8px"></i>
        <span style="font-size: 18px; font-weight: bold">用户信息概览</span>
      </div>
      <div class="info-overview">
        <div class="info-item">
          <span class="info-label">姓名：</span>
          <span class="info-value">{{ user.name || '未设置' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">卡号：</span>
          <span class="info-value">{{ user.username }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">账户积分：</span>
          <el-tag type="warning" size="medium">{{ user.account || 0 }} 分</el-tag>
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
                @keyup.enter="saveEdit('name')"
                ref="nameInput"
                style="width: 150px; margin-right: 8px;"
              ></el-input>
              <span v-else class="field-text">{{ user.name || '未设置' }}</span>
            </div>
            <div class="field-action">
              <i 
                v-if="editingField !== 'name'" 
                class="el-icon-edit action-icon" 
                @click="startEdit('name')"
                title="编辑"
              ></i>
              <template v-else>
                <i 
                  class="el-icon-check save-icon" 
                  @click="saveEdit('name')" 
                  title="保存"
                ></i>
                <i 
                  class="el-icon-close cancel-icon" 
                  @click="cancelEdit('name')" 
                  title="取消"
                ></i>
              </template>
            </div>
          </div>
        </div>

        <!-- 年龄字段（生日选择） -->
        <div class="form-item">
          <label class="field-label">年龄</label>
          <div class="field-row">
            <div class="field-content">
              <el-date-picker
                v-if="editingField === 'age'"
                v-model="tempValues.birthday"
                type="date"
                placeholder="选择生日"
                format="yyyy-MM-dd"
                value-format="yyyy-MM-dd"
                :picker-options="birthdayPickerOptions"
                @change="saveEdit('age')"
                ref="ageInput"
                style="width: 150px; margin-right: 8px;"
              ></el-date-picker>
              <span v-else class="field-text">{{ user.age ? user.age + ' 岁' : '未设置' }}</span>
            </div>
            <div class="field-action">
              <i 
                v-if="editingField !== 'age'" 
                class="el-icon-edit action-icon" 
                @click="startEdit('age')"
                title="编辑"
              ></i>
              <i 
                v-else 
                class="el-icon-close cancel-icon" 
                @click="cancelEdit('age')" 
                title="取消"
              ></i>
            </div>
          </div>
        </div>

        <!-- 性别字段 -->
        <div class="form-item">
          <label class="field-label">性别</label>
          <div class="field-row">
            <div class="field-content">
              <el-select 
                v-if="editingField === 'sex'" 
                v-model="tempValues.sex"
                placeholder="请选择性别"
                @change="saveEdit('sex')"
                ref="sexInput"
                style="width: 150px; margin-right: 8px;"
              >
                <el-option label="男" value="男"></el-option>
                <el-option label="女" value="女"></el-option>
              </el-select>
              <span v-else class="field-text">{{ user.sex || '未设置' }}</span>
            </div>
            <div class="field-action">
              <i 
                v-if="editingField !== 'sex'" 
                class="el-icon-edit action-icon" 
                @click="startEdit('sex')"
                title="编辑"
              ></i>
              <i 
                v-else 
                class="el-icon-close cancel-icon" 
                @click="cancelEdit('sex')" 
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
                @keyup.enter="saveEdit('phone')"
                ref="phoneInput"
                style="width: 150px; margin-right: 8px;"
              ></el-input>
              <span v-else class="field-text">{{ user.phone || '未设置' }}</span>
            </div>
            <div class="field-action">
              <i 
                v-if="editingField !== 'phone'" 
                class="el-icon-edit action-icon" 
                @click="startEdit('phone')"
                title="编辑"
              ></i>
              <template v-else>
                <i 
                  class="el-icon-check save-icon" 
                  @click="saveEdit('phone')" 
                  title="保存"
                ></i>
                <i 
                  class="el-icon-close cancel-icon" 
                  @click="cancelEdit('phone')" 
                  title="取消"
                ></i>
              </template>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 修改密码按钮和面板 -->
    <div style="margin-top: 20px;">
      <!-- 修改密码按钮 -->
      <el-button type="primary" @click="showPasswordPanel = !showPasswordPanel" icon="el-icon-key" style="width: 100%;">
        {{ showPasswordPanel ? '收起' : '修改密码' }}
      </el-button>
      
      <!-- 展开的安全设置面板 -->
      <el-collapse-transition>
        <div v-show="showPasswordPanel" style="margin-top: 15px;">
          <el-card shadow="hover">
            <div slot="header" class="card-header">
              <i class="el-icon-lock" style="font-size: 20px; color: #E6A23C; margin-right: 8px"></i>
              <span style="font-size: 18px; font-weight: bold">安全设置</span>
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
                <el-button type="primary" @click="changePassword" icon="el-icon-check">确认修改</el-button>
                <el-button @click="cancelPasswordChange" icon="el-icon-close">取消</el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </div>
      </el-collapse-transition>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request";
import Cookies from "js-cookie";

export default {
  name: "UserProfile",
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入的密码不一致'));
      } else {
        callback();
      }
    };
    
    return {
      user: {},
      editingField: null, // 当前正在编辑的字段
      tempValues: { // 临时存储编辑中的值
        name: '',
        birthday: '',
        sex: '',
        phone: ''
      },
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      showPasswordPanel: false,
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
      },
      birthdayPickerOptions: {
        disabledDate(time) {
          // 只能选择今天及以前的日期，且不超过150年前
          const today = new Date();
          const maxDate = today.getTime();
          const minDate = new Date(today.getFullYear() - 150, today.getMonth(), today.getDate()).getTime();
          return time.getTime() > maxDate || time.getTime() < minDate;
        }
      }
    };
  },
  created() {
    this.loadUserInfo();
  },
  methods: {
    loadUserInfo() {
      const userInfo = Cookies.get('user');
      if (userInfo) {
        const user = JSON.parse(userInfo);
        request.get(`/user/${user.id}`).then(res => {
          if (res.code === '200') {
            this.user = res.data;
          }
        });
      }
    },
    
    // 开始编辑字段
    startEdit(field) {
      this.editingField = field;
      // 保存当前值到临时变量
      if (field === 'age') {
        // 对于年龄字段，需要将当前年龄转换为生日日期用于显示在日期选择器中
        if (this.user.age && this.user.age > 0) {
          const currentYear = new Date().getFullYear();
          const birthYear = currentYear - this.user.age;
          this.tempValues.birthday = `${birthYear}-01-01`; // 默认使用1月1日作为生日
        } else {
          this.tempValues.birthday = '';
        }
      } else {
        this.tempValues[field] = this.user[field];
      }
      
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
      
      // 如果是年龄字段，需要将生日转换为年龄
      if (field === 'age') {
        console.log('开始处理年龄字段，tempValues.birthday:', this.tempValues.birthday);
        if (this.tempValues.birthday) {
          const birthDate = new Date(this.tempValues.birthday);
          const today = new Date();
          let age = today.getFullYear() - birthDate.getFullYear();
          const monthDiff = today.getMonth() - birthDate.getMonth();
          if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
            age--;
          }
          console.log('计算出的年龄:', age);
          // 确保年龄在合理范围内
          if (age >= 0 && age <= 150) {
            this.user.age = age;
            console.log('设置 user.age 为:', this.user.age);
          } else {
            this.$notify.error('请选择有效的生日');
            return;
          }
        } else {
          this.user.age = null;
          console.log('清空年龄');
        }
      } else {
        // 更新用户数据
        this.user[field] = this.tempValues[field];
      }
      
      // 准备要更新的数据，排除password字段
      const updateData = { ...this.user };
      delete updateData.password; // 删除password字段，避免覆盖数据库中的密码
      
      console.log('发送给后端的更新数据:', updateData);
      
      // 调用后端接口保存
      request.put('/user/update', updateData).then(res => {
        if (res.code === '200') {
          this.$notify.success('修改成功');
          // 更新Cookie中的用户信息 - 保留原有token
          const oldUserInfo = JSON.parse(Cookies.get('user') || '{}');
          const newUserInfo = { ...this.user, token: oldUserInfo.token };
          Cookies.set('user', JSON.stringify(newUserInfo));
          // 退出编辑模式
          this.editingField = null;
        } else {
          this.$notify.error(res.msg || '修改失败');
          // 恢复原值
          this.loadUserInfo(); // 重新加载用户信息
          if (field === 'age') {
            this.tempValues.birthday = this.user.age ? `${new Date().getFullYear() - this.user.age}-01-01` : '';
          } else {
            this.tempValues[field] = this.user[field];
          }
        }
      }).catch((error) => {
        console.error('保存失败:', error);
        this.$notify.error('网络错误，请稍后重试');
        this.loadUserInfo(); // 重新加载用户信息
        if (field === 'age') {
          this.tempValues.birthday = this.user.age ? `${new Date().getFullYear() - this.user.age}-01-01` : '';
        } else {
          this.tempValues[field] = this.user[field];
        }
      });
    },
    
    // 取消编辑
    cancelEdit(field) {
      this.editingField = null;
      // 恢复原值
      if (field === 'age') {
        this.tempValues.birthday = this.user.age ? `${new Date().getFullYear() - this.user.age}-01-01` : '';
      } else {
        this.tempValues[field] = this.user[field];
      }
    },
    
    // 失去焦点处理 - 已移除，不再使用
    
    // 修改密码
    changePassword() {
      this.$refs['passwordForm'].validate((valid) => {
        if (valid) {
          request.put('/user/password', {
            username: this.user.username,
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
              // 重置表单验证
              if (this.$refs['passwordForm']) {
                this.$refs['passwordForm'].resetFields();
              }
              // 收起面板
              this.showPasswordPanel = false;
              // 退出登录
              setTimeout(() => {
                Cookies.remove('user');
                this.$router.push('/login');
              }, 1500);
            } else {
              this.$notify.error(res.msg || '密码修改失败');
            }
          });
        }
      });
    },
    
    // 取消修改密码
    cancelPasswordChange() {
      // 清空表单
      this.passwordForm = {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      };
      // 重置表单验证
      if (this.$refs['passwordForm']) {
        this.$refs['passwordForm'].resetFields();
      }
      // 收起面板
      this.showPasswordPanel = false;
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
  gap: 8px;
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
  min-width: 80px;
  max-width: 200px;
  display: inline-block;
}

.field-action {
  display: flex;
  align-items: center;
  height: 32px;
  flex-shrink: 0;
  gap: 8px;
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
