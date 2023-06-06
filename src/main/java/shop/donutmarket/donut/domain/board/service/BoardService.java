package shop.donutmarket.donut.domain.board.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import shop.donutmarket.donut.domain.admin.model.Category;
import shop.donutmarket.donut.domain.admin.repository.CategoryRepository;
import shop.donutmarket.donut.domain.board.dto.BoardReq.BoardDeleteReqDTO;
import shop.donutmarket.donut.domain.board.dto.BoardReq.BoardSaveReqDTO;
import shop.donutmarket.donut.domain.board.dto.BoardReq.BoardSearchCategoryReqDto;
import shop.donutmarket.donut.domain.board.dto.BoardReq.BoardSearchLocationReqDto;
import shop.donutmarket.donut.domain.board.dto.BoardReq.BoardSearchReqDto;
import shop.donutmarket.donut.domain.board.dto.BoardReq.BoardUpdateReqDTO;
import shop.donutmarket.donut.domain.board.dto.BoardResp.BoardSaveRespDTO;
import shop.donutmarket.donut.domain.board.dto.BoardResp.BoardUpdateRespDTO;
import shop.donutmarket.donut.domain.board.model.Board;
import shop.donutmarket.donut.domain.board.model.Event;
import shop.donutmarket.donut.domain.board.model.Tag;
import shop.donutmarket.donut.domain.board.repository.BoardRepository;
import shop.donutmarket.donut.domain.board.repository.EventRepository;
import shop.donutmarket.donut.domain.board.repository.TagRepository;
import shop.donutmarket.donut.domain.user.model.User;
import shop.donutmarket.donut.domain.user.repository.UserRepository;
import shop.donutmarket.donut.global.auth.MyUserDetails;
import shop.donutmarket.donut.global.exception.Exception400;
import shop.donutmarket.donut.global.exception.Exception403;
import shop.donutmarket.donut.global.exception.Exception404;
import shop.donutmarket.donut.global.exception.Exception500;
import shop.donutmarket.donut.global.s3.S3Service;
import shop.donutmarket.donut.global.util.S3KeyGenerator;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final EventRepository eventRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final S3Service s3Service;

    @Transactional
    public BoardSaveRespDTO 게시글작성(MultipartFile multipartFile, BoardSaveReqDTO boardSaveReqDTO,
                                  @AuthenticationPrincipal MyUserDetails myUserDetails) {
        Optional<User> userOP = userRepository.findByIdJoinFetch(myUserDetails.getUser().getId());

        if (userOP.isEmpty()) {
            throw new Exception404("존재하지 않는 유저입니다");
        }

        Optional<Category> categoryOP = categoryRepository.findById(boardSaveReqDTO.getCategoryId());
        if (categoryOP.isEmpty()) {
            throw new Exception404("존재하지 않는 카테고리입니다");
        }

        try {
            // event 먼저 save
            Event event = boardSaveReqDTO.toEventEntity();
            event = eventRepository.save(event);
            User user = userOP.get();
            Category category = categoryOP.get();

            String imgKey;
            if (multipartFile == null) {
                // 존재하지 않을 경우 "카테고리 이름-Default".jpg
                imgKey = category.getName() + "-Default.jpg";
            } else {
                // 존재하면 S3에 업로드
                imgKey = S3KeyGenerator.makeKey(multipartFile);
                s3Service.upload(multipartFile, imgKey);
            }
            Board board = boardRepository.save(boardSaveReqDTO.toBoardEntity(event, category, imgKey, user));

            // tag save
            List<Tag> tagList = new ArrayList<>();
            if (boardSaveReqDTO.getComment() != null) {
                for (String comment : boardSaveReqDTO.getComment()) {
                    Tag tag = Tag.builder().boardId(board.getId()).comment(comment)
                            .createdAt(LocalDateTime.now()).build();
                    tagRepository.save(tag);
                    tagList.add(tag);
                }
            }

            BoardSaveRespDTO boardSaveRespDTO = new BoardSaveRespDTO(board, tagList);
            return boardSaveRespDTO;
        } catch (Exception e) {
            throw new Exception500("게시글 작성 실패 : " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Board 게시글상세보기(Long id) {
        Optional<Board> boardOp = boardRepository.findByIdWithAll(id);
        if (boardOp.isEmpty()) {
            throw new Exception404("존재하지 않는 게시글입니다");
        }

        Board boardPS = boardOp.get();

        if (boardPS.getStatusCode() == 203) {
            throw new Exception400("이미 삭제된 게시글입니다");
        }

        try {
            User organizer = boardPS.getOrganizer();
            Event event = boardPS.getEvent();
            Board board = Board.builder().id(boardPS.getId()).category(boardPS.getCategory()).title(boardPS.getTitle())
                    .imgKey(boardPS.getImgKey())
                    .organizer(organizer).content(boardPS.getContent()).event(event).statusCode(boardPS.getStatusCode())
                    .state(boardPS.getState()).city(boardPS.getCity()).town(boardPS.getTown())
                    .createdAt(boardPS.getCreatedAt()).build();
            return board;
        } catch (Exception e) {
            throw new Exception500("게시글 상세보기 실패 : " + e.getMessage());
        }
    }

    @Transactional
    public BoardUpdateRespDTO 게시글수정(BoardUpdateReqDTO boardUpdateReqDTO,
            @AuthenticationPrincipal MyUserDetails myUserDetails) {
        User userOP = myUserDetails.getUser();
        Optional<Board> boardOP = boardRepository.findByIdWithEvent(boardUpdateReqDTO.getId());
        if (boardOP.isEmpty()) {
            throw new Exception404("존재하지 않는 게시글입니다");
        }
        Board boardPS = boardOP.get();

        if (boardPS.getStatusCode() == 203) {
            throw new Exception400("이미 삭제된 게시글입니다");
        }

        // 권한 체크
        if (!Objects.equals(boardPS.getOrganizer().getId(), userOP.getId())) {
            throw new Exception403("게시글을 수정할 권한이 없습니다");
        }
        try {
            boardPS.getEvent().updateEvent(
                    boardUpdateReqDTO.getQty(), boardUpdateReqDTO.getPaymentType(),
                    boardUpdateReqDTO.getEndAt(), boardUpdateReqDTO.getPrice());

            List<String> tagList = new ArrayList<>();

            tagRepository.deleteAllByBoardId(boardUpdateReqDTO.getId());
            List<String> commentList = boardUpdateReqDTO.getComment();
            if (commentList != null) { // null 체크
                for (String comment : commentList) {
                    if (comment == null || comment.trim().isEmpty()) { // null 값 또는 빈 문자열인 경우 스킵
                        continue;
                    }
                    Tag tag = Tag.builder()
                            .boardId(boardPS.getId())
                            .comment(comment)
                            .createdAt(LocalDateTime.now())
                            .build();
                    tagRepository.save(tag);
                    tagList.add(comment);
                }
            }
            List<Tag> tag = tagRepository.findAllByBoardId(boardUpdateReqDTO.getId());

            Optional<Board> boardOP2 = boardRepository.findByIdWithAll(boardUpdateReqDTO.getId());
            Board board = boardOP2.get();

            BoardUpdateRespDTO boardUpdateRespDTO = new BoardUpdateRespDTO(board, tag);

            return boardUpdateRespDTO;
        } catch (Exception e) {
            throw new Exception500("게시글 수정하기 실패 : " + e.getMessage());
        }
    }

    @Transactional
    public void 게시글삭제(BoardDeleteReqDTO boardDeleteReqDTO, @AuthenticationPrincipal MyUserDetails myUserDetails) {

        User userOP = myUserDetails.getUser();
        Optional<Board> boardOP = boardRepository.findById(boardDeleteReqDTO.getBoardId());
        if (boardOP.isEmpty()) {
            throw new Exception404("존재하지 않는 게시글입니다");
        }

        Board boardPS = boardOP.get();

        // 권한 체크
        if (!Objects.equals(boardPS.getOrganizer().getId(), userOP.getId())) {
            throw new Exception403("게시글을 삭제할 권한이 없습니다");
        }

        if (boardPS.getStatusCode() == 203) {
            throw new Exception400("이미 삭제된 게시글입니다");
        }

        try {
            // 상태코드 삭제로
        } catch (Exception e) {
            throw new Exception500("게시글 삭제하기 실패 : " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<Board> 키워드검색(BoardSearchReqDto boardSearchReqDto) {

        List<Board> searchResult = new ArrayList<>();
        try {
            List<Long> searchIdList = boardRepository.findIdsBySearchWord(boardSearchReqDto.getWord());
            for (Long id : searchIdList) {
                Optional<Board> boardOP = boardRepository.findByIdWithAll(id);
                if (boardOP.isEmpty()) {
                    continue;
                } else {
                    Board boardPS = boardOP.get();
                    searchResult.add(boardPS);
                }
            }
        } catch (Exception e) {
            throw new Exception500("검색에 실패했습니다.");
        }

        if (searchResult.isEmpty()) {
            throw new Exception404("검색에 맞는 결과가 없습니다");
        }
        return searchResult;
    }

    @Transactional(readOnly = true)
    public List<Board> 지역별검색(BoardSearchLocationReqDto boardSearchLocationReqDto) {
        List<Board> searchResult = new ArrayList<>();
        try {
            List<Long> searchIdList = boardRepository.findByLocation(boardSearchLocationReqDto.getState(),
                    boardSearchLocationReqDto.getCity(), boardSearchLocationReqDto.getTown());
            for (Long id : searchIdList) {
                Optional<Board> boardOP = boardRepository.findByIdWithAll(id);
                if (boardOP.isEmpty()) {
                    continue;
                } else {
                    Board boardPS = boardOP.get();
                    searchResult.add(boardPS);
                }
            }
        } catch (Exception e) {
            throw new Exception500("검색에 실패했습니다.");
        }

        if (searchResult.isEmpty()) {
            throw new Exception404("검색에 맞는 결과가 없습니다");
        }
        return searchResult;
    }

    @Transactional(readOnly = true)
    public List<Board> 카테고리검색(BoardSearchCategoryReqDto boardSearchCategoryReqDto) {
        List<Board> searchResult = new ArrayList<>();
        try {
            searchResult = boardRepository.findByCategory(boardSearchCategoryReqDto.getCategoryId());
        } catch (Exception e) {
            throw new Exception500("검색에 실패했습니다.");
        }
        if (searchResult.isEmpty()) {
            throw new Exception404("검색에 맞는 결과가 없습니다");
        }
        return searchResult;
    }

}
