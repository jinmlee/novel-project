package com.project.novel.service;

import com.project.novel.config.WebConfig;
import com.project.novel.dto.BoardDto;
import com.project.novel.entity.BoardEntity;
import com.project.novel.entity.BoardFileEntity;
import com.project.novel.repository.BoardFileRepository;
import com.project.novel.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {

    public final BoardRepository boardRepository;

    public final BoardFileRepository boardFileRepository;

    private WebConfig webConfig;


    public void save(BoardDto boardDto) throws IOException {
        // 파일 첨부 여부에 따른 로직 분리
        if (boardDto.getFile().isEmpty()) {
            BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDto);
            boardRepository.save(boardEntity);
        } else {
            BoardEntity boardEntity = BoardEntity.toSaveFileEntity(boardDto);
            Long saveId = boardRepository.save(boardEntity).getId();
            BoardEntity board = boardRepository.findById(saveId).orElseThrow(() -> new RuntimeException("Board not found"));

            for (MultipartFile boardFile : boardDto.getFile()) {
                if (!boardFile.isEmpty()) {
                    String originalFileName = boardFile.getOriginalFilename();
                    String copyFileName = System.currentTimeMillis() + "_" + originalFileName;
                    String savePath = "/Users/ijinmo/Desktop/novel-img/" + copyFileName;
                    boardFile.transferTo(new File(savePath));

                    // 리사이징 추가
                    int resizedWidth = 300;
                    int resizedHeight = 300;

                    // 리사이징된 이미지를 새로운 경로에 저장
                    String resizedSavePath = "/Users/ijinmo/Desktop/novel-img/resized_" + copyFileName;

                    // 이미지 리사이징
                    Thumbnails.of(savePath)
                            .size(resizedWidth, resizedHeight)
                            .toFile(resizedSavePath);

                    BoardFileEntity boardFileEntity = BoardFileEntity.toBoardFileEntity(board, originalFileName, "resized_" + copyFileName);
                    boardFileRepository.save(boardFileEntity);
                }
            }
        }
    }


    @Transactional
    public List<BoardDto> findAll() {
        List<BoardEntity> boardEntityList = boardRepository.findAll();
        List<BoardDto> boardDtoList = new ArrayList<>();
        for(BoardEntity boardEntity: boardEntityList) {
            boardDtoList.add(BoardDto.toBoardDto(boardEntity));
        }
        return boardDtoList;
    }

    @Transactional
    public void updateHit(Long id) {
        boardRepository.updateHit(id);
    }

    @Transactional
    public BoardDto findById(Long id) {
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(id);
        if (optionalBoardEntity.isPresent()){
            BoardEntity boardEntity = optionalBoardEntity.get();
            BoardDto boardDto = BoardDto.toBoardDto(boardEntity);
            return boardDto;
        }else {
            return null;
        }
    }

    @Transactional
    public BoardDto modify(BoardDto boardDto) {
        BoardEntity boardEntity = BoardEntity.toModifyEntity(boardDto);
        boardRepository.save(boardEntity);
        return findById(boardDto.getId());
    }

    public void delete(Long id, String currentUsername) {
        // 게시글 작성자 확인
        String boardWriter = boardRepository.findBoardWriterById(id);

        if (!currentUsername.equals(boardWriter)) {
            // 현재 로그인한 사용자와 게시글 작성자가 다를 경우 예외 발생
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }

        boardRepository.deleteById(id);
    }

    public void delete(Long id) {
        boardRepository.deleteById(id);
    }


    // 보통은 이런 경우 QueryDSL을 사용
    public Page<BoardDto> searchAndPaging(String category, String keyword, Pageable pageable) {
        if (category != null && !category.isEmpty() && keyword != null && !keyword.isEmpty()) {

            if ("title".equals(category)) {
                return boardRepository.findBySubjectContaining(keyword, pageable).map(BoardDto::toBoardDto);
            } else if ("content".equals(category)) {
                return boardRepository.findByContentContaining(keyword, pageable).map(BoardDto::toBoardDto);
            } else if ("writer".equals(category)) {
                return boardRepository.findByMemberIdContaining(keyword, pageable).map(BoardDto::toBoardDto);
            }
        }
        return boardRepository.findAll(pageable).map(BoardDto::toBoardDto);
    }

    public boolean checkDeletePermission(Long id, String username) {
        // 게시글 작성자 확인
        String boardWriter = boardRepository.findBoardWriterById(id);

        // 현재 로그인한 사용자와 게시글 작성자가 일치하면 삭제 권한이 있다고 판단
        return username.equals(boardWriter);
    }

    public boolean checkModifyPermission(Long id, String username) {
        // 게시글 작성자 확인
        String boardWriter = boardRepository.findBoardWriterById(id);

        // 현재 로그인한 사용자와 게시글 작성자가 일치하면 수정 권한이 있다고 판단
        return username.equals(boardWriter);
    }

}
