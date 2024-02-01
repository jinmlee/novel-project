package com.project.novel.controller;

import com.project.novel.dto.CommentDto;
import com.project.novel.dto.JoinDto;
import com.project.novel.entity.CommentEntity;
import com.project.novel.service.BoardService;
import com.project.novel.service.CommentService;
import com.project.novel.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;


    @PostMapping("/write")
    public ResponseEntity write(@ModelAttribute CommentDto commentDto){
        log.info("commentDto==={}",commentDto);
        Long saveResult = commentService.save(commentDto);
        if (saveResult != null){
            List<CommentDto> commentDtoList = commentService.findAll(commentDto.getBoardId());
            return new ResponseEntity<>(commentDtoList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("해당 댓글이 존재하지 않습니다. 다시 확인해주세요.", HttpStatus.NOT_FOUND);
        }
    }


}
