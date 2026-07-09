import Vue from 'vue'
import VueRouter from 'vue-router'
import Layout from '../views/Layout.vue'
import Cookies from "js-cookie";

Vue.use(VueRouter)
const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err)
}
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: ()=>import('/src/views/login/Login.vue')
  },
  {
    path: '/sign',
    name: 'Sign',
    component: ()=>import('/src/views/login/Sign.vue')
  },
  {
    path: '/',
    name: 'Layout',
    component: Layout,
    redirect:'/home',
    meta: { requiresAuth: true, role: 'admin' },
    children:[
      { path: 'home', name: 'Home', component: ()=>import('/src/views/home/HomeView.vue')},
      // parking spots
      { path: 'parkingSpotList', name: 'ParkingSpotList', component: () => import('@/views/parkingSpot/List.vue')},
      { path: 'addParkingSpot', name: 'AddParkingSpot', component: () => import('@/views/parkingSpot/Add.vue')},
      { path: 'editParkingSpot', name: 'EditParkingSpot', component: () => import('@/views/parkingSpot/Edit.vue')},
      // spot types
      { path: 'spotTypeList', name: 'SpotTypeList', component: () => import('@/views/spotType/List.vue')},
      { path: 'addSpotType', name: 'AddSpotType', component: () => import('@/views/spotType/Add.vue')},
      { path: 'editSpotType', name: 'EditSpotType', component: () => import('@/views/spotType/Edit.vue')},
      // drivers
      { path: 'driverList', name: 'DriverList', component: () => import('@/views/driver/List.vue')},
      { path: 'addDriver', name: 'AddDriver', component: () => import('@/views/driver/Add.vue')},
      { path: 'editDriver', name: 'EditDriver', component: () => import('@/views/driver/Edit.vue')},
      // parking records
      { path: 'parkingRecordList', name: 'ParkingRecordList', component: () => import('@/views/parkingRecord/List.vue')},
      { path: 'addParkingRecord', name: 'AddParkingRecord', component: () => import('@/views/parkingRecord/Add.vue')},
      { path: 'editParkingRecord', name: 'EditParkingRecord', component: () => import('@/views/parkingRecord/Edit.vue')},
      // exit records
      { path: 'exitRecordList', name: 'ExitRecordList', component: () => import('@/views/exitRecord/List.vue')},
      // reservations
      { path: 'spotReservationList', name: 'SpotReservationList', component: () => import('@/views/spotReservation/List.vue')},
      // admin
      { path: 'adminList', name: 'AdminList', component: () => import('@/views/admin/List.vue')},
      { path: 'addAdmin', name: 'AddAdmin', component: () => import('@/views/admin/Add.vue')},
      { path: 'editAdmin', name: 'EditAdmin', component: () => import('@/views/admin/Edit.vue')},
      { path: 'adminProfile', name: 'AdminProfile', component: () => import('@/views/admin/AdminProfile.vue')},
      // garage report
      { path: 'garageReport', name: 'GarageReport', component: () => import('@/views/garageReport/Index.vue')},
    ]
  },
  {
    path: '/userHome',
    name: 'UserLayout',
    component: () => import('@/views/UserLayout.vue'),
    redirect: '/userHome/home',
    meta: { requiresAuth: true, role: 'user' },
    children: [
      { path: 'home', name: 'UserHome', component: () => import('@/views/home/UserHome.vue')},
      { path: 'userProfile', name: 'UserProfile', component: () => import('@/views/user/UserProfile.vue')},
    ]
  },
  {
    path: '*',
    component: ()=>import('@/views/404.vue')
  },
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

router.beforeEach((to, from, next) => {
  console.log(`导航守卫触发: ${from.path} -> ${to.path}`)
  const publicPaths = ['/login', '/sign']
  if (publicPaths.includes(to.path)) {
    console.log('访问开放路径，直接放行')
    return next()
  }
  const admin = Cookies.get("admin")
  const user = Cookies.get("user")
  if (!admin && !user) {
    console.log('未认证用户，重定向到登录页')
    return next("/login")
  }
  if (to.meta.role === 'admin' && !admin) {
    console.log('需要管理员权限，但当前是用户，重定向到用户首页')
    return next('/userHome')
  }
  if (to.meta.role === 'user' && !user) {
    console.log('需要用户权限，但当前是管理员，重定向到管理员首页')
    return next('/')
  }
  console.log('已认证用户，允许访问')
  next()
})
export default router