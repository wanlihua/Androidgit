package com.dtl.gemini.ui.asset.beans;

import com.dtl.gemini.bean.BaseBean;
import com.dtl.gemini.ui.asset.model.TransactionRecord;

import java.util.List;

/**
 * @author DTL
 * @date 2020/5/11
 **/
public class TransactionRecordBean extends BaseBean {

    /**
     * data : {"list":[{"id":3,"createDateTime":"2020-05-11 18:18:50","updateDateTime":null,"amount":-0.5,"currency":"ETH","status":0,"extra":null,"otherId":"c41177be-2baf-44bc-87d9-f08bbca6e0b4","type":2},{"id":2,"createDateTime":"2020-05-11 17:32:02","updateDateTime":null,"amount":-0.5,"currency":"ETH","status":0,"extra":null,"otherId":"8aff6965-6a5e-4e55-8949-2c85f03b9ed8","type":2},{"id":1,"createDateTime":"2020-05-11 17:19:27","updateDateTime":null,"amount":-0.5,"currency":"ETH","status":0,"extra":null,"otherId":"09ba3734-227e-4415-a12d-37562b7a5b28","type":2}],"totalPage":1,"totalRow":3,"sum":0}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * list : [{"id":3,"createDateTime":"2020-05-11 18:18:50","updateDateTime":null,"amount":-0.5,"currency":"ETH","status":0,"extra":null,"otherId":"c41177be-2baf-44bc-87d9-f08bbca6e0b4","type":2},{"id":2,"createDateTime":"2020-05-11 17:32:02","updateDateTime":null,"amount":-0.5,"currency":"ETH","status":0,"extra":null,"otherId":"8aff6965-6a5e-4e55-8949-2c85f03b9ed8","type":2},{"id":1,"createDateTime":"2020-05-11 17:19:27","updateDateTime":null,"amount":-0.5,"currency":"ETH","status":0,"extra":null,"otherId":"09ba3734-227e-4415-a12d-37562b7a5b28","type":2}]
         * totalPage : 1
         * totalRow : 3
         * sum : 0.0
         */

        private int totalPage;
        private int totalRow;
        private double sum;
        private List<TransactionRecord> list;

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }

        public int getTotalRow() {
            return totalRow;
        }

        public void setTotalRow(int totalRow) {
            this.totalRow = totalRow;
        }

        public double getSum() {
            return sum;
        }

        public void setSum(double sum) {
            this.sum = sum;
        }

        public List<TransactionRecord> getList() {
            return list;
        }

        public void setList(List<TransactionRecord> list) {
            this.list = list;
        }
    }
}
