// 生成占位图标 - 在 competition-mini 目录运行: node scripts/generate-icons.js
const fs = require('fs');
const path = require('path');

// 最小 40x40 蓝色 PNG (base64)
function makePng(r, g, b, a) {
  // 创建一个最小的 1x1 像素 PNG，会被拉伸到需要的大小
  // 实际用 canvas 更好，但这里用预生成的 base64
  const png = Buffer.from([
    0x89,0x50,0x4E,0x47,0x0D,0x0A,0x1A,0x0A, // PNG signature
    0x00,0x00,0x00,0x0D,0x49,0x48,0x44,0x52, // IHDR chunk
    0x00,0x00,0x00,0x28,0x00,0x00,0x00,0x28, // 40x40
    0x08,0x02,0x00,0x00,0x00,                  // 8-bit RGB
    0x00,0x00,0x00,                            // CRC placeholder
  ]);
  return png;
}

// 简单做法：创建纯色 40x40 PNG
function createSolidPng(r, g, b) {
  const width = 40, height = 40;
  // PNG 文件头
  const signature = Buffer.from([137,80,78,71,13,10,26,10]);
  
  // IHDR
  const ihdrData = Buffer.alloc(13);
  ihdrData.writeUInt32BE(width, 0);
  ihdrData.writeUInt32BE(height, 4);
  ihdrData[8] = 8;  // bit depth
  ihdrData[9] = 2;  // color type (RGB)
  ihdrData[10] = 0; ihdrData[11] = 0; ihdrData[12] = 0;
  const ihdr = createChunk('IHDR', ihdrData);
  
  // IDAT - raw image data (每行: filter byte + RGB*width)
  const rawData = Buffer.alloc(height * (1 + width * 3));
  for (let y = 0; y < height; y++) {
    const offset = y * (1 + width * 3);
    rawData[offset] = 0; // filter: none
    for (let x = 0; x < width; x++) {
      const px = offset + 1 + x * 3;
      rawData[px] = r; rawData[px+1] = g; rawData[px+2] = b;
    }
  }
  const zlib = require('zlib');
  const compressed = zlib.deflateSync(rawData);
  const idat = createChunk('IDAT', compressed);
  
  // IEND
  const iend = createChunk('IEND', Buffer.alloc(0));
  
  return Buffer.concat([signature, ihdr, idat, iend]);
}

function createChunk(type, data) {
  const length = Buffer.alloc(4);
  length.writeUInt32BE(data.length, 0);
  const typeAndData = Buffer.concat([Buffer.from(type), data]);
  const crc = crc32(typeAndData);
  const crcBuf = Buffer.alloc(4);
  crcBuf.writeUInt32BE(crc >>> 0, 0);
  return Buffer.concat([length, typeAndData, crcBuf]);
}

function crc32(buf) {
  let crc = 0xFFFFFFFF;
  for (let i = 0; i < buf.length; i++) {
    crc ^= buf[i];
    for (let j = 0; j < 8; j++) {
      crc = (crc >>> 1) ^ (crc & 1 ? 0xEDB88320 : 0);
    }
  }
  return (crc ^ 0xFFFFFFFF);
}

// 确保目录存在
function ensureDir(dir) {
  if (!fs.existsSync(dir)) fs.mkdirSync(dir, { recursive: true });
}

const baseDir = path.join(__dirname, '..');

// 生成 tabbar 图标
const tabbarIcons = [
  { name: 'home', color: [153,153,153], activeColor: [74,144,217] },
  { name: 'contest', color: [153,153,153], activeColor: [74,144,217] },
  { name: 'team', color: [153,153,153], activeColor: [74,144,217] },
  { name: 'message', color: [153,153,153], activeColor: [74,144,217] },
  { name: 'mine', color: [153,153,153], activeColor: [74,144,217] }
];

ensureDir(path.join(baseDir, 'static', 'tabbar'));
ensureDir(path.join(baseDir, 'static', 'icons'));

console.log('正在生成图标...');

tabbarIcons.forEach(icon => {
  const normalFile = path.join(baseDir, 'static', 'tabbar', `${icon.name}.png`);
  const activeFile = path.join(baseDir, 'static', 'tabbar', `${icon.name}-active.png`);
  
  fs.writeFileSync(normalFile, createSolidPng(...icon.color));
  console.log(`  ✓ static/tabbar/${icon.name}.png`);
  fs.writeFileSync(activeFile, createSolidPng(...icon.activeColor));
  console.log(`  ✓ static/tabbar/${icon.name}-active.png`);
});

// 生成其他图标
fs.writeFileSync(path.join(baseDir, 'static', 'icons', 'logo.png'), createSolidPng(74, 144, 217));
console.log('  ✓ static/icons/logo.png');
fs.writeFileSync(path.join(baseDir, 'static', 'icons', 'search.png'), createSolidPng(200, 200, 200));
console.log('  ✓ static/icons/search.png');
fs.writeFileSync(path.join(baseDir, 'static', 'icons', 'default-avatar.png'), createSolidPng(200, 200, 200));
console.log('  ✓ static/icons/default-avatar.png');

console.log('\n✅ 所有图标已生成! (纯色占位图标，可后续替换)');
