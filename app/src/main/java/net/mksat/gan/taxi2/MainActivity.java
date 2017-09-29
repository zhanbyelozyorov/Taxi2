package net.mksat.gan.taxi2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
    LocationManager lm;
    double lat;
    double lon;
    String path;
    RequestTask requestTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // прописываем вкладки
        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();
        TabHost.TabSpec tabSpec;
        // первая вкладка

        tabSpec = tabHost.newTabSpec("tag1");
        View v = getLayoutInflater().inflate(R.layout.efir, null);
        tabSpec.setIndicator(v);
        tabSpec.setContent(R.id.Tab1);
        tabHost.addTab(tabSpec);

        // вторая вкладка
        tabSpec = tabHost.newTabSpec("tag2");
        View v1 = getLayoutInflater().inflate(R.layout.predv, null);
        tabSpec.setIndicator(v1);
        tabSpec.setContent(R.id.Tab2);
        tabHost.addTab(tabSpec);

        // по умолчанию отображается открытой первая вкладка
        tabHost.setCurrentTabByTag("tag1");
        // получаем gps координаты
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "30 сек. Обновление!", Toast.LENGTH_LONG).show();
                    }
                });
                getLocationAndSendRequest();
            }
        }, 0, 30000);
    }

    public void getLocationAndSendRequest() {
        Location loc = getLocation();
        if (loc != null) {
            lat = loc.getLatitude();
            lon = loc.getLongitude();
            path = ("http://89.184.67.115/taxi/index.php?" +
                    "id_car=31&pass=123456&get_order=1&x=" +    // "id_car=273&pass=taxi_admin&get_order=1&x=" +
                    lat + "&y=" + lon);
            requestTask = new RequestTask(this);
            requestTask.execute(path);
        }
    }

    private Location getLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            return null;
        } else {
            return lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocationAndSendRequest();
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "На нет и суда нет!", Toast.LENGTH_LONG);
            toast.show();
        }
        return;
    }

    void fillList(String text) throws IOException {
        ArrayList<Order> orders = new ArrayList<>();
        ArrayList<Order> prOrders = new ArrayList<>();

        text = text.replaceAll("<br>", "\n");
        BufferedReader reader = new BufferedReader(new StringReader(text));
        String line = null;
        reader.readLine();

        if ((line = reader.readLine()).equals("<order>")) {
            while (!(line = reader.readLine()).equals("</order>")) {
                String nextOrder = new String("");
                String nextFrom = new String("");
                String nextTo = new String("");
                String sector = new String("Сектор?");
                String isEconom = new String("Эконом");
                String price = new String("");
                String startTime = new String("");
                int i = 0;
                for (; ; i++) {
                    if (line.charAt(i) != '|') {
                        nextOrder += line.charAt(i);
                    } else {
                        i += 2;
                        break;
                    }
                }

                for (; ; i++) {
                    if (line.charAt(i) != '|') {
                        nextFrom += line.charAt(i);
                    } else {
                        i += 2;
                        break;
                    }
                }

                for (; ; i++) {
                    if (line.charAt(i) != '|') {
                        nextTo += line.charAt(i);
                    } else {
                        i++;
                        break;
                    }
                }
                if (nextTo.equals("")) {
                    nextTo = "???";
                }

                for (; i < line.length(); i++) {
                    if (line.charAt(i) == ':' && line.charAt(i + 3) == '|' && line.charAt(i + 4) == '0') {
                        startTime = line.substring(i - 5, i);
                        break;
                    }
                }

                for (int k = 8; k > 0; ) {
                    i--;
                    if (line.charAt(i) == '|') {
                        k--;
                    }

                }
                sector = "Cектор" + line.charAt(i - 1);

                for (int m = 0; i < line.length(); i++) {

                    if (line.charAt(i) == '|') {
                        m++;
                        if (m==4){
                            i++;
                            if (line.charAt(i) != '0'){
                                isEconom = "Бизнес класс";
                            }
                        }
                        if (m==6){
                            i++;
                            while (line.charAt(i) != '|'){
                                price += line.charAt(i);
                                i++;
                            }
                            if (price.equals("")){
                                price = "? грн.";
                            }else {
                                price += " грн.";
                            }
                        }
                    }
                }

                orders.add(new Order(nextOrder, nextFrom, nextTo, sector, isEconom, price, startTime, false));
            }
        }

        if ((line = reader.readLine()).equals("<pr_order>")) {
            while (!(line = reader.readLine()).equals("</pr_order>")) {
                String nextPrOrder = new String("");
                String nextPrFrom = new String("");
                String nextPrTo = new String("");
                String sector = new String("");
                String isEconom = new String("");
                String price = new String("");
                String startTime = new String("");

                int i = 0;
                for (; ; i++) {
                    if (line.charAt(i) != '|') {
                        nextPrOrder += line.charAt(i);
                    } else {
                        i += 2;
                        break;
                    }
                }

                for (; ; i++) {
                    if (line.charAt(i) != '|') {
                        nextPrFrom += line.charAt(i);
                    } else {
                        i++;
                        break;
                    }
                }

                for (; ; i++) {
                    if (line.charAt(i) != '|') {
                        nextPrTo += line.charAt(i);
                    } else {
                        i++;
                        break;
                    }
                }
                prOrders.add(new Order(nextPrOrder, nextPrFrom, nextPrTo, sector, isEconom, price, startTime, false));
            }
        }


        // создаем адаптер
        BoxAdapter boxAdapter1 = new BoxAdapter(this, orders);
        BoxAdapter boxAdapter2 = new BoxAdapter(this, prOrders);
        // настраиваем список
        ListView lvMain1 = (ListView) findViewById(R.id.Tab1);
        lvMain1.setAdapter(boxAdapter1);

        ListView lvMain2 = (ListView) findViewById(R.id.Tab2);
        lvMain2.setAdapter(boxAdapter2);
    }
}
