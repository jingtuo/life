package com.jingtuo.android.libu.util;

import android.text.TextUtils;


/**
 * 工具类
 */
public class Utils {

    /**
     * 中国联通号段130、131、132、145、155、156、166、171、175、176、185、186
     */
    public static final String CHINA_UNICOM_PATTERN = "^((13[0-2])|(145)|(15[56])|(166)|(17[156])|(18[56]))\\d{8}$";

    /**
     * 中国移动号段
     * 134(0-8)、135、136、137、138、139、147、150、151、152、157、158、159、172、178、182、183、184、187、188、198
     */
    public static final String CHINA_MOBILE_PATTERN = "^(134[0-8]\\d{7})|(((13[5-9])|(147)|(15[0-27-9])|(17[28])|(18[2-478])|(198))\\d{8})$";


    /**
     * 中国电信号段
     * 133、149、153、173、177、180、181、189、191、199
     */
    public static final String CHINA_TELECOM_PATTERN = "^((133)|(149)|(153)|(17[37])|(18[019])|(19[19]))\\d{8}$";

    private Utils() {

    }

    /**
     * 检测手机号是否有效
     *
     * @param mobileNo
     * @return
     */
    public static boolean checkMobileNo(String mobileNo) {
        if (TextUtils.isEmpty(mobileNo)) {
            return false;
        }
        return mobileNo.matches(CHINA_MOBILE_PATTERN) || mobileNo.matches(CHINA_UNICOM_PATTERN) || mobileNo.matches(CHINA_TELECOM_PATTERN);
    }
}