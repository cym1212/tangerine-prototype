package io.mohajistudio.tangerine.prototype.infra.place.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import io.mohajistudio.tangerine.prototype.global.enums.ErrorCode;
import io.mohajistudio.tangerine.prototype.global.error.exception.BusinessException;
import io.mohajistudio.tangerine.prototype.infra.place.config.PlaceApiProperties;
import io.mohajistudio.tangerine.prototype.infra.place.dto.KakaoPlaceApiDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@Service
@Slf4j
@RequiredArgsConstructor
public class PlaceApiServiceImpl implements PlaceApiService {
    private final PlaceApiProperties placeSearchApiProperties;

    public KakaoPlaceApiDTO searchPlace(String query, int page, int size) {
        try {
            CloseableHttpClient client = HttpClientBuilder.create().build();
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            HttpGet getRequest = new HttpGet(placeSearchApiProperties.getUrl() + "?query=" + encodedQuery + "&page=" + page + "&size=" + size);
            getRequest.addHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + placeSearchApiProperties.getRestApiKey());
            CloseableHttpResponse response = client.execute(getRequest);
            ResponseHandler<String> handler = new BasicResponseHandler();
            String jsonString = handler.handleResponse(response);

            if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
                ObjectMapper objectMapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
                return objectMapper.readValue(jsonString, KakaoPlaceApiDTO.class);
            } else {
                throw new BusinessException(jsonString, ErrorCode.KAKAO_PLACE_SEARCH);
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessException(ErrorCode.KAKAO_PLACE_SEARCH);
        }
    }
}
