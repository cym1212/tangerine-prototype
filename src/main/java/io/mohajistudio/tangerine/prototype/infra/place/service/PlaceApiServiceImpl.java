package io.mohajistudio.tangerine.prototype.infra.place.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mohajistudio.tangerine.prototype.global.enums.ErrorCode;
import io.mohajistudio.tangerine.prototype.global.error.exception.BusinessException;
import io.mohajistudio.tangerine.prototype.infra.place.config.PlaceSearchApiProperties;
import io.mohajistudio.tangerine.prototype.infra.place.dto.PlaceKakaoSearchApiResultDTO;
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


@Service
@Slf4j
@RequiredArgsConstructor
public class PlaceApiServiceImpl implements PlaceApiService {
    private final PlaceSearchApiProperties placeApiConfig;

    public PlaceKakaoSearchApiResultDTO searchPlace(String query, int page, int size) {
        try {
            CloseableHttpClient client = HttpClientBuilder.create().build();
            HttpGet getRequest = new HttpGet(placeApiConfig.getUrl() + "?query=" + query + "&page=" + page + "&size=" + size);
            getRequest.addHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + placeApiConfig.getRestApiKey());

            CloseableHttpResponse response = client.execute(getRequest);
            ResponseHandler<String> handler = new BasicResponseHandler();
            String result = handler.handleResponse(response);
            ObjectMapper objectMapper = new ObjectMapper();
            PlaceKakaoSearchApiResultDTO placeKakaoSearchApiResultDTO = objectMapper.readValue(result, PlaceKakaoSearchApiResultDTO.class);

            if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
                return placeKakaoSearchApiResultDTO;
            } else {
                throw new BusinessException(result, ErrorCode.KAKAO_PLACE_SEARCH);
            }

        } catch (Exception e) {
            throw new BusinessException(ErrorCode.KAKAO_PLACE_SEARCH);
        }
    }

}
