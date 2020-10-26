package com.ihidea.as.citypicker.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ihidea.as.citypicker.R;
import com.sntap.Interface.OnCityItemClickListener;
import com.sntap.bean.CityBean;
import com.sntap.bean.DistrictBean;
import com.sntap.bean.ProvinceBean;
import com.sntap.style.cityjd.JDCityConfig;
import com.sntap.style.cityjd.JDCityPicker;

import java.util.ArrayList;
import java.util.List;

public class CitypickerJDActivity extends AppCompatActivity {
    JDCityPicker cityPicker;
    private Button jdBtn;
    private TextView resultV;
    TextView mTwoTv;
    TextView mThreeTv;

    public JDCityConfig.ShowType mWheelType = JDCityConfig.ShowType.PRO_CITY;


    private JDCityConfig jdCityConfig = new JDCityConfig.Builder().build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citypicker_jd);

        jdBtn = (Button) findViewById(R.id.jd_btn);
        resultV = (TextView) findViewById(R.id.result_tv);
        mTwoTv = (TextView) findViewById(R.id.two_tv);
        mThreeTv = (TextView) findViewById(R.id.three_tv);

        jdCityConfig.setShowType(mWheelType);

        //二级联动，只显示省份， 市，不显示区
        mTwoTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWheelType = JDCityConfig.ShowType.PRO_CITY;
                setWheelType(mWheelType);
                jdCityConfig.setShowType(mWheelType);
            }
        });

        //三级联动，显示省份， 市和区
        mThreeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWheelType = JDCityConfig.ShowType.PRO_CITY_DIS;
                setWheelType(mWheelType);
                jdCityConfig.setShowType(mWheelType);
            }
        });


        cityPicker = new JDCityPicker();
        //初始化数据
        cityPicker.init(this);
        //设置JD选择器样式位只显示省份和城市两级
        cityPicker.setConfig(jdCityConfig);
        List<ProvinceBean> provinceBeanList = new ArrayList<ProvinceBean>();
        ProvinceBean bean1 = new ProvinceBean();
        bean1.setId("1000");
        bean1.setName("北京");

        ArrayList<CityBean> cityBeansBeanList1 = new ArrayList<CityBean>();
        CityBean cityBean1 = new CityBean();
        cityBean1.setId("1000001");
        cityBean1.setName("东城区");
        cityBeansBeanList1.add(cityBean1);

        bean1.setCityList(cityBeansBeanList1);

        provinceBeanList.add(bean1);


        ProvinceBean bean2 = new ProvinceBean();
        bean2.setId("2001");
        bean2.setName("上海");

        ArrayList<CityBean> cityBeansBeanList2 = new ArrayList<CityBean>();
        CityBean cityBean2 = new CityBean();
        cityBean2.setId("2000001");
        cityBean2.setName("建安区区");
        cityBeansBeanList2.add(cityBean2);

        bean2.setCityList(cityBeansBeanList2);

        provinceBeanList.add(bean2);

        cityPicker.setProvinceList(provinceBeanList);

        try {
            cityPicker.setDefaultProvinceName("provinceName");
            cityPicker.setDefaultCityName("cityName");
            cityPicker.setSelectTitle("选择地区1232131231");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        cityPicker.setOnCityItemClickListener(new OnCityItemClickListener() {
            @Override
            public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {

                String proData = null;
                if (province != null) {
                    proData = "name:  " + province.getName() + "   id:  " + province.getId();
                }

                String cituData = null;
                if (city != null) {
                    cituData = "name:  " + city.getName() + "   id:  " + city.getId();
                }


                String districtData = null;
                if (district != null) {
                    districtData = "name:  " + district.getName() + "   id:  " + district.getId();
                }


                if (mWheelType == JDCityConfig.ShowType.PRO_CITY_DIS) {
                    resultV.setText("城市选择结果：\n" + proData + "\n"
                            + cituData + "\n"
                            + districtData);
                } else {
                    resultV.setText("城市选择结果：\n" + proData + "\n"
                            + cituData + "\n"
                    );
                }
            }

            @Override
            public void onCancel() {
            }
        });
        jdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showJD();
            }
        });
    }


    /**
     * @param wheelType
     */
    private void setWheelType(JDCityConfig.ShowType wheelType) {
        if (wheelType == JDCityConfig.ShowType.PRO_CITY) {
            mTwoTv.setBackgroundResource(R.drawable.city_wheeltype_selected);
            mThreeTv.setBackgroundResource(R.drawable.city_wheeltype_normal);
            mTwoTv.setTextColor(Color.parseColor("#ffffff"));
            mThreeTv.setTextColor(Color.parseColor("#333333"));
        } else {
            mTwoTv.setBackgroundResource(R.drawable.city_wheeltype_normal);
            mThreeTv.setBackgroundResource(R.drawable.city_wheeltype_selected);
            mTwoTv.setTextColor(Color.parseColor("#333333"));
            mThreeTv.setTextColor(Color.parseColor("#ffffff"));
        }
    }


    private void showJD() {
        cityPicker.showCityPicker();
    }
}
