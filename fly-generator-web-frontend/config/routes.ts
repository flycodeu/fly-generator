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
  { path: '/user/settings', component: './User/Settings',icon: 'user',name: '个人设置' },
  {
    path: '/admin',
    icon: 'crown',
    name: '管理页',
    access: 'canAdmin',
    routes: [
      { icon: 'table', path: '/admin/user', component: './Admin/User', name: '用户管理' },
      { icon: 'table', path: '/admin/generator', component: './Admin/Generator', name: '代码管理' }
    ],
  },
  { path: '/', redirect: '/welcome' },
  { path: '*', layout: false, component: './404' },
];
