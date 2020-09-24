package com.dtl.gemini.constants;

import com.blankj.utilcode.BuildConfig;

public class Constant {
    public static long DEFAULT_MILLISECONDS = 30000;

    public static String getServiceUrl() {
//        return "http://192.168.2.39:6666/api"; //  公司本地的服务器
//        return "http://192.168.2.68:8585/api"; //  公司本地的服务器
        return "http://192.168.2.68:6666/api"; //  公司本地的服务器
//        return "http://www.xsgemini.co/api";//生产
    }

//    public static final String KLINE_URL = "http://www.xsgemini.co/api";
//public static final String KLINE_URL = "http://192.168.2.39:6666/api";
    public static final String KLINE_URL = "http://192.168.2.68:6666/api";
//    public static final String KLINE_URL = "http://192.168.2.68:8585/api";
////    public static final String KLINE_URL = "http://192.168.2.68:8585/api";
//    public static final String CFD_IP = "http://cfd.xsgemini.co";
//    public static final String CFD_IP = "http://192.168.2.39:5555";
    public static final String CFD_IP = "http://192.168.2.68:5555";
//    public static final String CFD_IP = "http://192.168.2.68:8585";

//    public static final String CFD_IP = "http://192.168.2.68:8585";

    public static boolean isDebug = !"release".equals(BuildConfig.BUILD_TYPE);

    /**
     * Cache File Path Related
     */
    public static final String FILE = "/GeminiMex";
    public static final String IMG_FILE = "/img";
    public final static String FILEPROVIDER = "gemini.fileprovider";
    public final static String PACKAGE_NAME = "com.dtl.gemini";
    public static final String APP_FOLDER_NAME = "gemini";
    public static final int NOTIFY_ID = 0X100000;
    public static final String PUSH_CHANNEL_ID = "GEMINI_PUSH_NOTIFY_ID";
    public static final String PUSH_CHANNEL_NAME = "GEMINI_PUSH_NOTIFY_NAME";
    public static final String RECEIVER_ERROR_RELOGIN_MAINACTIVITY = "com.dtl.gemini.RECEIVER_ERROR_RELOGIN";
    public static final String NAME_STATUS = "status";
    public static final String ASSET = "asset";
    public static final String CURRENCY = "currency";
    public static final String BUNDLE = "bundle";
    public static final String TYPE = "type";
    public static final String MODEL = "model";
    public static final String FILE_HEAD = "head";
    public static final String FILE_REAL_NAME = "realname_img";

    public static final String DATE_FORMAT_SSS = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final String DATE_FORMAT_SSSZ = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String DATE_FORMAT_ALL = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_MM_DD = "MM/dd HH:mm";
    public static final String DATE_FORMAT_DD = "MM/dd";
    public static final String DATE_FORMAT_HH = "HH:mm";
    public static final String DATE_FORMAT_YY_mm = "yyyy-MM-dd HH:mm";
    public static final String DATE_FORMAT_YY_mms = "yyyy/MM/dd HH:mm";
    public static final int SUCCESS = 200;
    public static final int FAILURE = 401;//失效
    public static final int KLINE_SIZE = 250;//K线数据条数
    public static final int coinScale = 4;//保留小数点几位
    public static final String kefu_email = "sumbi888888@gmail.com";
    /**
     * json request参数
     */
    public static final String PLATFORMTYPE_PARAM = "platform";// 平台类型:android/ios/web
    public static final String APP_VERSION_PARAM = "app_version";//应用版本
    public static final String PHONE_MODEL_PARAM = "phone_model";//手机型号
    public static final String SERVICE_PARAM = "service_provider";//通讯运营商 1：移动、2：电信、3：联通、4：其他
    public static final String NETWORD_PARAM = "network_type";//网络类型 1：wifi、2：3g、3：gprs、4：其它
    public static final String TOKEN_PARAM = "token";//session_token
    public static final String APP_CHANNEL_PARAM = "channel";//APP_CHANNEL_PARAM app 渠道
    public static final String APP_SIGN__PARAM = "sign";//sign app 签名信息
    public static final String CLIENT_IP_PARAM = "client_ip";//ip地址
    public static final String TIMESTAMP_PARAM = "timestamp";//时间戳
    public static final String WIDTH_PARAM = "width";//屏幕宽度
    public static final String HEIGHT_PARAM = "height";//
    public static final String MAC_ADDRESS_PARAM = "mac_Address";//
    public static final String IMEI_PARAM = "imei";//
    public static final String PARAM = "data";//参数数组
    public static final String PLATFORMTYPE_ANDROID = "android";

    public static final String Authorization = "Authorization";//token
    public static final String X_TOKEN_PARAM = "X-Access-Token";//token
//服务条例
    public static final String SERVICE = getServiceUrl() + "/doc/service.html";
    //获取区号
    public static final String QUERY_AREA_CODE = getServiceUrl() + "/app/phone/qureyAreaCode";
    //发送注册短信验证码
    public static final String SEND_REGISTER_SMS_CODE = getServiceUrl() + "/app/user/sendRegisterSmsCode";
    //注册
    public static final String REGISTER = getServiceUrl() + "/app/user/register";

    //获取登录验证码
    public static final String SEND_LOGIN_SMS_CODE = getServiceUrl() + "/app/user/sendLoginSmsCode";

    //获取重置登录密码验证码
    public static final String SEND_REST_LOGIN_PWD_SMS_CODE = getServiceUrl() + "/app/user/sendRestLoginPasswordSmsCode";
    //用户重置登录密码
    public static final String RESET_LOGIN_PWD = getServiceUrl() + "/app/user/resetLoginPassword";
    //获取用户修改登录密码验证码
    public static final String SEND_UPDATE_LOGIN_PWD_SMS_CODE = getServiceUrl() + "/app/user/sendUpdateLoginPassword";
    //获取用户修改提币密码验证码
    public static final String SEND_UPDATE_ASSET_PWD_SMS_CODE = getServiceUrl() + "/app/user/sendUpdateExtractPasswordSmsCode";
    //用户修改登录密码
    public static final String UPDATE_LOGIN_PWD = getServiceUrl() + "/app/user/updateLoginPassword";
    //用户修改提币密码
    public static final String UPDATE_ASSET_PWD = getServiceUrl() + "/app/user/updateExtractPassword";
    //用户修改昵称
    public static final String UPDATE_USER_NIKENAME = getServiceUrl() + "/app/user/updateUserNickname";
    //用户修改头像
    public static final String UPDATE_USER_HEAD = getServiceUrl() + "/app/user/updateUserHeadImg";
    //获取当前人用户信息
    public static final String QUERY_USER_INFO = getServiceUrl() + "/app/user/getUserInfo";
    //获取用户下级团队    过了
    public static final String QUERY_USER_CHILDREN = getServiceUrl() + "/app/userLevel/getUserChildren";
    //查询用户等级信息     //没有通过，找数据库   找到了   我要审及
    public static final String QUERY_USER_LEVEL = getServiceUrl() + "/app/userLevel/getUserLevelByLevel";
    //购买下一等级扣除USDT
    public static final String PURCHASE_LEVEL = getServiceUrl() + "/app/user/assets/purchaseLevel";
    //提交用户认证信息
    public static final String SUMBIT_USER_VERIFIED = getServiceUrl() + "/app/user/sumbitUserVerified";
    //用户登录
    public static final String LOGIN = getServiceUrl() + "/app/login";
    //退出登录
    public static final String LOGOUT = getServiceUrl() + "/app/logout";
    //获取轮播图
    public static final String QUERY_CAROUSEL_LIST = getServiceUrl() + "/app/carousel/list";
    //获取通知消息
    public static final String QUERY_NOTICE_LIST = getServiceUrl() + "/app/notice/list";
    //获取版本信息     打通
    public static final String QUERY_VERSION = getServiceUrl() + "/app/version/details";
    //获取邀请信息   打通
    public static final String QUERY_SHARE_USER_INFO = getServiceUrl() + "/app/user/getShareInfo";
    //获取用户充币地址
    public static final String QUERY_ADDRESS = getServiceUrl() + "/app/user/assets/getUserAddress";
    //获取用户充币记录    充币  在钱包的充币里面
    public static final String QUERY_RECHARGE_RECORD = getServiceUrl() + "/app/user/assets/getRechargeRecord";
    //充值id获取用户的充值记录    在钱包的充币里面
    public static final String QUERY_RECHARGE_ID = getServiceUrl() + "/app/user/assets/getRechargeRecordById";
    //获取钱包资产  打通
    public static final String QUERY_WALLET_ASSET = getServiceUrl() + "/app/user/assets/getUserAssets";
    //获取用户资产变动记录   钱包里面的数据接口
    public static final String QUERY_ASSET_RECORD = getServiceUrl() + "/app/user/assets/getRecord";
    //获取提币设置
    public static final String QUERY_WITHDRAWAL_SYS = getServiceUrl() + "/app/withdrawal/findAllOfApp";
    //获取用户提币验证码   打通
    public static final String SEND_WITHDRAWAL_SMS_CODE = getServiceUrl() + "/app/user/assets/sendExtractValidateCode";
    //提币
    public static final String WITHDRAWAL = getServiceUrl() + "/app/user/assets/extract";
    //提币记录
    public static final String WITHDRAWAL_RECORD = getServiceUrl() + "/app/user/assets/withdrawalRecord";
    //提币id获取用户的提币记录    没有打通
    public static final String QUERY_WITHDRAWAL_ID = getServiceUrl() + "/app/user/assets/getWithdrawalRecordById";
    //兑换
    public static final String EXCHANGE = getServiceUrl() + "/app/user/assets/exchange";
    //兑换记录
    public static final String EXCHANGE_RECORD = getServiceUrl() + "/app/user/assets/getExchangeRecord";
    //获取兑换设置
    public static final String QUERY_EXCHANGE_ALL = getServiceUrl() + "/app/exchange/findAllOfApp";
    //获取单个交易对兑换设置  没有找到
    public static final String QUERY_EXCHANGE_SYMBOL = getServiceUrl() + "/app/exchange/findBySymbol";
    //文件上传form表单格式   没有找到
    public static final String FILE_UPLOAD = getServiceUrl() + "/file/upload";
    //获取合约所有帐户资产--测试  没有找到
    public static final String GET_CFD = getServiceUrl() + "/app/user/assets/getCfd";

    //查看合约账户  在合约的  1  和2    第2个框架  首页的双仓合约和自由合约的接口   利用typeEnum.grade的值 1和2 来区分
    public static final String QUERY_CFD_ASSET = CFD_IP + "/cfd/account/query";

    //查看合约账户记录    钱包里面的合约账号冻结
    public static final String QUERY_CFD_ASSET_RECORD = CFD_IP + "/cfd/account/record";
    //钱包账户划转合约账户   合约账号到自由账号   这两个下面有一个大的bug   注册账号生存三个表格后台的数据需要手动删除
    public static final String TRANSFER_WALLET_TO_CFD = CFD_IP + "/cfd/account/walletToCfd";

    //合约账户划转钱包账户     上面两个是钱包到的两种价格   还没有找到
    public static final String TRANSFER_CFD_TO_WALLET = CFD_IP + "/cfd/account/cfdToWallet";
    //查实时价格
    public static final String QUERY_CFD_CURR_PRICE = CFD_IP + "/cfd/realtimePrice";
    //查手续费
    public static final String QUERY_CFD_FEE = CFD_IP + "/cfd/fee";
    //查可买币种   调同  首页的
    public static final String QUERY_CFD_TOKENS = CFD_IP + "/cfd/tokens";
    //查可开倍数   调通
    public static final String QUERY_CFD_MULTIPLE = CFD_IP + "/cfd/multiple";
    //快速下单
    public static final String CFD_QUICK_CREATE = CFD_IP + "/cfd/quick/create";
    //计划下单   计划下单的接口 调通  计划开多
    public static final String CFD_PLAN_CREATE = CFD_IP + "/cfd/plan/create";


    //持仓记录    在自由合约的持仓
    public static final String CFD_ORDER_HOLD = CFD_IP + "/cfd/order/hold";
    //委托单记录    在合约的上面的平仓和持仓
    public static final String CFD_ORDER_PENDDING = CFD_IP + "/cfd/order/pendding";
    //撤销委托单
    public static final String CFD_ORDER_CANCEL = CFD_IP + "/cfd/order/cancle";


    //急速平仓
    public static final String CFD_ORDER_QUICK_CLOSE = CFD_IP + "/cfd/order/quickClose";
    //计划平仓  找到
    public static final String CFD_ORDER_PLAN_CLOSE = CFD_IP + "/cfd/order/planClose";
    //追加保证金
    public static final String CFD_ORDER_ADD_BALANCE = CFD_IP + "/cfd/order/appendBalance";
    //平仓委托单记录    调试通过
    public static final String QUERY_FINISH_PENDING = CFD_IP + "/cfd/finishOrder/pendding";
    //已平仓记录  调式通过

    public static final String QUERY_FINISH_HISTORY = CFD_IP + "/cfd/finishOrder/history";
    //取消计划平仓委托
    public static final String CFD_ORDER_CANCEL_CLOSE = CFD_IP + "/cfd/order/cancleClose";
    //取消计划平仓委托
    public static final String CFD_GET_TOKEN = CFD_IP + "/test/setToken?userId=08dd35396ffd4b7d";


    //获取 火币 24小时成交量数据    调试首页的但是自己的界面没有调试好
    public static final String HUOBI_GET_DETAILS = KLINE_URL + "/huobi/getDetails";


    //获取 火币 K线数据
    public static final String HUOBI_GET_KLINE = KLINE_URL + "/huobi/getKline";
    //获取火币合约指数信息
    public static final String HUOBI_GET_CONTRACT = KLINE_URL + "/huobi/getHuobiContract";
    //获取火币合约深度信息
    public static final String HUOBI_GET_DEPTH = KLINE_URL + "/huobi/getHuobiContractDepth";
    //获取火币行情深度信息
    public static final String HUOBI_GET_DEPTH_V2 = KLINE_URL + "/huobi/getHuobiContractDepthV2";
    //获取火币合约聚合行情       这个没有
    public static final String HUOBI_GET_TICKER = KLINE_URL + "/huobi/getHuobiContractTicker";

}
