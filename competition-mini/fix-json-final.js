// 生成完美 pages.json - 运行: node fix-json-final.js
const fs = require('fs');

const json = {
  "pages": [
    { "path": "pages/index/index", "style": { "navigationBarTitleText": "竞伙伴", "navigationBarBackgroundColor": "#4A90D9", "navigationBarTextStyle": "white", "enablePullDownRefresh": true } },
    { "path": "pages/contest/list", "style": { "navigationBarTitleText": "竞赛列表", "navigationBarBackgroundColor": "#4A90D9", "navigationBarTextStyle": "white", "enablePullDownRefresh": true } },
    { "path": "pages/contest/detail", "style": { "navigationBarTitleText": "竞赛详情", "navigationBarBackgroundColor": "#4A90D9", "navigationBarTextStyle": "white" } },
    { "path": "pages/contest/enroll", "style": { "navigationBarTitleText": "报名参赛", "navigationBarBackgroundColor": "#4A90D9", "navigationBarTextStyle": "white" } },
    { "path": "pages/team/square", "style": { "navigationBarTitleText": "组队广场", "navigationBarBackgroundColor": "#4A90D9", "navigationBarTextStyle": "white", "enablePullDownRefresh": true } },
    { "path": "pages/team/detail", "style": { "navigationBarTitleText": "团队详情", "navigationBarBackgroundColor": "#4A90D9", "navigationBarTextStyle": "white" } },
    { "path": "pages/team/create", "style": { "navigationBarTitleText": "发布组队", "navigationBarBackgroundColor": "#4A90D9", "navigationBarTextStyle": "white" } },
    { "path": "pages/match/index", "style": { "navigationBarTitleText": "智能匹配", "navigationBarBackgroundColor": "#4A90D9", "navigationBarTextStyle": "white" } },
    { "path": "pages/message/list", "style": { "navigationBarTitleText": "消息通知", "navigationBarBackgroundColor": "#4A90D9", "navigationBarTextStyle": "white" } },
    { "path": "pages/user/index", "style": { "navigationBarTitleText": "个人中心", "navigationBarBackgroundColor": "#4A90D9", "navigationBarTextStyle": "white" } },
    { "path": "pages/user/edit", "style": { "navigationBarTitleText": "编辑资料", "navigationBarBackgroundColor": "#4A90D9", "navigationBarTextStyle": "white" } },
    { "path": "pages/user/myEnroll", "style": { "navigationBarTitleText": "我的报名", "navigationBarBackgroundColor": "#4A90D9", "navigationBarTextStyle": "white" } },
    { "path": "pages/user/myTeam", "style": { "navigationBarTitleText": "我的团队", "navigationBarBackgroundColor": "#4A90D9", "navigationBarTextStyle": "white" } },
    { "path": "pages/login/index", "style": { "navigationBarTitleText": "", "navigationBarBackgroundColor": "#ffffff", "navigationBarTextStyle": "black", "navigationStyle": "custom" } },
    { "path": "pages/search/index", "style": { "navigationBarTitleText": "搜索", "navigationBarBackgroundColor": "#4A90D9", "navigationBarTextStyle": "white" } }
  ],
  "tabBar": {
    "color": "#999999", "selectedColor": "#4A90D9", "backgroundColor": "#ffffff", "borderStyle": "black",
    "list": [
      { "pagePath": "pages/index/index", "iconPath": "static/tabbar/home.png", "selectedIconPath": "static/tabbar/home-active.png", "text": "首页" },
      { "pagePath": "pages/contest/list", "iconPath": "static/tabbar/contest.png", "selectedIconPath": "static/tabbar/contest-active.png", "text": "竞赛" },
      { "pagePath": "pages/team/square", "iconPath": "static/tabbar/team.png", "selectedIconPath": "static/tabbar/team-active.png", "text": "组队" },
      { "pagePath": "pages/message/list", "iconPath": "static/tabbar/message.png", "selectedIconPath": "static/tabbar/message-active.png", "text": "消息" },
      { "pagePath": "pages/user/index", "iconPath": "static/tabbar/mine.png", "selectedIconPath": "static/tabbar/mine-active.png", "text": "我的" }
    ]
  },
  "globalStyle": { "navigationBarTextStyle": "white", "navigationBarTitleText": "竞伙伴", "navigationBarBackgroundColor": "#4A90D9", "backgroundColor": "#f5f5f5" }
};

const target = 'c:/Users/22641/Desktop/BookManageSystem/competition-mini/pages.json';
fs.writeFileSync(target, JSON.stringify(json, null, 2), 'utf8');

// 验证
const content = fs.readFileSync(target, 'utf8');
JSON.parse(content);
console.log('✅ pages.json 已完美生成！JSON语法验证通过！');
console.log('  路径: ' + target);
console.log('  文件大小: ' + content.length + ' 字节');
console.log('\n现在可以运行小程序了！');
