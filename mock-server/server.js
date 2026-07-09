const express = require('express');
const cors = require('cors');
const multer = require('multer');
const path = require('path');
const fs = require('fs');

const app = express();
const upload = multer({ dest: 'uploads/' });

app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// 确保上传目录存在
if (!fs.existsSync('uploads')) fs.mkdirSync('uploads');

// ========== 模拟数据库 ==========
let nextId = 100;

// 用户数据
let users = [
  { id: 1, username: 'user1', name: '张三', phone: '13800138001', email: 'zhangsan@qq.com', password: '123456', sex: '男', age: 25, account: 100, status: 1, createtime: '2024-01-15', address: '北京市朝阳区' },
  { id: 2, username: 'user2', name: '李四', phone: '13800138002', email: 'lisi@qq.com', password: '123456', sex: '女', age: 22, account: 50, status: 1, createtime: '2024-02-20', address: '上海市浦东新区' },
  { id: 3, username: 'user3', name: '王五', phone: '13800138003', email: 'wangwu@qq.com', password: '123456', sex: '男', age: 30, account: 80, status: 1, createtime: '2024-03-10', address: '广州市天河区' },
];

// 管理员数据
let admins = [
  { id: 1, username: 'admin', name: '系统管理员', phone: '13900139001', email: 'admin@bms.com', password: '123456', sex: '男', age: 28, status: 1, createtime: '2024-01-01' },
];

// 分类数据
let categories = [
  { id: 1, name: '文学', description: '文学作品', pid: null },
  { id: 2, name: '计算机', description: '计算机与编程', pid: null },
  { id: 3, name: '历史', description: '历史著作', pid: null },
  { id: 4, name: '哲学', description: '哲学思想', pid: null },
  { id: 5, name: '小说', description: '小说类', pid: 1 },
  { id: 6, name: '诗歌', description: '诗歌类', pid: 1 },
  { id: 7, name: '编程语言', description: '编程语言相关', pid: 2 },
  { id: 8, name: '数据库', description: '数据库技术', pid: 2 },
  { id: 9, name: '中国古代史', description: '中国古代历史', pid: 3 },
  { id: 10, name: '世界史', description: '世界历史', pid: 3 },
];

// 图书数据
let books = [
  { id: 1, name: '三国演义', description: '中国古代四大名著之一，描绘了三国时期的历史故事。', publishDate: '2010-01', author: '罗贯中', publisher: '人民文学出版社', category: '小说', bookNo: 'BK001', cover: 'https://img2.baidu.com/it/u=2828815297,1164361859&fm=253&fmt=auto&app=138&f=JPEG?w=333&h=500', score: 9, nums: 5, categories: ['文学', '小说'] },
  { id: 2, name: '红楼梦', description: '中国古典四大名著之首，以贾宝玉、林黛玉的爱情悲剧为主线。', publishDate: '2011-05', author: '曹雪芹', publisher: '中华书局', category: '小说', bookNo: 'BK002', cover: 'https://img1.baidu.com/it/u=4173119443,1858128123&fm=253&fmt=auto&app=138&f=JPEG?w=332&h=500', score: 10, nums: 3, categories: ['文学', '小说'] },
  { id: 3, name: 'Java编程思想', description: 'Java语言经典教材，深入讲解面向对象编程。', publishDate: '2015-03', author: 'Bruce Eckel', publisher: '机械工业出版社', category: '编程语言', bookNo: 'BK003', cover: 'https://img0.baidu.com/it/u=3658456415,2304831539&fm=253&fmt=auto&app=138&f=JPEG?w=350&h=500', score: 8, nums: 10, categories: ['计算机', '编程语言'] },
  { id: 4, name: 'MySQL必知必会', description: 'MySQL数据库入门经典，快速掌握SQL。', publishDate: '2016-08', author: 'Ben Forta', publisher: '人民邮电出版社', category: '数据库', bookNo: 'BK004', cover: 'https://img2.baidu.com/it/u=1817752164,1234567890&fm=253&fmt=auto&app=138&f=JPEG?w=350&h=500', score: 7, nums: 6, categories: ['计算机', '数据库'] },
  { id: 5, name: '史记', description: '中国第一部纪传体通史，记载了上至上古传说的黄帝时代，下至汉武帝太初四年间共3000多年的历史。', publishDate: '2009-12', author: '司马迁', publisher: '中华书局', category: '中国古代史', bookNo: 'BK005', cover: 'https://img1.baidu.com/it/u=9876543210,1234567890&fm=253&fmt=auto&app=138&f=JPEG?w=350&h=500', score: 9, nums: 4, categories: ['历史', '中国古代史'] },
  { id: 6, name: '活着', description: '余华代表作，讲述了一个人一生的故事。', publishDate: '2012-08', author: '余华', publisher: '作家出版社', category: '小说', bookNo: 'BK006', cover: 'https://img0.baidu.com/it/u=2468135790,1357924680&fm=253&fmt=auto&app=138&f=JPEG?w=333&h=500', score: 9, nums: 8, categories: ['文学', '小说'] },
  { id: 7, name: '世界通史', description: '全面介绍世界历史发展的经典教材。', publishDate: '2014-06', author: '斯塔夫里阿诺斯', publisher: '北京大学出版社', category: '世界史', bookNo: 'BK007', cover: 'https://img2.baidu.com/it/u=1122334455,6677889900&fm=253&fmt=auto&app=138&f=JPEG?w=350&h=500', score: 8, nums: 5, categories: ['历史', '世界史'] },
  { id: 8, name: '论语', description: '儒家经典，记录孔子及其弟子言行的著作。', publishDate: '2008-03', author: '孔子弟子及再传弟子', publisher: '中华书局', category: '哲学', bookNo: 'BK008', cover: 'https://img1.baidu.com/it/u=1357924680,2468135790&fm=253&fmt=auto&app=138&f=JPEG?w=350&h=500', score: 10, nums: 5, categories: ['哲学'] },
  { id: 9, name: 'JavaScript高级程序设计', description: '前端开发必读书籍，深入讲解JavaScript语言。', publishDate: '2020-01', author: 'Matt Frisbie', publisher: '人民邮电出版社', category: '编程语言', bookNo: 'BK009', cover: 'https://img0.baidu.com/it/u=1234567890,9876543210&fm=253&fmt=auto&app=138&f=JPEG?w=350&h=500', score: 9, nums: 7, categories: ['计算机', '编程语言'] },
  { id: 10, name: '资治通鉴', description: '中国第一部编年体通史，涵盖16朝1362年的历史。', publishDate: '2013-10', author: '司马光', publisher: '中华书局', category: '中国古代史', bookNo: 'BK010', cover: 'https://img2.baidu.com/it/u=2468135790,1357924680&fm=253&fmt=auto&app=138&f=JPEG?w=350&h=500', score: 9, nums: 3, categories: ['历史', '中国古代史'] },
];

// 借阅数据
let borrows = [
  { id: 1, bookName: '三国演义', bookNo: 'BK001', barcode: 'BC001001', bookItemId: 1, userNo: 'U001', userName: '张三', userPhone: '13800138001', createtime: '2024-06-15', updatetime: '2024-06-15', score: 5, status: '已归还', days: 30, nums: 1, returnDate: '2024-07-15', note: '正常' },
  { id: 2, bookName: 'Java编程思想', bookNo: 'BK003', barcode: 'BC003001', bookItemId: 2, userNo: 'U002', userName: '李四', userPhone: '13800138002', createtime: '2024-06-20', updatetime: '2024-06-20', score: 4, status: '已借出', days: 30, nums: 1, returnDate: '2024-07-20', note: '正常' },
  { id: 3, bookName: '活着', bookNo: 'BK006', barcode: 'BC006001', bookItemId: 3, userNo: 'U003', userName: '王五', userPhone: '13800138003', createtime: '2024-06-25', updatetime: '2024-06-25', score: null, status: '已借出', days: 30, nums: 1, returnDate: '2024-07-25', note: '即将到期' },
  { id: 4, bookName: '红楼梦', bookNo: 'BK002', barcode: 'BC002001', bookItemId: 4, userNo: 'U001', userName: '张三', userPhone: '13800138001', createtime: '2024-05-10', updatetime: '2024-06-10', score: 4, status: '已归还', days: 30, nums: 1, returnDate: '2024-06-09', note: '正常' },
  { id: 5, bookName: 'MySQL必知必会', bookNo: 'BK004', barcode: 'BC004001', bookItemId: 5, userNo: 'U003', userName: '王五', userPhone: '13800138003', createtime: '2024-06-01', updatetime: '2024-06-01', score: null, status: '待取书', days: 30, nums: 1, returnDate: '2024-07-01', note: '等待取书' },
];

// 归还数据
let returns = [
  { id: 1, bookName: '三国演义', bookNo: 'BK001', barcode: 'BC001001', userNo: 'U001', userName: '张三', userPhone: '13800138001', createtime: '2024-06-15', updatetime: '2024-07-10', score: 5, status: '已归还', days: 30, nums: 1, returnDate: '2024-07-10', note: '正常归还' },
  { id: 2, bookName: '红楼梦', bookNo: 'BK002', barcode: 'BC002001', userNo: 'U001', userName: '张三', userPhone: '13800138001', createtime: '2024-05-10', updatetime: '2024-06-10', score: 4, status: '已归还', days: 30, nums: 1, returnDate: '2024-06-10', note: '正常归还' },
];

// 预约数据
let reservations = [
  { id: 1, bookId: 3, bookName: 'Java编程思想', bookNo: 'BK003', userId: 1, userName: '张三', userPhone: '13800138001', status: '等待取书', reservedTime: '2024-06-25 10:00:00', expireTime: '2024-06-28 10:00:00', barcode: 'BC003003' },
  { id: 2, bookId: 5, bookName: '史记', bookNo: 'BK005', userId: 2, userName: '李四', userPhone: '13800138002', status: '已预约', reservedTime: '2024-06-25 14:00:00', expireTime: '2024-06-28 14:00:00', barcode: null },
  { id: 3, bookId: 8, bookName: '论语', bookNo: 'BK008', userId: 1, userName: '张三', userPhone: '13800138001', status: '已取消', reservedTime: '2024-06-20 09:00:00', expireTime: '2024-06-23 09:00:00', barcode: null },
];

// 公告数据
let informs = [
  { id: 1, title: '关于图书馆开放时间调整的通知', content: '自2024年7月1日起，图书馆开放时间调整为：周一至周五 8:00-22:00，周六周日 9:00-21:00。', createtime: '2024-06-20 10:00:00' },
  { id: 2, title: '新书上架通知', content: '本月新增图书50册，包括最新出版的编程技术类书籍和文学类新作，欢迎借阅。', createtime: '2024-06-15 09:00:00' },
  { id: 3, title: '暑假借阅规则', content: '暑假期间（7月1日-8月31日），每本书借阅天数延长至60天，每人最多可借10本书。', createtime: '2024-06-25 08:00:00' },
];

// Token 管理
let tokens = {};

function generateToken(userInfo, role) {
  const token = 'token_' + Date.now() + '_' + Math.random().toString(36).substr(2);
  tokens[token] = { ...userInfo, role, createTime: Date.now() };
  return token;
}

// ========== 工具函数 ==========
function pageData(list, params) {
  const pageNum = parseInt(params.pageNum) || 1;
  const pageSize = parseInt(params.pageSize) || 10;
  let result = [...list];

  // 搜索
  if (params.name) {
    result = result.filter(item => item.name && item.name.includes(params.name));
  }
  if (params.username) {
    result = result.filter(item => item.username && item.username.includes(params.username));
  }
  if (params.bookName) {
    result = result.filter(item => item.bookName && item.bookName.includes(params.bookName));
  }
  if (params.bookNo) {
    result = result.filter(item => item.bookNo && item.bookNo.includes(params.bookNo));
  }
  if (params.userName) {
    result = result.filter(item => item.userName && item.userName.includes(params.userName));
  }
  if (params.userNo) {
    result = result.filter(item => item.userNo && item.userNo.includes(params.userNo));
  }
  if (params.status) {
    result = result.filter(item => item.status === params.status);
  }
  if (params.category) {
    result = result.filter(item => {
      if (!item.categories) return true;
      return item.categories.some(c => c.includes(params.category));
    });
  }
  if (params.author) {
    result = result.filter(item => item.author && item.author.includes(params.author));
  }

  const total = result.length;
  const start = (pageNum - 1) * pageSize;
  const end = start + pageSize;
  const records = result.slice(start, end);

  return {
    code: '200',
    data: {
      list: records,
      records: records,
      total: total,
      size: pageSize,
      current: pageNum,
      pages: Math.ceil(total / pageSize),
    }
  };
}

function ok(data) {
  return { code: '200', msg: '操作成功', data };
}

function fail(msg) {
  return { code: '400', msg: msg || '操作失败' };
}

// ========== 认证中间件 ==========
function authMiddleware(req, res, next) {
  const token = req.headers.token;
  console.log(`[AUTH] ${req.method} ${req.url} token=${token}`);
  if (token && tokens[token]) {
    req.currentUser = tokens[token];
    next();
  } else {
    // 允许未认证访问某些接口
    next();
  }
}
app.use(authMiddleware);

// ========== Admin 接口 ==========
app.post('/api/admin/login', (req, res) => {
  const { username, password } = req.body;
  const admin = admins.find(u => u.username === username && u.password === password);
  if (admin) {
    const token = generateToken({ id: admin.id, username: admin.username, name: admin.name }, 'admin');
    res.json(ok({ token, user: { id: admin.id, username: admin.username, name: admin.name, phone: admin.phone, email: admin.email, sex: admin.sex, age: admin.age }}));
  } else {
    res.json(fail('用户名或密码错误'));
  }
});

app.post('/api/admin/sign', (req, res) => {
  const { username, password, name, phone, email } = req.body;
  if (admins.find(u => u.username === username)) {
    return res.json(fail('用户名已存在'));
  }
  const newAdmin = { id: ++nextId, username, password, name: name || username, phone: phone || '', email: email || '', sex: '男', age: 25, status: 1, createtime: new Date().toISOString().split('T')[0] };
  admins.push(newAdmin);
  res.json(ok(null));
});

app.put('/api/admin/password', (req, res) => {
  const { username, password, newPassword } = req.body;
  const admin = admins.find(u => u.username === username && u.password === password);
  if (!admin) return res.json(fail('原密码错误'));
  admin.password = newPassword;
  res.json(ok(null));
});

app.post('/api/admin/save', (req, res) => {
  const obj = req.body;
  obj.id = ++nextId;
  obj.createtime = new Date().toISOString().split('T')[0];
  obj.status = 1;
  admins.push(obj);
  res.json(ok(null));
});

app.delete('/api/admin/delete/:id', (req, res) => {
  const id = parseInt(req.params.id);
  admins = admins.filter(a => a.id !== id);
  res.json(ok(null));
});

app.put('/api/admin/update', (req, res) => {
  const obj = req.body;
  const idx = admins.findIndex(a => a.id === obj.id);
  if (idx >= 0) {
    admins[idx] = { ...admins[idx], ...obj };
    res.json(ok(null));
  } else {
    res.json(fail('未找到记录'));
  }
});

app.get('/api/admin/list', (req, res) => res.json(ok(admins)));

app.get('/api/admin/page', (req, res) => {
  let list = [...admins];
  if (req.query.username) list = list.filter(a => a.username.includes(req.query.username));
  if (req.query.name) list = list.filter(a => a.name.includes(req.query.name));
  res.json(pageData(list, req.query));
});

app.get('/api/admin/:id', (req, res) => {
  const admin = admins.find(a => a.id === parseInt(req.params.id));
  res.json(ok(admin || null));
});

// ========== User 接口 ==========
app.post('/api/user/login', (req, res) => {
  const { username, password } = req.body;
  const user = users.find(u => u.username === username && u.password === password);
  if (user) {
    const token = generateToken({ id: user.id, username: user.username, name: user.name, userNo: user.username }, 'user');
    res.json(ok({ token, user: { id: user.id, username: user.username, name: user.name, phone: user.phone, email: user.email, sex: user.sex, age: user.age, account: user.account } }));
  } else {
    res.json(fail('用户名或密码错误'));
  }
});

app.post('/api/user/sign', (req, res) => {
  const { username, password, name, phone, email } = req.body;
  if (users.find(u => u.username === username)) return res.json(fail('用户名已存在'));
  const newUser = { id: ++nextId, username, password, name: name || username, phone: phone || '', email: email || '', sex: '男', age: 20, account: 0, status: 1, address: '', createtime: new Date().toISOString().split('T')[0] };
  users.push(newUser);
  res.json(ok(null));
});

app.post('/api/user/account', (req, res) => {
  const { id, account } = req.body;
  const user = users.find(u => u.id === id);
  if (user) {
    user.account = (user.account || 0) + (account || 0);
    res.json(ok(user));
  } else {
    res.json(fail('用户不存在'));
  }
});

app.put('/api/user/password', (req, res) => {
  const { username, password, newPassword } = req.body;
  const user = users.find(u => u.username === username && u.password === password);
  if (!user) return res.json(fail('原密码错误'));
  user.password = newPassword;
  res.json(ok(null));
});

app.post('/api/user/save', (req, res) => {
  const obj = req.body;
  obj.id = ++nextId;
  obj.createtime = new Date().toISOString().split('T')[0];
  obj.account = 0;
  obj.status = 1;
  users.push(obj);
  res.json(ok(null));
});

app.delete('/api/user/delete/:id', (req, res) => {
  const id = parseInt(req.params.id);
  users = users.filter(u => u.id !== id);
  res.json(ok(null));
});

app.put('/api/user/update', (req, res) => {
  const obj = req.body;
  const idx = users.findIndex(u => u.id === obj.id);
  if (idx >= 0) {
    users[idx] = { ...users[idx], ...obj };
    res.json(ok(null));
  } else {
    res.json(fail('未找到记录'));
  }
});

app.get('/api/user/list', (req, res) => res.json(ok(users)));

app.get('/api/user/page', (req, res) => {
  let list = [...users];
  if (req.query.username) list = list.filter(u => u.username.includes(req.query.username));
  if (req.query.name) list = list.filter(u => u.name.includes(req.query.name));
  res.json(pageData(list, req.query));
});

app.get('/api/user/:id', (req, res) => {
  const user = users.find(u => u.id === parseInt(req.params.id));
  res.json(ok(user || null));
});

// ========== Category 接口 ==========
app.get('/api/category/list', (req, res) => res.json(ok(categories)));

app.get('/api/category/tree', (req, res) => {
  const buildTree = (pid) => {
    return categories
      .filter(c => c.pid === pid)
      .map(c => ({ ...c, children: buildTree(c.id) }));
  };
  res.json(ok(buildTree(null)));
});

app.get('/api/category/page', (req, res) => {
  let list = [...categories];
  if (req.query.name) list = list.filter(c => c.name.includes(req.query.name));
  res.json(pageData(list, req.query));
});

app.post('/api/category/save', (req, res) => {
  const obj = req.body;
  obj.id = ++nextId;
  categories.push(obj);
  res.json(ok(null));
});

app.delete('/api/category/delete/:id', (req, res) => {
  const id = parseInt(req.params.id);
  categories = categories.filter(c => c.id !== id);
  res.json(ok(null));
});

app.put('/api/category/update', (req, res) => {
  const obj = req.body;
  const idx = categories.findIndex(c => c.id === obj.id);
  if (idx >= 0) {
    categories[idx] = { ...categories[idx], ...obj };
    res.json(ok(null));
  } else {
    res.json(fail('未找到记录'));
  }
});

app.get('/api/category/:id', (req, res) => {
  const cat = categories.find(c => c.id === parseInt(req.params.id));
  res.json(ok(cat || null));
});

// ========== Book 接口 ==========
app.get('/api/book/list', (req, res) => res.json(ok(books)));

app.get('/api/book/page', (req, res) => {
  let list = [...books];
  if (req.query.name) list = list.filter(b => b.name.includes(req.query.name));
  if (req.query.bookNo) list = list.filter(b => b.bookNo.includes(req.query.bookNo));
  if (req.query.author) list = list.filter(b => b.author.includes(req.query.author));
  res.json(pageData(list, req.query));
});

app.get('/api/book/pageByCategory', (req, res) => {
  let list = [...books];
  if (req.query.name) list = list.filter(b => b.name.includes(req.query.name));
  if (req.query.category) {
    list = list.filter(b => b.categories && b.categories.some(c => c.includes(req.query.category)));
  }
  res.json(pageData(list, req.query));
});

app.get('/api/book/items/:id', (req, res) => {
  const book = books.find(b => b.id === parseInt(req.params.id));
  if (!book) return res.json(ok([]));
  const items = [];
  for (let i = 1; i <= (book.nums || 3); i++) {
    items.push({
      id: book.id * 100 + i,
      bookId: book.id,
      barcode: `BC${book.bookNo}_${String(i).padStart(3, '0')}`,
      status: i <= 2 ? '在馆' : (i === 3 ? '已借出' : '预约中'),
    });
  }
  res.json(ok(items));
});

app.post('/api/book/save', (req, res) => {
  const obj = req.body;
  obj.id = ++nextId;
  if (!obj.bookNo) obj.bookNo = 'BK' + String(nextId).padStart(3, '0');
  obj.nums = obj.nums || 3;
  obj.score = obj.score || 5;
  books.push(obj);
  res.json(ok(null));
});

app.delete('/api/book/delete/:id', (req, res) => {
  const id = parseInt(req.params.id);
  books = books.filter(b => b.id !== id);
  res.json(ok(null));
});

app.put('/api/book/update', (req, res) => {
  const obj = req.body;
  const idx = books.findIndex(b => b.id === obj.id);
  if (idx >= 0) {
    books[idx] = { ...books[idx], ...obj };
    res.json(ok(null));
  } else {
    res.json(fail('未找到记录'));
  }
});

app.get('/api/book/:id', (req, res) => {
  const book = books.find(b => b.id === parseInt(req.params.id));
  res.json(ok(book || null));
});

app.post('/api/book/file/upload', upload.single('file'), (req, res) => {
  res.json(ok({ url: '/api/book/file/download/' + req.file.filename }));
});

app.get('/api/book/file/download/:flag', (req, res) => {
  const filePath = path.join(__dirname, 'uploads', req.params.flag);
  if (fs.existsSync(filePath)) {
    res.sendFile(filePath);
  } else {
    // 返回默认图片
    res.redirect('https://img2.baidu.com/it/u=2828815297,1164361859&fm=253&fmt=auto&app=138&f=JPEG?w=333&h=500');
  }
});

// ========== Borrow 接口 ==========
app.get('/api/borrow/list', (req, res) => res.json(ok(borrows)));

app.get('/api/borrow/page', (req, res) => {
  let list = [...borrows];
  if (req.query.bookName) list = list.filter(b => b.bookName.includes(req.query.bookName));
  if (req.query.bookNo) list = list.filter(b => b.bookNo.includes(req.query.bookNo));
  if (req.query.userName) list = list.filter(b => b.userName.includes(req.query.userName));
  if (req.query.userNo) list = list.filter(b => b.userNo.includes(req.query.userNo));
  if (req.query.status) list = list.filter(b => b.status === req.query.status);
  res.json(pageData(list, req.query));
});

app.get('/api/borrow/pageRetur', (req, res) => {
  let list = [...returns, ...borrows.filter(b => b.status === '已归还')];
  if (req.query.bookName) list = list.filter(b => b.bookName.includes(req.query.bookName));
  if (req.query.userName) list = list.filter(b => b.userName.includes(req.query.userName));
  res.json(pageData(list, req.query));
});

app.post('/api/borrow/save', (req, res) => {
  const obj = req.body;
  obj.id = ++nextId;
  obj.createtime = new Date().toISOString().split('T')[0];
  obj.updatetime = obj.createtime;
  obj.status = obj.status || '已借出';
  obj.days = obj.days || 30;
  obj.nums = obj.nums || 1;
  borrows.push(obj);
  res.json(ok(null));
});

app.delete('/api/borrow/delete/:id', (req, res) => {
  const id = parseInt(req.params.id);
  borrows = borrows.filter(b => b.id !== id);
  res.json(ok(null));
});

app.put('/api/borrow/update', (req, res) => {
  const obj = req.body;
  const idx = borrows.findIndex(b => b.id === obj.id);
  if (idx >= 0) {
    borrows[idx] = { ...borrows[idx], ...obj, updatetime: new Date().toISOString().split('T')[0] };
    res.json(ok(null));
  } else {
    res.json(fail('未找到记录'));
  }
});

app.get('/api/borrow/:id', (req, res) => {
  const borrow = borrows.find(b => b.id === parseInt(req.params.id));
  res.json(ok(borrow || null));
});

app.post('/api/borrow/saveRetur', (req, res) => {
  const obj = req.body;
  obj.id = ++nextId;
  obj.createtime = new Date().toISOString().split('T')[0];
  obj.status = '已归还';
  returns.push(obj);
  // 更新借阅状态
  const borrow = borrows.find(b => b.bookNo === obj.bookNo && b.userNo === obj.userNo && b.status !== '已归还');
  if (borrow) borrow.status = '已归还';
  res.json(ok(null));
});

app.delete('/api/borrow/deleteRetur/:id', (req, res) => {
  const id = parseInt(req.params.id);
  returns = returns.filter(r => r.id !== id);
  res.json(ok(null));
});

app.get('/api/borrow/lineCharts/:timeRange', (req, res) => {
  const data = {
    week: [
      { date: '周一', count: 12 }, { date: '周二', count: 18 },
      { date: '周三', count: 15 }, { date: '周四', count: 22 },
      { date: '周五', count: 20 }, { date: '周六', count: 8 },
      { date: '周日', count: 5 },
    ],
    month: [
      { date: '第1周', count: 50 }, { date: '第2周', count: 65 },
      { date: '第3周', count: 58 }, { date: '第4周', count: 72 },
    ],
  };
  res.json(ok(data[req.params.timeRange] || data.week));
});

app.get('/api/borrow/reservationCharts/:timeRange', (req, res) => {
  const data = {
    week: [
      { date: '周一', count: 5 }, { date: '周二', count: 8 },
      { date: '周三', count: 6 }, { date: '周四', count: 10 },
      { date: '周五', count: 7 }, { date: '周六', count: 3 },
      { date: '周日', count: 2 },
    ],
  };
  res.json(ok(data[req.params.timeRange] || data.week));
});

app.get('/api/borrow/stats/:bookNo', (req, res) => {
  const book = books.find(b => b.bookNo === req.params.bookNo);
  const totalBorrows = borrows.filter(b => b.bookNo === req.params.bookNo).length;
  res.json(ok({
    bookName: book ? book.name : req.params.bookNo,
    totalNums: book ? book.nums : 5,
    borrowedNums: borrows.filter(b => b.bookNo === req.params.bookNo && b.status === '已借出').length,
    availableNums: (book ? book.nums : 5) - borrows.filter(b => b.bookNo === req.params.bookNo && b.status === '已借出').length,
    totalBorrows: totalBorrows,
    avgScore: 4.5,
  }));
});

app.get('/api/borrow/reservation-stats/:bookNo', (req, res) => {
  res.json(ok({ reservationCount: reservations.filter(r => r.bookNo === req.params.bookNo).length }));
});

// ========== Reservation 接口 ==========
app.post('/api/reservation/reserve', (req, res) => {
  const { bookId, userId } = req.body;
  const book = books.find(b => b.id === bookId);
  const user = users.find(u => u.id === userId);
  if (!book || !user) return res.json(fail('参数错误'));
  const reservation = {
    id: ++nextId,
    bookId: book.id,
    bookName: book.name,
    bookNo: book.bookNo,
    userId: user.id,
    userName: user.name,
    userPhone: user.phone,
    status: '已预约',
    reservedTime: new Date().toISOString().replace('T', ' ').substring(0, 19),
    expireTime: new Date(Date.now() + 3 * 24 * 60 * 60 * 1000).toISOString().replace('T', ' ').substring(0, 19),
    barcode: null,
  };
  reservations.push(reservation);
  res.json(ok(null));
});

app.post('/api/reservation/cancel/:reservationId', (req, res) => {
  const id = parseInt(req.params.reservationId);
  const r = reservations.find(r => r.id === id);
  if (r) r.status = '已取消';
  res.json(ok(null));
});

app.post('/api/reservation/confirm/:reservationId', (req, res) => {
  const id = parseInt(req.params.reservationId);
  const r = reservations.find(r => r.id === id);
  if (r) {
    r.status = '已完成';
    r.barcode = r.barcode || `BC${r.bookNo}_${String(Math.floor(Math.random() * 10) + 1).padStart(3, '0')}`;
  }
  res.json(ok(null));
});

app.get('/api/reservation/my', (req, res) => {
  const currentUser = req.currentUser;
  let list = currentUser ? reservations.filter(r => r.userId === currentUser.id) : reservations;
  if (req.query.status) list = list.filter(r => r.status === req.query.status);
  res.json(ok(list));
});

app.get('/api/reservation/waiting-pickup', (req, res) => {
  res.json(ok(reservations.filter(r => r.status === '等待取书' || r.status === '已预约')));
});

app.get('/api/reservation/admin/list', (req, res) => {
  res.json(pageData(reservations, req.query));
});

// ========== Report 接口 ==========
app.get('/api/report/top10', (req, res) => {
  const top10 = books
    .sort((a, b) => (b.score || 0) - (a.score || 0))
    .slice(0, 10)
    .map(b => ({ name: b.name, count: Math.floor(Math.random() * 50) + 10, score: b.score }));
  res.json(ok(top10));
});

app.get('/api/report/users', (req, res) => {
  const userTop = users.map(u => ({ name: u.name, count: Math.floor(Math.random() * 20) + 1 }));
  res.json(ok(userTop));
});

app.get('/api/report/categories', (req, res) => {
  const catTop = categories.filter(c => !c.pid).slice(0, 5).map(c => ({
    name: c.name,
    count: books.filter(b => b.categories && b.categories.includes(c.name)).length * 10 + Math.floor(Math.random() * 20),
  }));
  res.json(ok(catTop));
});

app.get('/api/report/recommend/user/:userId', (req, res) => {
  const recBooks = [...books].sort(() => Math.random() - 0.5).slice(0, 5);
  res.json(ok(recBooks));
});

// ========== 公告接口 ====
// 模拟系统API
app.get('/api/system/informs', (req, res) => res.json(ok(informs)));
app.get('/api/system/informs/page', (req, res) => res.json(pageData(informs, req.query)));

// 404 处理
app.use((req, res) => {
  console.log('[404]', req.method, req.url);
  // 返回空成功而不是错误，避免前端报错
  res.json(ok(null));
});

// ========== 启动服务器 ==========
const PORT = 9090;
app.listen(PORT, () => {
  console.log('');
  console.log('╔══════════════════════════════════════════════╗');
  console.log('║   图书管理系统 - 模拟后端 (Node.js)           ║');
  console.log(`║   运行端口: ${PORT}                             ║`);
  console.log('║   无需 Java / MySQL / Maven                  ║');
  console.log('╚══════════════════════════════════════════════╝');
  console.log('');
  console.log('  接口地址: http://localhost:' + PORT + '/api');
  console.log('');
  console.log('  【演示账号】');
  console.log('  管理员: admin / 123456');
  console.log('  用  户: user1 / 123456');
  console.log('');
});