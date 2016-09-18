package util;

import android.text.TextUtils;
import db.CoolWeatherDB;
import model.City;
import model.County;
import model.Province;

public class Utility {
	//detail province
	public synchronized static boolean handleProvinceResponse(CoolWeatherDB coolWeatherDB, String response){
		if(!TextUtils.isEmpty(response)) {
			String [] allProvinces = response.split(",");
			if(allProvinces != null && allProvinces.length > 0){
				for(String p : allProvinces){
					String[] provinces = p.split("\\|"); 
					Province province = new Province();
					province.setProvinceName(provinces[0]);
					province.setProvinceName(provinces[1]);
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}

	//detail city data
	public synchronized static boolean handleCityResponse(CoolWeatherDB coolWeatherDB, String response, int provinceId){
		if(!TextUtils.isEmpty(response)){
			String [] allCitys = response.split(",");
			if( allCitys != null && allCitys.length > 0){
				for(String p : allCitys){
					String [] array = p.split("\\|");
					City city = new City();
					city.setCityName(array[0]);
					city.setCityCode(array[1]);
					city.setProvinceId(provinceId);
					coolWeatherDB.saveCity(city);
				}
				return true;
			}

		}
		return false;
	}
	
	//detail county data
	public synchronized static boolean handleCountyResponse(CoolWeatherDB coolWeatherDB, String response, int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] counties = response.split(",");
			if( counties != null && counties.length > 0){
				for(String p : counties){
					String[] array = p.split("\\|");
					County county = new County();
					county.setCountyName(array[0]);
					county.setCountyCode(array[1]);
					county.setCityId(cityId);
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}

}
