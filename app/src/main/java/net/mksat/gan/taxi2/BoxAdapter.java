package net.mksat.gan.taxi2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ByelozyorovZ on 05.11.2016.
 */

public class BoxAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<Order> orders;

    BoxAdapter(Context context, ArrayList<Order> orders) {
        ctx = context;
        this.orders = orders;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // кол-во элементов
    @Override
    public int getCount() {
        return orders.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return orders.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    // пункт списка
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.list_item, parent, false);
        }
        Order ord = getOrder(position);
        // заполняем View в пункте списка данными
        ((TextView) view.findViewById(R.id.sector)).setText(ord.sector);
        ((TextView) view.findViewById(R.id.econom)).setText(ord.isEconom);
        ((TextView) view.findViewById(R.id.price)).setText(ord.price);
        ((TextView) view.findViewById(R.id.starttime)).setText(ord.startTime);
        //        ((TextView) view.findViewById(R.id.Sector)).setText(ord.order);
        ((TextView) view.findViewById(R.id.From)).setText(ord.from + "");
        ((TextView) view.findViewById(R.id.To)).setText(ord.to + "");

        return view;
    }

    // заказ по позиции
    Order getOrder(int position) {
        return ((Order) getItem(position));
    }
}
