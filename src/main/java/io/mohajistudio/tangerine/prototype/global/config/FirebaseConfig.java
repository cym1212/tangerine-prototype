package io.mohajistudio.tangerine.prototype.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FirebaseConfig {

    @Bean
    @Profile("local")
    FirebaseMessaging firebaseMessagingLocal() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(
                new ClassPathResource("firebase-service-account.json").getInputStream());
        FirebaseOptions firebaseOptions = FirebaseOptions.builder().setCredentials(googleCredentials).build();
        FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "trenvel");
        return FirebaseMessaging.getInstance(app);
    }

    @Bean
    @Profile("prod")
    FirebaseMessaging firebaseMessagingProd() throws IOException {
        // Firebase 서비스 계정 파일의 경로
        String serviceAccountFilePath = "/config/firebase-service-account.json";
        Path serviceAccountPath = Paths.get(serviceAccountFilePath);

        // Firebase 서비스 계정 파일을 FileSystemResource로 로드
        Resource serviceAccountResource = new FileSystemResource(serviceAccountPath);
        InputStream serviceAccountStream = serviceAccountResource.getInputStream();

        FirebaseOptions firebaseOptions = FirebaseOptions.builder().setCredentials(GoogleCredentials.fromStream(serviceAccountStream)).build();
        FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "trenvel");
        return FirebaseMessaging.getInstance(app);
    }
}