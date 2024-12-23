package com.tripleseven.orderapi.common.environmentutils;

// 테스트 코드에서 Mock 사용 시 Id 를 null 값으로 가져와 Exception 발생 방지 용도
public class EnvironmentUtil {
    public static boolean isTestEnvironment() {
        return System.getProperty("sun.java.command").contains("Test");
    }
}
