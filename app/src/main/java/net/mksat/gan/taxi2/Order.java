package net.mksat.gan.taxi2;

/**
 * Created by ByelozyorovZ on 05.11.2016.
 */

public class Order {
    String order;
    String from;
    String to;
    String sector;
    String isEconom;
    String price;
    String startTime;
    boolean box;

    public Order(String order, String from, String to, String sector, String isEconom, String price, String startTime, boolean box) {
        this.order = order;
        this.from = from;
        this.to = to;
        this.sector = sector;
        this.isEconom = isEconom;
        this.price = price;
        this.startTime = startTime;
        this.box = box;
    }

}
