package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Book;
import com.example.springboot.service.IBookService;
import com.github.pagehelper.PageInfo;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AI 图书助手 - 基于 Spring AI + OpenAI 的智能图书服务
 */
@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private IBookService bookService;

    // ===== 1. 自然语言搜书 =====
    @GetMapping("/search")
    public Result search(@RequestParam String query) {
        // 先用传统搜索获取候选集
        PageInfo<Book> page = bookService.pageByCategory(null, query, 1, 10);
        List<Book> candidates = page.getList();

        if (candidates.isEmpty()) {
            return Result.success("抱歉，没有找到与「" + query + "」相关的图书。");
        }

        // 把候选集交给AI生成自然语言回答
        String bookList = candidates.stream()
                .map(b -> String.format("《%s》- %s / %s / 库存%d / 评分%d",
                        b.getName(), b.getAuthor(), b.getPublisher(), b.getNums(), b.getScore()))
                .collect(Collectors.joining("\n"));

        String aiAnswer = chatClient.prompt()
                .user(u -> u.text("""
                        用户搜索：{query}
                        匹配到的图书：
                        {bookList}
                        
                        请从中推荐最合适的3本，用自然语言说明推荐理由（50字以内/本）。
                        """)
                        .param("query", query)
                        .param("bookList", bookList))
                .call()
                .content();

        return Result.success(aiAnswer);
    }

    // ===== 2. AI 个性化推荐 =====
    @GetMapping("/recommend/{userId}")
    public Result recommend(@PathVariable Integer userId) {
        List<Book> recommended = bookService.recommendBooks(userId);

        if (recommended.isEmpty()) {
            return Result.success("您还没有借阅记录，多借几本书后我会为您精准推荐！");
        }

        String bookList = recommended.stream()
                .map(b -> String.format("《%s》- %s / %s", b.getName(), b.getAuthor(), b.getCategory()))
                .collect(Collectors.joining("\n"));

        String aiAnswer = chatClient.prompt()
                .user(u -> u.text("""
                        基于该用户的借阅历史，协同过滤算法推荐了以下图书：
                        {bookList}
                        
                        请用亲切的语气为用户解释推荐理由，让用户感受到个性化。
                        """)
                        .param("bookList", bookList))
                .call()
                .content();

        return Result.success(aiAnswer);
    }

    // ===== 3. AI 图书问答 =====
    @GetMapping("/ask")
    public Result ask(@RequestParam String question) {
        // 获取全库图书摘要作为上下文
        PageInfo<Book> all = bookService.pageByCategory(null, null, 1, 100);
        String libraryCtx = all.getList().stream()
                .map(b -> String.format("《%s》|%s|%s|库存%d", b.getName(), b.getAuthor(), b.getCategory(), b.getNums()))
                .collect(Collectors.joining("; "));

        String answer = chatClient.prompt()
                .user(u -> u.text("""
                        你是一个大学图书馆的AI管理员。图书馆当前藏书：
                        {library}
                        
                        用户问题：{question}
                        
                        请基于实际馆藏回答，如果没有相关图书如实告知。
                        """)
                        .param("library", libraryCtx)
                        .param("question", question))
                .call()
                .content();

        return Result.success(answer);
    }
}
