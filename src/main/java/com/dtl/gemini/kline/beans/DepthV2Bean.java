package com.dtl.gemini.kline.beans;

import com.dtl.gemini.kline.model.Depth;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 合约指数信息
 *
 * @author DTL
 * @date 2020/5/19
 **/
@Getter
@Setter
public class DepthV2Bean {

    /**
     * ch : market.btcusdt.depth.step2
     * status : ok
     * ts : 1596442720886
     * tick : {"bids":[[11220,4.7552],[11219,0.153181],[11218,2.885067],[11217,2.643217],[11216,4.094525],[11215,2.589344],[11214,2.785712],[11213,5.867382],[11212,1.489276],[11211,1.489124],[11210,0.40771],[11209,2.372436],[11208,3.49176],[11207,0.351367],[11206,1.598863],[11205,3.966733],[11204,2.184722],[11203,6.999985],[11202,1.272451],[11201,3.452845]],"asks":[[11221,0.21781991055091865],[11222,0.015],[11223,0.512502],[11224,1.5395],[11225,0.559775],[11226,8.456484],[11227,6.635187],[11228,4.053182],[11229,5.920909],[11230,3.028424],[11231,4.407925],[11232,0.486195],[11233,5.485974],[11234,2.100768],[11235,0.61743],[11236,1.562897],[11237,8.678711],[11238,0.543829],[11239,1.251538],[11240,5.900762]],"version":110707565430,"ts":1596442720614}
     */

    private String ch;
    private String status;
    private TickBean tick;

    public String getCh() {
        return ch;
    }

    public void setCh(String ch) {
        this.ch = ch;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public TickBean getTick() {
        return tick;
    }

    public void setTick(TickBean tick) {
        this.tick = tick;
    }

    public static class TickBean {
        /**
         * bids : [[11220,4.7552],[11219,0.153181],[11218,2.885067],[11217,2.643217],[11216,4.094525],[11215,2.589344],[11214,2.785712],[11213,5.867382],[11212,1.489276],[11211,1.489124],[11210,0.40771],[11209,2.372436],[11208,3.49176],[11207,0.351367],[11206,1.598863],[11205,3.966733],[11204,2.184722],[11203,6.999985],[11202,1.272451],[11201,3.452845]]
         * asks : [[11221,0.21781991055091865],[11222,0.015],[11223,0.512502],[11224,1.5395],[11225,0.559775],[11226,8.456484],[11227,6.635187],[11228,4.053182],[11229,5.920909],[11230,3.028424],[11231,4.407925],[11232,0.486195],[11233,5.485974],[11234,2.100768],[11235,0.61743],[11236,1.562897],[11237,8.678711],[11238,0.543829],[11239,1.251538],[11240,5.900762]]
         * version : 110707565430
         * ts : 1596442720614
         */

        private long version;
        private long ts;
        private List<List<Double>> bids;
        private List<List<Double>> asks;

        public long getVersion() {
            return version;
        }

        public void setVersion(long version) {
            this.version = version;
        }

        public long getTs() {
            return ts;
        }

        public void setTs(long ts) {
            this.ts = ts;
        }

        public List<List<Double>> getBids() {
            return bids;
        }

        public void setBids(List<List<Double>> bids) {
            this.bids = bids;
        }

        public List<List<Double>> getAsks() {
            return asks;
        }

        public void setAsks(List<List<Double>> asks) {
            this.asks = asks;
        }
    }
}
