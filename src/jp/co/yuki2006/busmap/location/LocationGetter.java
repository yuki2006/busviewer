package jp.co.yuki2006.busmap.location;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import jp.co.yuki2006.busmap.Main;
import jp.co.yuki2006.busmap.R;

public class LocationGetter implements LocationListener {

    private static LocationGetter instance = null;

    public static LocationGetter getInstance() {
        return instance;
    }

    public static LocationGetter getInstance(Main act) {
        if (instance == null) {
            instance = new LocationGetter(act);
        }
        return instance;
    }

    /**
     * @param provider
     */
    public static LocationManager getLocationSettingDialog(final Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            return locationManager;
        }
        if (locationManager.getProviders(new Criteria(), true).size() == 0) {
            // 位置情報が有効になっていない場合は、
            // Google Maps アプリライクな [現在地機能を改善] ダイアログを起動します。
            new AlertDialog.Builder(context)
                    .setTitle("現在地取得設定")
                    .setMessage("現在、位置情報が有効ではありません。設定画面を表示しますか？")
                    .setPositiveButton("設定", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int which) {
                            // 端末の位置情報設定画面へ遷移
                            try {
                                context.startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
                            } catch (final ActivityNotFoundException e) {
                                // 位置情報設定画面がない端末の場合は
                            }
                        }
                    })
                    .setNegativeButton("スキップ", null)
                    .show();
            return null;
        }
        return locationManager;
    }

    private LocationManager locationManager;

    private boolean checkingFlag = false;

    private Timer timer;

    private Handler handler = new Handler();

    private Main act;

    private LocationGetter(Main act) {
        this.act = act;
    }

    public void destroy() {

        locationManager.removeUpdates(this);
        if (timer != null) {
            timer.cancel();
        }

        instance = null;
    }

    public void getLocation() {
        if (checkingFlag) {
            Toast.makeText(act, "現在地を取得中です。\n しばらくお待ちください。", Toast.LENGTH_SHORT).show();
            return;
        }
        locationManager = getLocationSettingDialog(act);
        if (locationManager == null) {
            return;
        }
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setBearingRequired(false); // 方位不要
        criteria.setSpeedRequired(false); // 速度不要
        criteria.setAltitudeRequired(false); // 高度不要

        String provider = locationManager.getBestProvider(criteria, true);

        // 最後に取得できた位置情報が2分以内のものであれば有効とします。
        final Location lastKnownLocation = locationManager.getLastKnownLocation(provider);
        if (lastKnownLocation != null && (new Date().getTime() - lastKnownLocation.getTime()) <= (2 * 60 * 1000L)) {
            onLocationChanged(lastKnownLocation);
            return;
        }

        // タイムアウト用のタイマー作成
        timer = new Timer(true);
        TimerTask mTask = new TimerTask() {

            @Override
            public void run() {

                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(act, "60秒以内にGPSの情報を受け取れませんでした。取得に失敗しました。", Toast.LENGTH_LONG).show();
                        locationManager.removeUpdates(LocationGetter.this);
                        checkingFlag = false;
                    }
                });
            }
        };

        timer.schedule(mTask, 60000);

        locationManager.requestLocationUpdates(provider, 60000, 0, this);
        if (provider.equals(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(act, "現在地を取得しています。\n 通知バーのGPSのアイコンが消えるまでしばらくお待ちください。\n(条件によっては長い時間かかる可能性があります)",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(act, "現在地を取得しています。\n しばらくお待ちください。", Toast.LENGTH_SHORT).show();

        }
        checkingFlag = true;
        return;

    }

    public void onLocationChanged(Location location) {
        // TODO 自動生成されたメソッド・スタブ
        // GeoPoint point = new GeoPoint((int) (location.getLatitude() *
        // 1000000.0),
        // (int) (location.getLongitude() * 1000000.0));

        // act.setMapPosition(point);

        Toast.makeText(act, act.getResources().getString(R.string.accuracy_fix_meter, (int) location.getAccuracy()),
                Toast.LENGTH_LONG).show();
        locationManager.removeUpdates(this);
        checkingFlag = false;
        if (timer != null) {
            timer.cancel();
        }

    }

    public void onProviderDisabled(String provider) {
        // TODO 自動生成されたメソッド・スタブ

    }

    public void onProviderEnabled(String provider) {
        // TODO 自動生成されたメソッド・スタブ

    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO 自動生成されたメソッド・スタブ

    }

}
