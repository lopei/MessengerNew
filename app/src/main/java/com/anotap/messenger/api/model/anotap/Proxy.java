package com.anotap.messenger.api.model.anotap;

public class Proxy {

//  "proxy": {
//        "ip": "188.130.138.69",
//                "port": "8080",
//                "country": "Russia",
//                "newversionip": "188.130.138.69",
//                "newversionport": "8080",
//                "newversioncountry": "Russia",
//                "newip": "188.130.138.69",
//                "newport": "8080",
//                "newcountry": "Russia"
//    },
//            "info": {
//        "number": "29",
//                "text": "Не забываем ставить пятёрочки, кто еще не поставил. Мы Вас любим, будь котиком ❤",
//                "url": "https://play.google.com/store/apps/details?id=com.staslysenko.proxyclientforvk"
//    }
//}

    private String ip;
    private int port;

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
