
var isReady=false;var onReadyCallbacks=[];
var isServiceReady=false;var onServiceReadyCallbacks=[];
var __uniConfig = {"pages":["pages/main/main","pages/pwd/pwd","pages/user/user","pages/user/login","pages/user/reg","pages/lend/main","pages/lend/details","pages/borrow/main","pages/lend/openStorage","pages/lend/confirmStorage","pages/common/success","pages/lend/lendLog","pages/lend/confirmLend","pages/lend/lendSuccess","pages/user/account","pages/user/claims","pages/user/progress","pages/user/pay","pages/user/withdrawal","pages/user/confirmWithdrawal","pages/user/lendLog","pages/borrow/borrow","pages/borrow/audit","pages/borrow/repayment","pages/borrow/confirmRepay","pages/borrow/log","pages/borrow/loanDet","pages/borrow/borrowPlan","pages/borrow/borrowDet","pages/user/myLendLog","pages/user/identityAuth"],"window":{"navigationStyle":"custom","navigationBarTextStyle":"white","navigationBarBackgroundColor":"#0faeff","backgroundColor":"#fbf9fe"},"tabBar":{"color":"#7a7e83","selectedColor":"#0faeff","backgroundColor":"#ffffff","list":[{"pagePath":"pages/main/main","text":"首页","iconPath":"static/img/tabBar_bor.png","selectedIconPath":"static/img/tabBar_bor_act.png"},{"pagePath":"pages/lend/main","text":"出借","iconPath":"static/img/tabBar_lend.png","selectedIconPath":"static/img/tabBar_lend_act.png"},{"pagePath":"pages/borrow/borrow","text":"借钱","iconPath":"static/img/tabBar_bor.png","selectedIconPath":"static/img/tabBar_bor_act.png"},{"pagePath":"pages/user/user","text":"我的","iconPath":"static/img/tabBar_user.png","selectedIconPath":"static/img/tabBar_user_act.png"}]},"networkTimeout":{"request":10000,"connectSocket":10000,"uploadFile":10000,"downloadFile":10000},"nvueCompiler":"uni-app","nvueStyleCompiler":"weex","renderer":"auto","splashscreen":{"alwaysShowBeforeRender":true,"autoclose":false},"appname":"万信P2P金融平台","compilerVersion":"3.3.11","entryPagePath":"pages/main/main"};
var __uniRoutes = [{"path":"/pages/main/main","meta":{"isQuit":true,"isTabBar":true},"window":{"navigationBarTitleText":"首页"}},{"path":"/pages/pwd/pwd","meta":{},"window":{"navigationBarTitleText":"找回密码"}},{"path":"/pages/user/user","meta":{"isQuit":true,"isTabBar":true},"window":{"navigationBarTitleText":"我的"}},{"path":"/pages/user/login","meta":{},"window":{"navigationBarTitleText":"登录"}},{"path":"/pages/user/reg","meta":{},"window":{"navigationBarTitleText":"注册"}},{"path":"/pages/lend/main","meta":{"isQuit":true,"isTabBar":true},"window":{"navigationBarTitleText":"出借"}},{"path":"/pages/lend/details","meta":{},"window":{"navigationBarTitleText":"出借详情"}},{"path":"/pages/borrow/main","meta":{},"window":{"navigationBarTitleText":"借钱"}},{"path":"/pages/lend/openStorage","meta":{},"window":{"navigationBarTitleText":"开通存管"}},{"path":"/pages/lend/confirmStorage","meta":{},"window":{"navigationBarTitleText":"确认开通存管"}},{"path":"/pages/common/success","meta":{},"window":{"navigationBarTitleText":"开户成功！"}},{"path":"/pages/lend/lendLog","meta":{},"window":{"navigationBarTitleText":"出借日志！"}},{"path":"/pages/lend/confirmLend","meta":{},"window":{"navigationBarTitleText":"确认出借！"}},{"path":"/pages/lend/lendSuccess","meta":{},"window":{"navigationBarTitleText":"出借成功！"}},{"path":"/pages/user/account","meta":{},"window":{"navigationBarTitleText":"账户余额！"}},{"path":"/pages/user/claims","meta":{},"window":{"navigationBarTitleText":"债权！"}},{"path":"/pages/user/progress","meta":{},"window":{"navigationBarTitleText":"收款进度！"}},{"path":"/pages/user/pay","meta":{},"window":{"navigationBarTitleText":"充值！"}},{"path":"/pages/user/withdrawal","meta":{},"window":{"navigationBarTitleText":"提现！"}},{"path":"/pages/user/confirmWithdrawal","meta":{},"window":{"navigationBarTitleText":"确认提现！"}},{"path":"/pages/user/lendLog","meta":{},"window":{"navigationBarTitleText":"我的出借记录！"}},{"path":"/pages/borrow/borrow","meta":{"isQuit":true,"isTabBar":true},"window":{"navigationBarTitleText":"借款首页！"}},{"path":"/pages/borrow/audit","meta":{},"window":{"navigationBarTitleText":"审核中！"}},{"path":"/pages/borrow/repayment","meta":{},"window":{"navigationBarTitleText":"还款！"}},{"path":"/pages/borrow/confirmRepay","meta":{},"window":{"navigationBarTitleText":"确认还款！"}},{"path":"/pages/borrow/log","meta":{},"window":{"navigationBarTitleText":"借还记录！"}},{"path":"/pages/borrow/loanDet","meta":{},"window":{"navigationBarTitleText":"还款详情！"}},{"path":"/pages/borrow/borrowPlan","meta":{},"window":{"navigationBarTitleText":"还款计划！"}},{"path":"/pages/borrow/borrowDet","meta":{},"window":{"navigationBarTitleText":"借款详情！"}},{"path":"/pages/user/myLendLog","meta":{},"window":{"navigationBarTitleText":"债权出借记录！"}},{"path":"/pages/user/identityAuth","meta":{},"window":{"navigationBarTitleText":"身份认证！"}}];
__uniConfig.onReady=function(callback){if(__uniConfig.ready){callback()}else{onReadyCallbacks.push(callback)}};Object.defineProperty(__uniConfig,"ready",{get:function(){return isReady},set:function(val){isReady=val;if(!isReady){return}const callbacks=onReadyCallbacks.slice(0);onReadyCallbacks.length=0;callbacks.forEach(function(callback){callback()})}});
__uniConfig.onServiceReady=function(callback){if(__uniConfig.serviceReady){callback()}else{onServiceReadyCallbacks.push(callback)}};Object.defineProperty(__uniConfig,"serviceReady",{get:function(){return isServiceReady},set:function(val){isServiceReady=val;if(!isServiceReady){return}const callbacks=onServiceReadyCallbacks.slice(0);onServiceReadyCallbacks.length=0;callbacks.forEach(function(callback){callback()})}});
service.register("uni-app-config",{create(a,b,c){if(!__uniConfig.viewport){var d=b.weex.config.env.scale,e=b.weex.config.env.deviceWidth,f=Math.ceil(e/d);Object.assign(__uniConfig,{viewport:f,defaultFontSize:Math.round(f/20)})}return{instance:{__uniConfig:__uniConfig,__uniRoutes:__uniRoutes,global:void 0,window:void 0,document:void 0,frames:void 0,self:void 0,location:void 0,navigator:void 0,localStorage:void 0,history:void 0,Caches:void 0,screen:void 0,alert:void 0,confirm:void 0,prompt:void 0,fetch:void 0,XMLHttpRequest:void 0,WebSocket:void 0,webkit:void 0,print:void 0}}}});
