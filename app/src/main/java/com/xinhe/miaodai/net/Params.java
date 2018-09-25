package com.xinhe.miaodai.net;

import com.meituan.android.walle.WalleChannelReader;
import com.xinhe.miaodai.MyApp;
import com.xinhe.miaodai.R;

/**
 * - @Author:  闫世豪
 * - @Time:  2018/5/23 下午4:22
 * - @Email whynightcode@gmail.com
 */
public class Params {

    public static String getAppName(){
       return MyApp.getApplication().getResources().getString(R.string.appName);
    }

    public static String getChannel(){
        return  WalleChannelReader.getChannel(MyApp.getApplication());

    }
}
