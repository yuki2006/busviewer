/**
 *
 */
package jp.co.yuki2006.busmap.web;

import android.content.Context;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import jp.co.yuki2006.busmap.etc.Etc;

/**
 * @author yuki
 */
public class Web {
    public static String getHostURL(Context context) {
        String host = "";
        if (Etc.isDebugMode(context)) {
            try {
                Enumeration<NetworkInterface> enuIfs = NetworkInterface.getNetworkInterfaces();
                if (null != enuIfs) {
                    while (enuIfs.hasMoreElements()) {
                        NetworkInterface ni = enuIfs.nextElement();
                        Enumeration<InetAddress> enuAddrs = ni.getInetAddresses();
                        while (enuAddrs.hasMoreElements()) {
                            InetAddress in4 = enuAddrs.nextElement();
                            byte[] ip = in4.getAddress();
                            //開発用ローカルサーバーアクセス
                            if (ip[0] == (byte) 192 && ip[2] == (byte) 24) {
                                host = "192.168.24.54";
                            }

                        }
                    }

                }

            } catch (SocketException e1) {
                e1.printStackTrace();
            }
        }
        String address = "http://" + host + "/bus_map/";

        return address;
    }
}