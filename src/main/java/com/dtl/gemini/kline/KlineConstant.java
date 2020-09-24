package com.dtl.gemini.kline;

/**
 * @author ZLL
 * @date 2020/4/11
 **/
public class KlineConstant {

    //黄金、白银k线图
    public static final String QUERY_METAL = "http://stock2.finance.sina.com.cn/futures/api/json.php/IndexService.getInnerFuturesMiniKLine";
    //外汇
    public static final String QUERY_WAIHUI = "https://vip.stock.finance.sina.com.cn/forex/api/jsonp.php/var/NewForexService.getMinKline?symbol=fx_s";
    //道琼斯/标普指数/纳斯达克
    public static final String QUERY_DBN = "https://stock.finance.sina.com.cn/usstock/api/jsonp_v2.php/var/US_MinKService.getMinK?symbol=";
    //上证指数/深证成指/创业板指
    public static final String QUERY_SSC = "http://money.finance.sina.com.cn/quotes_service/api/json_v2.php/CN_MarketData.getKLineData?symbol=";
    //恒生
    public static final String QUERY_HS = "https://quotes.sina.cn/hk/api/openapi.php/HK_MinlineService.getMinline?symbol=HSI&day=";
    //其它K线
    public static final String OTHER_KLINE1 = "https://stock.finance.sina.com.cn/usstock/api/jsonp_v2.php/var/US_MinKService.getMinK?symbol=";
    //其它K线
    public static final String OTHER_KLINE2 = "https://quotes.sina.cn/hk/api/openapi.php/HK_MinlineService.getMinline?symbol=";
    //其它K线
    public static final String OTHER_KLINE3 = "https://quotes.sina.cn/cn/api/jsonp_v2.php/var/CN_MarketDataService.getKLineData?symbol=";
    //其它K线
    public static final String OTHER_KLINE4 = "https://stock.finance.sina.com.cn/usstock/api/jsonp_v2.php/var/US_MinKService.getMinK?symbol=";
    //其它K线
    public static final String OTHER_KLINE5 = "https://stock2.finance.sina.com.cn/futures/api/jsonp.php/var/InnerFuturesNewService.getFewMinLine?symbol=";
    //其它K线
    public static final String OTHER_KLINE6 = "https://stock2.finance.sina.com.cn/futures/api/openapi.php/GlobalFuturesService.getGlobalFuturesMinLine?symbol=";
    //其它K线
    public static final String OTHER_KLINE7 = "https://vip.stock.finance.sina.com.cn/forex/api/jsonp.php/var/NewForexService.getMinKline?symbol=";
    //涨幅
    public static final String QUERY_PRICE = "http://hq.sinajs.cn/list=";
    //涨幅2
    public static final String QUERY_PRICE2 = "https://hq.sinajs.cn/rn=";

    //火币价格-所有
    public static final String QUERY_HUOBI_PRICE = "https://api.huobi.pro/market/tickers";
    //火币价格-单个
    public static final String QUERY_HUOBI_PRICE_ONE = "https://api.huobi.pro/market/detail?symbol=";
    //火币价格
//    public static final String QUERY_HUOBI_PRICE = "https://www.huobi.io/-/x/pro/market/overview5";
    //非小号价格
    public static final String QUERY_FXH_PRICE = "http://dncapi.bqiapp.com/api/coin/web-coinrank?page=1&type=-1&pagesize=10&webp=1";


    //    1min, 5min, 15min, 30min, 60min, 4hour, 1day, 1mon, 1week, 1year
    //火币K线
    public static final String QUERY_HUOBI_KLINE = "https://api.huobi.pro/market/history/kline?symbol=";
    //币安K线
    public static final String QUERY_BIAN_KLINE = "https://api.yshyqxx.com/api/v1/klines?limit=1000&symbol=";
    //币安K线-单条
    public static final String QUERY_BIAN_KLINE_ONE = "https://api.yshyqxx.com/api/v1/klines?limit=1&symbol=";
    //OKEX K线
    public static final String QUERY_OKEX_KLINE = "https://www.okex.me/v2/spot/instruments/";
}
