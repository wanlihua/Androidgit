package com.dtl.gemini.ui.cfd.model;

import lombok.Data;

/**
 * @author DTL
 * @date 2020/5/5
 **/
@Data
public class CfdDepth {
    private double price;
    private double number;

    public CfdDepth(double price, double number) {
        this.price = price;
        this.number = number;
    }
}
