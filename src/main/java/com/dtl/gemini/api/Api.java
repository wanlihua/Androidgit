package com.dtl.gemini.api;

import android.content.Context;
import android.util.Log;

import com.dtl.gemini.PunkApplication;
import com.dtl.gemini.bean.BaseBean;
import com.dtl.gemini.bean.DataBean;
import com.dtl.gemini.constants.Constant;
import com.dtl.gemini.db.StoreUtils;
import com.dtl.gemini.enums.CfdTypeEnum;
import com.dtl.gemini.ui.asset.beans.AssetCfdBean;
import com.dtl.gemini.ui.asset.beans.AssetWalletBean;
import com.dtl.gemini.ui.asset.beans.ExchangeBean;
import com.dtl.gemini.ui.asset.beans.ExchangeSysOneBean;
import com.dtl.gemini.ui.asset.beans.RechargeBean;
import com.dtl.gemini.ui.asset.beans.TransactionRecordBean;
import com.dtl.gemini.ui.asset.beans.WithdrawalBean;
import com.dtl.gemini.ui.asset.beans.WithdrawalSysBean;
import com.dtl.gemini.ui.asset.model.AssetCfdRecord;
import com.dtl.gemini.ui.asset.model.Recharge;
import com.dtl.gemini.ui.asset.model.Withdrawal;
import com.dtl.gemini.ui.cfd.beans.CurrPriceBean;
import com.dtl.gemini.ui.cfd.beans.MultipleBean;
import com.dtl.gemini.ui.cfd.beans.OrderCreateBean;
import com.dtl.gemini.ui.cfd.beans.OrderHistoryBean;
import com.dtl.gemini.ui.cfd.beans.OrderPenddingBean;
import com.dtl.gemini.ui.cfd.beans.TokenBean;
import com.dtl.gemini.ui.home.beans.LunBoBean;
import com.dtl.gemini.ui.home.beans.NoticesBean;
import com.dtl.gemini.ui.me.beans.UploadBean;
import com.dtl.gemini.ui.me.model.UserChildren;
import com.dtl.gemini.ui.me.model.UserLevel;
import com.dtl.gemini.ui.me.model.UserShareInfo;
import com.dtl.gemini.ui.other.beans.AreaCodeBean;
import com.dtl.gemini.ui.other.beans.LoginBean;
import com.dtl.gemini.ui.other.beans.VersionBean;
import com.dtl.gemini.utils.AndroidUtil;
import com.dtl.gemini.utils.DataUtil;
import com.dtl.gemini.utils.DeviceUtils;
import com.dtl.gemini.utils.DisplayUtil;
import com.dtl.gemini.utils.JsonConvert;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okgo.request.PostRequest;
import com.lzy.okrx.RxAdapter;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import rx.Observable;

/**
 *
 */
public class Api {

    public static volatile Api api;

    private Api() {
    }

    public static Api getInstance() {

        if (api == null) {
            api = new Api();
        }
        return api;
    }

    /**
     * 获取版本信息
     *
     * @param context
     * @return
     */
    public Observable<VersionBean> getAppVersion(Context context) {
        HttpParams params = new HttpParams();
        params.put("platform", Constant.PLATFORMTYPE_ANDROID);
        return getPostRequest(context, Constant.QUERY_VERSION, params).getCall(new JsonConvert<VersionBean>() {
        }, RxAdapter.create());
    }

    /**
     * 获取邀请信息
     *
     * @param context
     * @return
     */
    public Observable<DataBean<UserShareInfo>> getShareInfo(Context context) {
        HttpParams params = new HttpParams();
        return getPostRequest(context, Constant.QUERY_SHARE_USER_INFO, params).getCall(new JsonConvert<DataBean<UserShareInfo>>() {
        }, RxAdapter.create());
    }

    /**
     * 获取系统通知
     *
     * @param context
     * @return
     */
    public Observable<NoticesBean> queryNoticeAll(Context context) {
        HttpParams params = new HttpParams();
        return getPostRequest(context, Constant.QUERY_NOTICE_LIST, params).getCall(new JsonConvert<NoticesBean>() {
        }, RxAdapter.create());
    }

    /**
     * 获取轮播
     *
     * @param context
     * @return
     */
    public Observable<LunBoBean> queryListLunbo(Context context) {
        HttpParams params = new HttpParams();
        return getPostRequest(context, Constant.QUERY_CAROUSEL_LIST, params).getCall(new JsonConvert<LunBoBean>() {
        }, RxAdapter.create());
    }


    /**
     * 发送注册短信验证码
     *
     * @param context
     * @return
     */
    public Observable<BaseBean> sendRegisterSmsCode(Context context, String phone) {
        HttpParams params = new HttpParams();
        params.put("phone", phone);
        return getPostRequest(context, Constant.SEND_REGISTER_SMS_CODE, params).getCall(new JsonConvert<BaseBean>() {
        }, RxAdapter.create());
    }

    /**
     * 获取区号
     *
     * @param context
     * @return
     */
    public Observable<AreaCodeBean> qureyAreaCode(Context context) {
        HttpParams params = new HttpParams();
        return getPostRequest(context, Constant.QUERY_AREA_CODE, params).getCall(new JsonConvert<AreaCodeBean>() {
        }, RxAdapter.create());
    }


    /**
     * 注册
     *
     * @param context
     * @param userName
     * @param phone
     * @param loginPassword
     * @param extractPassword
     * @param registerInvitationCode
     * @param validateCode
     * @return
     */
    public Observable<BaseBean> register(Context context, String userName, String phone, String loginPassword, String extractPassword, String registerInvitationCode, String validateCode) {
        HttpParams params = new HttpParams();
        params.put("userName", userName);
        params.put("phone", phone);
        params.put("loginPassword", loginPassword);
//        params.put("extractPassword", extractPassword);
        params.put("registerInvitationCode", registerInvitationCode);
        params.put("validateCode", validateCode);
        return getPostRequest(context, Constant.REGISTER, params).getCall(new JsonConvert<BaseBean>() {
        }, RxAdapter.create());
    }


    /**
     * 获取登录验证码
     *
     * @param context
     * @return
     */
    public Observable<BaseBean> sendLoginSmsCode(Context context, String phone) {
        HttpParams params = new HttpParams();
        params.put("phone", phone);
        return getPostRequest(context, Constant.SEND_LOGIN_SMS_CODE, params).getCall(new JsonConvert<BaseBean>() {
        }, RxAdapter.create());
    }

    /**
     * 获取重置登录密码验证码
     *
     * @param context
     * @return
     */
    public Observable<BaseBean> sendRestLoginPasswordSmsCode(Context context, String phone) {
        HttpParams params = new HttpParams();
        params.put("phone", phone);
        return getPostRequest(context, Constant.SEND_REST_LOGIN_PWD_SMS_CODE, params).getCall(new JsonConvert<BaseBean>() {
        }, RxAdapter.create());
    }

    /**
     * 用户重置登录密码
     *
     * @param context
     * @return
     */
    public Observable<BaseBean> resetLoginPassword(Context context, String phone, String loginPassword, String validateCode) {
        HttpParams params = new HttpParams();
        params.put("phone", phone);
        params.put("loginPassword", loginPassword);
        params.put("validateCode", validateCode);
        return getPostRequest(context, Constant.RESET_LOGIN_PWD, params).getCall(new JsonConvert<BaseBean>() {
        }, RxAdapter.create());
    }

    /**
     * 获取用户修改登录密码验证码
     *
     * @param context
     * @return
     */
    public Observable<BaseBean> sendUpdateLoginPassword(Context context) {
        HttpParams params = new HttpParams();
        return getPostRequest(context, Constant.SEND_UPDATE_LOGIN_PWD_SMS_CODE, params).getCall(new JsonConvert<BaseBean>() {
        }, RxAdapter.create());
    }

    /**
     * 获取用户修改提币密码验证码
     *
     * @param context
     * @return
     */
    public Observable<BaseBean> sendUpdateExtractPasswordSmsCode(Context context) {
        HttpParams params = new HttpParams();
        return getPostRequest(context, Constant.SEND_UPDATE_ASSET_PWD_SMS_CODE, params).getCall(new JsonConvert<BaseBean>() {
        }, RxAdapter.create());
    }

    /**
     * 用户修改登录密码
     *
     * @param context
     * @return
     */
    public Observable<BaseBean> updateLoginPassword(Context context, String password, String validateCode) {
        HttpParams params = new HttpParams();
        params.put("password", password);
        params.put("validateCode", validateCode);
        return getPostRequest(context, Constant.UPDATE_LOGIN_PWD, params).getCall(new JsonConvert<BaseBean>() {
        }, RxAdapter.create());
    }

    /**
     * 用户修改提币密码
     *
     * @param context
     * @return
     */
    public Observable<BaseBean> updateExtractPassword(Context context, String password, String validateCode) {
        HttpParams params = new HttpParams();
        params.put("password", password);
        params.put("validateCode", validateCode);
        return getPostRequest(context, Constant.UPDATE_ASSET_PWD, params).getCall(new JsonConvert<BaseBean>() {
        }, RxAdapter.create());
    }

    /**
     * 用户修改昵称
     *
     * @param context
     * @return
     */
    public Observable<BaseBean> updateUserNickname(Context context, String nickname) {
        HttpParams params = new HttpParams();
        params.put("nickname", nickname);
        return getPostRequest(context, Constant.UPDATE_USER_NIKENAME, params).getCall(new JsonConvert<BaseBean>() {
        }, RxAdapter.create());
    }

    /**
     * 用户修改头像
     *
     * @param context
     * @param url
     * @return
     */
    public Observable<BaseBean> updateHeadImg(Context context, String url) {
        HttpParams params = new HttpParams();
        params.put("headUrl", url);
        return getPostRequest(context, Constant.UPDATE_USER_HEAD, params).getCall(new JsonConvert<BaseBean>() {
        }, RxAdapter.create());
    }

    /**
     * 获取当前人用户信息
     *
     * @param context
     * @return
     */
    public Observable<LoginBean> queryUserInfo(Context context) {
        HttpParams params = new HttpParams();
        return getPostRequest(context, Constant.QUERY_USER_INFO, params).getCall(new JsonConvert<LoginBean>() {
        }, RxAdapter.create());
    }

    /**
     * 获取用户下级团队
     *
     * @param context
     * @return
     */
    public Observable<DataBean<UserChildren>> getUserChildren(Context context, String phone) {
        HttpParams params = new HttpParams();
        params.put("phone", phone);
        return getPostRequest(context, Constant.QUERY_USER_CHILDREN, params).getCall(new JsonConvert<DataBean<UserChildren>>() {
        }, RxAdapter.create());
    }

    /**
     * 查询用户等级信息
     *
     * @param context
     * @return
     */
    public Observable<DataBean<UserLevel>> getUserLevelByLevel(Context context) {
        HttpParams params = new HttpParams();
        return getPostRequest(context, Constant.QUERY_USER_LEVEL, params).getCall(new JsonConvert<DataBean<UserLevel>>() {
        }, RxAdapter.create());
    }

    /**
     * 购买下一等级扣除USDT
     *
     * @param context
     * @return
     */
    public Observable<BaseBean> purchaseLevel(Context context) {
        HttpParams params = new HttpParams();
        return getPostRequest(context, Constant.PURCHASE_LEVEL, params).getCall(new JsonConvert<BaseBean>() {
        }, RxAdapter.create());
    }

    /**
     * 提交用户认证信息
     *
     * @param context
     * @param realName      真实姓名
     * @param idNumber      身份证号
     * @param idPositiveUrl 身份证正面照路径
     * @param idBackUrl     身份证背面照路径
     * @return
     */
    public Observable<BaseBean> sumbitUserVerified(Context context, String realName, String idNumber, String idPositiveUrl, String idBackUrl) {
        HttpParams params = new HttpParams();
        params.put("realName", realName);
        params.put("idNumber", idNumber);
        params.put("idPositiveUrl", idPositiveUrl);
        params.put("idBackUrl", idBackUrl);
        return getPostRequest(context, Constant.SUMBIT_USER_VERIFIED, params).getCall(new JsonConvert<BaseBean>() {
        }, RxAdapter.create());
    }

    /**
     * 登录
     *
     * @param context
     * @param username 用户名/手机号
     * @param password 密码/ 验证码
     * @param type     1:密码,2:验证码
     * @return
     */
    public Observable<LoginBean> login(Context context, String username, String password, int type) {
        HttpParams params = new HttpParams();
        params.put("username", username);
        params.put("password", password);
        if (type == 1) {//密码
            params.put("type", "password");
        } else {//验证码
            params.put("type", "validateCode");
        }
        return getPostRequest(context, Constant.LOGIN, params).getCall(new JsonConvert<LoginBean>() {
        }, RxAdapter.create());
    }

    /**
     * 退出登录
     *
     * @param context
     * @return
     */
    public Observable<BaseBean> logout(Context context) {
        HttpParams params = new HttpParams();
        return getPostRequest(context, Constant.LOGOUT, params).getCall(new JsonConvert<BaseBean>() {
        }, RxAdapter.create());
    }


    /**
     * 获取钱包资产
     *
     * @param context
     * @return
     */
    public Observable<AssetWalletBean> queryWalletAssets(Context context) {
        HttpParams params = new HttpParams();
        return getPostRequest(context, Constant.QUERY_WALLET_ASSET, params).getCall(new JsonConvert<AssetWalletBean>() {
        }, RxAdapter.create());
    }

    /**
     * 获取用户资产变动记录
     *
     * @param context
     * @param currency
     * @param page
     * @param size
     * @return
     */
    public Observable<TransactionRecordBean> queryTransactionRecord(Context context, String currency, int page, int size) {
        HttpParams params = new HttpParams();
        params.put("currency", currency);
        params.put("pageNum", page);
        params.put("pageSize", size);
        return getPostRequest(context, Constant.QUERY_ASSET_RECORD, params).getCall(new JsonConvert<TransactionRecordBean>() {
        }, RxAdapter.create());
    }

    /**
     * 查询充币地址
     *
     * @param context
     * @param currency
     * @return
     */
    public Observable<DataBean<String>> queryAddress(Context context, String currency) {
        HttpParams params = new HttpParams();
        params.put("currency", currency);
        return getPostRequest(context, Constant.QUERY_ADDRESS, params).getCall(new JsonConvert<DataBean<String>>() {
        }, RxAdapter.create());
    }

    /**
     * 查询充币记录
     *
     * @param context
     * @return
     */
    public Observable<RechargeBean> queryRechargeRecord(Context context, String currency) {
        HttpParams params = new HttpParams();
        params.put("currency", currency);
        return getPostRequest(context, Constant.QUERY_RECHARGE_RECORD, params).getCall(new JsonConvert<RechargeBean>() {
        }, RxAdapter.create());
    }

    /**
     * 充值id获取用户的充值记录
     *
     * @param context
     * @return
     */
    public Observable<DataBean<Recharge>> queryRechargeRecordById(Context context, String id) {
        HttpParams params = new HttpParams();
        params.put("id", id);
        return getPostRequest(context, Constant.QUERY_RECHARGE_ID, params).getCall(new JsonConvert<DataBean<Recharge>>() {
        }, RxAdapter.create());
    }

    /**
     * 获取提币设置
     *
     * @param context
     * @return
     */
    public Observable<WithdrawalSysBean> queryWithdrawalSys(Context context) {
        HttpParams params = new HttpParams();
        return getPostRequest(context, Constant.QUERY_WITHDRAWAL_SYS, params).getCall(new JsonConvert<WithdrawalSysBean>() {
        }, RxAdapter.create());
    }

    /**
     * 获取用户提币验证码
     *
     * @param context
     * @return
     */
    public Observable<BaseBean> sendWithdrawalSmsCode(Context context, String currency, double amount) {
        HttpParams params = new HttpParams();
        params.put("currency", currency);
        params.put("amount", amount);
        return getPostRequest(context, Constant.SEND_WITHDRAWAL_SMS_CODE, params).getCall(new JsonConvert<BaseBean>() {
        }, RxAdapter.create());
    }

    /***
     * 提币
     * @param context
     * @param currency
     * @param amount
     * @param address
     * @param payPassword
     * @return
     */
    public Observable<BaseBean> withdrawal(Context context, String currency, double amount, String address, String code, String payPassword) {
        HttpParams params = new HttpParams();
        params.put("currency", currency);
        params.put("amount", amount);
        params.put("address", address);
        params.put("validateCode", code);
        params.put("extractPwd", payPassword);
        return getPostRequest(context, Constant.WITHDRAWAL, params).getCall(new JsonConvert<BaseBean>() {
        }, RxAdapter.create());
    }

    /**
     * 提币记录
     *
     * @param context
     * @param currency
     * @return
     */
    public Observable<WithdrawalBean> queryWithdrawalRecord(Context context, String currency) {
        HttpParams params = new HttpParams();
        params.put("currency", currency);
        return getPostRequest(context, Constant.WITHDRAWAL_RECORD, params).getCall(new JsonConvert<WithdrawalBean>() {
        }, RxAdapter.create());
    }

    /**
     * 提币id获取用户的提币记录
     *
     * @param context
     * @return
     */
    public Observable<DataBean<Withdrawal>> queryWithdrawalRecordById(Context context, String id) {
        HttpParams params = new HttpParams();
        params.put("id", id);
        return getPostRequest(context, Constant.QUERY_WITHDRAWAL_ID, params).getCall(new JsonConvert<DataBean<Withdrawal>>() {
        }, RxAdapter.create());
    }

    /**
     * 获取兑换设置
     **/
    public Observable<BaseBean> queryExchangeSys(Context context) {
        HttpParams params = new HttpParams();
        return getPostRequest(context, Constant.QUERY_EXCHANGE_ALL, params).getCall(new JsonConvert<BaseBean>() {
        }, RxAdapter.create());
    }

    /**
     * 获取单个交易对兑换设置
     **/
    public Observable<ExchangeSysOneBean> queryExchangeSymbol(Context context, String symbol) {
        HttpParams params = new HttpParams();
        params.put("symbol", symbol);
        return getPostRequest(context, Constant.QUERY_EXCHANGE_SYMBOL, params).getCall(new JsonConvert<ExchangeSysOneBean>() {
        }, RxAdapter.create());
    }

    /**
     * 获取合约所有帐户资产--测试
     **/
    public Observable<DataBean> getCfd(Context context) {
        HttpParams params = new HttpParams();
//        params.put("currency", "USDT");
        return getPostRequest(context, Constant.GET_CFD, params).getCall(new JsonConvert<DataBean>() {
        }, RxAdapter.create());
    }


    /**
     * 兑换
     *
     * @param context
     * @param t1
     * @param t2
     * @param amount
     * @return
     */
    public Observable<BaseBean> exchange(Context context, String t1, String t2, double amount) {
        HttpParams params = new HttpParams();
        params.put("t1", t1);//用于兑换的币种
        params.put("t2", t2);//要兑换的币种
        params.put("amount", amount);//兑换数量
        return getPostRequest(context, Constant.EXCHANGE, params).getCall(new JsonConvert<BaseBean>() {
        }, RxAdapter.create());
    }

    /**
     * 查询兑换记录
     *
     * @param context
     * @param currency
     * @return
     */
    public Observable<ExchangeBean> queryExchangeRecord(Context context, String currency) {
        HttpParams params = new HttpParams();
        params.put("currency", currency);
        return getPostRequest(context, Constant.EXCHANGE_RECORD, params).getCall(new JsonConvert<ExchangeBean>() {
        }, RxAdapter.create());
    }

    /**
     * 资产划转
     *
     * @param context
     * @param amount
     * @param asset
     * @return
     */
    public Observable<BaseBean> assetTransfer(Context context, String amount, String asset, CfdTypeEnum typeEnum) {
        HttpParams params = new HttpParams();
        String url = Constant.TRANSFER_WALLET_TO_CFD;//数字转合约
        if (asset.equals("wallet"))
            url = Constant.TRANSFER_WALLET_TO_CFD;//数字转合约
        if (asset.equals("cfd"))
            url = Constant.TRANSFER_CFD_TO_WALLET;//合约转数字
        params.put("amount", Double.parseDouble(amount));//划转数量
        params.put("accountType", typeEnum.grade);
        return getPostRequest(context, url, params).getCall(new JsonConvert<BaseBean>() {
        }, RxAdapter.create());
    }

    /**
     * 查看合约账户
     *
     * @param context
     * @return
     */
    public Observable<AssetCfdBean> queryCfdAsset(Context context, CfdTypeEnum typeEnum) {
        HttpParams params = new HttpParams();
        params.put("accountType", typeEnum.grade);
        return getPostRequest(context, Constant.QUERY_CFD_ASSET, params).getCall(new JsonConvert<AssetCfdBean>() {
        }, RxAdapter.create());
    }

    /**
     * 查看合约账户记录
     *
     * @param context
     * @return
     */
    public Observable<DataBean<List<AssetCfdRecord>>> queryCfdAssetRecord(Context context, CfdTypeEnum typeEnum) {
        HttpParams params = new HttpParams();
        params.put("accountType", typeEnum.grade);
        return getPostRequest(context, Constant.QUERY_CFD_ASSET_RECORD, params).getCall(new JsonConvert<DataBean<List<AssetCfdRecord>>>() {
        }, RxAdapter.create());
    }

    /**
     * 查可买币种
     *
     * @param context
     * @return
     */
    public Observable<TokenBean> queryCfdTokens(Context context, CfdTypeEnum typeEnum) {
        HttpParams params = new HttpParams();
        params.put("accountType", typeEnum.grade);
        return getPostRequest(context, Constant.QUERY_CFD_TOKENS, params).getCall(new JsonConvert<TokenBean>() {
        }, RxAdapter.create());
    }

    /**
     * 查实时价格
     *
     * @param context
     * @param currency
     * @return
     */
    public Observable<CurrPriceBean> queryCfdCurrPrice(Context context, String currency, CfdTypeEnum typeEnum) {
        HttpParams params = new HttpParams();
        params.put("currency", currency);
        params.put("accountType", typeEnum.grade);
        return getPostRequest(context, Constant.QUERY_CFD_CURR_PRICE, params).getCall(new JsonConvert<CurrPriceBean>() {
        }, RxAdapter.create());
    }

    /**
     * 查手续费
     *
     * @param context
     * @return
     */
    public Observable<CurrPriceBean> queryCfdFee(Context context, CfdTypeEnum typeEnum) {
        HttpParams params = new HttpParams();
        params.put("accountType", typeEnum.grade);
        return getPostRequest(context, Constant.QUERY_CFD_FEE, params).getCall(new JsonConvert<CurrPriceBean>() {
        }, RxAdapter.create());
    }

    /**
     * 查可开倍数
     *
     * @param context
     * @return
     */
    public Observable<MultipleBean> queryCfdMultiple(Context context, CfdTypeEnum typeEnum) {
        HttpParams params = new HttpParams();
        params.put("accountType", typeEnum.grade);
        return getPostRequest(context, Constant.QUERY_CFD_MULTIPLE, params).getCall(new JsonConvert<MultipleBean>() {
        }, RxAdapter.create());
    }

    /**
     * 开仓
     *
     * @param context
     * @param type       1:极速开仓,2:计划开仓
     * @param currency   币种
     * @param trend      1、开多   2、开空
     * @param multiple   倍数
     * @param amount     数量
     * @param price      计划价格
     * @param stopProfit 止盈
     * @param stopLoss   止损
     * @return
     */
    public Observable<OrderCreateBean> cfdOrderCreate(Context context, int type, String currency, int trend, int multiple, double amount, double price, double stopProfit, double stopLoss, CfdTypeEnum typeEnum) {
        HttpParams params = new HttpParams();
        params.put("accountType", typeEnum.grade);
        String url = Constant.CFD_QUICK_CREATE;
        if (type == 1) {
            url = Constant.CFD_QUICK_CREATE;
        } else {
            url = Constant.CFD_PLAN_CREATE;
            params.put("price", price);
        }
        params.put("currency", currency);
        params.put("trend", trend);
        params.put("multiple", multiple);
        params.put("amount", amount);
        if (stopProfit > 0.00)
            params.put("stopProfit", stopProfit);
        if (stopLoss > 0.00)
            params.put("stopLoss", stopLoss);
        return getPostRequest(context, url, params).getCall(new JsonConvert<OrderCreateBean>() {
        }, RxAdapter.create());
    }

    /**
     * 查询持仓记录
     *
     * @param context
     * @return
     */
    public Observable<OrderPenddingBean> queryCfdOrderHold(Context context, CfdTypeEnum typeEnum) {
        HttpParams params = new HttpParams();
        params.put("accountType", typeEnum.grade);
        return getPostRequest(context, Constant.CFD_ORDER_HOLD, params).getCall(new JsonConvert<OrderPenddingBean>() {
        }, RxAdapter.create());
    }

    /**
     * 查询委托单记录
     *
     * @param context
     * @return
     */
    public Observable<OrderPenddingBean> queryCfdOrderPendding(Context context, CfdTypeEnum typeEnum) {
        HttpParams params = new HttpParams();
        params.put("accountType", typeEnum.grade);
        return getPostRequest(context, Constant.CFD_ORDER_PENDDING, params).getCall(new JsonConvert<OrderPenddingBean>() {
        }, RxAdapter.create());
    }

    /**
     * 撤销委托单
     *
     * @param context
     * @param orderId
     * @return
     */
    public Observable<BaseBean> cfdOrderCancel(Context context, String orderId, CfdTypeEnum typeEnum) {
        HttpParams params = new HttpParams();
        params.put("accountType", typeEnum.grade);
        params.put("orderId", orderId);
        return getPostRequest(context, Constant.CFD_ORDER_CANCEL, params).getCall(new JsonConvert<BaseBean>() {
        }, RxAdapter.create());
    }

    /**
     * 平仓
     *
     * @param context
     * @param type    1:闪电 2：计划
     * @param orderId
     * @param amount
     * @param price
     * @return
     */
    public Observable<BaseBean> cfdOrderPlanClose(Context context, int type, String orderId, double amount, double price, CfdTypeEnum typeEnum) {
        HttpParams params = new HttpParams();
        params.put("accountType", typeEnum.grade);
        String url = Constant.CFD_ORDER_QUICK_CLOSE;
        if (type == 1) {
            url = Constant.CFD_ORDER_QUICK_CLOSE;
        } else {
            url = Constant.CFD_ORDER_PLAN_CLOSE;
            params.put("price", price);
        }
        params.put("orderId", orderId);
        params.put("amount", amount);
        return getPostRequest(context, url, params).getCall(new JsonConvert<BaseBean>() {
        }, RxAdapter.create());
    }

    /**
     * 追加保证金
     *
     * @param context  
     * @param orderId
     * @param amount
     * @param typeEnum
     * @return
     */
    public Observable<DataBean> cfdOrderAddBalance(Context context, String orderId, double amount, CfdTypeEnum typeEnum) {
        HttpParams params = new HttpParams();
        params.put("accountType", typeEnum.grade);
        params.put("orderId", orderId);
        params.put("amount", amount);
        return getPostRequest(context, Constant.CFD_ORDER_ADD_BALANCE, params).getCall(new JsonConvert<DataBean>() {
        }, RxAdapter.create());
    }

    /**
     * @param context
     * @param type    1:平仓委托单记录 2：已平仓记录
     * @return
     */
    public Observable<OrderHistoryBean> queryFinishOrder(Context context, int type, CfdTypeEnum typeEnum) {
        HttpParams params = new HttpParams();
        params.put("accountType", typeEnum.grade);
        String url = Constant.QUERY_FINISH_PENDING;
        if (type == 1) {
            url = Constant.QUERY_FINISH_PENDING;
        } else {
            url = Constant.QUERY_FINISH_HISTORY;
        }
        return getPostRequest(context, url, params).getCall(new JsonConvert<OrderHistoryBean>() {
        }, RxAdapter.create());
    }


    /**
     * 取消计划平仓委托
     *
     * @param context
     * @param finishOrderId //撤销的平仓单ID，不是订单ID
     * @return
     */
    public Observable<BaseBean> cfdOrderCancelClose(Context context, String finishOrderId, CfdTypeEnum typeEnum) {
        HttpParams params = new HttpParams();
        params.put("accountType", typeEnum.grade);
        params.put("finishOrderId", finishOrderId);
        return getPostRequest(context, Constant.CFD_ORDER_CANCEL_CLOSE, params).getCall(new JsonConvert<BaseBean>() {
        }, RxAdapter.create());
    }

    /**
     * 上传照片
     */
    public Observable<UploadBean> uploadFile(Context context, File file, String filename) {
        return getFileParamsPostRequest(context, Constant.FILE_UPLOAD, file, filename).getCall(new JsonConvert<UploadBean>() {
        }, RxAdapter.create());
    }

    private PostRequest getPostRequest(Context context, String url, HttpParams params) {
        HttpHeaders headers = new HttpHeaders();
        String token = StoreUtils.init(context).getToken();
        if (token != null && !token.equals("")) {
            headers.put(Constant.Authorization, StoreUtils.init(context).getRongToken());
            headers.put(Constant.X_TOKEN_PARAM, token);
        }
        headers.put(HttpHeaders.HEAD_KEY_ACCEPT_LANGUAGE, DataUtil.returnLanguage(context));
        Log.e("Api", "请求参数：" + params.toString());
        return OkGo.post(url)
                .tag(context)//以对应activity或fragment作为网络请求tag，以便即时取消网络请求
                .headers(headers)
                .params(params);
    }

    private PostRequest getPostRequest(Context context, String url, HashMap<String, Object> params) {
        HttpHeaders headers = new HttpHeaders();
        String token = StoreUtils.init(context).getToken();
        if (token != null && !token.equals("")) {
//            params.put(Constant.TOKEN_PARAM, token);
            headers.put(Constant.X_TOKEN_PARAM, token);
        }
        headers.put(HttpHeaders.HEAD_KEY_ACCEPT_LANGUAGE, DataUtil.returnLanguage(context));
        return OkGo.post(url)
                .tag(context)//以对应activity或fragment作为网络请求tag，以便即时取消网络请求
                .headers(headers)
                .upJson(new Gson().toJson(params));
    }

    /**
     * 一般GET请求
     *
     * @param context
     * @param url
     * @param params
     * @return
     */
    private GetRequest getGetRequest(Context context, String url, HttpParams params) {
        HttpHeaders headers = new HttpHeaders();
        String token = StoreUtils.init(context).getToken();
        if (token != null && !token.equals("")) {
//            params.put(Constant.TOKEN_PARAM, token);
            headers.put(Constant.X_TOKEN_PARAM, token);
        }
        headers.put(HttpHeaders.HEAD_KEY_ACCEPT_LANGUAGE, DataUtil.returnLanguage(context));
        return OkGo.get(url)
                .headers(headers)
                .tag(context)//以对应activity或fragment作为网络请求tag，以便即时取消网络请求
                .params(params);
    }

    /**
     * 一般POST请求-携带附加参数
     *
     * @param context
     * @param url
     * @param params
     * @return
     */
    private PostRequest getPostRequests(Context context, String url, HashMap<String, Object> params) {
        HashMap<String, Object> map = addBaseParams(new HashMap<String, Object>());
        HttpHeaders headers = new HttpHeaders();
        String token = StoreUtils.init(context).getToken();
        if (token != null && !token.equals("")) {
//            params.put(Constant.TOKEN_PARAM, token);
            headers.put(Constant.X_TOKEN_PARAM, token);
        }
        headers.put(HttpHeaders.HEAD_KEY_ACCEPT_LANGUAGE, DataUtil.returnLanguage(context));
        return OkGo.post(url)
                .tag(context)//以对应activity或fragment作为网络请求tag，以便即时取消网络请求
                .headers(headers)
                .upJson(new Gson().toJson(map));
    }

    private PostRequest getPostRequestsNoPara(Context context, String url) {
        return OkGo.post(url).tag(context).upJson("");
    }

    /**
     * 上传文件
     *
     * @param context
     * @param url
     * @return
     */
    private PostRequest getFileParamsPostRequest(Context context, String url, File file, int type, long userId) {
        HttpHeaders headers = new HttpHeaders();
        String token = StoreUtils.init(context).getToken();
        if (token != null && !token.equals("")) {
            headers.put(Constant.X_TOKEN_PARAM, token);
        }
        headers.put(HttpHeaders.HEAD_KEY_ACCEPT_LANGUAGE, DataUtil.returnLanguage(context));
        return OkGo.post(url)
                .isMultipart(true)
                .tag(context)//以对应activity或fragment作为网络请求tag，以便即时取消网络请求
                .params("type", type)
                .params("userId", userId)
                .headers(headers)
                .params("file", file);
    }

    private PostRequest getFileParamsPostRequestVideo(Context context, String url, File file, String videoIndex) {
        return OkGo.post(url)
                .isMultipart(true)
                .tag(context)//以对应activity或fragment作为网络请求tag，以便即时取消网络请求
                .params("token", StoreUtils.init(context).getToken())
                .params("file", file)
                .params(" videoIndex", videoIndex);
    }

    /**
     * 上传文件
     *
     * @param context
     * @param url
     * @param file
     * @param filename
     * @return
     */
    private PostRequest getFileParamsPostRequest(Context context, String url, File file, String filename) {
        String token = StoreUtils.init(context).getToken();
        if (token == null || token.equals(""))
            token = "";
        return OkGo.post(url)
                .isMultipart(true)
                .tag(context)//以对应activity或fragment作为网络请求tag，以便即时取消网络请求
                .headers(Constant.X_TOKEN_PARAM, token)
                .params(filename, file);
    }


    //    文件下载
    public void downLoadFile(Context context, String url, FileCallback fileCallback) {
        OkGo.get(url).tag(context).execute(fileCallback);
    }

    /**
     * 添加网络请求基本参数
     *
     * @param params
     * @return
     */
    public static HashMap<String, Object> addBaseParams(HashMap<String, Object> params) {
        params.put(Constant.PLATFORMTYPE_PARAM,
                Constant.PLATFORMTYPE_ANDROID);
        params.put(Constant.APP_VERSION_PARAM,
                AndroidUtil.getVersionName(PunkApplication.getAppContext()));
        params.put(Constant.PHONE_MODEL_PARAM, DeviceUtils.getModel());
        params.put(Constant.SERVICE_PARAM,
                DeviceUtils.getServiceProvider(PunkApplication.getAppContext()));
        params.put(Constant.NETWORD_PARAM,
                DeviceUtils.getCurrentNetType(PunkApplication.getAppContext()));
        params.put(Constant.APP_CHANNEL_PARAM, null);
        params.put(Constant.APP_SIGN__PARAM, null);
        params.put(Constant.CLIENT_IP_PARAM, null);
        params.put(Constant.IMEI_PARAM, null);
        params.put(Constant.MAC_ADDRESS_PARAM, null);
        params.put(Constant.WIDTH_PARAM, DisplayUtil.getScreenWidth(PunkApplication.getAppContext()));
        params.put(Constant.HEIGHT_PARAM, DisplayUtil.getScreenHeight(PunkApplication.getAppContext()));
        params.put(Constant.TIMESTAMP_PARAM, System.currentTimeMillis());
        return params;
    }

}
