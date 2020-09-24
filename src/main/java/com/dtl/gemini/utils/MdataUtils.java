package com.dtl.gemini.utils;

/***
 **时间：2018/10/8
 ***/
public class MdataUtils {
    //资产刷新
    public static final int ASSET_REFERSH = 0x1001;
    //资产隐藏
    public static final int ASSET_SEE = 0x1002;
    //资产提币
    public static final int ASSET_WITHDRAW = 0x1003;
    //资产兑换
    public static final int ASSET_EXCHANGE = 0x1004;
    //资产划转
    public static final int ASSET_TRANSFER = 0x1005;

    //合约开仓刷新
    public static final int CFD_OPEN_REFERSH = 0x1006;
    //合约开仓刷新完毕
    public static final int CFD_OPEN_REFERSH_STOP = 0x1007;
    //合约持仓刷新
    public static final int CFD_HOLD_REFERSH = 0x1008;
    //合约持仓刷新完毕
    public static final int CFD_HOLD_REFERSH_STOP = 0x1009;
    //合约记录刷新
    public static final int CFD_RECORD_REFERSH = 0x10010;
    //合约记录刷新完毕
    public static final int CFD_RECORD_REFERSH_STOP = 0x10011;
    //合约开仓加载
    public static final int CFD_OPEN_LOAD = 0x10012;
    //合约持仓加载
    public static final int CFD_HOLD_LOAD = 0x1013;
    //合约记录加载
    public static final int CFD_RECORD_LOAD = 0x1014;
    //交易刷新
    public static final int CFD_TRANSACTION_REFERSH = 0x1015;
    //交易刷新完毕
    public static final int CFD_TRANSACTION_REFERSH_STOP = 0x1016;
    //交易刷新完毕
    public static final int CFD_KLINE_TO_TRANSACTION = 0x1017;
    //倍数获取完毕
    public static final int CFD_MULTIPLE = 0x1018;
    //币种获取完毕
    public static final int CFD_TOKEN = 0x1019;
    //获取合约资产
    public static final int ASSET_CFD_REFERSH = 0x1020;
}
