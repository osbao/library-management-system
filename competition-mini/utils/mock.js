// 竞赛数据
const contests=[
  {id:1,name:'挑战杯全国大学生课外学术科技作品竞赛',org:'共青团中央',level:'国家级',bg:'#667eea',statusText:'报名中',deadlineText:'2026-09-30',tags:['科技创新','学术论文'],enrolled:1523,desc:'国家级A类竞赛，含金量极高。',requirements:['全日制在校大学生','可组队3-5人'],contestTime:'2026年9月-12月'},
  {id:2,name:'中国互联网+大学生创新创业大赛',org:'教育部',level:'国家级',bg:'#764ba2',statusText:'报名中',deadlineText:'2026-08-15',tags:['创业计划','互联网'],enrolled:2341,desc:'全国最大规模的创新创业赛事。',requirements:['在校大学生','团队参赛'],contestTime:'2026年8月-11月'},
  {id:3,name:'全国大学生数学建模竞赛',org:'中国工业与应用数学学会',level:'国家级',bg:'#f093fb',statusText:'报名中',deadlineText:'2026-07-20',tags:['数学','建模','编程'],enrolled:856,desc:'培养解决实际问题能力。',requirements:['本科生','3人一组'],contestTime:'2026年9月'},
  {id:4,name:'全国大学生电子设计竞赛',org:'教育部',level:'国家级',bg:'#4facfe',statusText:'即将开始',deadlineText:'2026-10-01',tags:['电子','嵌入式'],enrolled:432,desc:'电子信息类最权威竞赛。',requirements:['电子/信息专业'],contestTime:'2026年10月'},
  {id:5,name:'ACM国际大学生程序设计竞赛',org:'ACM协会',level:'国家级',bg:'#43e97b',statusText:'报名中',deadlineText:'2026-06-30',tags:['算法','C++'],enrolled:678,desc:'全球最具影响力编程竞赛。',requirements:['全日制本科生','3人一队'],contestTime:'2026年11月'},
  {id:6,name:'全国大学生创业计划大赛',org:'教育部',level:'省部级',bg:'#fa709a',statusText:'报名中',deadlineText:'2026-08-01',tags:['创业','路演'],enrolled:345,desc:'展示商业才华的舞台。',requirements:['在校生','可组队'],contestTime:'2026年10月-12月'}
];
// 组队数据
const teams=[
  {id:1,userName:'张伟',userAvatar:'/static/icons/default-avatar.png',contestName:'挑战杯',desc:'找技术大牛做AI+医疗，需Python和后端。',tags:['Python','AI'],statusText:'招募中',statusColor:'tag-success',members:2,maxMembers:5,createdAt:'2026-03-15',memberList:[{nickName:'张伟',avatar:'/static/icons/default-avatar.png',role:'队长',skills:['Python']}]},
  {id:2,userName:'李明',userAvatar:'/static/icons/default-avatar.png',contestName:'数学建模',desc:'数学建模老手带队，缺论文队友。',tags:['Matlab','论文'],statusText:'招募中',statusColor:'tag-success',members:1,maxMembers:3,createdAt:'2026-03-14',memberList:[{nickName:'李明',avatar:'/static/icons/default-avatar.png',role:'队长',skills:['Matlab']}]},
  {id:3,userName:'赵强',userAvatar:'/static/icons/default-avatar.png',contestName:'互联网+',desc:'后端熟手，需前端小伙伴。',tags:['Node.js','Vue'],statusText:'已满员',statusColor:'tag-danger',members:5,maxMembers:5,createdAt:'2026-03-12',memberList:[]},
  {id:4,userName:'陈雪',userAvatar:'/static/icons/default-avatar.png',contestName:'创业计划大赛',desc:'有好idea和路演经验，寻合伙人。',tags:['商业模式','路演'],statusText:'招募中',statusColor:'tag-success',members:2,maxMembers:4,createdAt:'2026-03-10',memberList:[]}
];
// 智能匹配
const matches=[
  {nickName:'数据魔法师',avatar:'/static/icons/default-avatar.png',school:'北京大学',skills:['Python','机器学习'],matchScore:95,lookingFor:'需前端和设计',remark:'顶会论文发表经验'},
  {nickName:'前端小旋风',avatar:'/static/icons/default-avatar.png',school:'浙江大学',skills:['JavaScript','Vue.js'],matchScore:88,lookingFor:'需后端和算法',remark:'3年前端经验'},
  {nickName:'路演达人',avatar:'/static/icons/default-avatar.png',school:'复旦大学',skills:['路演答辩','PPT'],matchScore:82,lookingFor:'需技术和设计',remark:'多次路演一等奖'}
];
// 消息
const messages=[
  {id:1,typeIcon:'👥',bg:'#4A90D9',title:'组队邀请',content:'张伟邀请你加入挑战杯团队',time:'2小时前',read:false},
  {id:2,typeIcon:'✅',bg:'#67C23A',title:'报名成功',content:'已成功报名互联网+大赛',time:'1天前',read:false},
  {id:3,typeIcon:'📢',bg:'#E6A23C',title:'系统公告',content:'竞伙伴V2.0上线，新增智能匹配功能',time:'2天前',read:true},
  {id:4,typeIcon:'🔔',bg:'#F56C6C',title:'竞赛提醒',content:'数学建模竞赛报名即将截止',time:'3天前',read:true}
];


// ===== Mock请求处理器 =====
function delay(t=150){ return new Promise(r=>setTimeout(r,t+Math.random()*200)); }

export default async function mockRequest(url, method, params) {
  await delay();
  const p = url.replace('http://localhost:9091/api/', '');
  
  if (p === 'contest/list') {
    let list = [...contests];
    if (params && params.keyword) { const kw = params.keyword.toLowerCase(); list = list.filter(c => c.name.toLowerCase().includes(kw) || c.tags.some(t => t.toLowerCase().includes(kw))); }
    if (params && params.level) list = list.filter(c => c.level === params.level);
    if (params && params.status === '报名中') list = list.filter(c => c.statusText === '报名中');
    return { code: '200', data: { list, total: list.length } };
  }
  if (p.startsWith('contest/detail') || /^contest\/\d+$/.test(p)) {
    const id = parseInt(p.split('id=')[1] || p.split('/').pop());
    return { code: '200', data: contests.find(c => c.id === id) || contests[0] };
  }
  if (p === 'contest/register') return { code: '200', msg: '报名成功' };
  
  if (p === 'team/list') {
    let list = [...teams];
    if (params && params.keyword) { const kw = params.keyword.toLowerCase(); list = list.filter(t => t.contestName.toLowerCase().includes(kw) || t.tags.some(tag => tag.toLowerCase().includes(kw))); }
    return { code: '200', data: { list, total: list.length } };
  }
  if (p.startsWith('team/detail')) {
    const id = parseInt(p.split('id=')[1]);
    return { code: '200', data: teams.find(t => t.id === id) || teams[0] };
  }
  if (p === 'team/create') return { code: '200', msg: '团队创建成功' };
  if (p === 'team/join') return { code: '200', msg: '申请已发送' };
  
  if (p === 'match/teammate') return { code: '200', data: { list: matches } };
  
  if (p === 'contest/my-enrollments') return { code: '200', data: { list: [
    { id:1,contestName:'挑战杯',role:'技术开发',statusText:'已报名',statusColor:'tag-primary',createdAt:'2026-03-20',teamName:'AI创新团队'},
    { id:2,contestName:'数学建模',role:'数据分析',statusText:'审核中',statusColor:'tag-warning',createdAt:'2026-03-18',teamName:''}
  ]}};
  
  if (p === 'user/teams') return { code: '200', data: { list: [
    { id:1,contestName:'挑战杯',role:'队长',statusText:'组队中',statusColor:'tag-success',tags:['Python','AI'],memberCount:3,maxMembers:5,createdAt:'2026-03-15',members:[{avatar:'/static/icons/default-avatar.png'},{avatar:'/static/icons/default-avatar.png'},{avatar:'/static/icons/default-avatar.png'}]}
  ]}};
  
  if (p === 'message/list') {
    let list = [...messages];
    if (params && params.type && params.type !== 'all') {
      const map = { team:'组队', system:'系统', contest:'竞赛' };
      const filter = map[params.type];
      if (filter) list = list.filter(m => m.title.includes(filter));
    }
    return { code: '200', data: { list, unreadCount: list.filter(m => !m.read).length } };
  }
  
  if (p === 'user/profile') return { code: '200', data: { nickName:'代码诗人',avatar:'/static/icons/default-avatar.png',school:'清华大学',major:'计算机科学',skills:['Python','C++','算法竞赛'],desc:'热爱编程',enrollCount:3,teamCount:2,inviteCount:5 }};
  
  if (p.includes('report/recommend')) return { code: '200', data: [
    { id:1,name:'挑战杯',author:'共青团',publishDate:'2026',nums:0,score:8},
    { id:2,name:'互联网+',author:'教育部',publishDate:'2026',nums:0,score:9},
    { id:4,name:'电子设计',author:'教育部',publishDate:'2026',nums:0,score:6}
  ]};
  
  if (p === 'user/list') return { code: '200', data: { list: [
    { nickName:'代码诗人',avatar:'/static/icons/default-avatar.png',school:'清华大学',skills:['Python','C++'] },
    { nickName:'设计师小王',avatar:'/static/icons/default-avatar.png',school:'中央美术学院',skills:['UI设计','海报设计'] },
    { nickName:'数据魔法师',avatar:'/static/icons/default-avatar.png',school:'北京大学',skills:['Python','数据分析'] }
  ]}};
  
  return { code: '200', data: null };
}