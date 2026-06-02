package com.example.onlyone.Service.ServiceImpl;

import com.example.onlyone.Entity.SensitiveWord;
import com.example.onlyone.Mapper.SensitiveWordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class SensitiveWordEngine {



    private final SensitiveWordMapper sensitiveWordMapper;
    private volatile TrieNode root;  // 当前生效的AC自动机根节点

    public SensitiveWordEngine(SensitiveWordMapper sensitiveWordMapper) {
        this.sensitiveWordMapper = sensitiveWordMapper;
        this.root = buildTrie();
    }

    /**
     * 判断文本是否包含任何敏感词
     */
    public boolean containsSensitiveWord(String text) {
        if (text == null || text.isEmpty()) return false;
        TrieNode node = root;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            while (node != root && !node.children.containsKey(c)) {
                node = node.fail;
            }
            if (node.children.containsKey(c)) {
                node = node.children.get(c);
                if (node.isEnd) return true;
            }
        }
        return false;
    }

    /**
     * 返回文本中所有命中的敏感词
     */
    public Set<String> findAllSensitiveWords(String text) {
        Set<String> result = new HashSet<>();
        if (text == null || text.isEmpty()) return result;
        TrieNode node = root;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            while (node != root && !node.children.containsKey(c)) {
                node = node.fail;
            }
            if (node.children.containsKey(c)) {
                node = node.children.get(c);
                if (node.isEnd) {
                    result.add(node.word);
                }
                // 检查fail指针链上是否有其他结束节点
                TrieNode temp = node.fail;
                while (temp != root) {
                    if (temp.isEnd) {
                        result.add(temp.word);
                    }
                    temp = temp.fail;
                }
            }
        }
        return result;
    }

    /**
     * 重新加载敏感词库（管理接口触发）
     */
    public synchronized void reload() {
        this.root = buildTrie();
        log.info("敏感词库重载完成");
    }


    //内部节点
    static class TrieNode {
        //key是字符
        Map<Character, TrieNode> children = new HashMap<>();
        TrieNode fail;
        boolean isEnd;
        String word;

    }

    //添加敏感词
    private void addWord(TrieNode root, String word) {
        TrieNode node = root;
        //遍历每一个字符
        for(char c : word.toCharArray()) {
            if(!node.children.containsKey(c)) {
                node.children.put(c, new TrieNode());
            }
            node = node.children.get(c);

        }
        node.isEnd = true;
        node.word = word;

    }

    //构建敏感树
    private TrieNode buildTrie() {

        //获取全部敏感词
        List<SensitiveWord> words = sensitiveWordMapper.selectList();
        TrieNode root = new TrieNode();
        //构建字典树
        for (SensitiveWord word : words) {
            addWord(root, word.getWord());
        }
        //bfs编写失配指针
        buildFailPointers(root);
        return root;
    }

    //构建失败指针
    private void buildFailPointers(TrieNode root){
        Queue<TrieNode> queue = new LinkedList<>();
        //根节点的失配指针指向自己
        root.fail = root;
        //遍历子节点
        for (TrieNode node : root.children.values()) {
            node.fail = root;
            queue.add(node);
        }
        while (!queue.isEmpty()) {
            //获取第一个节点
            TrieNode current = queue.poll();
            //获取该节点的所有子节点
            for (Map.Entry<Character, TrieNode> entry : current.children.entrySet()) {
                char c = entry.getKey();
                TrieNode child = entry.getValue();
                TrieNode fail = current.fail;
                while (fail != root && !fail.children.containsKey(c)) {
                    fail = fail.fail;
                }
                if (fail.children.containsKey(c)) {
                    child.fail = fail.children.get(c);
                }else {
                    child.fail = root;
                }
                queue.add(child);
            }

        }
    }

}
