export default [
  {
    path: '/user',
    layout: false,
    routes: [
      { path: '/user/login', component: './User/Login' },
      { path: '/user/register', component: './User/Register' },
    ],
  },
  { path: '/', icon: 'home', component: './Index', name: '主页' },

  {
    path: '/generator/add',
    icon: 'plus',
    component: './Generator/Add',
    name: '创建生成器',
  },
  {
    path: '/generator/update',
    icon: 'plus',
    component: './Generator/Add',
    name: '修改生成器',
    hideInMenu: true,
  },

  { path: '/user/generator', component: './User/Generator', icon: 'user', name: '我的生成器' },
  { path: '/user/settings', component: './User/Settings', icon: 'user', name: '个人设置' },
  {
    path: '/test/file',
    icon: 'home',
    component: './Test/File',
    name: '文件上传下载测试',
    hideInMenu: true,
  },

  {
    path: '/generator/detail/:id',
    icon: 'home',
    component: './Generator/Detail',
    name: '修改生成器',
    hideInMenu: true,
  },
  {
    path: '/generator/use/:id',
    icon: 'home',
    component: './Generator/Use',
    name: '使用生成器',
    hideInMenu: true,
  },
  {
    path: '/admin',
    icon: 'crown',
    name: '管理页',
    access: 'canAdmin',
    routes: [
      { icon: 'table', path: '/admin/user', component: './Admin/User', name: '用户管理' },
      { icon: 'table', path: '/admin/generator', component: './Admin/Generator', name: '代码管理' },
    ],
  },
  { path: '/', redirect: '/welcome' },
  { path: '*', layout: false, component: './404' },
];
