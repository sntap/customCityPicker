package com.sntap.Interface;

import com.sntap.bean.CustomCityData;

/**
 * 作者：liji on 2017/11/16 10:06
 * 邮箱：lijiwork@sina.com
 * QQ ：275137657
 */

public abstract class OnCustomCityPickerItemClickListener {

    /**
     * 当选择省市区三级选择器时，需要覆盖此方法
     *
     * @param province
     * @param city
     * @param district
     */
    public void onSelected(CustomCityData province, CustomCityData city, CustomCityData district) {

    }

    /**
     * 取消
     */
    public void onCancel() {

    }
}
