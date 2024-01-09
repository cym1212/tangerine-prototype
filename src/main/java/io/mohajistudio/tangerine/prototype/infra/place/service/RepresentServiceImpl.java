package io.mohajistudio.tangerine.prototype.infra.place.service;


import io.mohajistudio.tangerine.prototype.domain.place.controller.RepresentService;
import io.mohajistudio.tangerine.prototype.infra.place.dto.AddressDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class RepresentServiceImpl implements RepresentService {
    //post에서 수정, 저장 로직 만들때 대표지역을 설정하기 위해 사용
    //post 수정, 저장 만들때 같은 지역 입력받으면 400에러 보내도록 예외처리 요망
    //인프라에 넣어놓긴 했는데 매개변수를 수정해서 place로 옮길 여지 있음(실질적으로 수정,삭제는 도메인에서 이루어지기 때문)
    public List<String> extract(List<AddressDTO> places) {
        List<String> result = new ArrayList<>();
        result.add("전국");
        if (places.size() == 1) {
            result.addAll(getOneLists(places));
            return result;
        }
        Map<String, Integer> provinces = new HashMap<>();
        Map<String, Integer> cities = new HashMap<>();
        Map<String, Integer> districts = new HashMap<>();
        extractForPlace(places, provinces, cities, districts);

        result.addAll(findKeysInOrder(districts, cities, provinces));

        return result;
    }



    private static void extractForPlace(List<AddressDTO> places, Map<String, Integer> provinces, Map<String, Integer> cities, Map<String, Integer> districts) {
        for (AddressDTO place : places) {
            //key value로 저장//동일한 키 존재시 value1 증가
            String ProvinceKey = place.getProvince() ;
            String CityKey = ProvinceKey + "_" + place.getCity();
            String DistrictKey = CityKey + "_" + place.getDistrict();
            provinces.compute(ProvinceKey, (key, value) -> (value == null) ? 1 : ++value);
            cities.compute(CityKey, (key, value) -> (value == null) ? 1 : ++value);
            districts.compute(DistrictKey, (key, value) -> (value == null) ? 1 : ++value);
        }
    }

    private static List<String> findKeysInOrder(Map<String, Integer>... maps) {
        List<String> keys = new ArrayList<>();
        int halfSize = (maps.length + 1) / 2;
        Map<String,String> addedParentRegions = new HashMap<>();
        for (Map<String, Integer> map : maps) {
            List<String> key = findKey(map , halfSize);
            if (key != null) {
                for(String ke : key){
                    // 언더바가 있는 경우, 언더바 이후의 문자열만 추가//한 번의 반복동안 ke를 통해 cleanedKey키를 구함
                    int lastUnderscoreIndex = ke.lastIndexOf('_');
                    String cleanedKey = (lastUnderscoreIndex != -1) ? ke.substring(lastUnderscoreIndex + 1) : ke;
                    //중복이 제거된 addedParentRegions에 cleanedKey가 등록되어있지 않은 경우
                    if(!addedParentRegions.containsKey(cleanedKey)){
                        keys.add(cleanedKey);   //개안동//등촌동//눌차동//강서구//서울//부산
                    }
                     else {
                        keys.add(ke);           //부산_강서구//인천_강서구//서울_강서구
                        if(keys.remove(cleanedKey)){
                            keys.add(addedParentRegions.get(cleanedKey));
                        }
                    }
                    addedParentRegions.put(cleanedKey,ke);

                }

            }
        }
        Collections.reverse(keys);
        return keys;
    }

    private static  List<String> findKey(Map<String, Integer> map, int halfSize) {
        int Value = Integer.MIN_VALUE;
        List <String> Keys = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            int currentValue = entry.getValue();
            if (currentValue >= halfSize) {
                Value = currentValue;
                Keys.add(entry.getKey());
            }
        }
        return (Value == Integer.MIN_VALUE ? null : Keys);
    }
    private static List<String> getOneLists(List<AddressDTO> places) {
        List<String> result = new ArrayList<>();
        AddressDTO place = places.get(0);
        result.add(place.getProvince());
        result.add(place.getCity());
        result.add(place.getDistrict());
        return result;
    }

    //정규표현식으로 주소 스트링을 시,도,구 등을 구분하여 데이터화 하는 로직
  /*  public static AddressDTO extracted(String address) {
        String regex = "^(?<province>\\S+)\\s+(?<city>\\S+)\\s+(?<district>\\S+)(\\s+(?<detail>.+))?$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(address);

        if (matcher.matches()) {
            String province = matcher.group("province");
            String city = matcher.group("city");
            String district = matcher.group("district");
            String detail = matcher.group("detail");
            // detail이 null이면 빈 문자열로 처리
            detail = (detail != null) ? detail : " ";
            return AddressDTO.builder().province(province).city(city).district(district).detail(detail).build();
        } else {
            log.error("오류발생 주소 = " + address);
            throw new BusinessException(ErrorCode.KAKAO_REGULAR_EXPRESSION);
        }
    }*/
}
