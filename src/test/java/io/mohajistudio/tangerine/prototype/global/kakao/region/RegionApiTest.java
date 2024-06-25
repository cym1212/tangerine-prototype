package io.mohajistudio.tangerine.prototype.global.kakao.region;


import io.mohajistudio.tangerine.prototype.infra.place.dto.AddressDTO;
import io.mohajistudio.tangerine.prototype.infra.place.dto.KakaoPlaceApiDTO;
import io.mohajistudio.tangerine.prototype.infra.place.service.RepresentServiceImpl;
import io.mohajistudio.tangerine.prototype.infra.place.service.PlaceApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

@SpringBootTest
@ActiveProfiles("local")
class RegionApiTest {

    //    @Test
//    void getRegionDatum() {
//        KakaoPlaceApiDTO data = placeApiService.searchPlace("범박고등학교",1,1);
//        System.out.println("data = " + data.toString());
//        String strings = data.getDocuments().get(0).getAddressName();
//        System.out.println("strings = " + strings);
//    }
    @Test
    void getOfficialName() {
        String address1 = "대구광역시 달서구 두류동 산 302-11";
        String address2 = "대구광역시 중구 포정동 21-3";
        String address3 = "대구광역시 동구 신청동 425-14";

        String city = "서울";
        Map<String, String> cityMap = new HashMap<>();
        cityMap.put("서울", "서울특별시");
        cityMap.put("부산", "부산광역시");
        cityMap.put("대구", "대구광역시");
        cityMap.put("인천", "인천광역시");
        cityMap.put("광주", "광주광역시");
        cityMap.put("울산", "울산광역시");
        cityMap.put("전남", "전라남도");
        cityMap.put("경남", "경상남도");
        cityMap.put("경북", "경상북도");
        cityMap.put("충남", "충청남도");
        cityMap.put("충북", "충청북도");
        cityMap.put("전북특별자치도", "전북특별자치도");
        cityMap.put("제주특별자치도", "제주특별자치도");
        cityMap.put("강원특별자치도", "강원특별자치도");
        cityMap.put("세종특별자치시", "세종특별자치시");
        city = cityMap.getOrDefault(city, city);
        System.out.println("city = " + city);
    }

    @Test
    void test() {
        int a = 4;

        if(a > 3 && a < 5)  {
            System.out.println("저는 4에요");
        }

    }

//    @Test
//    void getRegionData() {
//
//        List<KakaoPlaceApiDTO> data = new ArrayList<>();
//        data.add(placeApiService.searchPlace("강원도청",1,1));
//        data.add(placeApiService.searchPlace("경기도청",1,1));
//        data.add(placeApiService.searchPlace("서울시청",1,1));
//        data.add(placeApiService.searchPlace("전라도청",1,1));
//        data.add(placeApiService.searchPlace("경상도청",1,1));
//        int i = 1;
//        for(KakaoPlaceApiDTO datum : data){
//         System.out.println("data = " + datum.toString());
//         String strings = datum.getDocuments().get(0).getAddressName();
//         System.out.println("strings("+ i +") = " + strings);
//            i++;
//        }
//    }
//    @Test
//    void generateProvinces(){
//        List<AddressDTO> places = new ArrayList<>();
//        places.add(AddressDTO.builder().province("서울").city("마포구").district("서교동").detail("393").build());
//        places.add(AddressDTO.builder().province("서울").city("강남구").district("역삼동").detail("858").build());
//        places.add(AddressDTO.builder().province("서울").city("용산구").district("동자동").detail("43-205").build());
//        places.add(AddressDTO.builder().province("부산").city("동구").district("초량동").detail("1187-1").build());
//        places.add(AddressDTO.builder().province("경기").city("용인시").district("기흥구").detail("상갈동 496").build());
//        List<String> RepresentativeRegion = RRG.extract(places);
//        System.out.println("대표지역 = " + RepresentativeRegion);
//        List<String> expectedList = Arrays.asList("전국","서울");
//        assertArrayEquals(expectedList.toArray(), RepresentativeRegion.toArray());
//    }
//    @Test
//    void generateCities(){
//        List<AddressDTO> places = new ArrayList<>();
//        places.add(AddressDTO.builder().province("경기").city("광명시").district("철산동").detail("222-1").build());
//        places.add(AddressDTO.builder().province("경기").city("광명시").district("하안4동").detail("25").build());
//        places.add(AddressDTO.builder().province("경기").city("광명시").district("소하동").detail("1335").build());
//        places.add(AddressDTO.builder().province("경기").city("광명시").district("일직동").detail("276-8").build());
//        places.add(AddressDTO.builder().province("경기").city("광명시").district("광명동").detail("158-211").build());
//
//        List<String> RepresentativeRegion = RRG.extract(places);
//        System.out.println("대표지역 = " + RepresentativeRegion);
//        List<String>  expectedList = Arrays.asList("전국","경기","경기 광명시");
//        assertArrayEquals(expectedList.toArray(), RepresentativeRegion.toArray());
//    }
//    @Test
//    void generateDistricts(){
//        List<AddressDTO> places = new ArrayList<>();
//        places.add(AddressDTO.builder().province("인천").city("연수구").district("송도동").detail("24-5").build());
//        places.add(AddressDTO.builder().province("인천").city("연수구").district("송도동").detail(" ").build());
//        places.add(AddressDTO.builder().province("인천").city("연수구").district("송도동").detail("9-1").build());
//        places.add(AddressDTO.builder().province("인천").city("연수구").district("송도동").detail("806-12").build());
//        places.add(AddressDTO.builder().province("인천").city("연수구").district("송도동").detail("5-1").build());
//        List<String> RepresentativeRegion = RRG.extract(places);
//        System.out.println("대표지역 = " + RepresentativeRegion);
//        List<String> expectedList = Arrays.asList("전국","인천","인천 연수구","연수구 송도동");
//        assertArrayEquals(expectedList.toArray(), RepresentativeRegion.toArray());
//    }
//    @Test //동일 지역명 예외처리
//    void generateException1(){
//        List<AddressDTO> places = new ArrayList<>();
//        places.add(AddressDTO.builder().province("서울").city("강서구").district("등촌동").detail("24-5").build());
//        places.add(AddressDTO.builder().province("부산").city("강서구").district("눌차동").detail("55-1").build());
//        places.add(AddressDTO.builder().province("부산").city("동래구").district("사직동").detail("55-1").build());
//        places.add(AddressDTO.builder().province("부산").city("동래구").district("사직동").detail("56-1").build());
//
//        List<String> RepresentativeRegion = RRG.extract(places);
//        System.out.println("대표지역 = " + RepresentativeRegion);
//        List<String> expectedList = Arrays.asList("전국","부산","부산 동래구","동래구 사직동");
//        assertArrayEquals(expectedList.toArray(), RepresentativeRegion.toArray());
//    }
//    @Test //동일 구 예외처리
//    void generateException2(){
//        List<AddressDTO> places = new ArrayList<>();
//        places.add(AddressDTO.builder().province("서울").city("강서구").district("등촌동").detail("24-5").build());
//        places.add(AddressDTO.builder().province("서울").city("강서구").district("등촌동").detail("24-5").build());
//        places.add(AddressDTO.builder().province("부산").city("강서구").district("눌차동").detail("55-1").build());
//        places.add(AddressDTO.builder().province("부산").city("강서구").district("눌차동").detail("55-1").build());
//
//
//        List<String> RepresentativeRegion = RRG.extract(places);
//        System.out.println("대표지역 = " + RepresentativeRegion);
//        List<String> expectedList = Arrays.asList("전국","부산","서울","서울 강서구","부산 강서구","강서구 등촌동","강서구 눌차동");
//        assertArrayEquals(expectedList.toArray(), RepresentativeRegion.toArray());
//    }  @Test //동일 동 예외처리
//    void generateException3(){
//        List<AddressDTO> places = new ArrayList<>();
//        places.add(AddressDTO.builder().province("서울").city("강서구").district("등촌동").detail("24-5").build());
//        places.add(AddressDTO.builder().province("서울").city("강서구").district("등촌동").detail("24-5").build());
//        places.add(AddressDTO.builder().province("서울").city("팔달구").district("등촌동").detail("55-1").build());
//        places.add(AddressDTO.builder().province("서울").city("팔달구").district("등촌동").detail("55-1").build());
//
//
//        List<String> RepresentativeRegion = RRG.extract(places);
//        System.out.println("대표지역 = " + RepresentativeRegion);
//        List<String> expectedList = Arrays.asList("전국","서울","서울 강서구","서울 팔달구","강서구 등촌동","팔달구 등촌동");
//        assertArrayEquals(expectedList.toArray(), RepresentativeRegion.toArray());
//    }
//    @Test //동일 지역 3개이상 예외처리
//    void generateException4(){
//        List<AddressDTO> places = new ArrayList<>();
//        places.add(AddressDTO.builder().province("서울").city("강서구").district("등촌동").detail("24-5").build());
//        places.add(AddressDTO.builder().province("서울").city("강서구").district("등촌동").detail("24-6").build());
//        places.add(AddressDTO.builder().province("서울").city("강서구").district("등촌동").detail("24-7").build());
//        places.add(AddressDTO.builder().province("서울").city("강서구").district("등촌동").detail("24-8").build());
//        places.add(AddressDTO.builder().province("부산").city("강서구").district("눌차동").detail("55-1").build());
//        places.add(AddressDTO.builder().province("부산").city("강서구").district("눌차동").detail("55-2").build());
//        places.add(AddressDTO.builder().province("인천").city("강서구").district("개안동").detail("55-1").build());
//        places.add(AddressDTO.builder().province("인천").city("강서구").district("개안동").detail("55-2").build());
//
//
//        List<String> RepresentativeRegion = RRG.extract(places);
//        System.out.println("대표지역 = " + RepresentativeRegion);
//        List<String> expectedList = Arrays.asList("전국","서울","서울 강서구","강서구 등촌동");
//        assertArrayEquals(expectedList.toArray(), RepresentativeRegion.toArray());
//    }
//    @Test
//    void generateKorea(){
//        List<AddressDTO> places = new ArrayList<>();
//        places.add(AddressDTO.builder().province("강원특별자치도").city("춘천시").district("봉의동").detail("15").build());
//        places.add(AddressDTO.builder().province("경기").city("수원시").district("영통구").detail("이의동").build());
//        places.add(AddressDTO.builder().province("서울").city("강서구").district("등촌동").detail("24-5").build());
//
//
//        List<String> RepresentativeRegion = RRG.extract(places);
//        System.out.println("대표지역 = " + RepresentativeRegion);
//        List<String> expectedList = List.of("전국");
//        assertArrayEquals(expectedList.toArray(), RepresentativeRegion.toArray());
//    }
//    @Test
//    void generateOne(){
//        List<AddressDTO> places = new ArrayList<>();
//        places.add(AddressDTO.builder().province("경기").city("부천시").district("소사구").detail("범박동 113-70").build());
//        List<String> RepresentativeRegion = RRG.extract(places);
//        System.out.println("대표지역 = " + RepresentativeRegion);
//        List<String> expectedList = Arrays.asList("전국","경기","경기 부천시","부천시 소사구");
//        assertArrayEquals(expectedList.toArray(), RepresentativeRegion.toArray());
//    }
}