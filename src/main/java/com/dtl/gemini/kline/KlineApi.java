package com.dtl.gemini.kline;

import android.content.Context;
import android.util.Log;

import com.dtl.gemini.MainActivity;
import com.dtl.gemini.constants.Constant;
import com.dtl.gemini.kline.beans.MarketBean;
import com.dtl.gemini.utils.DataUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;

import java.util.Date;

import okhttp3.Call;
import okhttp3.Response;

/**
 * @author DTL
 * @date 2020/6/13
 * 差价合约其它K线数据
 **/
public class KlineApi {
    /**
     * K线数据
     */
    public static void queryMetal(Context context, String time, String type, KlineApiImpl impl) {
        KlineApiImpl cfdKlineApiImpl = impl;
        try {
            String url = "";
            if (type.equals("AU0") || type.equals("AG0") || type.equals("hf_CL"))//黄金/白银
                url = KlineConstant.QUERY_METAL + time + "?symbol=" + type;
            if (type.equals("USDJPY") || type.equals("USDCHF") || type.equals("USDCAD") ||
                    type.equals("EURUSD") || type.equals("GBPUSD") || type.equals("AUDUSD") || type.equals("NZDUSD"))//外汇
                url = KlineConstant.QUERY_WAIHUI + DataUtil.toLower(type) + "&scale=" + time + "&datalen=600";
            if (type.equals("int_dji"))//道琼斯
                url = KlineConstant.QUERY_DBN + ".DJI&type=" + time.replace("m", "");
            if (type.equals("int_sp500"))//标普指数
                url = KlineConstant.QUERY_DBN + ".INX&type=" + time.replace("m", "");
            if (type.equals("int_nasdaq"))//纳斯达克
                url = KlineConstant.QUERY_DBN + ".IXIC&type=" + time.replace("m", "");
            if (type.equals("gb_jpxn"))//日经
                url = KlineConstant.QUERY_DBN + "JPXN&type=" + time.replace("m", "");
            if (type.equals("s_sh000001") || type.equals("s_sz399001") || type.equals("s_sz399006"))//上证指数/深证成指/创业板指
                url = KlineConstant.QUERY_SSC + type.substring(type.indexOf("_") + 1, type.length()) + "&scale=" + time.replace("m", "") + "&ma=1" + "&datalen=600";
            if (type.equals("int_hangseng"))//恒生
                url = KlineConstant.QUERY_HS + time;
            OkGo.get(url)
                    .tag(context)  // 请求方式和请求url
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                            cfdKlineApiImpl.onError(e.getMessage() + "");
                        }

                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            cfdKlineApiImpl.onSuccess(s + "");
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * 价格
     */
    public static void queryPrice(Context context, String goodNmae, String goodId, int goodType, KlineApiImpl impl) {
        KlineApiImpl cfdKlineApiImpl = impl;
        try {
            String url = "";
            long time = new Date().getTime();
            String url2 = KlineConstant.QUERY_PRICE2 + time + "&list=";
            if (goodType == 1) {//加密货币
                url = KlineConstant.QUERY_PRICE + goodId;
            } else if (goodType == 2) {//股指
                if (goodId.equals(".dji")) {//道琼斯
                    url = url2 + "gb_$dji";
                } else if (goodId.equals(".ixic")) {//纳斯达克
                    url = url2 + "gb_ixic";
                } else if (goodId.equals(".inx")) {//标普500指数
                    url = url2 + "gb_$inx";
                } else if (goodId.equals("hsi")) {//恒生
                    url = url2 + "rt_hkHSI";
                } else if (goodId.equals("sz399001")) {//深证成份指数
                    url = url2 + "sz399001";
                } else if (goodId.equals("sh000001")) {//上证综合指数
                    url = url2 + "sh000001";
                } else if (goodId.equals("sz399006")) {//创业板指数P
                    url = url2 + "sz399006";
                } else if (goodId.equals("b_NKY")) {//日经指数
                    url = url2 + "b_NKY";
                }
            } else if (goodType == 3) {//金属
                if (goodId.equals("oil"))
                    url = url2 + "hf_" + DataUtil.toUpper(goodId);
                else
                    url = url2 + DataUtil.toUpper(goodId);
            } else if (goodType == 4) {//外汇
                url = url2 + "fx_s" + goodId;
            } else if (goodType == 5) {//股票
                if (DataUtil.isNumeric(goodId))
                    url = url2 + "rt_hk" + goodId;
                else
                    url = url2 + "gb_" + goodId;//美股
            }
            OkGo.get(url)
                    .tag(context)  // 请求方式和请求url
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                            cfdKlineApiImpl.onError(e.getMessage() + "");
                        }

                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            String strs = s;
                            String data = strs.substring(strs.indexOf('"') + 1, strs.lastIndexOf('"'));
                            String[] array = data.split(",");
                            //振幅计算（最高-最低）/昨收盘*100
                            //涨幅计算 （现价-昨收）/昨收*100
                            double price = 0.00, gain = 0.00;
                            String gains = "---", hight = "---", low = "---", vol = "---";
                            if (goodType == 2) {
                                if (goodId.equals(".dji") || goodId.equals(".ixic") || goodId.equals(".inx")) {//道琼斯、纳斯达克、标普500指数
                                    price = Double.parseDouble(array[1]);//最新价
                                    gain = Double.parseDouble(array[4]);//涨跌额
                                    gains = DataUtil.numberTwo(Double.parseDouble(array[2]));//涨幅
                                    hight = DataUtil.numberTwo(Double.parseDouble(array[6]));//最高价
                                    low = DataUtil.numberTwo(Double.parseDouble(array[7]));//最低价
                                    vol = DataUtil.numberTwo(Double.parseDouble(array[10]));//成交
                                } else if (goodId.equals("hsi")) {//恒生
                                    price = Double.parseDouble(array[6]);//最新价
                                    gain = Double.parseDouble(array[7]);//涨跌额
                                    gains = DataUtil.numberTwo(Double.parseDouble(array[8]));//涨幅
                                    hight = DataUtil.numberTwo(Double.parseDouble(array[4]));//最高价
                                    low = DataUtil.numberTwo(Double.parseDouble(array[5]));//最低价
                                    vol = DataUtil.numberTwo(Double.parseDouble(array[10]));//成交
                                } else if (goodId.equals("sz399001") || goodId.equals("sh000001") || goodId.equals("sz399006")) {//深证成份指数、上证综合指数、创业板指数P
                                    price = Double.parseDouble(array[3]);//最新价
                                    gain = price - Double.parseDouble(array[2]);//涨跌额   今收盘-昨收盘
                                    gains = DataUtil.numberTwo(gain / Double.parseDouble(array[2]) * 100);//涨幅
                                    hight = DataUtil.numberTwo(Double.parseDouble(array[4]));//最高价
                                    low = DataUtil.numberTwo(Double.parseDouble(array[5]));//最低价
                                    vol = "---";//成交
                                } else if (goodId.equals("b_NKY")) {//日经指数
                                    price = Double.parseDouble(array[1]);//最新价
                                    gain = Double.parseDouble(array[2]);//涨跌额
                                    gains = DataUtil.numberTwo(Double.parseDouble(array[3]));//涨幅
                                    hight = DataUtil.numberTwo(Double.parseDouble(array[8]));//最高价
                                    low = DataUtil.numberTwo(Double.parseDouble(array[9]));//最低价
                                    vol = "---";//成交
                                }
                            } else if (goodType == 3) {//金属
                                if (goodId.equals("oil")) {
                                    price = Double.parseDouble(array[0]);//最新价
                                    gain = price - Double.parseDouble(array[7]);//涨跌额   今收盘-昨收盘
                                    gains = DataUtil.numberTwo(gain / Double.parseDouble(array[7]) * 100);//涨幅
                                    hight = DataUtil.numberTwo(Double.parseDouble(array[4]));//最高价
                                    low = DataUtil.numberTwo(Double.parseDouble(array[5]));//最低价
                                    vol = "---";//成交
                                } else {
                                    price = Double.parseDouble(array[8]);//最新价
                                    gain = price - Double.parseDouble(array[10]);//涨跌额   今收盘-昨收盘
                                    gains = DataUtil.numberTwo(gain / Double.parseDouble(array[10]) * 100);//涨幅
                                    hight = DataUtil.numberTwo(Double.parseDouble(array[3]));//最高价
                                    low = DataUtil.numberTwo(Double.parseDouble(array[4]));//最低价
                                    vol = DataUtil.numberTwo(Double.parseDouble(array[13]));//成交
                                }
                            } else if (goodType == 4) {//外汇
                                if (array.length >= 2)
                                    price = Double.parseDouble(array[2]);
                                if (array.length >= 11)
                                    gain = Double.parseDouble(array[11]);//涨跌额
                                if (array.length >= 10)
                                    gains = DataUtil.numberTwo(Double.parseDouble(array[10]));//涨幅
                                if (array.length >= 6)
                                    hight = DataUtil.numberTwo(Double.parseDouble(array[6]));//最高价
                                if (array.length >= 7)
                                    low = DataUtil.numberTwo(Double.parseDouble(array[7]));//最低价
                                vol = "---";//成交
                            } else if (goodType == 5) {//股票
                                if (DataUtil.isNumeric(goodId)) {
                                    price = Double.parseDouble(array[6]) * MainActivity.usd;
                                    gain = Double.parseDouble(array[7]);//涨跌额
                                    gains = DataUtil.numberTwo(Double.parseDouble(array[8]));//涨幅
                                    hight = DataUtil.numberTwo(Double.parseDouble(array[4]));//最高价
                                    low = DataUtil.numberTwo(Double.parseDouble(array[5]));//最低价
                                    vol = DataUtil.numberTwo(Double.parseDouble(array[12]));//成交
                                } else {//美股
                                    price = Double.parseDouble(array[1]) * MainActivity.usd;
                                    gain = Double.parseDouble(array[4]);//涨跌额
                                    gains = DataUtil.numberTwo(Double.parseDouble(array[2]));//涨幅
                                    hight = DataUtil.numberTwo(Double.parseDouble(array[6]));//最高价
                                    low = DataUtil.numberTwo(Double.parseDouble(array[7]));//最低价
                                    vol = DataUtil.numberTwo(Double.parseDouble(array[10]));//成交量
                                }
                            } else {
                                gain = 0.00;//涨跌额
                                gains = "---";//涨幅
                            }
                            MarketBean marketBeans = new MarketBean(goodNmae, goodId, DataUtil.numberTwo(price), gains, gain, hight, low, vol);
                            cfdKlineApiImpl.onSuccess(marketBeans);
                        }
                    });
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * K线数据-股票
     */
    public static void queryMetals(Context context, String url, KlineApiImpl impl) {
        KlineApiImpl cfdKlineApiImpl = impl;
        try {
            OkGo.get(url)
                    .tag(context)  // 请求方式和请求url
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                            cfdKlineApiImpl.onError(e.getMessage() + "");
                        }

                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            cfdKlineApiImpl.onSuccess(s + "");
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public static void queryMetals(Context context, String goodId, int goodType, String time, KlineApiImpl impl) {
        KlineApiImpl cfdKlineApiImpl = impl;
        String url = "";

        if (goodType == 2) {//股指
            if (goodId.equals(".dji") || goodId.equals(".ixic") || goodId.equals(".inx")) {//道琼斯、纳斯达克、标普500指数
                url = KlineConstant.OTHER_KLINE4 + goodId + "&type=" + time.replaceAll("min", "");
            } else if (goodId.equals("hsi")) {//恒生
//                url = KlineConstant.OTHER_KLINE2 + goodId + "&day=" + time.replaceAll("min", "");
            } else if (goodId.equals("sz399001") || goodId.equals("sh000001") || goodId.equals("sz399006")) {//深证成份指数、上证综合指数、创业板指数P
                url = KlineConstant.OTHER_KLINE3 + goodId + "&scale=" + time.replaceAll("min", "") + "&ma=no&datalen=2000";
            } else if (goodId.equals("b_NKY")) {//日经指数

            }
        } else if (goodType == 3) {//原油贵金属
            if (goodId.equals("oil")) {
                url = KlineConstant.OTHER_KLINE6 + DataUtil.toUpper(goodId);
            } else {
                url = KlineConstant.OTHER_KLINE5 + DataUtil.toUpper(goodId) + "&type=" + time.replaceAll("min", "");
            }
        } else if (goodType == 4) {//外汇
            goodId = "fx_s" + goodId;
            url = KlineConstant.OTHER_KLINE7 + goodId + "&scale=" + time.replaceAll("min", "") + "&datalen=2000";
        } else if (goodType == 5) {//股票
            if (DataUtil.isNumeric(goodId)) {
                url = KlineConstant.OTHER_KLINE2 + goodId + "&day=" + time.replaceAll("min", "");
            } else {
                url = KlineConstant.OTHER_KLINE1 + goodId + "&type=" + time.replaceAll("min", "");
            }
        }
        try {
            OkGo.get(url)
                    .tag(context)  // 请求方式和请求url
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                            cfdKlineApiImpl.onError(e.getMessage() + "");
                        }

                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            cfdKlineApiImpl.onSuccess(s + "");
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * 火币价格
     */
    public static void queryHuoBiPrice(Context context, KlineApiImpl impl) {
        KlineApiImpl cfdKlineApiImpl = impl;
        try {
            OkGo.get(KlineConstant.QUERY_HUOBI_PRICE)
                    .tag(context)  // 请求方式和请求url
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                            cfdKlineApiImpl.onError(e.getMessage() + "");
                        }

                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            cfdKlineApiImpl.onSuccess(s + "");
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * 火币价格
     *
     * @param context
     * @param currency
     * @param impl
     */
    public static void queryHuoBiPriceOne(Context context, String currency, KlineApiImpl impl) {
        KlineApiImpl cfdKlineApiImpl = impl;
        String url = KlineConstant.QUERY_HUOBI_PRICE_ONE + DataUtil.toLower(currency) + "usdt";
        try {
            OkGo.get(url)
                    .tag(context)  // 请求方式和请求url
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                            cfdKlineApiImpl.onError(e.getMessage() + "");
                        }

                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            cfdKlineApiImpl.onSuccess(s + "");
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * 非小号价格
     */
    public static void queryFxhPrice(Context context, KlineApiImpl impl) {
        KlineApiImpl cfdKlineApiImpl = impl;
        try {
            OkGo.get(KlineConstant.QUERY_FXH_PRICE)
                    .tag(context)  // 请求方式和请求url
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                            cfdKlineApiImpl.onError(e.getMessage() + "");
                        }

                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            cfdKlineApiImpl.onSuccess(s + "");
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * 火币K线
     *
     * @param context
     * @param currency 币种
     * @param time     时间
     * @param size     条数
     * @param impl
     */
    public static void queryHuobiKline(Context context, String currency, String time, int size, KlineApiImpl impl) {
        KlineApiImpl cfdKlineApiImpl = impl;
        if (time.equals("1hour")) {
            time = "60min";
        } else if (time.equals("1month")) {
            time = "1mon";
        }
        String url = KlineConstant.QUERY_HUOBI_KLINE + DataUtil.toLower(currency) + "usdt&period=" + time + "&size=" + size;
        Log.e("火币K线", url);
        try {
            OkGo.get(url)
                    .tag(context)  // 请求方式和请求url
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                            cfdKlineApiImpl.onError(e.getMessage() + "");
                        }

                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            cfdKlineApiImpl.onSuccess(s + "");
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * 火币K线-日K单条
     *
     * @param context
     * @param currency 币种
     * @param impl
     */
    public static void queryHuobiDayK(Context context, String currency, KlineApiImpl impl) {
        KlineApiImpl cfdKlineApiImpl = impl;
        String url = KlineConstant.QUERY_HUOBI_KLINE + DataUtil.toLower(currency) + "usdt&period=1day&size=1";
        try {
            OkGo.get(url)
                    .tag(context)  // 请求方式和请求url
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                            cfdKlineApiImpl.onError(e.getMessage() + "");
                        }

                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            cfdKlineApiImpl.onSuccess(s + "");
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * 币安K线
     *
     * @param context
     * @param currency 币种
     * @param time     时间
     * @param impl
     */
    public static void queryBiAnKline(Context context, String currency, String time, KlineApiImpl impl) {
        KlineApiImpl cfdKlineApiImpl = impl;
        String times = "1m";
        if (time.equals("1min")) {
            times = "1m";
        } else if (time.equals("5min")) {
            times = "5m";
        } else if (time.equals("30min")) {
            times = "30m";
        } else if (time.equals("60min")) {
            times = "1h";
        } else if (time.equals("1day")) {
            times = "1d";
        } else if (time.equals("1week")) {
            times = "1w";
        } else if (time.equals("1mon")) {
            times = "1M";
        }
        String url = KlineConstant.QUERY_BIAN_KLINE + currency + "USDT&interval=" + times;
        try {
            OkGo.get(url)
                    .tag(context)  // 请求方式和请求url
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                            cfdKlineApiImpl.onError(e.getMessage() + "");
                        }

                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            cfdKlineApiImpl.onSuccess(s + "");
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    /**
     * 币安K线-单条
     *
     * @param context
     * @param currency 币种
     * @param time     时间
     * @param impl
     */
    public static void queryBiAnKlineOne(Context context, String currency, String time, KlineApiImpl impl) {
        KlineApiImpl cfdKlineApiImpl = impl;
        String times = "1m";
        if (time.equals("1min")) {
            times = "1m";
        } else if (time.equals("5min")) {
            times = "5m";
        } else if (time.equals("30min")) {
            times = "30m";
        } else if (time.equals("60min")) {
            times = "1h";
        } else if (time.equals("1day")) {
            times = "1d";
        } else if (time.equals("1week")) {
            times = "1w";
        } else if (time.equals("1mon")) {
            times = "1M";
        }
        String url = KlineConstant.QUERY_BIAN_KLINE_ONE + currency + "USDT&interval=" + times;
        try {
            OkGo.get(url)
                    .tag(context)  // 请求方式和请求url
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                            cfdKlineApiImpl.onError(e.getMessage() + "");
                        }

                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            cfdKlineApiImpl.onSuccess(s + "");
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * OKEX K线
     *
     * @param context
     * @param currency 币种
     * @param time     时间
     * @param impl
     */
    public static void queryOkexKline(Context context, String currency, String time, KlineApiImpl impl) {
        KlineApiImpl cfdKlineApiImpl = impl;
        long times = 60;
        if (time.equals("1min")) {
            times = 60 * 1;
        } else if (time.equals("5min")) {
            times = 60 * 2;
        } else if (time.equals("30min")) {
            times = 60 * 30;
        } else if (time.equals("1hour")) {
            times = 60 * 60;
        } else if (time.equals("1day")) {
            times = 60 * 60 * 24;
        } else if (time.equals("1week")) {
            times = 60 * 60 * 24 * 7;
        } else if (time.equals("1month")) {
            times = 60 * 60 * 24 * 30;
        }
        String url = KlineConstant.QUERY_OKEX_KLINE + currency + "-USDT/candles?granularity=" + times + "&size=1000&t=" + System.currentTimeMillis();
        try {
            OkGo.get(url)
                    .tag(context)  // 请求方式和请求url
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                            cfdKlineApiImpl.onError(e.getMessage() + "");
                        }

                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            cfdKlineApiImpl.onSuccess(s + "");
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    /**
     * 火币成交量---自己服务器获取
     *
     * @param context
     * @param currency 币种
     * @param impl
     */
    public static void getHuobiDetails(Context context, String currency, KlineApiImpl impl) {
        Log.e("KlineApi", "数据的接口 =" + currency + "");
        KlineApiImpl cfdKlineApiImpl = impl;
        HttpParams params = new HttpParams();

        params.put("symbol", DataUtil.toLower(currency) + "usdt");
        try {
            OkGo.post(Constant.HUOBI_GET_DETAILS)
                    .params(params)
                    .tag(context)  // 请求方式和请求url
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                            cfdKlineApiImpl.onError(e.getMessage() + "");
                        }

                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            if (s != null && !s.equals(""))
                                cfdKlineApiImpl.onSuccess(s + "");
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * 火币K线---自己服务器获取
     *
     * @param context
     * @param currency 币种
     * @param time     时间
     * @param size     条数
     * @param impl
     */
    public static void getHuobiKline(Context context, String currency, String time, int size, KlineApiImpl impl) {
        KlineApiImpl cfdKlineApiImpl = impl;
        if (time.equals("1hour")) {
            time = "60min";
        } else if (time.equals("1month")) {
            time = "1mon";
        }
        HttpParams params = new HttpParams();
        params.put("symbol", DataUtil.toLower(currency) + "usdt");
        params.put("timer", time);
        params.put("size", size);
        try {
            OkGo.post(Constant.HUOBI_GET_KLINE)
                    .params(params)
                    .tag(context)  // 请求方式和请求url
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                            cfdKlineApiImpl.onError(e.getMessage() + "");
                        }

                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            if (s != null && !s.equals(""))
                                cfdKlineApiImpl.onSuccess(s + "");
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * 获取火币合约指数信息---自己服务器获取
     *
     * @param context
     * @param impl
     */
    public static void getHuobiContract(Context context, String symbol, KlineApiImpl impl) {
        KlineApiImpl cfdKlineApiImpl = impl;
        HttpParams params = new HttpParams();
        params.put("symbol", symbol);
        try {
            OkGo.post(Constant.HUOBI_GET_CONTRACT)
                    .params(params)
                    .tag(context)  // 请求方式和请求url
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                            cfdKlineApiImpl.onError(e.getMessage() + "");
                        }

                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            if (s != null && !s.equals(""))
                                cfdKlineApiImpl.onSuccess(s + "");
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    /**
     * 获取火币合约深度信息---自己服务器获取
     *
     * @param context
     * @param impl
     */
    public static void getHuobiContractDepth(Context context, String symbol, String type, KlineApiImpl impl) {
        KlineApiImpl cfdKlineApiImpl = impl;
        HttpParams params = new HttpParams();
        params.put("symbol", symbol);
        params.put("type", type);
        try {
            OkGo.post(Constant.HUOBI_GET_DEPTH)
                    .params(params)
                    .tag(context)  // 请求方式和请求url
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                            cfdKlineApiImpl.onError(e.getMessage() + "");
                        }

                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            if (s != null && !s.equals(""))
                                cfdKlineApiImpl.onSuccess(s + "");
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * 获取火币行情深度信息---自己服务器获取
     *
     * @param context
     * @param impl
     */
    public static void getHuobiContractDepthV2(Context context, String symbol, String type, KlineApiImpl impl) {
        KlineApiImpl cfdKlineApiImpl = impl;
        HttpParams params = new HttpParams();
        params.put("symbol", symbol);
        params.put("type", type);
        try {
            OkGo.post(Constant.HUOBI_GET_DEPTH_V2)
                    .params(params)
                    .tag(context)  // 请求方式和请求url
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                            cfdKlineApiImpl.onError(e.getMessage() + "");
                        }

                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            if (s != null && !s.equals(""))
                                cfdKlineApiImpl.onSuccess(s + "");
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

}
