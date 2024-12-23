package org.Plucky;

import static org.Plucky.callapi.CallApi.getApiMessage;

public class Main {
    public static void main(String[] args) {
        String apiMessage = getApiMessage();
        System.out.println(apiMessage);
    }


}