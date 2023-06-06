package shop.donutmarket.donut.domain.board.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.donutmarket.donut.domain.admin.model.Category;
import shop.donutmarket.donut.domain.board.model.Board;
import shop.donutmarket.donut.domain.board.model.Event;
import shop.donutmarket.donut.domain.user.model.User;

public class BoardReq {
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoardSaveReqDTO {
        // board
        @NotNull(message = "카테고리를 선택해주세요.")
        private Long categoryId;
        @NotBlank(message = "제목을 입력해주세요.")
        private String title;
        @NotBlank(message = "내용을 입력해주세요.")
        private String content;
        private String imgKey;
        @NotBlank(message = "광역시 또는 도를 입력해주세요.")
        private String state;
        @NotBlank(message = "시 또는 군 또는 자치구를 입력해주세요.")
        private String city;
        @NotBlank(message = "행정시,구 또는 읍,면,동을 입력해주세요.")
        private String town;
        //event
        @NotNull(message = "위도를 입력해주세요.")
        private double latitude;
        @NotNull(message = "경도를 입력해주세요.")
        private double longtitude;
        @NotNull(message = "수량을 입력해주세요.")
        private int qty;
        @NotBlank(message = "결제 방식을 선택해주세요.")
        private String paymentType;
        @NotNull(message = "종료일을 선택해주세요.")
        private LocalDateTime endAt;
        @NotNull(message = "가격을 입력해주세요.")
        private int price;
        // tag
        private List<String> comment;

        public Board toBoardEntity(Event event, Category category, String imgKey, User organizer){
            return Board.builder().category(category).title(title).organizer(organizer).content(content).imgKey(imgKey)
            .event(event).views(0).recommend(false).state(state).city(city).town(town).createdAt(LocalDateTime.now()).build();
        }

        public Event toEventEntity(){
            return Event.builder().latitude(latitude).longtitude(longtitude).qty(qty).paymentType(paymentType)
            .startAt(LocalDateTime.now()).endAt(endAt).price(price).createdAt(LocalDateTime.now()).build();
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BoardUpdateReqDTO {
        // board
        @NotNull(message = "게시글ID를 입력해주세요.")
        private Long id;
        //event
        private int qty;
        private String paymentType;
        private LocalDateTime endAt;
        private int price;
        // tag
        private List<String> comment;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BoardDeleteReqDTO {
        // board
        @NotNull(message = "게시글ID를 입력해주세요.")
        private Long boardId;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BoardSearchReqDto {

        @NotNull(message = "검색어를 입력해주세요.")
        private String word;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BoardSearchLocationReqDto {

        @NotBlank(message = "광역시 또는 도를 입력해주세요.")
        private String state;
        @NotBlank(message = "시 또는 군 또는 자치구를 입력해주세요.")
        private String city;
        @NotBlank(message = "행정시,구 또는 읍,면,동을 입력해주세요.")
        private String town;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BoardSearchCategoryReqDto {

        @NotNull(message = "카테고리 ID를 입력해주세요.")
        private Long categoryId;
    }
}

