package io.mohajistudio.tangerine.prototype.infra.place.service;

import io.mohajistudio.tangerine.prototype.infra.place.dto.AddressDTO;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RepresentServiceImpl implements RepresentService {
    //post에서 수정, 저장 로직 만들때 대표지역을 설정하기 위해 사용
    //post 수정, 저장 만들때 같은 지역 입력받으면 400에러 보내도록 예외처리 요망
    //인프라에 넣어놓긴 했는데 매개변수를 수정해서 place로 옮길 여지 있음(실질적으로 수정,삭제는 도메인에서 이루어지기 때문)
    public List<String> extract(List<AddressDTO> addressDTOList) {
        List<String> result = new ArrayList<>();
        result.add("전국");
        if (addressDTOList.size() == 1) {
            result.addAll(getOneLists(addressDTOList));
            return result;
        }
        Map<String, Integer> provinces = new HashMap<>();
        Map<String, Integer> cities = new HashMap<>();
        Map<String, Integer> districts = new HashMap<>();
        extractFromAddress(addressDTOList, provinces, cities, districts);
        result.addAll(findKeysInOrder(districts, cities, provinces));
        return result;
    }

    //각각의 맵들에 key가 존재하면 value 를 1 증가 시킴
    private static void extractFromAddress(List<AddressDTO> addressDTOList, Map<String, Integer> provinces, Map<String, Integer> cities, Map<String, Integer> districts) {
        for (AddressDTO addressDTO : addressDTOList) {
            //key value로 저장//동일한 키 존재시 value1 증가
            String ProvinceKey = addressDTO.getProvince();
            String CityKey = ProvinceKey + " " + addressDTO.getCity();
            String DistrictKey = addressDTO.getCity() + " " + addressDTO.getDistrict();
            provinces.compute(ProvinceKey, (key, value) -> (value == null) ? 1 : ++value);
            cities.compute(CityKey, (key, value) -> (value == null) ? 1 : ++value);
            districts.compute(DistrictKey, (key, value) -> (value == null) ? 1 : ++value);
        }
    }

    @SafeVarargs
    private static List<String> findKeysInOrder(Map<String, Integer>... maps) {
        List<String> result = new ArrayList<>();
        double sum = maps[0].values().stream()
                .mapToInt(Integer::intValue)
                .sum();

        int halfSize = (int) (sum / 2) + (sum % 2 == 1 ? 1 : 0);
        for (Map<String, Integer> map : maps) {
            List<String> keys = findKey(map, halfSize);
            if (keys != null) {
                result.addAll(keys);
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
        result.add(place.getProvince() + " " + place.getCity());
        result.add(place.getCity() + " " + place.getDistrict());
        return result;
    }
}
