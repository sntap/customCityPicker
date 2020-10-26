import com.example.lib_citydata.bean.Area;
import com.example.lib_citydata.bean.City;
import com.example.lib_citydata.bean.Province;
import com.example.lib_citydata.utils.FileUtils;
import com.example.lib_citydata.utils.JSONFormatUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class Test {

    public static void main(String[] args) {
        try {
            // 2018年11月中华人民共和国县以上行政区划代码网页
            Document doc = Jsoup.connect("http://www.mca.gov.cn/article/sj/xzqh/2018/201804-12/20181101021046.html")
                    .maxBodySize(0).get();
            Elements elements = doc.getElementsByClass("xl7024197");
            List<String> stringList = elements.eachText();
            List<String> stringName = new ArrayList<String>();
            List<String> stringCode = new ArrayList<String>();
            for (int i = 0; i < stringList.size(); i++) {
                if (i % 2 == 0) {
                    // 地区代码
                    stringCode.add(stringList.get(i));
                } else {
                    // 地区名字
                    stringName.add(stringList.get(i));
                }
            }
            stringName.add("end");
            stringCode.add("000000");
            // 正常情况 两个 list size 应该 一样
            System.out.println("stringName  size= " + stringName.size() + "   stringCode   size= " + stringCode.size());
            if (stringName.size() != stringCode.size()) {
                throw new RuntimeException("数据错误");
            }
//			List<Province> provinceList = processData(stringName, stringCode);
//			String path = FileUtils.getProjectDir() + "/2018年11月中华人民共和国县以上行政区划代码" + ".json";
//			JSONFormatUtils.jsonWriter(provinceList, path);
//
            List<Province> provinceList = getProvincesList(stringName, stringCode);
            String path = FileUtils.getProjectDir() + "/2018年11月中华人民共和国县以上行政区划代码" + ".json";
            JSONFormatUtils.jsonWriter(provinceList, path);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * @param stringName
     * @param stringCode
     * @return
     */
    public static List<Province> getProvincesList(List<String> stringName, List<String> stringCode) {

        List<Province> provinceList = new ArrayList<Province>();
        List<Integer> provinceIndexList = new ArrayList<Integer>();

        for (int i = 0; i < stringCode.size(); i++) {
            String code = stringCode.get(i);
            if (code.endsWith("0000")) {
                provinceIndexList.add(i);
            }
        }

        for (int i = 0; i < provinceIndexList.size() - 1; i++) {
            // 每个省份所在的下标，省份所在的所有省市区数据区间为[index,index-1]
            int index = provinceIndexList.get(i);
            int nextIndex = provinceIndexList.get(i + 1);

            List<String> name = new ArrayList<String>();
            List<String> code = new ArrayList<String>();

            // 每个省份下面的所有数据集合
            for (int j = index; j < nextIndex; j++) {
                name.add(stringName.get(j));
                code.add(stringCode.get(j));
            }

            // 获取省份的下标
            int proIndex = getProIndex(name, code);

            // 设置省份数据
            Province province = new Province();
            province.setCode(code.get(proIndex));
            province.setName(name.get(proIndex));
            List<City> cities = new ArrayList<City>();
            province.setCityList(cities);

            // 获取城市前四位code
            List<String> cityList = getPre4CodeList(name, code);

            // 根据每个前四位code赖取出集合中所有的相同前四位数据，然后根据是否是00结尾的分类
            for (int j = 0; j < cityList.size(); j++) {
                // code前四位
                String codeStub = cityList.get(j);

                List<String> cityCodeList = new ArrayList<String>();
                List<String> cityNameList = new ArrayList<String>();

                // 获取前四位每组所有的城市数据
                for (int k = 0; k < name.size(); k++) {
                    String tempName = name.get(k);
                    String tempCode = code.get(k);
                    if (tempCode.startsWith(codeStub) && !tempCode.endsWith("0000")) {
                        cityCodeList.add(tempCode);
                        cityNameList.add(tempName);
                    }
                }

                // 判断是否是省直辖县级行政单位，如果一组数据中有00结尾的，则是正常的市级城市数据
                boolean is = isContainer(cityCodeList, cityNameList);
                if (is) {
                    City city = new City();
                    city.setName("省直辖县级行政单位");
                    city.setCode(province.getCode());
                    List<Area> areas = new ArrayList<Area>();
                    // 省直辖县级行政单位
                    for (int k = 0; k < cityCodeList.size(); k++) {
                        String tempName = cityNameList.get(k);
                        String tempCode = cityCodeList.get(k);
                        Area area = new Area();
                        area.setName(tempName);
                        area.setCode(tempCode);
                        areas.add(area);
                    }
                    city.setAreaList(areas);
                    cities.add(city);
                } else {
                    // 正常的市级城市数据

                    City city = new City();
                    List<Area> areas = new ArrayList<Area>();
                    for (int k = 0; k < cityCodeList.size(); k++) {
                        String tempName = cityNameList.get(k);
                        String tempCode = cityCodeList.get(k);
                        // 市
                        if (tempCode.endsWith("00")) {
                            city.setName(tempName);
                            city.setCode(tempCode);
                        } else {
                            // 区
                            Area area = new Area();
                            area.setName(tempName);
                            area.setCode(tempCode);
                            areas.add(area);
                        }
                    }
                    city.setAreaList(areas);
                    cities.add(city);
                }
            }
            provinceList.add(province);
            System.out.println(province);
        }
        return provinceList;

    }

    private static boolean isContainer(List<String> cityCodeList, List<String> cityNameList) {
        boolean isContainer = true;
        // 判断是否是省直辖县级行政单位，如果一组数据中有00结尾的，则是正常的市级城市数据
        for (int k = 0; k < cityCodeList.size(); k++) {
            String tempName = cityNameList.get(k);
            String tempCode = cityCodeList.get(k);
            if (tempCode.endsWith("00")) {
                isContainer = false;
                break;
            }
        }

        return isContainer;
    }

    /**
     * // 获取城市前四位code
     *
     * @param name
     * @param code
     * @return
     */
    private static List<String> getPre4CodeList(List<String> name, List<String> code) {

        Set<String> citySet = new HashSet<String>();
        for (int j = 0; j < name.size(); j++) {
            String tempCode = code.get(j).substring(0, 4);
            citySet.add(tempCode);
        }
        Iterator<String> iterator = citySet.iterator();
        List<String> cityList = new ArrayList<String>();
        while (iterator.hasNext()) {
            String stub = iterator.next();
            cityList.add(stub);
        }
        return cityList;
    }

    /**
     * // 获取省份的下标
     *
     * @param name
     * @param code
     * @return
     */
    private static int getProIndex(List<String> name, List<String> code) {
        int proIndex = 0;
        for (int j = 0; j < name.size(); j++) {
            String tempName = name.get(j);
            String tempCode = code.get(j);
            if (tempCode.endsWith("0000")) {
                proIndex = j;
                break;
            }
        }
        return proIndex;
    }

//	//市
//	if (tempCode.endsWith("00")) {
//
//	}else {
//	//省直辖县级行政单位
//

    /**
     * 生成省份列表数据
     *
     * @param stringName
     * @param stringCode
     * @return
     */

    private static List<Province> processData(List<String> stringName, List<String> stringCode) {
        List<Province> provinceList = new ArrayList<Province>();
        for (int i = 0; i < stringCode.size(); i++) {
            String provinceName = stringName.get(i);
            String provinceCode = stringCode.get(i);
            if (provinceCode.endsWith("0000")) {
                Province province = new Province();
                provinceList.add(province);
                province.setCode(provinceCode);
                province.setName(provinceName);
                List<City> cities = new ArrayList<City>();
                province.setCityList(cities);
                // 香港，澳门，台湾，没有市级行政单位划分，城市 地区 和省份保持一致
                if (provinceName.contains("香港") || provinceName.contains("澳门") || provinceName.contains("台湾")) {
                    City city = new City();
                    List<Area> areas = new ArrayList<Area>();
                    city.setName(provinceName);
                    city.setCode(provinceCode);
                    city.setAreaList(areas);
                    cities.add(city);
                    Area area = new Area();
                    area.setName(provinceName);
                    area.setCode(provinceCode);
                    areas.add(area);
                }
                // 直辖市 城市和省份名称一样
                if (provinceName.contains("北京") || provinceName.contains("上海") || provinceName.contains("天津")
                        || provinceName.contains("重庆")) {
                    City city = new City();
                    List<Area> areas = new ArrayList<Area>();
                    city.setName(provinceName);
                    city.setCode(provinceCode);
                    city.setAreaList(areas);
                    cities.add(city);
                    // 县区
                    for (int k = 0; k < stringCode.size(); k++) {
                        String areaName = stringName.get(k);
                        String areaCode = stringCode.get(k);
                        if (!provinceCode.equals(areaCode) && areaCode.startsWith(provinceCode.substring(0, 2))) {
                            Area area = new Area();
                            area.setName(areaName);
                            area.setCode(areaCode);
                            areas.add(area);
                        }
                    }
                }
                for (int j = 0; j < stringCode.size(); j++) {
                    String cityName = stringName.get(j);
                    String cityCode = stringCode.get(j);
                    // 遍历获取地级市
                    if (!cityCode.equals(provinceCode) && cityCode.startsWith(provinceCode.substring(0, 2))
                            && cityCode.endsWith("00")) {
                        City city = new City();
                        List<Area> areas = new ArrayList<Area>();
                        city.setName(cityName);
                        city.setCode(cityCode);
                        city.setAreaList(areas);
                        cities.add(city);
                        // 遍历获取县区
                        for (int k = 0; k < stringCode.size(); k++) {
                            String areaName = stringName.get(k);
                            String areaCode = stringCode.get(k);
                            if (!areaCode.equals(cityCode) && areaCode.startsWith(cityCode.substring(0, 4))) {
                                Area area = new Area();
                                area.setName(areaName);
                                area.setCode(areaCode);
                                areas.add(area);
                            }
                        }
                    }
                }
            }
        }
        return provinceList;
    }

}
