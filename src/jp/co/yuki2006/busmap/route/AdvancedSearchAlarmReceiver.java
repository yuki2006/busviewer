/**
 *
 */
package jp.co.yuki2006.busmap.route;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import java.util.ArrayList;

import jp.co.yuki2006.busmap.R;
import jp.co.yuki2006.busmap.route.store.BusRouteElement;
import jp.co.yuki2006.busmap.route.store.RouteData;
import jp.co.yuki2006.busmap.values.IntentValues;

/**
 * @author yuki
 */
public class AdvancedSearchAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent data) {

        Intent intent = new Intent(context, AdvancedSearchResultActivity.class);
        BusRouteElement element = (BusRouteElement) data.getSerializableExtra("bus_route");
        ArrayList<BusRouteElement> routeResultElements = (ArrayList<BusRouteElement>) data
                .getSerializableExtra(IntentValues.ROUTE_RESULT);
        RouteData routeData = (RouteData) data
                .getSerializableExtra(IntentValues.TRANSITION_ADVANCED_SEARCH);

        intent.putExtra(IntentValues.TRANSITION_ADVANCED_SEARCH, routeData);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
        // 通知バーに情報を設定する。
        Notification notification = new Notification(
                R.drawable.ic_launcher,
                "10分後にバスの予定があります。",
                System.currentTimeMillis()
        );

        notification.setLatestEventInfo(context,
                "10分後にバスの予定があります。",
                element.getBusStop().getBusStopName() + "からのバスにご乗車ください",
                contentIntent);

        // ライトを表示する。
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        // notification.defaults |= Notification.DEFAULT_SOUND;
        // しばらく経ったら自動的に削除される
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        notification.sound = uri;
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(element.hashCode(), notification);
    }
}