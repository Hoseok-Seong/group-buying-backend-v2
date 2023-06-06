package shop.donutmarket.donut.domain.main.dto;

import lombok.Getter;
import lombok.Setter;
import shop.donutmarket.donut.domain.board.model.Board;
import shop.donutmarket.donut.domain.myCategory.model.MyCategory;
import shop.donutmarket.donut.domain.myLocation.model.MyLocation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class MainResp {
    private List<MyCategoryDTO> myCategories;
    private MyLocationDTO myLocation;
    private List<BoardDTO> boards;

    public MainResp(List<MyCategory> myCategories,
                    MyLocationDTO myLocation,
                    List<Board> boards) {
        this.myCategories = myCategories
                .stream().map(myCategory -> new MyCategoryDTO(myCategory))
                .collect(Collectors.toList());
        this.myLocation = myLocation;
        this.boards = boards
                .stream().map((board -> new BoardDTO(board)))
                .collect(Collectors.toList());
    }

    @Getter
    @Setter
    public static class BoardDTO {
        private Long id;
        private String title;
        private String state;
        private String city;
        private String town;
        private String imgKey;
        private int price;
        private int qty;
        private double latitude;
        private double longtitude;
        private String paymentType;
        private boolean recommend;
        private LocalDateTime endAt;

        public BoardDTO(Board board) {
            this.id = board.getId();
            this.title = board.getTitle();
            this.state = board.getState();
            this.city = board.getCity();
            this.town = board.getTown();
            this.imgKey = board.getImgKey();
            this.price = board.getEvent().getPrice();
            this.qty = board.getEvent().getQty();
            this.latitude = board.getEvent().getLatitude();
            this.longtitude = board.getEvent().getLongtitude();
            this.paymentType = board.getEvent().getPaymentType();
            this.recommend = board.isRecommend();
            this.endAt = board.getEvent().getEndAt();
        }
    }

    @Getter
    @Setter
    public static class MyCategoryDTO {
        private String name;

        public MyCategoryDTO(MyCategory myCategory) {
            this.name = myCategory.getCategory().getName();
        }
    }

    @Getter
    @Setter
    public static class MyLocationDTO {
        private String town;

        public MyLocationDTO(MyLocation myLocation) {
            this.town = myLocation.getTown();
        }
    }
}
