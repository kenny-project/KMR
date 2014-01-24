/*
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

package com.kenny.ppareit.swiftp.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import com.kenny.ppareit.swiftp.Defaults;
import com.kenny.ppareit.swiftp.FTPServerService;

import android.util.Log;

public class NormalDataSocketFactory extends DataSocketFactory {
    private static final String TAG = NormalDataSocketFactory.class.getSimpleName();

    /**
     * This class implements normal, traditional opening and closing of data sockets used
     * for transmitting directory listings and file contents. PORT and PASV work according
     * to the FTP specs. This is in contrast to a ProxyDataSocketFactory, which performs
     * contortions to allow data sockets to be proxied through a server out in the cloud.
     * 
     */

    // Listener socket used for PASV mode
    ServerSocket server = null;
    // Remote IP & port information used for PORT mode
    InetAddress remoteAddr;
    int remotePort;
    boolean isPasvMode = true;

    public NormalDataSocketFactory() {
        clearState();
    }

    private void clearState() {
        /**
         * Clears the state of this object, as if no pasv() or port() had occurred. All
         * sockets are closed.
         */
        if (server != null) {
            try {
                server.close();
            } catch (IOException e) {
            }
        }
        server = null;
        remoteAddr = null;
        remotePort = 0;
        Log.d(TAG, "NormalDataSocketFactory state cleared");
    }

    @Override
    public int onPasv() {
        clearState();
        try {
            // Listen on any port (port parameter 0)
            server = new ServerSocket(0, Defaults.tcpConnectionBacklog);
            Log.d(TAG, "Data socket pasv() listen successful");
            return server.getLocalPort();
        } catch (IOException e) {
            Log.e(TAG, "Data socket creation error");
            clearState();
            return 0;
        }
    }

    @Override
    public boolean onPort(InetAddress remoteAddr, int remotePort) {
        clearState();
        this.remoteAddr = remoteAddr;
        this.remotePort = remotePort;
        return true;
    }

    @Override
    public Socket onTransfer() {
        if (server == null) {
            // We're in PORT mode (not PASV)
            if (remoteAddr == null || remotePort == 0) {
                Log.i(TAG, "PORT mode but not initialized correctly");
                clearState();
                return null;
            }
            Socket socket;
            try {
                socket = new Socket(remoteAddr, remotePort);
            } catch (IOException e) {
                Log.i(TAG, "Couldn't open PORT data socket to: " + remoteAddr.toString()
                        + ":" + remotePort);
                clearState();
                return null;
            }

            // Kill the socket if nothing happens for X milliseconds
            try {
                socket.setSoTimeout(Defaults.SO_TIMEOUT_MS);
            } catch (Exception e) {
                Log.e(TAG, "Couldn't set SO_TIMEOUT");
                clearState();
                return null;
            }

            return socket;
        } else {
            // We're in PASV mode (not PORT)
            Socket socket = null;
            try {
                socket = server.accept();
                Log.d(TAG, "onTransfer pasv accept successful");
            } catch (Exception e) {
                Log.i(TAG, "Exception accepting PASV socket");
                socket = null;
            }
            clearState();
            return socket; // will be null if error occurred
        }
    }

    /**
     * Return the port number that the remote client should be informed of (in the body of
     * the PASV response).
     * 
     * @return The port number, or -1 if error.
     */
    public int getPortNumber() {
        if (server != null) {
            return server.getLocalPort(); // returns -1 if serversocket is unbound
        } else {
            return -1;
        }
    }

    @Override
    public InetAddress getPasvIp() {
        return FTPServerService.getLocalInetAddress();
    }

    @Override
    public void reportTraffic(long bytes) {
        // ignore, we don't care about how much traffic goes over wifi.
    }
}