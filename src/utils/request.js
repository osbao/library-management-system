import axios from "axios";
import router from "@/router"
import Cookies from "js-cookie";

const request =axios.create(
    {
        baseURL: 'http://localhost:9090/api',
        timeout: 5000
    }
)

request.interceptors.request.use(config =>{
    config.headers['Content-Type'] = 'application/json;charset=utf-8';
    const adminJson = Cookies.get('admin')
    const userJson = Cookies.get('user')
    //设置请求头
    if(adminJson) {
        config.headers['token'] = JSON.parse(adminJson).token
    } else if(userJson) {
        config.headers['token'] = JSON.parse(userJson).token
    }
    return config;
},  error => {
    return Promise.reject(error);
});

request.interceptors.response.use( response=>{
    let res = response.data;
    if(typeof res == 'string'){
        res = res ? JSON.parse(res) : res;
    }
    if(res.code === '401'){
        router.push('/login')
    }
    return res;
},error =>{
    console.log('err' + error)
    return Promise.reject(error)
})

// 预约相关API
export const reservationApi = {
    // 预约图书
    reserve(data) {
        return request({
            url: '/reservation/reserve',
            method: 'post',
            data
        })
    },
    
    // 取消预约
    cancel(reservationId) {
        return request({
            url: `/reservation/cancel/${reservationId}`,
            method: 'post'
        })
    },
    
    // 查询我的预约列表
    getMyReservations(status) {
        return request({
            url: '/reservation/my',
            method: 'get',
            params: status ? { status } : {}
        })
    },
    
    // 管理员确认取书
    confirm(reservationId) {
        return request({
            url: `/reservation/confirm/${reservationId}`,
            method: 'post'
        })
    },
    
    // 管理员查询待取书列表
    getWaitingPickupList() {
        return request({
            url: '/reservation/waiting-pickup',
            method: 'get'
        })
    }
}

export default request