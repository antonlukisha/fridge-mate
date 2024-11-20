package com.example.fridgemate.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

public class ElasticsearchConfig {
    public static RestClient createClient() {
        return RestClient.builder(
                new HttpHost("localhost", 9200)
        ).build();
    }
}
