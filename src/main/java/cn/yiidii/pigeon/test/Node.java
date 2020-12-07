package cn.yiidii.pigeon.test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author yiidii Wang
 * @desc [
 * {"name": "parentNode", "id": "0", "parentId": null},
 * {"name": "Node1", "id": "1", "parentId": "0"},
 * {"name": "Node2", "id": "2", "parentId": "0"},
 * {"name": "Node3", "id": "3", "parentId": "0"},
 * {"name": "Node1-1", "id": "1-1", "parentId": "1"},
 * {"name": "Node1-2", "id": "1-2", "parentId": "1"},
 * ]
 * 高效的构造成一棵树, 写出实现代码?
 */
@Data
@Slf4j
public class Node {
    @JSONField(ordinal = 1)
    private String id;
    @JSONField(ordinal = 2)
    private String name;
    @JSONField(ordinal = 3)
    private String parentId;
    @JSONField(ordinal = 4)
    private List<Node> children;

    public static void main(String[] args) {
        List<Node> orgNodeList = getOrgNodeList();
        List<Node> tree = buildTree(orgNodeList);
        log.info("Tree: {}", JSONObject.toJSONString(tree));
    }

    private static List<Node> getOrgNodeList() {
        String nodeListStr = " [\n" +
                "        {\"name\": \"parentNode\", \"id\": \"0\", \"parentId\": null},\n" +
                "        {\"name\": \"Node1\", \"id\": \"1\", \"parentId\": \"0\"},\n" +
                "        {\"name\": \"Node2\", \"id\": \"2\", \"parentId\": \"0\"},\n" +
                "        {\"name\": \"Node3\", \"id\": \"3\", \"parentId\": \"0\"},\n" +
                "        {\"name\": \"Node1-1\", \"id\": \"1-1\", \"parentId\": \"1\"},\n" +
                "        {\"name\": \"Node1-2\", \"id\": \"1-2\", \"parentId\": \"1\"},\n" +
                "    ] \n";
        List<Node> nodeList = new ArrayList<>();
        JSONArray nodeListJa = JSONArray.parseArray(nodeListStr);
        for (Object obj : nodeListJa) {
            JSONObject jo = (JSONObject) obj;
            Node node = new Node();
            try {
                BeanUtils.copyProperties(node, jo);
            } catch (Exception e) {
            }
            nodeList.add(node);
        }
        return nodeList;
    }

    private static List<Node> buildTree(List<Node> nodeList) {
        List<Node> treeList = new ArrayList<>();
        // 父节点
        for (Node node : nodeList) {
            if (Objects.isNull(node.getParentId())) {
                treeList.add(node);
            }
        }
        // 构建子节点
        for (Node tree : nodeList) {
            buildChild(treeList, tree);
        }
        return treeList;
    }

    private static void buildChild(List<Node> treeList, Node tree) {
        for (Node node : treeList) {
            if (StringUtils.equals(tree.getParentId(), node.getId())) {
                if (Objects.isNull(node.getChildren())) {
                    node.setChildren(new ArrayList<>());
                }
                node.getChildren().add(tree);
            }
            if (!Objects.isNull(node.getChildren())) {
                //递归
                buildChild(node.getChildren(), tree);
            }
        }
    }

}
