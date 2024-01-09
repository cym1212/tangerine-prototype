package io.mohajistudio.tangerine.prototype.global.kakao.region;


import io.mohajistudio.tangerine.prototype.infra.place.dto.AddressDTO;
import io.mohajistudio.tangerine.prototype.infra.place.dto.PlaceKakaoSearchApiResultDTO;
import io.mohajistudio.tangerine.prototype.infra.place.service.RepresentServiceImpl;
import io.mohajistudio.tangerine.prototype.infra.place.service.PlaceApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

@SpringBootTest
@ActiveProfiles("dev")
class regionApiTest {

    @Autowired
    private PlaceApiService placeApiService;
    @Autowired
    private RepresentServiceImpl RRG;


    @Test
    void getRegionDatum() {
        PlaceKakaoSearchApiResultDTO data = placeApiService.searchPlace("범박고등학교",1,1);
        System.out.println("data = " + data.toString());
        String strings = data.getDocuments().get(0).getAddressName();
        System.out.println("strings = " + strings);
    }
    @Test
    void getRegionData() {

        List<PlaceKakaoSearchApiResultDTO> data = new ArrayList<>();
        data.add(placeApiService.searchPlace("강원도청",1,1));
        data.add(placeApiService.searchPlace("경기도청",1,1));
        data.add(placeApiService.searchPlace("서울시청",1,1));
        data.add(placeApiService.searchPlace("전라도청",1,1));
        data.add(placeApiService.searchPlace("경상도청",1,1));
        int i = 1;
        for(PlaceKakaoSearchApiResultDTO datum : data){
         System.out.println("data = " + datum.toString());
         String strings = datum.getDocuments().get(0).getAddressName();
         System.out.println("strings("+ i +") = " + strings);
            i++;
        }
    }
    @Test
    void generateProvinces(){
        List<AddressDTO> places = new ArrayList<>();
        places.add(AddressDTO.builder().province("서울").city("마포구").district("서교동").detail("393").build());
        places.add(AddressDTO.builder().province("서울").city("강남구").district("역삼동").detail("858").build());
        places.add(AddressDTO.builder().province("서울").city("용산구").district("동자동").detail("43-205").build());
        places.add(AddressDTO.builder().province("부산").city("동구").district("초량동").detail("1187-1").build());
        places.add(AddressDTO.builder().province("경기").city("용인시").district("기흥구").detail("상갈동 496").build());
        List<String> RepresentativeRegion = RRG.extract(places);
        System.out.println("대표지역 = " + RepresentativeRegion);
        List<String> expectedList = Arrays.asList("전국","서울");
        assertArrayEquals(expectedList.toArray(), RepresentativeRegion.toArray());
    }
    @Test
    void generateCities(){
        List<AddressDTO> places = new ArrayList<>();
        places.add(AddressDTO.builder().province("경기").city("광명시").district("철산동").detail("222-1").build());
        places.add(AddressDTO.builder().province("경기").city("광명시").district("하안4동").detail("25").build());
        places.add(AddressDTO.builder().province("경기").city("광명시").district("소하동").detail("1335").build());
        places.add(AddressDTO.builder().province("경기").city("광명시").district("일직동").detail("276-8").build());
        places.add(AddressDTO.builder().province("경기").city("광명시").district("광명동").detail("158-211").build());

        List<String> RepresentativeRegion = RRG.extract(places);
        System.out.println("대표지역 = " + RepresentativeRegion);
        List<String>  expectedList = Arrays.asList("전국","경기","경기 광명시");
        assertArrayEquals(expectedList.toArray(), RepresentativeRegion.toArray());
    }
    @Test
    void generateDistricts(){
        List<AddressDTO> places = new ArrayList<>();
        places.add(AddressDTO.builder().province("인천").city("연수구").district("송도동").detail("24-5").build());
        places.add(AddressDTO.builder().province("인천").city("연수구").district("송도동").detail(" ").build());
        places.add(AddressDTO.builder().province("인천").city("연수구").district("송도동").detail("9-1").build());
        places.add(AddressDTO.builder().province("인천").city("연수구").district("송도동").detail("806-12").build());
        places.add(AddressDTO.builder().province("인천").city("연수구").district("송도동").detail("5-1").build());
        List<String> RepresentativeRegion = RRG.extract(places);
        System.out.println("대표지역 = " + RepresentativeRegion);
        List<String> expectedList = Arrays.asList("전국","인천","인천 연수구","연수구 송도동");
        assertArrayEquals(expectedList.toArray(), RepresentativeRegion.toArray());
    }
    @Test //동일 지역명 예외처리
    void generateException1(){
        List<AddressDTO> places = new ArrayList<>();
        places.add(AddressDTO.builder().province("서울").city("강서구").district("등촌동").detail("24-5").build());
        places.add(AddressDTO.builder().province("부산").city("강서구").district("눌차동").detail("55-1").build());
        places.add(AddressDTO.builder().province("부산").city("동래구").district("사직동").detail("55-1").build());
        places.add(AddressDTO.builder().province("부산").city("동래구").district("사직동").detail("56-1").build());

        List<String> RepresentativeRegion = RRG.extract(places);
        System.out.println("대표지역 = " + RepresentativeRegion);
        List<String> expectedList = Arrays.asList("전국","부산","부산 동래구","동래구 사직동");
        assertArrayEquals(expectedList.toArray(), RepresentativeRegion.toArray());
    }
    @Test //동일 구 예외처리
    void generateException2(){
        List<AddressDTO> places = new ArrayList<>();
        places.add(AddressDTO.builder().province("서울").city("강서구").district("등촌동").detail("24-5").build());
        places.add(AddressDTO.builder().province("서울").city("강서구").district("등촌동").detail("24-5").build());
        places.add(AddressDTO.builder().province("부산").city("강서구").district("눌차동").detail("55-1").build());
        places.add(AddressDTO.builder().province("부산").city("강서구").district("눌차동").detail("55-1").build());


        List<String> RepresentativeRegion = RRG.extract(places);
        System.out.println("대표지역 = " + RepresentativeRegion);
        List<String> expectedList = Arrays.asList("전국","부산","서울","서울 강서구","부산 강서구","강서구 등촌동","강서구 눌차동");
        assertArrayEquals(expectedList.toArray(), RepresentativeRegion.toArray());
    }  @Test //동일 동 예외처리
    void generateException3(){
        List<AddressDTO> places = new ArrayList<>();
        places.add(AddressDTO.builder().province("서울").city("강서구").district("등촌동").detail("24-5").build());
        places.add(AddressDTO.builder().province("서울").city("강서구").district("등촌동").detail("24-5").build());
        places.add(AddressDTO.builder().province("서울").city("팔달구").district("등촌동").detail("55-1").build());
        places.add(AddressDTO.builder().province("서울").city("팔달구").district("등촌동").detail("55-1").build());


        List<String> RepresentativeRegion = RRG.extract(places);
        System.out.println("대표지역 = " + RepresentativeRegion);
        List<String> expectedList = Arrays.asList("전국","서울","서울 강서구","서울 팔달구","강서구 등촌동","팔달구 등촌동");
        assertArrayEquals(expectedList.toArray(), RepresentativeRegion.toArray());
    }
    @Test //동일 지역 3개이상 예외처리
    void generateException4(){
        List<AddressDTO> places = new ArrayList<>();
        places.add(AddressDTO.builder().province("서울").city("강서구").district("등촌동").detail("24-5").build());
        places.add(AddressDTO.builder().province("서울").city("강서구").district("등촌동").detail("24-5").build());
        places.add(AddressDTO.builder().province("부산").city("강서구").district("눌차동").detail("55-1").build());
        places.add(AddressDTO.builder().province("부산").city("강서구").district("눌차동").detail("55-1").build());
        places.add(AddressDTO.builder().province("인천").city("강서구").district("개안동").detail("55-1").build());
        places.add(AddressDTO.builder().province("인천").city("강서구").district("개안동").detail("55-1").build());


        List<String> RepresentativeRegion = RRG.extract(places);
        System.out.println("대표지역 = " + RepresentativeRegion);
        List<String> expectedList = Arrays.asList("전국","인천","부산","서울","인천 강서구","서울 강서구","부산 강서구","강서구 등촌동","강서구 개안동","강서구 눌차동");
        assertArrayEquals(expectedList.toArray(), RepresentativeRegion.toArray());
    }
    @Test
    void generateKorea(){
        List<AddressDTO> places = new ArrayList<>();
        places.add(AddressDTO.builder().province("강원특별자치도").city("춘천시").district("봉의동").detail("15").build());
        places.add(AddressDTO.builder().province("경기").city("수원시").district("영통구").detail("이의동").build());


        List<String> RepresentativeRegion = RRG.extract(places);
        System.out.println("대표지역 = " + RepresentativeRegion);
        List<String> expectedList = Arrays.asList("전국");
        assertArrayEquals(expectedList.toArray(), RepresentativeRegion.toArray());
    }
    @Test
    void generateOne(){
        List<AddressDTO> places = new ArrayList<>();
        places.add(AddressDTO.builder().province("경기").city("부천시").district("소사구").detail("범박동 113-70").build());
        List<String> RepresentativeRegion = RRG.extract(places);
        System.out.println("대표지역 = " + RepresentativeRegion);
        List<String> expectedList = Arrays.asList("전국","경기","경기 부천시","부천시 소사구");
        assertArrayEquals(expectedList.toArray(), RepresentativeRegion.toArray());
    }
}