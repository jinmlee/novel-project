package com.project.novel.service;

import com.project.novel.dto.CommentDto;
import com.project.novel.entity.BoardEntity;
import com.project.novel.entity.CommentEntity;
import com.project.novel.repository.BoardRepository;
import com.project.novel.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final BoardRepository boardRepository;


    /*public Long save(CommentDto commentDto) {
        // 부모 엔티티 조회
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(commentDto.getBoardId());
        if (optionalBoardEntity.isPresent()){
            BoardEntity boardEntity = optionalBoardEntity.get();
            CommentEntity commentEntity = CommentEntity.toSaveEntity(commentDto, boardEntity);
            return commentRepository.save(commentEntity).getId();
        } else {
            return null;
        }
    }*/

    public Long save(CommentDto commentDto) {
        // 부모 엔티티 조회
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(commentDto.getBoardId());
        if (optionalBoardEntity.isPresent()){
            BoardEntity boardEntity = optionalBoardEntity.get();
            // CommentDto.toSaveEntity 메서드 업데이트
            CommentEntity commentEntity = CommentEntity.toSaveEntity(commentDto, boardEntity);
            return commentRepository.save(commentEntity).getId();
        } else {
            return null;
        }
    }

    public List<CommentDto> findAll(Long boardId) {
        //select * from board_comment where board_id=? order by id desc;
        BoardEntity boardEntity = boardRepository.findById(boardId).get();
        List<CommentEntity> commentEntityList = commentRepository.findAllByBoardEntityOrderByIdDesc(boardEntity);
        //EntityList -> DtoList 변환
        List<CommentDto> commentDtoList = new ArrayList<>();
        for (CommentEntity commentEntity: commentEntityList){
            CommentDto commentDto = CommentDto.toCommentDto(commentEntity, boardId);
            commentDtoList.add(commentDto);
        }
        return commentDtoList;

    }

    public List<CommentEntity> getAllComments() {
        // 댓글 목록을 가져오는 로직
        return commentRepository.findAll();
    }

}
