const express = require('express');

const app = express();
// 手动设置 CORS 头，避免 cors 模块缺失
app.use((req, res, next) => {
  res.header('Access-Control-Allow-Origin', '*');
  res.header('Access-Control-Allow-Headers', 'Content-Type, Authorization');
  res.header('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS');
  if (req.method === 'OPTIONS') return res.sendStatus(200);
  next();
});
app.use(express.json());

// ============ 模拟数据库 ============
let nextId = 200;

// 技能标签
const skillTags = [
  'Python', 'Java', 'C++', 'JavaScript', '前端开发', '后端开发',
  '机器学习', '数据分析', 'UI设计', '海报设计', '视频剪辑',
  '文案策划', '路演答辩', '市场调研', '财务分析', '项目管理',
  '嵌入式开发', '算法竞赛', '英语翻译', '摄影摄像'
];

// 预设用户
let users = [
  { id: 1, openId: 'wx_user_001', nickName: '代码诗人', avatar: '/icons/default-avatar.png', realName: '张伟', phone: '138****8001', school: '清华大学', major: '计算机科学与技术', grade: '大三', skills: ['Python', 'C++', '算法竞赛', '机器学习'], bio: 'ACM银牌，寻找数学建模队友', score: 95, contestCount: 5, winCount: 2, createTime: '2025-09-01' },
  { id: 2, openId: 'wx_user_002', nickName: '设计师小王', avatar: '/icons/default-avatar.png', realName: '王芳', phone: '138****8002', school: '中央美术学院', major: '视觉传达设计', grade: '大二', skills: ['UI设计', '海报设计', '视频剪辑'], bio: '会PS/AI/PR，想做互联网+', score: 88, contestCount: 3, winCount: 1, createTime: '2025-09-10' },
  { id: 3, openId: 'wx_user_003', nickName: '数据魔法师', avatar: '/icons/default-avatar.png', realName: '李明', phone: '138****8003', school: '北京大学', major: '统计学', grade: '研一', skills: ['Python', '数据分析', '机器学习', '英语翻译'], bio: '统计学研究生，擅长数据分析', score: 92, contestCount: 4, winCount: 2, createTime: '2025-09-15' },
  { id: 4, openId: 'wx_user_004', nickName: '前端小旋风', avatar: '/icons/default-avatar.png', realName: '赵强', phone: '138****8004', school: '浙江大学', major: '软件工程', grade: '大三', skills: ['JavaScript', '前端开发', '后端开发', '项目管理'], bio: '全栈开发，找靠谱队友', score: 85, contestCount: 2, winCount: 0, createTime: '2025-10-01' },
  { id: 5, openId: 'wx_user_005', nickName: '路演达人', avatar: '/icons/default-avatar.png', realName: '陈雪', phone: '138****8005', school: '复旦大学', major: '工商管理', grade: '大四', skills: ['路演答辩', '文案策划', '市场调研', '财务分析'], bio: '拿过挑战杯国奖，擅长商业计划书', score: 96, contestCount: 6, winCount: 4, createTime: '2025-09-05' },
  { id: 6, openId: 'wx_user_006', nickName: '摄影小哥', avatar: '/icons/default-avatar.png', realName: '刘洋', phone: '138****8006', school: '中国传媒大学', major: '广播电视编导', grade: '大三', skills: ['摄影摄像', '视频剪辑', '文案策划'], bio: '会航拍会剪辑，大创项目找队友', score: 78, contestCount: 1, winCount: 0, createTime: '2025-10-15' },
  { id: 7, openId: 'wx_user_007', nickName: '嵌入式达人', avatar: '/icons/default-avatar.png', realName: '周杰', phone: '138****8007', school: '哈尔滨工业大学', major: '电子信息工程', grade: '大四', skills: ['嵌入式开发', 'C++', 'Python'], bio: 'RoboMaster参赛经验', score: 90, contestCount: 3, winCount: 2, createTime: '2025-09-20' },
  { id: 8, openId: 'wx_user_008', nickName: '财务小能手', avatar: '/icons/default-avatar.png', realName: '黄丽', phone: '138****8008', school: '上海财经大学', major: '会计学', grade: '大三', skills: ['财务分析', '市场调研', '项目管理'], bio: 'CPA在考，熟悉财务建模', score: 82, contestCount: 2, winCount: 1, createTime: '2025-10-10' },
];

// 预设竞赛
let contests = [
  { id: 1, name: '2026年"挑战杯"全国大学生课外学术科技作品竞赛', shortName: '挑战杯', type: '学科竞赛', level: '国家级A类', organizer: '共青团中央、中国科协、教育部', deadline: '2026-04-30', startDate: '2026-05-01', endDate: '2026-07-31', location: '北京', maxTeamSize: 8, minTeamSize: 3, description: '挑战杯是"挑战杯"全国大学生系列科技学术竞赛的简称，是全国性的大学生课外学术实践竞赛。', tags: ['科技创新', '学术论文', '社会调研'], status: '报名中', enrollmentCount: 156, coverUrl: '/icons/contest-tiaozhan.png', rules: '1. 每队3-8人\n2. 作品需为原创\n3. 每人限报1个项目' },
  { id: 2, name: '2026年"互联网+"大学生创新创业大赛', shortName: '互联网+', type: '创新创业', level: '国家级A类', organizer: '教育部', deadline: '2026-05-15', startDate: '2026-05-20', endDate: '2026-08-31', location: '杭州', maxTeamSize: 10, minTeamSize: 3, description: '中国"互联网+"大学生创新创业大赛，是全国影响力最大的高校创新创业赛事。', tags: ['创新创业', '商业计划', '互联网'], status: '报名中', enrollmentCount: 234, coverUrl: '/icons/contest-ihub.png', rules: '1. 每队3-10人\n2. 需提交商业计划书\n3. 路演答辩' },
  { id: 3, name: '2026全国大学生数学建模竞赛', shortName: '数学建模', type: '学科竞赛', level: '国家级A类', organizer: '中国工业与应用数学学会', deadline: '2026-06-15', startDate: '2026-06-20', endDate: '2026-06-26', location: '线上+各高校', maxTeamSize: 3, minTeamSize: 3, description: '全国大学生数学建模竞赛创办于1992年，每年一届，目前已成为全国高校规模最大的基础性学科竞赛。', tags: ['数学建模', '编程', '论文写作'], status: '即将开始', enrollmentCount: 510, coverUrl: '/icons/contest-math.png', rules: '1. 每队必须3人\n2. 72小时内完成论文\n3. 可查阅资料' },
  { id: 4, name: '2026年蓝桥杯全国软件和信息技术专业人才大赛', shortName: '蓝桥杯', type: '学科竞赛', level: '国家级B类', organizer: '工业和信息化部人才交流中心', deadline: '2026-03-31', startDate: '2026-04-10', endDate: '2026-05-20', location: '各高校赛点', maxTeamSize: 1, minTeamSize: 1, description: '蓝桥杯大赛是面向高校大学生的IT学科竞赛，包含C/C++、Java、Python等多个组别。', tags: ['编程', '算法', '个人赛'], status: '报名中', enrollmentCount: 320, coverUrl: '/icons/contest-lanqiao.png', rules: '1. 个人参赛\n2. 4小时编程\n3. 分省赛和国赛' },
  { id: 5, name: '2026年全国大学生电子设计竞赛', shortName: '电子设计', type: '学科竞赛', level: '国家级A类', organizer: '教育部、工业和信息化部', deadline: '2026-07-01', startDate: '2026-07-15', endDate: '2026-07-28', location: '各高校赛点', maxTeamSize: 3, minTeamSize: 3, description: '全国大学生电子设计竞赛是面向大学生的群众性科技活动，推动电子信息类专业课程建设。', tags: ['电子设计', '嵌入式', '硬件'], status: '即将开始', enrollmentCount: 189, coverUrl: '/icons/contest-elec.png', rules: '1. 每队3人\n2. 半封闭式\n3. 四天三夜制作' },
  { id: 6, name: '2026年"外研社·国才杯"全国英语演讲大赛', shortName: '外研社杯', type: '语言类', level: '国家级A类', organizer: '外语教学与研究出版社', deadline: '2026-05-30', startDate: '2026-06-01', endDate: '2026-09-30', location: '北京', maxTeamSize: 1, minTeamSize: 1, description: '外研社杯全国英语演讲大赛是面向全国高校大学生的英语演讲赛事。', tags: ['英语演讲', '个人赛', '语言'], status: '报名中', enrollmentCount: 98, coverUrl: '/icons/contest-english.png', rules: '1. 个人参赛\n2. 定题演讲+即兴演讲\n3. 全英文' },
  { id: 7, name: '2026年全国大学生创新创业训练计划（大创）', shortName: '大创项目', type: '创新创业', level: '国家级', organizer: '教育部', deadline: '2026-04-15', startDate: '2026-04-20', endDate: '2027-04-20', location: '各高校', maxTeamSize: 5, minTeamSize: 2, description: '国家级大学生创新创业训练计划，包含创新训练、创业训练和创业实践三类。', tags: ['创新创业', '科研', '实践'], status: '报名中', enrollmentCount: 445, coverUrl: '/icons/contest-dachuang.png', rules: '1. 每队2-5人\n2. 项目周期1年\n3. 需指导老师' },
  { id: 8, name: '2026年全国大学生广告艺术大赛', shortName: '大广赛', type: '艺术设计', level: '国家级B类', organizer: '教育部高等学校新闻传播学类专业教学指导委员会', deadline: '2026-06-01', startDate: '2026-06-10', endDate: '2026-08-31', location: '线上', maxTeamSize: 5, minTeamSize: 1, description: '全国大学生广告艺术大赛（简称大广赛）是全国规模最大的高校广告艺术类赛事。', tags: ['广告设计', '创意', '文案'], status: '报名中', enrollmentCount: 267, coverUrl: '/icons/contest-ad.png', rules: '1. 可个人或组队(≤5人)\n2. 按命题创作\n3. 可提交多个作品' },
];

// 组队招募
let teamRecruits = [
  { id: 1, contestId: 1, contestName: '挑战杯', leaderId: 1, leaderName: '代码诗人', leaderAvatar: '/icons/default-avatar.png', teamName: '星辰大海队', title: 'AI医疗影像诊断系统项目招募', description: '我们的项目是用深度学习做早期肺癌CT影像筛查，已有初步数据。急需：\n1. 医学影像处理经验者\n2. 前端开发（展示平台）\n3. 文案策划（商业计划书）', requiredSkills: ['Python', '机器学习', '前端开发', '文案策划'], currentSize: 3, maxSize: 8, status: '招募中', createTime: '2026-01-15', applications: [
    { id: 301, userId: 3, userName: '数据魔法师', skills: ['Python', '机器学习'], status: '已通过', applyTime: '2026-01-16' },
    { id: 302, userId: 4, userName: '前端小旋风', skills: ['前端开发', 'JavaScript'], status: '待审批', applyTime: '2026-01-18' },
    { id: 303, userId: 5, userName: '路演达人', skills: ['文案策划', '路演答辩'], status: '待审批', applyTime: '2026-01-19' },
  ]},
  { id: 2, contestId: 2, contestName: '互联网+', leaderId: 5, leaderName: '路演达人', leaderAvatar: '/icons/default-avatar.png', teamName: '创无止境', title: '校园二手物品交换平台，招募技术合伙人', description: '商业计划书已完成，商业模式清晰。需要：\n1. 后端开发（Java或Node.js）\n2. UI设计师\n3. 前端开发', requiredSkills: ['后端开发', '前端开发', 'UI设计'], currentSize: 2, maxSize: 10, status: '招募中', createTime: '2026-02-01', applications: [
    { id: 304, userId: 4, userName: '前端小旋风', skills: ['前端开发', 'JavaScript'], status: '待审批', applyTime: '2026-02-02' },
  ]},
  { id: 3, contestId: 3, contestName: '数学建模', leaderId: 3, leaderName: '数据魔法师', leaderAvatar: '/icons/default-avatar.png', teamName: '建模三人组', title: '数学建模队伍缺1人（编程手）', description: '已有2人：一个擅长建模（统计学研究生），一个擅长写作（中文系）。急缺编程手，要求熟练Python或MATLAB。', requiredSkills: ['Python', '算法竞赛'], currentSize: 2, maxSize: 3, status: '招募中', createTime: '2026-02-10', applications: [
    { id: 305, userId: 1, userName: '代码诗人', skills: ['Python', '算法竞赛', 'C++'], status: '待审批', applyTime: '2026-02-11' },
    { id: 306, userId: 7, userName: '嵌入式达人', skills: ['C++', 'Python'], status: '待审批', applyTime: '2026-02-12' },
  ]},
  { id: 4, contestId: 7, contestName: '大创项目', leaderId: 8, leaderName: '财务小能手', leaderAvatar: '/icons/default-avatar.png', teamName: '绿手指', title: '大学生理财教育平台，招募开发小伙伴', description: '想做一个面向大学生的理财知识学习+模拟投资平台。已有财务专业背景，需要技术实现。', requiredSkills: ['前端开发', '后端开发', 'UI设计'], currentSize: 1, maxSize: 5, status: '招募中', createTime: '2026-02-20', applications: []},
  { id: 5, contestId: 5, contestName: '电子设计', leaderId: 7, leaderName: '嵌入式达人', leaderAvatar: '/icons/default-avatar.png', teamName: '硬核玩家', title: '智能家居中控系统，缺硬件调试+软件开发', description: '基于ESP32的智能家居中控，已完成原理图。需要：\n1. 硬件调试（示波器/万用表熟手）\n2. App前端开发', requiredSkills: ['嵌入式开发', '前端开发'], currentSize: 2, maxSize: 3, status: '已完成', createTime: '2026-01-05', applications: []},
];

// 报名记录
let enrollments = [
  { id: 401, userId: 1, userName: '代码诗人', contestId: 1, contestName: '挑战杯', teamName: '星辰大海队', role: '队长', status: '已通过', skills: ['Python', 'C++', '算法竞赛', '机器学习'], createTime: '2026-01-15' },
  { id: 402, userId: 3, userName: '数据魔法师', contestId: 1, contestName: '挑战杯', teamName: '星辰大海队', role: '队员', status: '已通过', skills: ['Python', '数据分析', '机器学习'], createTime: '2026-01-16' },
  { id: 403, userId: 5, userName: '路演达人', contestId: 2, contestName: '互联网+', teamName: '创无止境', role: '队长', status: '已通过', skills: ['路演答辩', '文案策划'], createTime: '2026-02-01' },
  { id: 404, userId: 3, userName: '数据魔法师', contestId: 3, contestName: '数学建模', teamName: '建模三人组', role: '队长', status: '已通过', skills: ['Python', '数据分析', '机器学习'], createTime: '2026-02-10' },
  { id: 405, userId: 5, userName: '路演达人', contestId: 4, contestName: '蓝桥杯', teamName: '个人参赛', role: '个人', status: '已通过', skills: ['Python'], createTime: '2026-02-15' },
];

// 消息通知
let messages = [
  { id: 501, userId: 1, type: 'team_apply', title: '组队申请通知', content: '数据魔法师 申请加入你的团队"星辰大海队"', relatedId: 301, isRead: true, createTime: '2026-01-16 10:30:00' },
  { id: 502, userId: 1, type: 'team_apply', title: '组队申请通知', content: '前端小旋风 申请加入你的团队"星辰大海队"', relatedId: 302, isRead: false, createTime: '2026-01-18 14:20:00' },
  { id: 503, userId: 1, type: 'team_apply', title: '组队申请通知', content: '路演达人 申请加入你的团队"星辰大海队"', relatedId: 303, isRead: false, createTime: '2026-01-19 09:15:00' },
  { id: 504, userId: 4, type: 'apply_result', title: '申请结果通知', content: '你申请加入"星辰大海队"的结果正在等待审批', relatedId: 302, isRead: true, createTime: '2026-01-18 14:25:00' },
  { id: 505, userId: 5, type: 'contest_deadline', title: '竞赛提醒', content: '你报名的"互联网+"大学生创新创业大赛将在15天后截止报名', relatedId: 2, isRead: false, createTime: '2026-05-01 08:00:00' },
  { id: 506, userId: 3, type: 'contest_deadline', title: '竞赛提醒', content: '你报名的数学建模竞赛将在30天后开始', relatedId: 3, isRead: false, createTime: '2026-05-20 08:00:00' },
  { id: 507, userId: 1, type: 'system', title: '系统通知', content: '欢迎加入竞伙伴！完善你的技能标签，让更多团队找到你~', relatedId: null, isRead: true, createTime: '2026-01-15 12:00:00' },
];

// Token管理
let tokens = {};
function generateToken(userInfo) {
  const token = 'token_' + Date.now() + '_' + Math.random().toString(36).substr(2);
  tokens[token] = { ...userInfo, createTime: Date.now() };
  return token;
}

// 工具函数
function ok(data) { return { code: '200', msg: '操作成功', data }; }
function fail(msg) { return { code: '400', msg: msg || '操作失败' }; }
function pageData(list, params) {
  const pageNum = parseInt(params.pageNum) || 1;
  const pageSize = parseInt(params.pageSize) || 10;
  let result = [...list];
  if (params.keyword) {
    const kw = params.keyword.toLowerCase();
    result = result.filter(item => {
      return (item.name && item.name.toLowerCase().includes(kw)) ||
             (item.shortName && item.shortName.toLowerCase().includes(kw)) ||
             (item.title && item.title.toLowerCase().includes(kw)) ||
             (item.description && item.description.toLowerCase().includes(kw));
    });
  }
  if (params.type) result = result.filter(item => item.type === params.type);
  if (params.level) result = result.filter(item => item.level === params.level);
  if (params.status) result = result.filter(item => item.status === params.status);
  if (params.contestId) result = result.filter(item => item.contestId == params.contestId);
  if (params.userId) result = result.filter(item => item.userId == params.userId);
  if (params.skill) {
    result = result.filter(item => {
      const skills = item.requiredSkills || item.skills || [];
      return skills.some(s => s.includes(params.skill));
    });
  }
  const total = result.length;
  const start = (pageNum - 1) * pageSize;
  const records = result.slice(start, start + pageSize);
  return { list: records, total, size: pageSize, current: pageNum, pages: Math.ceil(total / pageSize) };
}

// 认证中间件
function authMiddleware(req, res, next) {
  const token = req.headers.token || req.query.token;
  if (token && tokens[token]) {
    req.currentUser = tokens[token];
  }
  next();
}
app.use(authMiddleware);

// ============ 用户接口 ============
app.post('/api/user/login', (req, res) => {
  const { nickName, avatar } = req.body;
  let user = users.find(u => u.nickName === nickName);
  if (!user) {
    user = {
      id: ++nextId,
      openId: 'wx_' + Date.now(),
      nickName: nickName || '新用户',
      avatar: avatar || '/icons/default-avatar.png',
      realName: '',
      phone: '',
      school: '',
      major: '',
      grade: '',
      skills: [],
      bio: '',
      score: 50,
      contestCount: 0,
      winCount: 0,
      createTime: new Date().toISOString().split('T')[0]
    };
    users.push(user);
  }
  const token = generateToken({ id: user.id, nickName: user.nickName });
  res.json(ok({ token, user }));
});

app.get('/api/user/profile', (req, res) => {
  const user = users.find(u => u.id === req.currentUser?.id);
  if (!user) return res.json(fail('未登录'));
  res.json(ok(user));
});

app.put('/api/user/profile', (req, res) => {
  const { realName, phone, school, major, grade, skills, bio } = req.body;
  const idx = users.findIndex(u => u.id === req.currentUser?.id);
  if (idx < 0) return res.json(fail('未登录'));
  users[idx] = { ...users[idx],
    realName: realName || users[idx].realName,
    phone: phone || users[idx].phone,
    school: school || users[idx].school,
    major: major || users[idx].major,
    grade: grade || users[idx].grade,
    skills: skills || users[idx].skills,
    bio: bio || users[idx].bio
  };
  res.json(ok(users[idx]));
});

app.post('/api/user/sync', (req, res) => {
  const { nickName, avatar } = req.body;
  let user = users.find(u => u.nickName === nickName);
  if (!user) {
    user = { id: ++nextId, openId: 'wx_' + Date.now(), nickName: nickName || '新用户', avatar: avatar || '/icons/default-avatar.png', realName: '', phone: '', school: '', major: '', grade: '', skills: [], bio: '', score: 50, contestCount: 0, winCount: 0, createTime: new Date().toISOString().split('T')[0] };
    users.push(user);
  } else {
    if (avatar) user.avatar = avatar;
  }
  const token = generateToken({ id: user.id, nickName: user.nickName });
  res.json(ok({ token, user }));
});

// ============ 竞赛接口 ============
app.get('/api/contest/list', (req, res) => {
  const result = pageData(contests, req.query);
  res.json(ok(result));
});

app.get('/api/contest/hot', (req, res) => {
  const hot = [...contests].sort((a, b) => (b.enrollmentCount || 0) - (a.enrollmentCount || 0)).slice(0, 4);
  res.json(ok(hot));
});

app.get('/api/contest/types', (req, res) => {
  const types = [...new Set(contests.map(c => c.type))];
  res.json(ok(types));
});

app.get('/api/contest/:id', (req, res) => {
  const contest = contests.find(c => c.id == req.params.id);
  if (!contest) return res.json(fail('竞赛不存在'));
  const enrollmentUsers = enrollments.filter(e => e.contestId == contest.id);
  res.json(ok({ ...contest, enrollmentUsers }));
});

// 竞赛报名
app.post('/api/contest/register', (req, res) => {
  const { contestId, teamName, role, skills } = req.body;
  const user = req.currentUser;
  if (!user) return res.json(fail('请先登录'));
  const contest = contests.find(c => c.id == contestId);
  if (!contest) return res.json(fail('竞赛不存在'));
  // 检查是否已报名
  const exists = enrollments.find(e => e.userId === user.id && e.contestId == contestId);
  if (exists) return res.json(fail('您已报名该竞赛'));
  const enrollment = {
    id: ++nextId,
    userId: user.id,
    userName: user.nickName,
    contestId: contest.id,
    contestName: contest.name,
    teamName: teamName || '个人参赛',
    role: role || (contest.maxTeamSize === 1 ? '个人' : '队长'),
    status: '已通过',
    skills: skills || [],
    createTime: new Date().toISOString().split('T')[0]
  };
  enrollments.push(enrollment);
  contest.enrollmentCount = (contest.enrollmentCount || 0) + 1;
  // 更新用户竞赛次数
  const uidx = users.findIndex(u => u.id === user.id);
  if (uidx >= 0) users[uidx].contestCount = (users[uidx].contestCount || 0) + 1;
  // 发消息
  messages.push({
    id: ++nextId, userId: user.id, type: 'enrollment', title: '报名成功',
    content: '你已成功报名"' + (contest.shortName || contest.name) + '"',
    relatedId: enrollment.id, isRead: false,
    createTime: new Date().toISOString().replace('T', ' ').substring(0, 19)
  });
  res.json(ok(enrollment));
});

// 用户报名列表
app.get('/api/contest/my-enrollments', (req, res) => {
  if (!req.currentUser) return res.json(fail('请先登录'));
  const list = enrollments.filter(e => e.userId === req.currentUser.id);
  res.json(ok(list));
});

// 用户团队列表
app.get('/api/user/teams', (req, res) => {
  if (!req.currentUser) return res.json(fail('请先登录'));
  const myTeams = teamRecruits.filter(t => t.leaderId === req.currentUser.id || t.applications.some(a => a.userId === req.currentUser.id && a.status === '已通过'));
  res.json(ok(myTeams));
});

// ============ 组队接口 ============
app.get('/api/team/list', (req, res) => {
  const result = pageData(teamRecruits.filter(t => t.status === '招募中'), req.query);
  res.json(ok(result));
});

app.get('/api/team/:id', (req, res) => {
  const team = teamRecruits.find(t => t.id == req.params.id);
  if (!team) return res.json(fail('团队不存在'));
  const leader = users.find(u => u.id === team.leaderId);
  res.json(ok({ ...team, leaderInfo: leader }));
});

// 发布组队
app.post('/api/team/create', (req, res) => {
  if (!req.currentUser) return res.json(fail('请先登录'));
  const { contestId, contestName, teamName, title, description, requiredSkills, maxSize } = req.body;
  const recruit = {
    id: ++nextId, contestId: contestId || 0, contestName: contestName || '待定',
    leaderId: req.currentUser.id, leaderName: req.currentUser.nickName,
    leaderAvatar: users.find(u => u.id === req.currentUser.id)?.avatar || '/icons/default-avatar.png',
    teamName: teamName || '新团队', title, description,
    requiredSkills: requiredSkills || [],
    currentSize: 1, maxSize: maxSize || 5, status: '招募中',
    createTime: new Date().toISOString().split('T')[0], applications: []
  };
  teamRecruits.push(recruit);
  res.json(ok(recruit));
});

// 申请加入
app.post('/api/team/apply', (req, res) => {
  if (!req.currentUser) return res.json(fail('请先登录'));
  const { teamId } = req.body;
  const team = teamRecruits.find(t => t.id == teamId);
  if (!team) return res.json(fail('团队不存在'));
  const exists = team.applications.find(a => a.userId === req.currentUser.id);
  if (exists) return res.json(fail('您已申请，请等待审批'));
  const application = {
    id: ++nextId, userId: req.currentUser.id, userName: req.currentUser.nickName,
    skills: users.find(u => u.id === req.currentUser.id)?.skills || [],
    status: '待审批',
    applyTime: new Date().toISOString().split('T')[0]
  };
  team.applications.push(application);
  // 通知队长
  messages.push({
    id: ++nextId, userId: team.leaderId, type: 'team_apply', title: '新的组队申请',
    content: req.currentUser.nickName + ' 申请加入你的团队"' + team.teamName + '"',
    relatedId: application.id, isRead: false,
    createTime: new Date().toISOString().replace('T', ' ').substring(0, 19)
  });
  res.json(ok(application));
});

// 队长审批
app.put('/api/team/approve', (req, res) => {
  if (!req.currentUser) return res.json(fail('请先登录'));
  const { teamId, applicationId, approved } = req.body;
  const team = teamRecruits.find(t => t.id == teamId);
  if (!team) return res.json(fail('团队不存在'));
  if (team.leaderId !== req.currentUser.id) return res.json(fail('只有队长可以审批'));
  const app = team.applications.find(a => a.id == applicationId);
  if (!app) return res.json(fail('申请不存在'));
  app.status = approved ? '已通过' : '已拒绝';
  if (approved) team.currentSize++;
  if (team.currentSize >= team.maxSize) team.status = '已完成';
  // 通知申请人
  messages.push({
    id: ++nextId, userId: app.userId, type: 'apply_result', title: '申请结果通知',
    content: '你申请加入"' + team.teamName + '"' + (approved ? '已通过' : '已拒绝'),
    relatedId: app.id, isRead: false,
    createTime: new Date().toISOString().replace('T', ' ').substring(0, 19)
  });
  res.json(ok(app));
});

// ============ 智能匹配接口 ============
app.get('/api/match/teammate', (req, res) => {
  if (!req.currentUser) return res.json(fail('请先登录'));
  const mySkills = users.find(u => u.id === req.currentUser.id)?.skills || [];
  // 计算匹配度：技能重合度 + 互补度
  const scored = users
    .filter(u => u.id !== req.currentUser.id)
    .map(u => {
      const overlap = mySkills.filter(s => u.skills.includes(s)).length;
      const complement = u.skills.filter(s => !mySkills.includes(s)).length;
      const matchScore = overlap * 20 + complement * 10 + (u.score || 50) * 0.3;
      return {
        ...u,
        matchScore: Math.min(Math.round(matchScore), 99),
        matchReason: complement > 0
          ? '技能互补（+' + complement + '项新技能）'
          : '有' + overlap + '项共同技能'
      };
    })
    .sort((a, b) => b.matchScore - a.matchScore)
    .slice(0, 5);
  res.json(ok(scored));
});

app.get('/api/match/team', (req, res) => {
  if (!req.currentUser) return res.json(fail('请先登录'));
  const mySkills = users.find(u => u.id === req.currentUser.id)?.skills || [];
  const scored = teamRecruits
    .filter(t => t.status === '招募中' && t.leaderId !== req.currentUser.id)
    .map(t => {
      const needed = t.requiredSkills.filter(s => mySkills.includes(s));
      const matchScore = needed.length * 25 + (t.maxSize - t.currentSize > 2 ? 10 : 0);
      return {
        ...t,
        matchScore: Math.min(Math.round(matchScore), 99),
        matchReason: needed.length > 0
          ? '匹配' + needed.length + '项所需技能'
          : '团队规模合适'
      };
    })
    .sort((a, b) => b.matchScore - a.matchScore)
    .slice(0, 5);
  res.json(ok(scored));
});

// ============ 消息接口 ============
app.get('/api/message/list', (req, res) => {
  if (!req.currentUser) return res.json(fail('请先登录'));
  const list = messages.filter(m => m.userId === req.currentUser.id).sort((a, b) => b.id - a.id);
  const unreadCount = list.filter(m => !m.isRead).length;
  res.json(ok({ list, unreadCount }));
});

app.put('/api/message/read/:id', (req, res) => {
  const msg = messages.find(m => m.id == req.params.id);
  if (msg) msg.isRead = true;
  res.json(ok(null));
});

app.put('/api/message/read-all', (req, res) => {
  if (!req.currentUser) return res.json(fail('请先登录'));
  messages.filter(m => m.userId === req.currentUser.id).forEach(m => m.isRead = true);
  res.json(ok(null));
});

// ============ 技能标签接口 ============
app.get('/api/skill/list', (req, res) => {
  res.json(ok(skillTags));
});

// ============ 首页数据接口 ============
app.get('/api/home/banner', (req, res) => {
  const banners = [
    { id: 1, title: '挑战杯报名倒计时', image: '/icons/banner-challenge.png', link: '/pages/contest/detail?id=1' },
    { id: 2, title: '找队友？来组队广场', image: '/icons/banner-team.png', link: '/pages/team/square' },
    { id: 3, title: 'AI智能匹配队友', image: '/icons/banner-match.png', link: '/pages/match/index' },
  ];
  res.json(ok(banners));
});

app.get('/api/home/stats', (req, res) => {
  res.json(ok({
    contestCount: contests.length,
    userCount: users.length,
    teamCount: teamRecruits.length,
    enrollmentCount: enrollments.length,
  }));
});

// 404
app.use((req, res) => { res.json(ok(null)); });

// ============ 启动 ============
const PORT = 9091;
app.listen(PORT, () => {
  console.log('');
  console.log('============================================');
  console.log('  竞伙伴 - 模拟后端 (Node.js)');
  console.log('  运行端口: ' + PORT);
  console.log('  接口地址: http://localhost:' + PORT + '/api');
  console.log('============================================');
  console.log('');
  console.log('  内置用户（登录时使用 nickName）：');
  console.log('  代码诗人、设计师小王、数据魔法师、前端小旋风');
  console.log('  路演达人、摄影小哥、嵌入式达人、财务小能手');
  console.log('');
});