package dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ValidUserRequest {

    private String userName;
    private String password;


}
