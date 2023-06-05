package shop.donutmarket.donut.domain.board.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.donutmarket.donut.domain.board.dto.BoardReq.BoardDeleteReqDTO;
import shop.donutmarket.donut.domain.board.dto.BoardReq.BoardSaveReqDTO;
import shop.donutmarket.donut.domain.board.dto.BoardReq.BoardSearchCategoryReqDto;
import shop.donutmarket.donut.domain.board.dto.BoardReq.BoardSearchReqDto;
import shop.donutmarket.donut.domain.board.dto.BoardReq.BoardUpdateReqDTO;
import shop.donutmarket.donut.domain.board.dto.BoardResp.BoardDetailRespDTO;
import shop.donutmarket.donut.domain.board.dto.BoardResp.BoardSaveRespDTO;
import shop.donutmarket.donut.domain.board.dto.BoardResp.BoardUpdateRespDTO;
import shop.donutmarket.donut.domain.board.model.Board;
import shop.donutmarket.donut.domain.board.service.BoardService;
import shop.donutmarket.donut.domain.board.service.TagService;
import shop.donutmarket.donut.global.auth.MyUserDetails;
import shop.donutmarket.donut.global.dto.ResponseDTO;


@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {
    
    private final BoardService boardService;
    private final TagService tagService;

//    @PostMapping
//    public ResponseEntity<?> save(@AuthenticationPrincipal MyUserDetails myUserDetails, @RequestBody @Valid BoardSaveReqDTO boardSaveReqDTO, BindingResult bindingResult) {
//        BoardSaveRespDTO saveRespDTO = boardService.게시글작성(boardSaveReqDTO, myUserDetails);
//        ResponseDTO<?> responseDTO = new ResponseDTO<>(saveRespDTO);
//        return ResponseEntity.ok(responseDTO);
//    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {

        Board board = boardService.게시글상세보기(id);
        List<String> tags = tagService.상세보기(id);

        BoardDetailRespDTO detailRespDTO = new BoardDetailRespDTO(board, tags);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(detailRespDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping
    public ResponseEntity<?> update(@AuthenticationPrincipal MyUserDetails myUserDetails, @RequestBody @Valid BoardUpdateReqDTO boardUpdateReqDTO, BindingResult bindingResult) {
        BoardUpdateRespDTO updateRespDTO = boardService.게시글수정(boardUpdateReqDTO, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(updateRespDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/delete")
    public ResponseEntity<?> delete(@AuthenticationPrincipal MyUserDetails myUserDetails, @RequestBody @Valid BoardDeleteReqDTO boardDeleteReqDTO, BindingResult bindingResult){
        boardService.게시글삭제(boardDeleteReqDTO, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>("게시글 삭제 성공");
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestBody @Valid BoardSearchReqDto boardSearchReqDto, BindingResult bindingResult) {
	    List<Board> searchresult = boardService.키워드검색(boardSearchReqDto);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(searchresult);
	    return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/search/category")
    public ResponseEntity<?> search(@RequestBody @Valid BoardSearchCategoryReqDto boardSearchCategoryReqDto, BindingResult bindingResult) {
	    List<Board> searchresult = boardService.카테고리검색(boardSearchCategoryReqDto);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(searchresult);
	    return ResponseEntity.ok(responseDTO);
    }
}
