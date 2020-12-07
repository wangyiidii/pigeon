package cn.yiidii.pigeon.es.service.impl;

import cn.yiidii.pigeon.es.service.IRestHighLevelClientService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author yiidii Wang
 * @desc es常用操作
 */
@Service
@Slf4j
public class RestHighLevelClientService implements IRestHighLevelClientService {

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private ObjectMapper mapper;

    @Override
    public CreateIndexResponse createIndex(String indexName, String settings, String mapping) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(indexName);
        if (null != settings && !"".equals(settings)) {
            request.settings(settings, XContentType.JSON);
        }
        if (null != mapping && !"".equals(mapping)) {
            request.mapping(mapping, XContentType.JSON);
        }
        return client.indices().create(request, RequestOptions.DEFAULT);
    }

    @Override
    public boolean indexExists(String indexName) throws IOException {
        GetIndexRequest request = new GetIndexRequest();
        request.indices(indexName);
        return client.indices().exists(request, RequestOptions.DEFAULT);
    }

    @Override
    public SearchResponse search(String field, String key, String rangeField, String
            from, String to, String termField, String termVal,
                                 String... indexNames) throws IOException {
        SearchRequest request = new SearchRequest(indexNames);

        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(new MatchQueryBuilder(field, key))
                .must(new RangeQueryBuilder(rangeField).from(from).to(to))
                .must(new TermsQueryBuilder(termField, termVal))
        ;
        builder.query(boolQueryBuilder);
        request.source(builder);
        log.info("[搜索语句为:{}]", request.source().toString());
        return client.search(request, RequestOptions.DEFAULT);
    }

    @Override
    public BulkResponse importAll(String indexName, boolean isAutoId, String source) throws IOException {
        if (0 == source.length()) {
            //todo 抛出异常 导入数据为空
        }
        BulkRequest request = new BulkRequest();
        JsonNode jsonNode = mapper.readTree(source);

        if (jsonNode.isArray()) {
            for (JsonNode node : jsonNode) {
                if (isAutoId) {
                    request.add(new IndexRequest(indexName).source(node.asText(), XContentType.JSON));
                } else {
                    request.add(new IndexRequest(indexName)
                            .id(node.get("id").asText())
                            .source(node.asText(), XContentType.JSON));
                }
            }
        }
        return client.bulk(request, RequestOptions.DEFAULT);
    }
}
