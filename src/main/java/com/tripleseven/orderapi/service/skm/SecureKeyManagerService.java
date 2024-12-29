package com.tripleseven.orderapi.service.skm;



import com.tripleseven.orderapi.dto.secure.KeyResponseDTO;
import com.tripleseven.orderapi.exception.KeyManagerException;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustSelfSignedStrategy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.net.ssl.SSLContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.List;

import java.io.InputStream;

@Profile("prod")
@Service
public class SecureKeyManagerService {

    @Value("${url}")
    private String url;

    @Value("${appKey}")
    private String appKey;

    @Value("${keyId}")
    private String keyId;

    @Value("${P12_PASSWORD}")
    private String password;

    public String fetchSecretFromKeyManager() {
        try {

            // 환경변수에서 P12 파일 데이터를 읽고 디코딩
            String p12Data = System.getenv("P12_FILE");
            if (p12Data == null || p12Data.isEmpty()) {
                throw new KeyManagerException("P12 file data not found in environment variables");
            }
            // 디코딩된 P12 데이터를 InputStream으로 변환
            InputStream keyStoreInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(p12Data));

            // 키 저장소 객체를 만들되 키 유형이 PKCS12인 인스턴스를 가져오기
            KeyStore clientStore = KeyStore.getInstance("PKCS12");

            // Java KeyStore (JKS) 객체에 키 저장소 파일이랑 비밀번호 입력
            clientStore.load(keyStoreInputStream, password.toCharArray());            // 키 저장소 객체를 만들되 키 유형이 PKCS12인 인스턴스를 가져오기

            // SSL TLS 연결을 설정하는 과정
            // 1. 프로토콜은 TLS
            // 2. clientStore 안의 private 키 값을 가져오기 위해서 clientStore 와 password 로 private key 값 로드
            // 3. 인증 기관은 자체 서명된 인증서로 로드 할 수 있게 만듬
            SSLContext sslContext = SSLContexts.custom()
                    .setProtocol("TLS")
                    .loadKeyMaterial(clientStore, password.toCharArray()) // 키를 로드하기 위해 p12 파일과 비밀번호로 비밀키 꺼내서 sslcontext에 넣음
                    .loadTrustMaterial(new TrustSelfSignedStrategy()) // 자체 서명된 인증서를 신뢰하도록 TrustManager 설정
                    .build();

            SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
                    .setSslContext(sslContext)
                    .build();

            HttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
                    .setSSLSocketFactory(sslSocketFactory)
                    .build();

            // CloseableHttpClient (httpclient + 자원 관리) 객체를 만들고 HttpClients.custom()로 httpclient 객체 커스텀
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setConnectionManager(cm)
                    .evictExpiredConnections()
                    .build();

            // HttpComponentsClientHttpRequestFactory는 Spring에서 HTTP 요청을 처리할 때 사용하는 클래스
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setHttpClient(httpClient);

            RestTemplate restTemplate = new RestTemplate(requestFactory);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            // URL과 경로 지정해서 URI 생성
            URI uri = UriComponentsBuilder
                    .fromUriString(url)
                    .path("/keymanager/v1.0/appkey/{appkey}/secrets/{keyid}")
                    .encode()
                    .build()
                    .expand(appKey, keyId)
                    .toUri();

            ResponseEntity<KeyResponseDTO> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    KeyResponseDTO.class
            );

            if (response.getBody() != null && response.getBody() != null) {
                return response.getBody().getBody().getSecret();
            } else {
                throw new KeyManagerException("Invalid response from Key Manager");
            }

        } catch (KeyStoreException | IOException | CertificateException
                 | NoSuchAlgorithmException | UnrecoverableKeyException
                 | KeyManagementException e) {
            throw new KeyManagerException("Error while fetching secret: " + e.getMessage());
        }
    }
}
