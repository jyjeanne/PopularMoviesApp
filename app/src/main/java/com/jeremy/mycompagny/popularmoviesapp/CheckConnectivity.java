package com.jeremy.mycompagny.popularmoviesapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.InetAddress;

/**
 * Source: http://www.andygup.net/check-network-connectivity-in-your-android-app/
 **/

public class CheckConnectivity {
    ConnectivityManager connectivityManager;
    NetworkInfo wifiInfo, mobileInfo;

    /**
     * Check for <code>TYPE_WIFI</code> and <code>TYPE_MOBILE</code> connection using <code>isConnected()</code>
     * Checks for generic Exceptions and writes them to logcat as <code>CheckConnectivity Exception</code>.
     * Make sure AndroidManifest.xml has appropriate permissions.
     *
     * @param con Application context
     * @return Boolean
     */
    public Boolean isNetworkConnected(Context con) {

        try {
            connectivityManager = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
            wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (wifiInfo.isConnected() || mobileInfo.isConnected()) {
                return true;
            }
        } catch (Exception e) {
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
        }

        return false;
    }

    public boolean isInternetAvailable(String url) {
        try {
            InetAddress ipAddr = InetAddress.getByName(url); //You can replace it with your name

            if (ipAddr.equals("")) {
                return false;
            } else {
                return true;
            }

        } catch (Exception e) {
            return false;
        }

    }
}