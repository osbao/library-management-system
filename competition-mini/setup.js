// 竞伙伴小程序 - 一键初始化脚本
// 用法: node setup.js

const fs = require('fs');
const path = require('path');
const zlib = require('zlib');

console.log('🚀 竞伙伴小程序 - 初始化中...\n');

// ===== 1. 修复 pages.json =====
console.log('📄 正在修复 pages.json...');
const pagesFile = path.join(__dirname, 'pages.json');
let content = fs.readFileSync(pagesFile, 'utf8');

// 修复竞赛列表和组队广场缺失的逗号
content = content.replace(
  /("navigationBarTextStyle":\s*"white")\s*\n(\s*"enablePullDownRefresh":\s*true)/g,
  '$1,\n$2'
);

// 修复 login 后多余的空条目
content = content.replace(
  /("navigationStyle":\s*"custom"\s*\n\s*\}\s*\n)\s*\}\s*\n\s*(\},\s*\n\s*\{)/,
  '$1    },\n    {'
);

fs.writeFileSync(pagesFile, content, 'utf8');

// 验证 JSON
try {
  JSON.parse(content);
  console.log('  ✅ pages.json 语法正确');
} catch (e) {
  console.log('  ⚠️ JSON验证警告:', e.message);
  console.log('  💡 如编译失败，用 pages-fixed.json 替换即可');
}

// ===== 2. 生成占位图标 =====
console.log('\n🎨 正在生成占位图标...');

function createSolidPng(r, g, b) {
  const width = 40, height = 40;
  const signature = Buffer.from([137, 80, 78, 71, 13, 10, 26, 10]);
  
  function createChunk(type, data) {
    const length = Buffer.alloc(4);
    length.writeUInt32BE(data.length, 0);
    const typeAndData = Buffer.concat([Buffer.from(type), data]);
    let crc = 0xFFFFFFFF;
    for (let i = 0; i < typeAndData.length; i++) {
      crc ^= typeAndData[i];
      for (let j = 0; j < 8; j++) crc = (crc >>> 1) ^ (crc & 1 ? 0xEDB88320 : 0);
    }
    const crcBuf = Buffer.alloc(4);
    crcBuf.writeUInt32BE((crc ^ 0xFFFFFFFF) >>> 0, 0);
    return Buffer.concat([length, typeAndData, crcBuf]);
  }

  const ihdrData = Buffer.alloc(13);
  ihdrData.writeUInt32BE(width, 0);
  ihdrData.writeUInt32BE(height, 4);
  ihdrData[8] = 8; ihdrData[9] = 2; ihdrData[10] = ihdrData[11] = ihdrData[12] = 0;
  
  const rawData = Buffer.alloc(height * (1 + width * 3));
  for (let y = 0; y < height; y++) {
    rawData[y * (1 + width * 3)] = 0;
    for (let x = 0; x < width; x++) {
      const px = y * (1 + width * 3) + 1 + x * 3;
      rawData[px] = r; rawData[px + 1] = g; rawData[px + 2] = b;
    }
  }
  const compressed = zlib.deflateSync(rawData);
  
  return Buffer.concat([
    signature,
    createChunk('IHDR', ihdrData),
    createChunk('IDAT', compressed),
    createChunk('IEND', Buffer.alloc(0))
  ]);
}

function ensureDir(dir) {
  if (!fs.existsSync(dir)) fs.mkdirSync(dir, { recursive: true });
}

ensureDir(path.join(__dirname, 'static', 'tabbar'));
ensureDir(path.join(__dirname, 'static', 'icons'));

const icons = [
  ['tabbar/home.png', 153, 153, 153],
  ['tabbar/home-active.png', 74, 144, 217],
  ['tabbar/contest.png', 153, 153, 153],
  ['tabbar/contest-active.png', 74, 144, 217],
  ['tabbar/team.png', 153, 153, 153],
  ['tabbar/team-active.png', 74, 144, 217],
  ['tabbar/message.png', 153, 153, 153],
  ['tabbar/message-active.png', 74, 144, 217],
  ['tabbar/mine.png', 153, 153, 153],
  ['tabbar/mine-active.png', 74, 144, 217],
  ['icons/logo.png', 74, 144, 217],
  ['icons/search.png', 200, 200, 200],
  ['icons/default-avatar.png', 200, 200, 200]
];

icons.forEach(([name, r, g, b]) => {
  fs.writeFileSync(path.join(__dirname, 'static', name), createSolidPng(r, g, b));
  console.log(`  ✅ static/${name}`);
});

// ===== 3. 完成 =====
console.log('\n' + '='.repeat(50));
console.log('🎉 初始化完成！');
console.log('='.repeat(50));
console.log('\n下一步：');
console.log('  1. npm install');
console.log('  2. HBuilderX 打开项目');
console.log('  3. 运行到微信开发者工具');
console.log('\n💡 Mock数据已内置，无需后端即可运行！');
