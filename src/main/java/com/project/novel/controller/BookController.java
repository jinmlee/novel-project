package com.project.novel.controller;

import com.project.novel.dto.BookDto;
import com.project.novel.dto.BookUploadDto;
import com.project.novel.dto.CustomUserDetails;
import com.project.novel.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/book")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;

    @GetMapping("/write")
    public String write(Model model) {
        model.addAttribute("bookUploadDto",new BookUploadDto());
        return "book/write";
    }

    @PostMapping("/write")
    public String writeProcess(@Valid @ModelAttribute BookUploadDto bookUploadDto,
                               BindingResult bindingResult,
                               @AuthenticationPrincipal CustomUserDetails customUserDetails,
                               Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("bookUploadDto", bookUploadDto);
            return "book/write";
        }

        bookService.write(bookUploadDto, customUserDetails);
        return "redirect:/member/myBookList";
    }

    @GetMapping("/library")
    public String library() {
        return "book/library";
    }


    @GetMapping("/library/{bookId}")
    public String getBookById(@PathVariable(name="bookId") Long bookId, Model model,
                              @AuthenticationPrincipal CustomUserDetails customUserDetails,
                              @RequestParam(name="order", defaultValue = "Update") String order) {

        BookDto bookDto = bookService.getBook(bookId, customUserDetails.getLoggedMember().getId(), order);
        model.addAttribute("bookInfo", bookDto);

        return "book/info";
    }
}
