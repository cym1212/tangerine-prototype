package io.mohajistudio.tangerine.prototype.infra.region.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mohajistudio.tangerine.prototype.global.enums.ErrorCode;
import io.mohajistudio.tangerine.prototype.global.error.exception.BusinessException;
import io.mohajistudio.tangerine.prototype.infra.region.config.RegionProperties;
import io.mohajistudio.tangerine.prototype.infra.region.dto.RegionCityDTO;
import io.mohajistudio.tangerine.prototype.infra.region.dto.RegionProvinceDTO;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RegionService {
    private final RegionProperties regionProperties;

    public List<RegionProvinceDTO> findAllRegions() {
        List<RegionCityDTO> cities = findAllCities();
        return findAllProvinces(cities);
    }

    private List<RegionProvinceDTO> findAllProvinces(List<RegionCityDTO> cities) {
        try {
            CloseableHttpClient client = HttpClientBuilder.create().build();
            HttpGet getRequest = new HttpGet(regionProperties.getUrl() + "&data=" + regionProperties.getProvinceKey() + "&key=" + regionProperties.getKey());

            CloseableHttpResponse response = client.execute(getRequest);
            ResponseHandler<String> handler = new BasicResponseHandler();
            String jsonString = handler.handleResponse(response);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonString);
            JsonNode featuresNode = rootNode.path("response").path("result").path("featureCollection").path("features");

            List<RegionProvinceDTO> regionProvinceList = new ArrayList<>();
            for (JsonNode featureNode : featuresNode) {
                String code = featureNode.path("properties").path("ctprvn_cd").asText();
                String name = featureNode.path("properties").path("ctp_kor_nm").asText();
                RegionProvinceDTO regionProvince = RegionProvinceDTO.builder().name(name).code(code).regionCities(new ArrayList<>()).build();
                cities.forEach(city -> {
                    String provinceCode = city.getCode().substring(0, 2);
                    if(Objects.equals(code, provinceCode)) {
                        regionProvince.addRegionCity(city);
                    }
                });
                regionProvinceList.add(regionProvince);
            }
            return regionProvinceList;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.REGION_PROVINCE);
        }
    }

    private List<RegionCityDTO> findAllCities() {
        try {
            CloseableHttpClient client = HttpClientBuilder.create().build();
            HttpGet getRequest = new HttpGet(regionProperties.getUrl() + "&data=" + regionProperties.getCityKey() + "&key=" + regionProperties.getKey());

            CloseableHttpResponse response = client.execute(getRequest);
            ResponseHandler<String> handler = new BasicResponseHandler();
            String jsonString = handler.handleResponse(response);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonString);
            JsonNode featuresNode = rootNode.path("response").path("result").path("featureCollection").path("features");

            List<RegionCityDTO> regionProvinceList = new ArrayList<>();
            for (JsonNode featureNode : featuresNode) {
                String code = featureNode.path("properties").path("sig_cd").asText();
                String fullName = featureNode.path("properties").path("full_nm").asText();
                String name = featureNode.path("properties").path("sig_kor_nm").asText();

                regionProvinceList.add(RegionCityDTO.builder().code(code).fullName(fullName).name(name).build());
            }
            return regionProvinceList;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.REGION_PROVINCE);
        }
    }
}
