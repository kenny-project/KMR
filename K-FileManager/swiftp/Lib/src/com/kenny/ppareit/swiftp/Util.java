/*
Copyright 2011-2013 Pieter Pareit
Copyright 2009 David Revell

This file is part of SwiFTP.

SwiFTP is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SwiFTP is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.kenny.ppareit.swiftp;

import java.net.InetAddress;
import java.net.UnknownHostException;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.util.Log;

abstract public class Util {
    static String TAG = Util.class.getSimpleName();

    public static byte byteOfInt(int value, int which) {
        int shift = which * 8;
        return (byte) (value >> shift);
    }

    public static String ipToString(int addr, String sep) {
        // myLog.l(Log.DEBUG, "IP as int: " + addr);
        if (addr > 0) {
            StringBuffer buf = new StringBuffer();
            buf.append(byteOfInt(addr, 0)).append(sep).append(byteOfInt(addr, 1))
                    .append(sep).append(byteOfInt(addr, 2)).append(sep)
                    .append(byteOfInt(addr, 3));
            Log.d(TAG, "ipToString returning: " + buf.toString());
            return buf.toString();
        } else {
            return null;
        }
    }

    public static InetAddress intToInet(int value) {
        byte[] bytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            bytes[i] = byteOfInt(value, i);
        }
        try {
            return InetAddress.getByAddress(bytes);
        } catch (UnknownHostException e) {
            // This only happens if the byte array has a bad length
            return null;
        }
    }

    public static String ipToString(int addr) {
        if (addr == 0) {
            // This can only occur due to an error, we shouldn't blindly
            // convert 0 to string.
            Log.e(TAG, "ipToString won't convert value 0");
            return null;
        }
        return ipToString(addr, ".");
    }

    public static void newFileNotify(String path) {
        if (Defaults.do_mediascanner_notify) {
            Log.d(TAG, "Notifying others about new file: " + path);
            new MediaScannerNotifier(Globals.getContext(), path);
        }
    }

    public static void deletedFileNotify(String path) {
        // This might not work, I couldn't find an API call for this.
        if (Defaults.do_mediascanner_notify) {
            Log.d(TAG, "Notifying others about deleted file: " + path);
            new MediaScannerNotifier(Globals.getContext(), path);
        }
    }

    // A class to help notify the Music Player and other media services when
    // a file has been uploaded. Thanks to Dave Sparks in his post to the
    // Android Developers mailing list on 14 Feb 2009.
    private static class MediaScannerNotifier implements MediaScannerConnectionClient {
        private final MediaScannerConnection connection;
        private final String path;

        public MediaScannerNotifier(Context context, String path) {
            this.path = path;
            connection = new MediaScannerConnection(context, this);
            connection.connect();
        }

        public void onMediaScannerConnected() {
            connection.scanFile(path, null); // null: we don't know MIME type
        }

        public void onScanCompleted(String path, Uri uri) {
            connection.disconnect();
        }
    }

    public static String[] concatStrArrays(String[] a1, String[] a2) {
        String[] retArr = new String[a1.length + a2.length];
        System.arraycopy(a1, 0, retArr, 0, a1.length);
        System.arraycopy(a2, 0, retArr, a1.length, a2.length);
        return retArr;
    }

    public static void sleepIgnoreInterupt(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }
}
