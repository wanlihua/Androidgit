package com.dtl.gemini.db;


import android.content.Context;
import android.content.SharedPreferences;
import com.dtl.gemini.ui.home.model.Market;
import java.util.ArrayList;
import java.util.List;

public class MarketStoreUtils {

    private static MarketStoreUtils utils;

    Context context;
    SharedPreferences preferences;

    private MarketStoreUtils(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences("home_market", Context.MODE_PRIVATE);
    }

    public static MarketStoreUtils init(Context context) {
        if (utils == null) {
            utils = new MarketStoreUtils(context);
        }
        return utils;
    }

    public void setParameter(String key, String value) {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public String getParameter(String name) {
        return preferences.getString(name, null);
    }

    public boolean addMarket(Market bean) {
        String markets = getParameter("markets");
        if (markets == null || markets.equals("")) {
            setParameter("markets", "#" + bean.getTokenId() + ";" + bean.getCurrency() + ";"
                    + bean.getVol() + ";" + bean.getUs() + ";" + bean.getCny() + ";"
                    + bean.getGain() + ";" + bean.getGains()+ ";" + bean.getMode());
            return true;
        }
        if (markets.contains(bean.getCurrency())) {
            return false;
        }
        markets += "#" + bean.getTokenId() + ";" + bean.getCurrency() + ";"
                + bean.getVol() + ";" + bean.getUs() + ";" + bean.getCny() + ";"
                + bean.getGain() + ";" + bean.getGains()+ ";" + bean.getMode();
        setParameter("markets", markets);
        return true;
    }

    public void deleteMarket(String currency) {
        String markets = getParameter("markets");
        if (markets == null || markets.equals("") || !markets.contains("#") || !markets.contains(";")) {
            return;
        }

        List<Market> marketList = new ArrayList<>();
        String[] w = markets.split("#");
        for (String s : w) {
            if (s.contains(";")) {
                String[] v = s.split(";");
                if (v.length == 8) {
                    Market d = new Market();
                    d.setTokenId(Integer.parseInt(v[0]));
                    d.setCurrency(v[1]);
                    d.setVol(v[2]);
                    d.setUs(v[3]);
                    d.setCny(v[4]);
                    d.setGain(Double.parseDouble(v[5]));
                    d.setGains(v[6]);
                    d.setMode(Integer.parseInt(v[7]));
                    marketList.add(d);
                }
            }
        }

        if (marketList.size() == 0) {
            return;
        }

        setParameter("markets", "");

        for (Market mb : marketList) {
            if (mb.getCurrency().equals(currency)) {
                continue;
            }
            addMarket(mb);
        }
    }

    public Market getMarket(String currency) {
        String markets = getParameter("markets");
        if (markets == null || markets.equals("") || !markets.contains("#") || !markets.contains(";")) {
            return null;
        }
        String[] w = markets.split("#");
        for (String s : w) {
            if (s.contains(";")) {
                String[] v = s.split(";");
                if (v.length == 8) {
                    if (currency.equals(v[1])) {
                        Market d = new Market();
                        d.setTokenId(Integer.parseInt(v[0]));
                        d.setCurrency(v[1]);
                        d.setVol(v[2]);
                        d.setUs(v[3]);
                        d.setCny(v[4]);
                        d.setGain(Double.parseDouble(v[5]));
                        d.setGains(v[6]);
                        d.setMode(Integer.parseInt(v[7]));
                        return d;
                    }
                }
            }
        }
        return null;
    }

    public List<Market> getMarkets() {
        String markets = getParameter("markets");
        if (markets == null || markets.equals("") || !markets.contains("#") || !markets.contains(";")) {
            return null;
        }
        List<Market> mbs = new ArrayList<>();
        String[] w = markets.split("#");
        for (String s : w) {
            if (s.contains(";")) {
                String[] v = s.split(";");
                if (v.length == 8) {
                    Market d = new Market();
                    d.setTokenId(Integer.parseInt(v[0]));
                    d.setCurrency(v[1]);
                    d.setVol(v[2]);
                    d.setUs(v[3]);
                    d.setCny(v[4]);
                    d.setGain(Double.parseDouble(v[5]));
                    d.setGains(v[6]);
                    d.setMode(Integer.parseInt(v[7]));
                    mbs.add(d);
                }
            }
        }
        return mbs;
    }
}
