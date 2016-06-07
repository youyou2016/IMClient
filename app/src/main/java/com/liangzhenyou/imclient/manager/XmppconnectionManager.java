package com.liangzhenyou.imclient.manager;

import android.util.Log;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.iqregister.packet.Registration;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;


/**
 * Created by youyou on 2016/3/17.
 * 用来管理通过xmpp协议和服务器通信的类
 */

public class XmppconnectionManager {

    private static final String TAG = "XmppconnectionManager";

    //服务器名字
    private static final String SERVICE_NAME = "youyou-pc";
    //host的ip
    //private static final String HOST_IP = "172.30.195.5";
    private static final String HOST_IP = "192.168.191.1";
    //服务器端口
    private static final int PORT = 5222;
    //连接超时时间
    private static final int TIME_OUT = 15000;

    private static XMPPTCPConnectionConfiguration xmpptcpConnectionConfiguration;

    private static XMPPTCPConnection xmpptcpConnection;

    private static void init() {
        xmpptcpConnectionConfiguration =
                XMPPTCPConnectionConfiguration.builder()
                        .setConnectTimeout(TIME_OUT)
                        .setHost(HOST_IP)
                        .setServiceName(SERVICE_NAME)
                        .setPort(PORT)
                        .setDebuggerEnabled(true)
                        .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                        .build();
        xmpptcpConnection = new XMPPTCPConnection(xmpptcpConnectionConfiguration);

        //添加联系人需要手动同意或者拒绝
        Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.accept_all);
        ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(xmpptcpConnection);
        reconnectionManager.enableAutomaticReconnection();

    }

    public static XMPPTCPConnection getXmpptcpConnection() {
        if (xmpptcpConnection == null) {
            init();
        }

        return xmpptcpConnection;
    }

    /**
     * 连接
     *
     * @return
     */
    public static boolean connect() {
        init();
        try {
            xmpptcpConnection.connect();
            return true;
        } catch (SmackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XMPPException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 登陆
     *
     * @param userName
     * @param password
     */
    public static boolean login(final String userName, final String password) {
        if (xmpptcpConnection == null) {
            return false;
        }
        try {
            xmpptcpConnection.login(userName, password);
            return true;
        } catch (SmackException e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
        } catch (XMPPException e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
        }
        return false;

    }


    /**
     * 注册
     *
     * @param userName
     * @param password
     */
    public static int register(String userName, String password) {
        if (xmpptcpConnection != null) {
            AccountManager accountManager = AccountManager.getInstance(xmpptcpConnection);
            try {
                accountManager.createAccount(userName, password);
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
                //注册失败
                return -1;
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
                //注册失败
                return -1;
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
                //注册失败
                return -1;
            }
            //注册成功
            return 0;

        }
        return -1;
    }

    /**
     * 修改密码
     *
     * @param password
     * @return
     */
    public static boolean changePassword(String password) {
        if (xmpptcpConnection != null) {
            AccountManager accountManager = AccountManager.getInstance(xmpptcpConnection);
            try {
                accountManager.changePassword(password);
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
                return false;
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
                return false;
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
                return false;
            }

        }
        return true;
    }

    public static List<HashMap<String, String>> searchUsers(String userName) {
        HashMap<String, String> user;
        List<HashMap<String, String>> results = new ArrayList<>();

        UserSearchManager userSearchManager = new UserSearchManager(XmppconnectionManager.getXmpptcpConnection());

        try {
            Form searchForm = userSearchManager.getSearchForm("search." + xmpptcpConnection.getServiceName());
            Form answerForm = searchForm.createAnswerForm();
            answerForm.setAnswer("Username", true);
            answerForm.setAnswer("Name", true);
            answerForm.setAnswer("search", userName);
            ReportedData data = userSearchManager.getSearchResults(answerForm,
                    "search." + xmpptcpConnection.getServiceName());
            List<ReportedData.Row> rows = data.getRows();

            for (int i = 0; i < rows.size(); i++) {
                ReportedData.Row row = rows.get(i);
                user = new HashMap<>();
                user.put("Jid", row.getValues("Jid").get(0));
                user.put("Name", row.getValues("Name").get(0));
                results.add(user);
            }

        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

        return results;
    }

    /**
     * 添加好友
     *
     * @param user
     * @param name
     * @return
     */
    public static boolean addFriend(String user, String name) {
        if (xmpptcpConnection == null) {
            return false;
        }
        Roster roster = Roster.getInstanceFor(xmpptcpConnection);
        try {
            roster.createEntry(user, name, null);
            return true;
        } catch (SmackException.NotLoggedInException | XMPPException.XMPPErrorException | SmackException.NoResponseException | SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 删除好友
     *
     * @param userName
     * @return
     */
    public static boolean removeFriend(String userName) {
        if (xmpptcpConnection == null) {
            return false;
        }
        Roster roster = Roster.getInstanceFor(xmpptcpConnection);
        RosterEntry rosterEntry = roster.getEntry(userName);
        try {
            roster.removeEntry(rosterEntry);
            return true;
        } catch (SmackException.NotLoggedInException | SmackException.NoResponseException | SmackException.NotConnectedException | XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取所有的好友信息
     *
     * @return
     */
    public static Set<RosterEntry> getAllEntries() {
        if (xmpptcpConnection == null) {
            return null;
        }
        Roster roster = Roster.getInstanceFor(xmpptcpConnection);
        Set<RosterEntry> rosterEntrySet = roster.getEntries();
        return rosterEntrySet;
    }


}
