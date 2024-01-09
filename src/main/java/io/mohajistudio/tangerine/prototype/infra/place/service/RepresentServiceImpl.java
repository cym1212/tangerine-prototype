package io.mohajistudio.tangerine.prototype.infra.place.service;

import io.mohajistudio.tangerine.prototype.domain.place.controller.RepresentService;
import io.mohajistudio.tangerine.prototype.infra.place.dto.AddressDTO;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
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
        extractFromAddress(places, provinces, cities, districts);
        result.addAll(findKeysInOrder(districts, cities, provinces));
        return result;
    }

    //각각의 맵들에 key가 존재하면 value 를 1 증가 시킴
    private static void extractFromAddress(List<AddressDTO> places, Map<String, Integer> provinces, Map<String, Integer> cities, Map<String, Integer> districts) {
        for (AddressDTO place : places) {
            //key value로 저장//동일한 키 존재시 value1 증가
            String ProvinceKey = place.getProvince();
            String CityKey = ProvinceKey + " " + place.getCity();
            String DistrictKey = place.getCity() + " " + place.getDistrict();
            provinces.compute(ProvinceKey, (key, value) -> (value == null) ? 1 : ++value);
            cities.compute(CityKey, (key, value) -> (value == null) ? 1 : ++value);
            districts.compute(DistrictKey, (key, value) -> (value == null) ? 1 : ++value);
        }
    }
   /* //군/시/도 순서로 맵을 돌면서findKey() 실행// 결과에 같은 지역이 있을 경우 상위 행정구역을 앞에 붙여서 반환
    private static List<String> findKeysInOrder(Map<String, Integer>... maps) {
        List<String> result = new ArrayList<>();
        int halfSize = (maps.length + 1) / 2;
        Map<String, String> addedParentRegions = new HashMap<>();
        for (Map<String, Integer> map : maps) {
            List<String> keys = findKey(map, halfSize);
            if (keys != null) {
                for (String key : keys) {
                    // 언더바가 있는 경우, 언더바 이후의 문자열만 추가//한 번의 반복동안 key를 통해 cleanedKey키를 구함
                    int lastUnderscoreIndex = key.lastIndexOf(' ');
                    String cleanedKey = (lastUnderscoreIndex != -1) ? key.substring(lastUnderscoreIndex + 1) : key;
                    //중복이 제거된 addedParentRegions에 cleanedKey가 등록되어있지 않은 경우
                    if (!addedParentRegions.containsKey(cleanedKey)) {
                        result.add(cleanedKey);   //개안동//등촌동//눌차동//강서구//서울//부산
                    } else {
                        result.add(key);           //부산 강서구//인천 강서구//서울 강서구
                        if (result.remove(cleanedKey)) {
                            result.add(addedParentRegions.get(cleanedKey));
                        }
                    }
                    addedParentRegions.put(cleanedKey, key);
                }
            }
        }
        Collections.reverse(result);
        return result;
    } */
    //군/시/도 순서로 맵을 돌면서findKey() 실행//상위 행정구역을 앞에 붙여서 반환
    private static List<String> findKeysInOrder(Map<String, Integer>... maps) {
        List<String> result = new ArrayList<>();
        double sum = maps[0].values().stream()
                .mapToInt(Integer::intValue)
                .sum();

        int halfSize = (int) (sum / 2) + (sum % 2 == 1 ? 1 : 0);
        System.out.println("halfSize = " + halfSize);
        for (Map<String, Integer> map : maps) {
            List<String> keys = findKey(map, halfSize);
            if (keys != null) {
                for (String key : keys) {
                    result.add(key);
                }
            }
        }
        Collections.reverse(result);
        return result;
    }

    //각각의 맵의 총 크기의 반 이상이 되는 value 들을 모두 대표 지역으로 설정
    private static List<String> findKey(Map<String, Integer> map, int halfSize) {
        int Value = Integer.MIN_VALUE;
        List<String> Keys = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            int currentValue = entry.getValue();
            if (currentValue >= halfSize) {
                Value = currentValue;
                Keys.add(entry.getKey());
            }
        }
        return (Value == Integer.MIN_VALUE ? null : Keys);
    }
    // 하나 들어왔을때 그 데이터가 대표 지역
    private static List<String> getOneLists(List<AddressDTO> places) {
        List<String> result = new ArrayList<>();
        AddressDTO place = places.get(0);
        result.add(place.getProvince());
        result.add(place.getProvince()+ " " +place.getCity());
        result.add(place.getCity()+ " " +place.getDistrict());
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
