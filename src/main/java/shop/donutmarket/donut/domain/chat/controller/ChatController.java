package shop.donutmarket.donut.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    @MessageMapping("/chatroom/{id}")
    public void sendMessage(@DestinationVariable("id") Long id) {

    }
}
