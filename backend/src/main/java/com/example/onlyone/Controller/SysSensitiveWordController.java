package com.example.onlyone.Controller;

import com.example.onlyone.Entity.SensitiveWord;
import com.example.onlyone.Mapper.SensitiveWordMapper;
import com.example.onlyone.Service.ServiceImpl.SensitiveWordEngine;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/sys/sensitive-word")
public class SysSensitiveWordController {

    @Resource
    private SensitiveWordMapper sensitiveWordMapper;

    @Resource
    private SensitiveWordEngine sensitiveWordEngine;

    @GetMapping("/list")
    public List<SensitiveWord> list() {
        return sensitiveWordMapper.selectList();
    }

    @PostMapping("/add")
    public String add(@RequestParam String word, @RequestParam(defaultValue = "1") Integer level) {
        SensitiveWord sw = new SensitiveWord();
        sw.setWord(word);
        sw.setLevel(level);
        sw.setCreatedAt(LocalDateTime.now());
        sensitiveWordMapper.insert(sw);
        sensitiveWordEngine.reload();
        return "添加成功";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long id) {
        sensitiveWordMapper.deleteById(id);
        sensitiveWordEngine.reload();
        return "删除成功";
    }

    @PostMapping("/reload")
    public String reload() {
        sensitiveWordEngine.reload();
        return "敏感词库已重载";
    }
}