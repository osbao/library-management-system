// Mock请求拦截器 - 延迟初始化，确保uni已就绪
import mockRequest from './mock.js';
const USE_MOCK = true;

function initMock() {
  if (typeof uni === 'undefined') return;
  const _request = uni.request;
  uni.request = function(options) {
    if (typeof options === 'string') options = { url: options };
    const url = options.url || '';
    const method = (options.method || 'GET').toLowerCase();
    const data = options.data || null;
    return mockRequest(url, method, data).then(function(res) {
      if (options.success) options.success({ data: res, statusCode: 200 });
      if (options.complete) options.complete({ data: res, statusCode: 200 });
      return [null, { data: res }];
    }).catch(function(err) {
      if (options.fail) options.fail(err);
      if (options.complete) options.complete(err);
      return [err, null];
    });
  };
}

// 延迟初始化，给uni-app框架时间准备
if (USE_MOCK) {
  try { initMock(); } catch(e) { setTimeout(initMock, 100); }
}

export default {};

