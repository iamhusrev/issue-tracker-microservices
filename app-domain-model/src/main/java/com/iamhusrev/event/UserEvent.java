package com.iamhusrev.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserEvent extends BaseEvent {

    private Long userId;
    private String userName;
    private String firstName;
    private String lastName;

    public UserEvent(String eventType, Long userId, String userName,
                     String firstName, String lastName) {
        super(eventType, "user-service");
        this.userId = userId;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
