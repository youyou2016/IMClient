package com.liangzhenyou.imclient.manager;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;


/**
 * Created by youyou on 2016/3/17.
 * 用来管理通过xmpp协议和服务器通信的类
 */

public class XmppconnectionManager {

    private static final String TAG = "XmppconnectionManager";

    //服务器名字
    private final String SERVICE_NAME = "youyou-pc";
    //host的ip
    private final String HOST_IP = "10.132.31.204";
    //服务器端口
    private final int PORT = 5222;
    //连接超时时间
    private final int TIME_OUT = 10000;

    private XMPPTCPConnectionConfiguration xmpptcpConnectionConfiguration;

    private XMPPTCPConnection xmpptcpConnection;

    public void init() {
        xmpptcpConnectionConfiguration =
                XMPPTCPConnectionConfiguration.builder()
                        .setConnectTimeout(TIME_OUT)
                        .setHost(HOST_IP)
                        .setServiceName(SERVICE_NAME)
                        .setPort(PORT)
                        .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                        .build();

        //添加联系人需要手动同意或者拒绝
        Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.manual);
        xmpptcpConnection = new XMPPTCPConnection(xmpptcpConnectionConfiguration);
    }

    public XMPPTCPConnection getXmpptcpConnection() {
        if (xmpptcpConnection == null) {
            synchronized (XmppconnectionManager.class) {
                init();
            }
        }
        return xmpptcpConnection;
    }

    public static XmppconnectionManager getNewInstance() {
        return new XmppconnectionManager();
    }


}
