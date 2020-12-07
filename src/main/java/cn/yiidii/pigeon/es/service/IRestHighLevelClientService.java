package cn.yiidii.pigeon.es.service;


import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchResponse;

import java.io.IOException;

public interface IRestHighLevelClientService {
    /**
     * 创建索引
     *
     * @param indexName
     * @param settings
     * @param mapping
     * @return
     * @throws IOException
     */
    public CreateIndexResponse createIndex(String indexName, String settings, String mapping) throws IOException;

    /**
     * 判断索引是否存在
     *
     * @param indexName 索引名称
     * @return
     * @throws IOException
     */
    public boolean indexExists(String indexName) throws IOException;

    /**
     * 搜索
     *
     * @param field
     * @param key
     * @param rangeField
     * @param from
     * @param to
     * @param termField
     * @param termVal
     * @param indexNames
     * @return
     * @throws IOException
     */
    public SearchResponse search(String field, String key, String rangeField, String
            from, String to, String termField, String termVal,
                                 String... indexNames) throws IOException;

    /**
     * 批量导入
     *
     * @param indexName
     * @param isAutoId  使用自动id 还是使用传入对象的id
     * @param source
     * @return
     * @throws IOException
     */
    public BulkResponse importAll(String indexName, boolean isAutoId, String source) throws IOException;

}
