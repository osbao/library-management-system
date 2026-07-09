// 修复 pages.json 格式问题的脚本
// 在 competition-mini 目录运行: node fix-pages.js

const fs = require('fs');
const path = require('path');

const filePath = path.join(__dirname, 'pages.json');

try {
  let content = fs.readFileSync(filePath, 'utf8');

  // 修复 login 条目后的多余闭合括号
  // 当前问题:  login 条目关闭了两次 "    }\n    },\n    {"
  // 正确应为: "    },\n    {"
  const pattern = /("navigationStyle":\s*"custom"\s*\n\s*\}\s*\n)\s*\}\s*\n(\s*\},\s*\n\s*\{)/;
  const replacement = '$1    },\n    {';
  
  if (pattern.test(content)) {
    content = content.replace(pattern, replacement);
    console.log('✅ 已修复 login 条目后的多余闭合');
  } else {
    // 尝试另一种修复方式 - 直接处理已知的行结构
    const lines = content.split(/\r?\n/);
    let fixed = [];
    let skipNext = false;
    
    for (let i = 0; i < lines.length; i++) {
      if (skipNext) {
        skipNext = false;
        continue;
      }
      
      const line = lines[i];
      // 检测: 前一行是单独的 "    }" （没有逗号），当前行是 "    },"，下一行是 "    {"
      if (/^\s*\}\s*$/.test(line) && 
          i + 2 < lines.length &&
          /^\s*\},\s*$/.test(lines[i + 1]) &&
          /^\s*\{\s*$/.test(lines[i + 2])) {
        // 合并为 "    },"
        fixed.push('    },');
        skipNext = true; // 跳过下一行 "    },"
        continue;
      }
      fixed.push(line);
    }
    
    content = fixed.join('\n');
    console.log('✅ 已通过逐行分析修复');
  }

  fs.writeFileSync(filePath, content, 'utf8');
  
  // 验证 JSON
  JSON.parse(content);
  console.log('✅ JSON 格式验证通过!');
  console.log('✅ pages.json 修复成功!');
} catch (e) {
  console.error('❌ 修复失败:', e.message);
  if (e.message.includes('JSON')) {
    console.error('请手动检查 pages.json 第 115-118 行的 JSON 语法');
  }
}
