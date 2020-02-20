package com.anuj.telegrambot.model.db;

import com.anuj.telegrambot.contant.LanguageType;
import com.anuj.telegrambot.model.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User {

    @Id
    @Column(name="id_user")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRole;

    @Column(name = "telegram_user_id")
    private Integer telegramUserId;

    @Column(name = "telegram_user_name")
    private String telegramUserName;

    @Enumerated(EnumType.STRING)
    @Column(name = "language_type")
    private LanguageType languageType;

    @OneToMany(mappedBy = "user",fetch = FetchType.EAGER)
    private List<Report> reportList=new ArrayList<>();

    public static User getUserDto(UserDto userDto){
        User user = new User();
        user.setTelegramUserId(userDto.getTelegramUserId());
        user.setTelegramUserName(userDto.getTelegramUserName());
        user.setLanguageType(userDto.getLanguageType());
        return user;
    }
}
